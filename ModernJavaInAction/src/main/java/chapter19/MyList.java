package chapter19;

import java.util.function.Predicate;

public interface MyList<T> {
    T head();

    MyList<T> tail();

    MyList<T> filter(Predicate<T> predicate);

    default boolean isEmpty() {
        return true;
    }
}
