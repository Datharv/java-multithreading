package multithreading.reetrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockDemo {


    private final static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {

        Runnable task = ()-> {

            String name = Thread.currentThread().getName();
            try{
                if(lock.tryLock(500, TimeUnit.MILLISECONDS)){

                    try{
                        System.out.println(name + " got the lock ");
                        Thread.sleep(1000);
                    }finally {
                        lock.unlock();
                        System.out.println(name + " released the lock");
                    }

                }else{

                    System.out.println(name + " could not acquire lock, skipping ...");
                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");

        t1.start();
        t2.start();
    }
}
