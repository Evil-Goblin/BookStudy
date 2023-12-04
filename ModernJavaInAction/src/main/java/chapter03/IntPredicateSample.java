package chapter03;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class IntPredicateSample {
    public static List<Integer> filter(List<Integer> list, IntPredicate predicate) {
        List<Integer> result = new ArrayList<>();
        for (Integer t : list) {
            if (predicate.test(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        /**
         * IntPredicate
         * int 를 입력받아 boolean 을 리턴한다.
         *
         * Predicate 는 박싱된 Integer 를 전달받기 때문에 int 를 전달받아 Integer 로 박싱하지만
         * IntPredicate 는 기본형인 int 자체를 지원한다.
         * 외에도 기본형 특화 함수형 인터페이스가 존재한다.
         */
        List<Integer> filter = filter(List.of(1, 2, 3, 4, 5), i -> i > 2);
        System.out.println("filter = " + filter);
    }
}
