package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolCounterDemo {

    public static void main(String[] args) throws InterruptedException {

        int numTasks = 6;
        int poolSize = 3;

        AtomicInteger counter = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(numTasks);

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        Runnable task = ()-> {

            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " started task");

            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet();
            }

            System.out.println(threadName + " finished task");
            latch.countDown();
        };

        System.out.println("Submitting Task...........");

        for (int i = 0; i < numTasks; i++) {
            executor.submit(task);
        }

        latch.await();

        System.out.println("All tasks finished. Final counter value : " + counter.get());

        executor.shutdown();
    }



}
