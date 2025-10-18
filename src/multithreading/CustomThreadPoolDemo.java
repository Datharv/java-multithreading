package multithreading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolDemo {

    public static void main(String[] args) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

        Runnable task = ()-> {
            String name = Thread.currentThread().getName();
            System.out.println(name + " is executing task");
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            System.out.println(name + " finished task");
        };

        System.out.println("Submitting 8 tasks");

        for (int i = 1; i <=8 ; i++) {
            final int id = i;
            executor.submit(()-> {
                System.out.println("Task " + id + " started by " + Thread.currentThread().getName());
                task.run();
            });
        }

        executor.shutdown();
    }
}
