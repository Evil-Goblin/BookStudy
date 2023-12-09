package chapter17.flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class TempSubscription implements Subscription {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Subscriber<? super TempInfo> subscriber;
    private final String town;

    public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
        this.subscriber = subscriber;
        this.town = town;
    }

    @Override
    public void request(long n) {
        // 이 코드는 단일 스레드에서 수행되다보니 스택을 사용하게 되고 결과 수행이 늘어날 수록 stackoverflow 를 발생시킨다.
//        for (long i = 0L; i < n; i++) {
//            try {
//                subscriber.onNext(TempInfo.fetch(town));
//            } catch (Exception e) {
//                subscriber.onError(e);
//                break;
//            }
//        }

        executor.submit(() -> {
            for (long i = 0L; i < n; i++) {
                try {
                    subscriber.onNext(TempInfo.fetch(town));
                } catch (Exception e) {
                    subscriber.onError(e);
                    break;
                }
            }
        });
    }

    @Override
    public void cancel() {
        subscriber.onComplete();
    }
}
