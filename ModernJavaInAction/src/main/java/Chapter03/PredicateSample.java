package Chapter03;

import Chapter01.Apple;

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

    public static void negativePredicate(Predicate<Apple> isRedApple) {
        Predicate<Apple> isNotRedApple = isRedApple.negate(); // Predicate 의 조건을 반전시킨 객체
    }

    public static void predicateChain(Predicate<Apple> isRedApple) {
        Predicate<Apple> redAndHeavyApple = isRedApple.and(apple -> apple.getWeight() > 150);
        Predicate<Apple> redOrHeavyApple = isRedApple.or(apple -> apple.getWeight() > 150);
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
