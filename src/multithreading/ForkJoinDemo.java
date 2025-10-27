package multithreading;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10;
    private final int[] arr;
    private final int start, end;

    SumTask(int[] arr, int start, int end) {
        this.arr = arr; this.start = start; this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) { // base case
            long sum = 0;
            for (int i = start; i < end; i++) sum += arr[i];
            return sum;
        } else { // split
            int mid = (start + end) / 2;
            SumTask left = new SumTask(arr, start, mid);
            SumTask right = new SumTask(arr, mid, end);
            left.fork(); // execute left asynchronously
            long rightResult = right.compute(); // compute right in current thread
            long leftResult = left.join(); // wait for left
            return leftResult + rightResult;
        }
    }
}

public class ForkJoinDemo {
    public static void main(String[] args) {
        int[] array = new int[100];
        for (int i = 0; i < array.length; i++) array[i] = i + 1;

        ForkJoinPool pool = new ForkJoinPool();
        long sum = pool.invoke(new SumTask(array, 0, array.length));
        System.out.println("Sum = " + sum);
    }
}
