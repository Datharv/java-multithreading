package multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiDownloadToy {

    static class DownloadTask implements Runnable{

        private final String fileName;
        private final int chunks;
        private final AtomicInteger completedChunks;

        public DownloadTask(String fileName, int chunks, AtomicInteger completedChunks) {
            this.fileName = fileName;
            this.chunks = chunks;
            this.completedChunks = completedChunks;
        }

        @Override
        public void run() {

            String name = Thread.currentThread().getName();;
            Random rnd = new Random();

            for(int i = 1;i<=chunks;i++) {
                try{
                    Thread.sleep(100 + rnd.nextInt(200));
                }catch (InterruptedException e){
                    System.out.printf("%s : interrupted while downloading %s%n", name, fileName);
                    Thread.currentThread().interrupt();
                    return;
                }

                completedChunks.incrementAndGet();
                System.out.printf("%s: %s chunk %d/%d done %n", name, fileName, i, chunks);

            }
            System.out.printf("%s : completed %s%n", name, fileName);
        }
    }

    public static void main(String[] args)  throws InterruptedException{

        AtomicInteger completedChunks = new AtomicInteger(0);
        String[] files = {"a.zip", "b.zip", "c.zip"};
        List<Thread> workers = new ArrayList<>();

        for (int i = 0;i< files.length;i++) {
            Thread t = new Thread(new DownloadTask(files[i], 5, completedChunks), "dl-" + (i+1));
            workers.add(t);
            t.start();
        }

        Thread reporter = new Thread(()-> {
            try{
                while (true) {
                    System.out.printf("Total Completed Chunks : %d%n", completedChunks.get());
                    Thread.sleep(300);
                    boolean allDead = workers.stream().allMatch(w -> !w.isAlive());
                    if(allDead) break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Reporter Exiting");
        }, "reporter");

        reporter.setDaemon(true);
        reporter.start();

        for(Thread w : workers) {
            w.join();
        }
        System.out.println("All downloads finished");
    }
}
