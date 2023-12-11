package chapter19;

import static chapter19.LazyList.*;

public class Client {
    public static void main(String[] args) {
//        LazyList<Integer> numbers = LazyList.from(2);
//        int two = numbers.head();
//        int three = numbers.tail().head();
//        int four = numbers.tail().tail().head();
//
//        System.out.println(two + " " + three + " " + four);

        LazyList<Integer> numbers = from(2);
        int two = primes(numbers).head();
        int three = primes(numbers).tail().head();
        int five = primes(numbers).tail().tail().head();

        System.out.println(two + " " + three + " " + five);

//        printAll(primes(from(2))); // stackoverflow
    }

    static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(
                numbers.head(),
                () -> primes(
                        numbers.tail()
                                .filter(n -> n % numbers.head() != 0)
                )
        );
    }
}
