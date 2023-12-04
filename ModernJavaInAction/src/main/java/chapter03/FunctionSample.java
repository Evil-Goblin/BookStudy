package chapter03;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunctionSample {
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T t : list) {
            result.add(function.apply(t));
        }
        return result;
    }

    public static void complexFunction() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = f.andThen(g); // f 다음 g 수행
        int result = h.apply(1);
        System.out.println("f.andThen(g): g(f(x)) result = " + result);

        h = f.compose(g); // g 다음 f 수행
        result = h.apply(1);
        System.out.println("f.compose(g): f(g(x)) result = " + result);
    }

    public static void main(String[] args) {
        /**
         * Function
         * T 를 입력받아 R 을 리턴한다.
         */
        List<Integer> map = map(List.of("lambda", "in", "action"), String::length);
        System.out.println("map = " + map);

        complexFunction();
    }
}
