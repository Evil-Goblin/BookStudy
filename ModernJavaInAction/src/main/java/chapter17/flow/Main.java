package chapter17.flow;

import java.util.concurrent.Flow;

public class Main {
    public static void main(String[] args) {
        getTemperatures("New York").subscribe(new TempSubscriber());
    }

    private static Flow.Publisher<TempInfo> getTemperatures(String town) {
//        return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
        return subscriber -> {
            TempProcessor tempProcessor = new TempProcessor();
            tempProcessor.subscribe(subscriber);
            tempProcessor.onSubscribe(new TempSubscription(tempProcessor, town));
        };
    }
}
