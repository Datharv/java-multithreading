package multithreading.stream;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

//Sort a list of integers or strings using Java Streams.
public class Problem6 {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 1, 9, 3, 7);

        List<Integer> sortedAsc = numbers.stream().sorted().toList();
        System.out.println(sortedAsc);

        List<Integer> sortedDesc = numbers.stream().sorted((a, b)-> b-a).toList();

        List<Integer> sortedDesc2 = numbers.stream().sorted(Comparator.reverseOrder()).toList();
        System.out.println(sortedDesc);
        System.out.println(sortedDesc2);


        List<String> words = Arrays.asList("apple", "banana", "kiwi", "grape");

        List<String> sortedWords = words.stream().sorted().toList();
        System.out.println(sortedWords);

//        sorting by length
        List<String> sortedByLength = words.stream().sorted(Comparator.comparing(String::length)).toList();
        System.out.println("Sorted by Length : " + sortedByLength);


    }
}
