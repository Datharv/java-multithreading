package multithreading.stream;

//Remove duplicates from a list using Java Streams.

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Problem7 {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 2, 4, 1, 5);

        List<Integer> uniques = numbers.stream().distinct().toList();
        System.out.println("uniques : " + uniques);

//        Alternate Collectors.toSet

        Set<Integer> uniques2 = numbers.stream().collect(Collectors.toSet());
    }
}
