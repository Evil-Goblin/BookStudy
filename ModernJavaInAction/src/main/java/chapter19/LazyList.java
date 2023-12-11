package chapter19;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazyList<T> implements MyList<T> {
    private final T head;
    private final Supplier<MyList<T>> tail;

    public LazyList(T head, Supplier<MyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public MyList<T> filter(Predicate<T> p) {
        return isEmpty() ? this : p.test(head()) ?
                new LazyList<>(head(), () -> tail().filter(p)) :
                tail().filter(p);
    }

    public static LazyList<Integer> from(int n) {
        // LazyLoading 이기 때문에 무한정생성되지 않는다.
        return new LazyList<>(n, () -> from(n + 1));
    }

    static <T> void printAll(MyList<T> list) {
        if (list.isEmpty())
            return;

        System.out.println("list.head() = " + list.head());
        printAll(list.tail());
    }
}
