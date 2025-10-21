package multithreading.reetrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class FairLockDemo {
    private static final ReentrantLock lock = new ReentrantLock(true); // ⚖️ fair mode

    public static void main(String[] args) {
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < 2; i++) {
                lock.lock();
                try {
                    System.out.println(name + " acquired lock");
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        };

        for (int i = 1; i <= 3; i++) {
            new Thread(task, "Worker-" + i).start();
        }
    }
}
