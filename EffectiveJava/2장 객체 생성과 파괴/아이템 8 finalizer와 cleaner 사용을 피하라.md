## `finalizer`는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로는 불필요하다. 
오동작, 낮은 성능, 이식성 문제의 원인이 된다.\
자바 9에서 `finalizer`를 `deprecated` API로 지정하고 `cleaner`를 대안으로 소개했다.\
`cleaner`는 `finalizer`보다는 덜 위험하지만, 여전히 예측할 수 없고, 느리고, 일반적으로 불필요하다.

### [finalizer](https://docs.oracle.com/javase/specs/jls/se8/html/jls-12.html#jls-12.6)
일종의 소멸자로 볼 수 있다.\
하지만 `C++`의 소멸자(destructor)와는 다른 개념이다.\
`finalizer`는 `GC`에 의해 인스턴스가 회수 될 때 수행된다.

## `finalizer`의 문제점
`finalizer`는 `GC`에 의해 인스턴스가 회수 될 때 수행되기 때문에 즉시 수행된다는 보장이 없다.\
객체에 접근할 수 없게 된 후 `finalizer`나 `cleaner`가 실행되기까지 얼마나 걸릴지 알 수 없다.\
**즉, `finalizer`와 `clenaer`로는 제때 실행되어야 하는 작업은 절대 할 수 없다.**
- 만약 파일 닫기 등의 자원 회수를 `finalizer` 또는  `cleaner`에 맡기게 된다면 회수 지연에 의해 `file descriptor`가 부족해져 정상적인 동작이 불가할 수 있다.
---
`finalizer`에 의해 그 인스턴스의 자원 회수가 지연될 수 있다.\
`finalizer`스레드는 다른 애플리케이션 스레드보다 우선순위가 낮아 실행될 기회를 얻지 못하는 경우가 있다.\
만약 계속해서 `finalizer`가 실행될 기회를 얻지 못한다면 `finalizer`에서 회수하려고 했던 자원이 영원히 회수 되지 않아 `OutOfMemoryError`를 발생시킬 수 있다.\
`cleaner`의 경우 자신을 수행할 스레드를 제어할 수 있기 때문에 조금 낫지만 `GC`의 통제하에 있다는 점에서 즉각 수행되리라는 보장이 없는 것은 매한가지이다.
---
자바 언어 명세는 `finalizer`나 `cleaner`의 수행 시점뿐 아니라 수행 여부조차 보장하지 않는다.\
접근할 수 없는 일부 객체에 딸린 종료 작업을 전혀 수행하지 못한 채 프로그램이 중단될 수 있다.\
대표적인 예로 데이터베이스 같은 공유 자원의 영구 락 해제를 `finalizer`나 `cleaner`에 맡겨 놓으면 분산 시스템 전체가 서서히 멈출 것이다.\
따라서  프로그램 생애주기와 상관없는, **상태를 영구적으로 수정하는 작업에서는 절대 `finalizer`나 `cleaner`에 의존해서는 안 된다.**
---
`System.gc`나 `System.runFinalization` 메소드는 `finalizer`나 `cleaner`의 실행 가능성을 높혀주기는 하지만 수행을 보장해주진 않는다.\
`System.runFinalizersOnExit`와 `Runtime.runFinalizersOnExit`는 이를 보장해주지만 두 메소드는 심각한 결함에의해 사용되지 않는다.(ThreadStop의 위험이 있다고 한다.)
---
`finalizer` 동작 중 발생한 예외는 무시되며 처리할 작업이 남아있더라도 그 순간 종료된다.\
이는 마무리를 못한 작업이 남을 수 있다는 것을 뜻하고 영원한 누수로 남게 된다.\
만약 다른 스레드에서 작업이 마무리 되지 않은 훼손된 객체를 사용하려 한다면 동작을 예측할 수 없게 된다.\
일반적으로 `UncaughtException`이 스레드를 중단시키고 스택트레이스를 출력하지만 `finalizer`에서 일어나게 된다면 경고조차 출력되지 않는다.\
그나마 `cleaner`를 사용하는 라이브러리는 자신의 스레드를 통제하기 때문에 이러한 문제가 발생하지는 않는다.
---
`finalizer`와 `cleaner`는 심각한 성능 문제도 동반한다.\
`finalizer`가 `GC`의 효율을 떨어뜨리기 때문이다.
---
`finalizer`를 사용한 클래스는 `finalizer`공격에 노출되어 심각한 보안 문제를 일으킬 수 있다.\
`finalizer`공격 원리는 생성자나 직렬화 과정에서 예외가 발생하면, 이 객체에서 악의적인 하위클래스의 `finalizer`가 수행될 수 있게 된다.\
이 `finalizer`는 정적 필드에 자신의 참조를 할당하여 `GC`에 의해 회수되지 않도록 할 수도 있다.\
**객체 생성을 막기 위해 생성자에서 예외를 던지는 것만으로 충분하지만, `finalizer`가 있다면 그렇지도 않다.**\
클래스를 `final`클래스로 만든다면 하위 클래스를 만들 수 없기 때문에 `finalizer`공격에 대해서 안전하다.\
또는 `finalize`메소드를 `final`로 선언함으로서 방지할 수 있다.

