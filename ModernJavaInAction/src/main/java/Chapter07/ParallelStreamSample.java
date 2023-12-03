package Chapter07;

import java.util.stream.Stream;

public class ParallelStreamSample {
    public static void main(String[] args) {
        long n = 10;
        long sequentialed = sequentialSum(n);
        System.out.println("sequentialed = " + sequentialed);
        long parallelled = parallelSum(n);
        System.out.println("parallelled = " + parallelled);

        // parallel 을 통해 이후 연산이 병렬로 수행
        // sequential 을 통해 이후 연산이 순차로 수행
        // stream.parallel()
        //       .filter(...)
        //       .sequential()
        //       .map(...)
        //       .parallel()
        //       .reduce();
    }

    private static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .parallel()
                .reduce(0L, Long::sum);
    }

    private static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .reduce(0L, Long::sum);
    }


}
