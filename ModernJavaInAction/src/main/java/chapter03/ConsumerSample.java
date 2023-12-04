package chapter03;

import java.util.List;
import java.util.function.Consumer;

public class ConsumerSample {
    public static <T> void forEach(List<T> list, Consumer<T> consumer) {
        for (T t : list) {
            consumer.accept(t);
        }
    }

    public static void main(String[] args) {
        /**
         * Consumer
         * T 를 입력받아 void 를 리턴한다.
         */
        forEach(List.of(1,2,3,4,5), i -> System.out.println("i = " + i));
    }
}
