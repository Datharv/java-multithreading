package multithreading.stream;


//Count how many strings start with a specific letter using Java Streams.

import java.util.Arrays;
import java.util.List;

public class Problem4 {

    public static void main(String[] args) {

        List<String> names = Arrays.asList("Alice", "Bob", "Ankit", "Arjun", "Bharat");
//        with letter "A"

        int count = (int)names.stream().filter(s-> s.toUpperCase().startsWith("A")).count();
        System.out.println("names starting with A : " + count);

        int cnt = names.stream().mapToInt(s-> s.toUpperCase().startsWith("A") ? 1 : 0).sum();
        System.out.println(cnt);
    }
}
