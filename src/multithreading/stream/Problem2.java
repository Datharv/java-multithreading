package multithreading.stream;


import java.util.Arrays;
import java.util.List;


//Find the sum of all even numbers in a list using Java Streams.
public class Problem2 {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(3, 6, 9, 12, 15, 18);

        System.out.println("Numbers : " + numbers);

//        1. Typecasting to primitive and sum
        int tsum = numbers.stream().filter(n-> (n&1) == 0).mapToInt(n->n).sum();
        System.out.println("TypeCasting Sum : " + tsum);

//        2. Using reduce
        int rsum = numbers.stream().filter(n->(n&1)==0).reduce(0, (a, b)-> a+b);
        System.out.println("Reduce Sum : " + rsum);

    }
}
