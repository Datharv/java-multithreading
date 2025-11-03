package multithreading.stream;


import java.util.Arrays;
import java.util.List;

//Convert a list of strings to uppercase using Java Streams.
public class Problem3 {

    public static void main(String[] args) {

        List<String> words = Arrays.asList("java", "stream", "api");
        System.out.println("List before Operation : " + words);

        List<String> upperWords = words.stream().map(String::toUpperCase).toList();
        System.out.println("List After Operation : " + upperWords);

        List<String> upperWords2 =  upperWords.stream().map(String::trim).filter(s-> !s.isEmpty()).map(String::toUpperCase).toList();

    }
}
