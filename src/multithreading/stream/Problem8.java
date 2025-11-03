package multithreading.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Group a list of strings by their length using Java Streams.
public class Problem8 {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("hi", "hello", "world", "to", "java", "go");

        Map<Integer, List<String>> map = words.stream().collect(Collectors.groupingBy(s -> s.length()));
        System.out.println(map);

//        Example – Group by First Letter

        Map<Character, List<String>> charMap = words.stream().collect(Collectors.groupingBy(word -> word.charAt(0)));
        System.out.println(charMap);

//        count of words per length
        Map<Integer, Long> countPerWord = words.stream().collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println(countPerWord);

    }
}
