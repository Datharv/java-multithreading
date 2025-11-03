package multithreading.stream;


// Convert a list of integers to a list of their squares using Java Streams.

import java.util.Arrays;
import java.util.List;

public class Problem1 {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("numbers : " + numbers);
        List<Integer> squares = numbers.stream().map(n -> n*n).toList();
        System.out.println("Squares : " + squares);

    }
}
