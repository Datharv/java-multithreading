package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatchEx {

    public static void main(String[] args) throws InterruptedException{

        int numTreads = 3;
        CountDownLatch latch = new CountDownLatch(numTreads);
        AtomicInteger counter = new AtomicInteger();

        Runnable task = ()-> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet();
            }
            latch.countDown();
        };

        for (int i = 0; i < numTreads; i++) {
            new Thread(task).start();
        }
        latch.await();
        System.out.println("Final counter : " + counter.get());

    }
}
