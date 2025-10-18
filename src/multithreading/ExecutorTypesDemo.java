package multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTypesDemo {

    public static void main(String[] args) throws InterruptedException{

        ExecutorService fixedPool = Executors.newFixedThreadPool(2);

        ExecutorService cachedPool = Executors.newCachedThreadPool();

        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + " is executing task");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };


        System.out.println("-----------Fixed Pool-------");
        for (int i = 0; i < 4; i++) {
            fixedPool.submit(task);
        }

        System.out.println("-----------Cached Pool-------");
        for (int i = 0; i < 4; i++) {
            cachedPool.submit(task);
        }

        System.out.println("-----------Scheduled Pool-------");

        scheduledPool.scheduleAtFixedRate(task,  1, 2 , TimeUnit.SECONDS);

        Thread.sleep(6000);
        fixedPool.shutdown();
        cachedPool.shutdown();
        scheduledPool.shutdown();
    }
}
