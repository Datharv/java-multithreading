package multithreading.stream;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Partition a list of integers into even and odd numbers using Java Streams.
public class Problem10 {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Map<Boolean, List<Integer>> map = numbers.stream().collect(Collectors.partitioningBy(n-> n%2 == 0));
        System.out.println(map);
        System.out.println("Even numbers : " + map.get(true));
        System.out.println("Odd numbers : " + map.get(false));

//        Count
        Map<Boolean, Long> countMap = numbers.stream().collect(Collectors.partitioningBy(n->n%2 == 0, Collectors.counting()));
        System.out.println(countMap);

    }
}