## `finalizer`의 대안
**`AutoCloseable`을 구현하여 자원 사용 후 `close`메소드를 호출한다.**
- `try-with-resources`를 사용한다.(아이템 9)
```java
public class AutoCloseSample implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("AutoCloseSample.close");
    }

    public void doSomething() {
        System.out.println("AutoCloseSample.doSomething");
    }

    public static void main(String[] args) {
        try (AutoCloseSample autoCloseSample = new AutoCloseSample()) {
            autoCloseSample.doSomething();
        }
        System.out.println("after try-with-resources");
    }
}
// 실행 결과
// AutoCloseSample.doSomething
// AutoCloseSample.close
// after try-with-resources
```
- 추가로 각 인스턴스는 자신의 `close`메소드 수행 여부를 추적하면 좋다.
- `close`메소드의 수행 여부는 이 객체의 유효성을 나타내기 때문에 필드에 기록하고 다른 메소드는 이 필드를 검사해서 객체가 닫힌 후에 불렸다면 에러를 던지도록 한다.

## `finalizer`의 사용처
1. 자원 회수에 대한 안전망 역할을 한다.
   - `finalizer`나 `cleaner`가 즉시 호출되리라는 보장은 없지만, 클라이언트가 하지 않은 자원 회수를 늦게라도 해주는 것이 아예 안하는 것보다는 낫다.
   - `FileInputStream`, `FileOutputStream`, `ThreadPoolExecutor`가 대표적이다.
   - ```java
      protected void finalize() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null || acc == null) {
          shutdown();
        } else {
          PrivilegedAction<Void> pa = () -> { shutdown(); return null; };
          AccessController.doPrivileged(pa, acc);
        }
      }
      ```
   - 자바 8 `ThreadPoolExecutor`의 `finalize`메소드 구현부이다.
2. 네이티브 피어(native peer)와 연결된 객체에서 사용된다.
   - 네이티브 피어란 일반 자바 객체가 네이티브 메소드를 통해 기능을 위임한 네이티브 객체를 말한다.
   - 네이티브 피어는 자바 객체가 아니라서 `GC`가 그 존재를 알지 못한다고 한다.
   - 그렇기 때문에 자바 피어를 회수할 때 네이티브 객체까지 회수하지 못함으로 `finalizer`나 `cleaner`를 통해 처리하도록 한다.
   - 이 또한 성능 저하를 감당할 수 있거나 네이티브 피어가 심각한 자원을 가지고 있는 경우에만 해당한다.
   - 성능 저하를 감당할 수 없거나 자원을 즉시 회수해야 한다면 `close`메소드를 사용해야한다.

## `cleaner`를 안전망으로 활용하는 `AutoCloseable` 클래스 예시
```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // 절대 Room을 참조해서는 안 된다.
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        @Override
        public void run() {
            System.out.println("State.run");
            numJunkPiles = 0;
        }
    }
    
    private final State state;
    
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override
    public void close() {
        System.out.println("Room.close");
        cleanable.clean();
    }

    public void doSomething() {
        System.out.println("Room.doSomething");
    }
}
```
- `static` 중첩 클래스인 `State`는 `cleaner`가 정리해야할 자원을 담고 있다.
- `State`는 `Runnable`을 구현하고, `run`메소드는 `cleanable`에 의해 딱 한 번만 호출될 것이다.
- `run`메소드는 `Room`의 `close`메소드를 수행하거나 `GC`에 의해 `Room`이 회수될 때까지 `close`가 수행되지 않았을때 호출된다.

```java
public class Item8Client {
    public void scope() {
        Room room = new Room(1);
        room.doSomething();
    }
    public static void main(String[] args) throws InterruptedException {
        Item8Client client = new Item8Client();
        client.scope();
        System.gc();
        Thread.sleep(1000);
    }
}
// 실행 결과
// Room.doSomething
// State.run
```
- `GC`에 의해 `cleaner`가 호출된 경우 (언제나 동작하리라 장담할 수 없다.)

```java
public class Item8Client {
    public static void main(String[] args) throws InterruptedException {
        try (Room room = new Room(1)) {
            room.doSomething();
        }
    }
}
// 실행 결과
// Room.doSomething
// Room.close
// State.run
```
- `try-with-resource`를 통해 `close`가 호출된 경우

이때 `State`인스턴스는 `Room`인스턴스를 절대로 참조해서는 안 된다.\
`Room`인스턴스를 참조하게 되면 순환참조에 의해 `GC`가 `Room`인스턴스를 회수할 수 없게 된다.\
`State`클래스를 `inner`클래스가 아닌 `static` 중첩 클래스로 만든 이유가 여기에 있다.\
`inner`클래스의 경우 자동으로 바깥 객체의 참조를 갖게 되기 때문이다.(아이템 24)[[중첩 클래스](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html)]\
람다 또한 바깥 객체의 참조를 깆기 쉬우므로 사용하지 않는 것이 좋다.

## 정리
```
'cleaner' 는 안전망 역할이나 중요하지 않은 네이티브 자원 회수 용으로만 사용하되 불확실성과 성능 저하에 주의해야 한다.  
```
