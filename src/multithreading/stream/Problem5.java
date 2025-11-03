package multithreading.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


//Find the maximum and minimum elements in a list using Java Streams.

public class Problem5 {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(12, 45, 2, 67, 34, 9);
        int max = numbers.stream().max(Integer::compareTo).get();
        int max2 = numbers.stream().max((a, b)-> a-b).get();
        int max3 = numbers.stream().max(Comparator.comparingInt(Integer::intValue)).get();
        System.out.println(max3);


        int min = numbers.stream().mapToInt(n->n).min().getAsInt();
        System.out.println(min);


        List<String> names = Arrays.asList("Atharv", "Bob", "Christopher", "Alex");

        String longest = names.stream().max(Comparator.comparing(String::length)).orElse("No names");
//        String longest = names.stream().max((a, b) -> a.length() - b.length()).get();
        System.out.println(longest); // Christopher


    }
}
