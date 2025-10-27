package multithreading.reetrantlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteDemo {

    private static final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
    private static final Lock readLock = rwlock.readLock();
    private static final Lock writeLock = rwlock.writeLock();
    private static int sharedData = 0;

    public static void main(String[] args) throws InterruptedException{

        Runnable reader = ()-> {

            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " reads " + sharedData);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                readLock.unlock();
            }
        };

        Runnable writer = () -> {
            writeLock.lock();
            try {
                sharedData++;
                System.out.println(Thread.currentThread().getName() + " writes " + sharedData);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        };

        Thread r1 = new Thread(reader, "Reader-1");
        Thread r2 = new Thread(reader, "Reader-2");
        Thread w1 = new Thread(writer, "Writer-1");

        r1.start();
        r2.start();
        Thread.sleep(50); // Ensure readers start first
        w1.start();
    }
}
