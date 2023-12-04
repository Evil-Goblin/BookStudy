package chapter05;

import java.util.stream.Stream;

public class Infinite {
    public static void main(String[] args) {
        // iterate
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);

        // iterate 를 활용한 피보나치 수열
        Stream.iterate(new int[]{0, 1}, arr -> new int[]{arr[1], arr[0] + arr[1]})
                .limit(20)
                .forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));

        // iterate Predicate 를 이용한 작업 제한
        Stream.iterate(0, n -> n < 100, n -> n + 4)
                .forEach(System.out::println);

        // generate
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);
    }
}
