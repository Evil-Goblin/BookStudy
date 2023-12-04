package chapter03;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class UnaryOperatorSample {
    public static <T> List<T> sameMap(List<T> list, UnaryOperator<T> unaryOperator) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            result.add(unaryOperator.apply(t));
        }
        return result;
    }

    public static void main(String[] args) {
        /**
         * UnaryOperator
         * T 를 입력받아 T 을 리턴한다.
         *
         * Function 을 상속받아 구현되었다.
         * 입력과 출력의 타입이 다른 Function 과 달리 입력과 출력이 같다.
         */
        List<Integer> unary = sameMap(List.of(1,2,3,4,5), i -> i * 10);
        System.out.println("unary = " + unary);
    }
}
