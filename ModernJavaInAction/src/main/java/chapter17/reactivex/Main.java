package chapter17.reactivex;


import chapter17.TempInfo;
import io.reactivex.rxjava3.core.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
//        Observable<TempInfo> observable = getNegativeTemperature("NewYork");
        Observable<TempInfo> observable = getCelsiusTemperatures("NewYork", "Chicago", "SanFrancisco");
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

    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(tempInfo -> new TempInfo(tempInfo.getTown(), (tempInfo.getTemp() - 32) * 5 / 9));
    }

    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                .map(Main::getCelsiusTemperature)
                .toList());
    }

    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getCelsiusTemperature(town)
                .filter(tempInfo -> tempInfo.getTemp() < 0);
    }
}
