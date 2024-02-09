## wait 와 notify 는 올바르게 사용하기가 까다롭기 때문에 고수준 동시성 유틸리티를 사용하자.
- `java.util.concurrent` 는 세 범주로 나눌 수 있다.
  - 실행자 프레임워크
  - 동시성 컬렉션(`concurrent collection`)
  - 동기화 장치(`synchronizer`)

### 동시성 컬렉션
- 동시성 컬렉션은 `List` , `Queue` , `Map` 같은 표준 컬렉션 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션이다.
  - 높은 동시성에 도달하기 위해 동기화를 각자의 내부에서 수행한다.
- **동시성 컬렉션에서 동시성을 무력화하는 건 불가능하며, 외부에서 락을 추가로 사용하면 오히려 속도가 느려진다.**
- 동시성 컬렉션에서 동시성을 무력화하지 못하므로 여러 메서드를 원자적으로 묶어 호출하는 일 역시 불가능하다.
  - 때문에 여러 기본 동작을 하나의 원자적 동작으로 묶는 '상태 의존적 수정' 메서드들이 추가되었다.
  - 대표적으로 값이 없다면 새 값을 넣는 `Map` 의 `putIfAbsent(key, value)` 메서드가 있다.
- 동시성 컬렉션은 동기화된 컬렉션보다 성능이 월등히 좋다.
  - **`Collections.synchronizedMap` 보다 `ConcurrentHashMap` 의 성능이 훨씬 좋다.**
- 컬렉션 인터페이스 중 일부는 작업이 성공적으로 완료될 때까지 기다리도록 확장되었다.
  - 예를 들면 `Queue` 를 확장한 `BlockingQueue` 에 추가된 메서드 중 `take` 는 큐의 첫 원소를 꺼내지만, 만약 큐가 비어있다면 새로운 원소가 추가될 때까지 기다린다.
  - 이런 특성 덕분에 `BlockingQueue` 는 작업 큐로 사용하기 적합하다.
  - `ThreadPoolExecutor` 를 포함한 대부분의 실행자 서비스 구현체가 `BlockingQueue` 를 사용한다.
- 동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 하여 서로의 작업을 조율할 수 있게 해준다.
  - 가장 많이 사용되는 동기화 장치는 `CountDownLatch` , `Semaphore` 이다.
  - 가장 강력한 동기화 장치는 `Phaser` 이다.
- `CountDownLatch` 는 하나 이상의 스레드가 또 다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다.
  - `CountDownLatch` 의 유일한 생성자는 `int` 값을 받아 `countDown` 이 몇번 호출되어야 대기 중인 스레드들을 깨우는지 결정한다.
- 이를 이용해 `wait` , `notify` 만으로 구현하는 것 보다 직관적인 코드로 구현할 수 있게 된다.

```java
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
    CountDownLatch ready = new CountDownLatch(concurrency);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(concurrency);

    for (int i = 0; i < concurrency; i++) {
        executor.execute(() -> {
            ready.countDown();
            try {
                start.await();
                action.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        });
    }
    
    ready.await();
    long startNanos = System.nanoTime();
    start.countDown();
    done.await();
    return System.nanoTime() - startNanos;
}
```
- 카운트다운 래치 3개를 이용하여 모든 작업자들을 동시에 수행시키고 작업이 완료된 시간을 측정한다.

## wait 과 notify 사용 방침
- `wait` 메서드는 스레드가 어떤 조건이 충족되기를 기다리게 할 때 사용한다.
- 락 객체의 `wait` 메서드는 반드시 그 객체를 잠근 동기화 영역 안에서 호출해야 한다.

```java
synchronized (obj) {
    while (<조건 불충족>)
        obj.wait();
    
    ... // 조건 충족시의 동작 수행
}
```
- `wait` 의 표준 사용 방식은 위와 같다.
- **`wait` 메서드를 사용할 때는 반드시 대기 반복문(`wait loop`) 관용구를 사용하라. 반복문 밖에서는 절대로 호출하지 말자.**
- 이 반복문은 `wait` 호출 전후로 조건이 만족하는지를 검사하는 역할을 한다.
  - 대기 전 조건을 검사하여 조건 충족 여부에 따라 `wait` 을 건너뛰게 한 것은 응답 불가 상태를 예방하는 조치이다.
  - 만약 조건이 이미 충족되었는데 스레드가 `notify` 를 먼저 호출한 후 대기 상태로 빠지면, 그 스레드를 다시 깨울 수 있다고 보장할 수 없다.
  - 대기 후 조건을 재검사 후 다시 대기하는 것은 안전 실패를 막는 조치이다.
  - 만약 조건이 충족되지 않았는데 스레드가 동작을 이어가면 락이 보호하는 불변식을 깨뜨릴 위험이 있다.
- 조건이 만족되지 않아도 스레드가 깨어날 수 있는 상황이 몇가지 있다.
  - 스레드가 `notify` 를 호출한 다음 대기 중이던 스레드가 깨어나는 사이에 다른 스레드가 락을 얻어 그 락이 보호하는 상태를 변경한다.
  - 조건이 만족되지 않았음에도 다른 스레드가 실수로 혹은 악의적으로 `notify` 를 호출한다.
  - 깨우는 스레드는 지나치게 관대해서, 대기 중인 스레드 중 일부만 조건이 충족되어도 `notifyAll` 을 호출해 모든 스레드를 깨울 수도 있다.
  - 대기 중인 스레드가 `notify` 없이도 깨어나는 경우가 있다.(허위 각성)

### notify , notifyAll
- `notify` 는 스레드 하나만 깨우고, `notifyAll` 은 모든 스레드를 깨운다.
- 일반적으로는 `notifyAll` 을 사용하는 것이 합리적이지만 모든 스레드가 같은 조건을 기다리고, 조건이 한 번 충족될 때마다 단 하나의 스레드만 혜택을 받을 수 있다면 `notify` 를 통해 최적화할 수 있다.
- 외부로 공개된 객체에 대해서 실수, 혹은 악의적으로 `notify` 를 호출하는 상황을 대비하기 위해 `wait` 을 반복문 안에서 호출했듯, `notify` 대신 `notifyAll` 을 사용하면 관련 없는 스레드가 실수로 혹은 악의적으로 `wait` 을 호출하는 공격으로부터 보호할 수 있다.
  - 이런 스레드가 중요한 `notify` 를 삼켜버린다면 꼭 깨어났어야 할 스레드들이 영원히 대기하게 될 수 있다.

## 정리
- `wait` 과 `notify` 를 직접 사용하는 것을 동시성 '어셈블리 언어'로 프로그래밍하는 것에 비유할 수 있다.
- `java.util.concurrent` 는 고수준 언어에 비유할 수 있다.
- 코드를 새로 작성한다면 `wait` , `notify` 를 쓸 이유가 거의 없다.
- 이들을 사용하는 레거시 코드를 유지보수해야 한다면 `wait` 은 항상 표준 관용구에 따라 `while` 문 안에서 호출하도록 하자.
- 일반적으로 `notify` 보다는 `notifyAll` 을 사용해야 한다.
- 혹시라도 `notify` 를 사용한다면 응답 불가 상태에 빠지지 않도록 각별히 주의하자.
