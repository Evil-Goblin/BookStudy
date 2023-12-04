package chapter05;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Pythagoras {
    public static void main(String[] args) {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .flatMap(a ->
                    IntStream.rangeClosed(a, 100)
                            .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                            .mapToObj(b -> new int[]{a, b, (int)Math.sqrt(a* a + b * b)})
                ).forEach(arr -> System.out.println(arr[0] + ", " + arr[1] + ", " + arr[2]));

        IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 100)
                        .mapToObj(
                                b -> new double[]{a, b, Math.sqrt(a*a + b*b)}
                        ).filter(t -> t[2] % 1 == 0))
                .map(arr -> Arrays.stream(arr).mapToInt(value -> (int) value).toArray())
                .forEach(arr -> System.out.println(arr[0] + ", " + arr[1] + ", " + arr[2]));
    }
}
