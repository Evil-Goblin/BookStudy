package chapter15;

public class StringPublisher implements Publisher<String> {
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        subscriber.onNext("onNext!!");
    }
}
