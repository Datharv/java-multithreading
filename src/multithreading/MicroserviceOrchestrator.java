package multithreading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MicroserviceOrchestrator {

    static class Service{
        final String name;
        final List<String> dependencies;
        final List<String> dependents = new ArrayList<>();
        final CountDownLatch dependencyLatch;
        volatile boolean healthy = false;

        Service(String name, List<String> dependencies, CountDownLatch dependencyLatch){
            this.name = name;
            this.dependencies = dependencies;
            this.dependencyLatch = dependencyLatch;
        }

//        simulate startup work

        void startup() throws InterruptedException{

            if(dependencyLatch.getCount() > 0) {
                System.out.printf("[%s] waiting for %d dependencies to be ready%n", name, dependencyLatch.getCount());
                dependencyLatch.await();
//                blocks until all dependencies call countdown
            }

            int startupMs = 500 + (int)(Math.random()*1500);
            System.out.printf("[%s] starting (will take %dms)...%n", name, startupMs);
            Thread.sleep(startupMs);

// Simulated health check: here we just set healthy = true
            healthy = true;
            System.out.printf("[%s] is READY%n", name);
        }

        void restart() throws InterruptedException{

            System.out.printf("[%s] stopping for restart...%n", name);

            Thread.sleep(200 + (int)(Math.random()*400));
            healthy = false;
            System.out.printf("[%s] starting after restart...%n", name);
            Thread.sleep(300 + (int)(Math.random()*700));
            healthy = true;
            System.out.printf("[%s] back UP after restart%n", name);

        }

        // Simulated health check as Callable so it can be timed out
        Callable<Boolean> healthCheckTask() {
            return () -> {
                // Simulate variable response and occasional transient failure
                Thread.sleep(100 + (int)(Math.random() * 200));
                // 95% chance healthy if healthy flag is true, else 20% chance still returns true
                if (healthy) return Math.random() < 0.95;
                return Math.random() < 0.20;
            };
        }
    }

    public static void main(String[] args) throws Exception{

        Map<String , List<String>> deps = Map.of(
                "Auth", List.of(),
                "User", List.of("Auth"),
                "Billing", List.of("Auth", "User"),
                "Catalog", List.of("Auth"),
                "Order", List.of("User", "Catalog", "Billing"),
                "Notification", List.of("User")
        );

        // Create CountDownLatch for each service equal to its dependency count
        Map<String, CountDownLatch> readyLatches = new HashMap<>();
        for (var entry : deps.entrySet()) {
            readyLatches.put(entry.getKey(), new CountDownLatch(entry.getValue().size()));
        }

        // Create Service objects and map name->Service
        Map<String, Service> services = new HashMap<>();
        for (var entry : deps.entrySet()) {
            services.put(entry.getKey(),
                    new Service(entry.getKey(), entry.getValue(), readyLatches.get(entry.getKey())));
        }

        // Build dependents lists (so a service can notify its dependents after it becomes ready)

        for(var s : services.values()){
            for(String dep: s.dependencies) {
                Service depService = services.get(dep);
                depService.dependents.add(s.name);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(6);

        // Start all services concurrently: each service waits internally on its dependencyLatch

        System.out.println("=== Orchestrator: Starting services (respecting dependencies) ===");

        for (Service s : services.values()) {
            executor.submit(() -> {
                try {
                    s.startup(); // waits for dependencies internally
                    // Notify dependents: decrement their latch counts
                    for (String dependentName : s.dependents) {
                        CountDownLatch latchOfDependent = readyLatches.get(dependentName);
                        // one dependency is satisfied
                        latchOfDependent.countDown();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Wait a bit for the system to stabilize
        Thread.sleep(3000);

        // Quick health check across all services using Futures with timeouts
        System.out.println("\n=== Orchestrator: Performing initial health checks ===");
        performHealthChecks(executor, services, 500);

        // Simulate triggering a rolling update (e.g., new version deploy)
        System.out.println("\n=== Orchestrator: Triggering rolling update ===");
        performRollingUpdate(executor, services, 2, 3);

        // Final health check
        System.out.println("\n=== Orchestrator: Final health checks ===");
        performHealthChecks(executor, services, 500);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("\n=== Orchestrator: Done ===");
    }


    static void performHealthChecks(ExecutorService executor, Map<String, Service> services, long timeoutMs) {
        List<Future<Boolean>> futures = new ArrayList<>();
        List<String> names = new ArrayList<>(services.keySet());
        for (String name : names) {
            Service s = services.get(name);
            futures.add(executor.submit(s.healthCheckTask()));
        }

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Future<Boolean> f = futures.get(i);
            try {
                Boolean ok = f.get(timeoutMs, TimeUnit.MILLISECONDS);
                System.out.printf("[health-check] %s -> %s%n", name, ok ? "OK" : "FAIL");
            } catch (TimeoutException te) {
                System.out.printf("[health-check] %s -> TIMEOUT%n", name);
                f.cancel(true);
            } catch (Exception e) {
                System.out.printf("[health-check] %s -> ERROR (%s)%n", name, e.getMessage());
            }
        }
    }

    /**
     * Rolling update:
     * - windowSize: how many services can be restarted concurrently (Semaphore permits)
     * - batchSize: we group services into batches of this size to coordinate phases across the batch using CyclicBarrier
     */
    static void performRollingUpdate(ExecutorService executor,
                                     Map<String, Service> services,
                                     int windowSize,
                                     int batchSize) throws InterruptedException {
        List<Service> serviceList = new ArrayList<>(services.values());

        // We'll just update services in the current order (could be topo sort if you want dependency-aware rolling)
        List<List<Service>> batches = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i += batchSize) {
            batches.add(serviceList.subList(i, Math.min(i + batchSize, serviceList.size())));
        }

        for (int batchIndex = 0; batchIndex < batches.size(); batchIndex++) {
            List<Service> batch = batches.get(batchIndex);
            System.out.printf("%n--- Rolling update: batch %d -> %s%n", batchIndex + 1,
                    batch.stream().map(s -> s.name).collect(Collectors.joining(", ")));

            // Semaphore limits concurrent restarts across the entire batch (window)
            Semaphore window = new Semaphore(windowSize);

            // Barrier to sync: prepare -> restart -> post-check
            CyclicBarrier barrier = new CyclicBarrier(batch.size(),
                    () -> System.out.println(">>> batch barrier action: phase complete"));

            // For each service in batch, submit a restart task
            List<Future<?>> futures = new ArrayList<>();
            for (Service s : batch) {
                futures.add(executor.submit(() -> {
                    try {
                        // PHASE 1: prepare (e.g., drain connections, take out of LB)
                        System.out.printf("[%s] PREPARE for restart (drain)...%n", s.name);
                        Thread.sleep(200 + (int)(Math.random() * 300));
                        barrier.await(); // wait for other batch services to finish prepare

                        // PHASE 2: restart (limited by window semaphore)
                        window.acquire();
                        try {
                            System.out.printf("[%s] performing restart (acquired window permit)%n", s.name);
                            s.restart();
                        } finally {
                            window.release();
                            System.out.printf("[%s] released window permit%n", s.name);
                        }
                        barrier.await(); // wait for others to finish restart

                        // PHASE 3: post-check (run health check)
                        System.out.printf("[%s] POST-CHECK starting%n", s.name);
                        Future<Boolean> hc = Executors.newSingleThreadExecutor().submit(s.healthCheckTask());
                        try {
                            boolean ok = hc.get(700, TimeUnit.MILLISECONDS);
                            System.out.printf("[%s] POST-CHECK -> %s%n", s.name, ok ? "OK" : "FAIL");
                        } catch (TimeoutException te) {
                            System.out.printf("[%s] POST-CHECK -> TIMEOUT%n", s.name);
                            hc.cancel(true);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } finally {
                            // small delay to simulate reattaching to LB
                            Thread.sleep(100);
                        }
                        barrier.await(); // finish post-check phase and let batch proceed (barrier action prints)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (BrokenBarrierException e) {
                        System.out.printf("[%s] barrier broken: %s%n", s.name, e.getMessage());
                    }
                }));
            }

            // Wait for all futures in this batch to finish
            for (Future<?> f : futures) {
                try { f.get(); } catch (Exception ignored) { }
            }
            System.out.printf("--- Batch %d completed%n", batchIndex + 1);
        }
    }
}
