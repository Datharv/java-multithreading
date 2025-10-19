package multithreading;

import java.util.concurrent.*;

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException  {

        BlockingQueue<String> buffer = new ArrayBlockingQueue<>(5);

        Runnable producer = () -> {
            String threadName = Thread.currentThread().getName();

            for (int i = 1; i <= 10 ; i++) {
                try {
                    String item = "Dish-"+i;
                    buffer.put(item);
                    System.out.println(threadName + " prepared " + item);
                    Thread.sleep(100);
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };


        Runnable consumer = () -> {

            String threadName = Thread.currentThread().getName();



                try {
                    for(int i = 1;i<=10;i++) {
                        String item = buffer.take();
                        System.out.println(threadName + " served " + item);
                        Thread.sleep(150);
                    }
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
        };

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(producer);
        executor.submit(producer);
        executor.submit(consumer);
        executor.submit(consumer);
        executor.shutdown();

        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("All dishes prepared and served.");
    }
}
