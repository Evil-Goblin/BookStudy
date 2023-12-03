package Chapter07;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@State(Scope.Benchmark)
public class ParallelStreamBenchmark {
    private static final long N = 10_000_000L;

    @Benchmark
    public long sequentialSum() {
        // iterate 는 제네릭타입이다보니 박싱된 기본타입을 이용하게 되어 오토박싱 언박싱의 반복때문에 성능이 별로 좋지 않다.
        // rangeClosed 는 언박싱된 기본타입을 이용하게 되어 성능이 월등히 좋다.
        // 또한 iterate 는 연산의 분할이 불가능하기 때문에 parallel 을 이용하면 성능이 오히려 나빠진다.(스레드의 생성 비용)
        // 반면 rangeClosed 는 연산의 분할이 가능하기 때문에 성능이 좋다.
        return Stream.iterate(1L, i -> i + 1)
                .limit(N)
                .reduce(0L, Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }
}
