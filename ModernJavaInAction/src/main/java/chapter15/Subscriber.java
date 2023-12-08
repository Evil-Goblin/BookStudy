package chapter15;

public interface Subscriber<T> {
    void onNext(T t);
}
