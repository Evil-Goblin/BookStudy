package Chapter03;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateSample {
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        /**
         * Predicate
         * T 를 입력받아 boolean 을 리턴한다.
         */
        List<Integer> filter = filter(List.of(1, 2, 3, 4, 5), i -> i > 2);
        System.out.println("filter = " + filter);
    }
}
