package Chapter03;

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

    public static void main(String[] args) {
        /**
         * Function
         * T 를 입력받아 R 을 리턴한다.
         */
        List<Integer> map = map(List.of("lambda", "in", "action"), String::length);
        System.out.println("map = " + map);
    }
}
