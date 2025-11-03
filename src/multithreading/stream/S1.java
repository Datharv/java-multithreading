package multithreading.stream;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class S1 {
    public static void main(String[] args) {

//  Creation of Stream
//        1. From arrays
        String[] fruits = {"apple", "banana", "mango"};
        Stream<String> stringStream = Arrays.stream(fruits);
        stringStream.forEach(System.out::println);

//        2. Stream.of
        Stream<String> s = Stream.of("A", "B", "C");
        s.forEach(System.out::println);

//        3. Specialized Stream
        int [] num = {2, 4, 5, 6, 6};
        IntStream intStream = Arrays.stream(num);
        int sum = intStream.sum();
        System.out.println(sum);

//        4. static factory methods
        Stream<String> st = Stream.of("Atharv", "Java", "Streams");
        Stream<String> emptyStream = Stream.empty();

//        5. Infinite Stream
        Stream<Double> doubleStream = Stream.generate(Math::random);
        doubleStream.limit(5).forEach(System.out::println);

        Stream<Integer> integerStream = Stream.iterate(0, n->n+2);
        integerStream.limit(5).forEach(System.out::println);

//        6. Using Stream Builder

        Stream<String> stringStream1 = Stream.<String>builder().add("abc").add("def").build();
        stringStream1.forEach(System.out::println);
    }
}
