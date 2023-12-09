package chapter17.reactivex;


import chapter17.TempInfo;
import io.reactivex.rxjava3.core.Observable;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Observable<TempInfo> observable = getTemperature("NewYork");
        observable.blockingSubscribe(new TempObserver());
    }
    public static Observable<TempInfo> getTemperature(String town) {
        return Observable.create(emitter ->
                Observable.interval(1, TimeUnit.SECONDS)
                        .subscribe(i -> {
                            if (!emitter.isDisposed()) {
                                if (i >= 5) {
                                    emitter.onComplete();
                                } else {
                                    try {
                                        emitter.onNext(TempInfo.fetch(town));
                                    } catch (Exception e) {
                                        emitter.onError(e);
                                    }
                                }
                            }
                        }));
    }
}
