package Chapter06;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ImproveSample {
    public static void main(String[] args) {
        Map<Boolean, List<Integer>> improvePrimeNumber = IntStream.rangeClosed(2, 100).boxed()
                .collect(new PrimeNumbersCollector());
        System.out.println("improvePrimeNumber = " + improvePrimeNumber);
    }
}
