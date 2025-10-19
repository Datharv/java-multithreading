package multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class RaceSimulation {

    public static void main(String[] args) throws InterruptedException{

        int carCount = 5;
        int laps = 3;

        CountDownLatch raceStart = new CountDownLatch(1);
        CyclicBarrier lapBarrier = new CyclicBarrier(carCount, () -> System.out.println("\n🏁 All cars completed lap. Starting next lap!\n"));

        Semaphore pitStop = new Semaphore(3);
        System.out.println("Race setup complete. Cars getting ready...");

        for(int i=1;i<=carCount;i++) {
            new Thread(new Car("Car-" + i, raceStart, lapBarrier, pitStop, laps)).start();
        }

        Thread.sleep(2000);
        System.out.println("\n📣 All cars ready! Race starting in 3...2...1...\n");
        raceStart.countDown();
    }
}

class Car implements Runnable{

    private final String name;
    private final CountDownLatch receStart;
    private final CyclicBarrier lapBarrier;
    private final Semaphore pitStop;
    private final int totalLaps;


    public Car(String name, CountDownLatch receStart, CyclicBarrier lapBarrier, Semaphore pitStop, int totalLaps) {
        this.name = name;
        this.receStart = receStart;
        this.lapBarrier = lapBarrier;
        this.pitStop = pitStop;
        this.totalLaps = totalLaps;
    }


    @Override
    public void run() {
        try{
            System.out.println(name + " ready at the start line ...");

            receStart.await();

            for(int lap = 1; lap <= totalLaps; lap++) {
                driveLap(lap);

                if(Math.random() < 0.5) {
                    pitStop();
                }

                lapBarrier.await();
            }

            System.out.println(name + " 🏆 finished the race!");
        }catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void driveLap(int lap) throws InterruptedException{
        System.out.println(name + " driving lap " + lap + "....");
        Thread.sleep((int)(Math.random() * 2000) + 1000);
        System.out.println(name + " finished lap " + lap);
    }

    private void pitStop() throws InterruptedException {
        System.out.println(name + " requesting pit stop..");
        pitStop.acquire();
        System.out.println(name + " entered pit stop 🧰");
        Thread.sleep((int)(Math.random() * 1500) + 500);
        System.out.println(name + " leaving pit stop.");
        pitStop.release();
    }
}

