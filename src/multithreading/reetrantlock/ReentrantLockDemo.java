package multithreading.reetrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    private static final ReentrantLock lock = new ReentrantLock();
    private static int sharedCounter = 0;

    public static void main(String[] args) {
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < 3; i++) {
                lock.lock();   // 🔒 acquire lock
                try {
                    int oldValue = sharedCounter;
                    System.out.printf("%s acquired lock, old=%d%n", name, oldValue);
                    sharedCounter = oldValue + 1;
                    Thread.sleep(200);  // simulate work
                    System.out.printf("%s releasing lock, new=%d%n", name, sharedCounter);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock(); // 🔓 always release
                }
            }
        };

        Thread t1 = new Thread(task, "Worker-1");
        Thread t2 = new Thread(task, "Worker-2");
        Thread t3 = new Thread(task, "Worker-3");

        t1.start();
        t2.start();
        t3.start();
    }
}
