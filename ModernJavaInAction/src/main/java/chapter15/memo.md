## interface 와 메서드참조
```java
public interface Subscriber<T> {
    void onNext(T t);
}
```
- `void onNext(T t)` 를 가지고 있는 인터페이스 `Subscriber`

```java
public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
    private int value = 0;
    private String name;
    private List<Subscriber<? super Integer>> subscribers = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscribers.add(subscriber);
    }

    private void notifyAllSubscribers() {
        subscribers.forEach(s -> s.onNext(this.value));
    }

    @Override
    public void onNext(Integer newValue) {
        this.value = newValue;
        System.out.println(this.name + " : " + this.value);
        notifyAllSubscribers();
    }
}
```
- `Subscriber` 를 매개변수로 받는 `subscribe` 메소드
- 그런데 이 `subscribe` 메소드가 전달받을 수 있는 타입은 `Subscriber` 인터페이스를 구현한 인스턴스만이 아닌 `void onNext(T t)` 와 함수 시그니처가 같은 메소드 또한 가능하다.

```java
public class ArithmeticCell extends SimpleCell {

    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        onNext(left + this.right);
    }

    public void setRight(int right) {
        this.right = right;
        onNext(right + this.left);
    }
}
```
- 위와 같이 `void` 의 리턴타입을 가지고 `int` 매개변수를 받는 `setLeft` , `setRight` 는 `void onNext(T t)` 와 같은 함수 시그니처를 갖기 때문에 매개변수로 활용이 가능하다.
- 위의 예시에서는 `SimpleCell` 이 `Integer` 제네릭 타입을 이용하였기 때문에 `int` 매개변수를 갖는 `setLeft` , `setRight` 를 매개변수로 가질 수 있다.
- 하지만 `void func(String param)` 과 같은 시그니처는 `Interger` 제네릭에는 사용할 수 없다.

```java
public class StringPublisher implements Publisher<String> {
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        subscriber.onNext("onNext!!");
    }
}

public class MethodRefTest {
    public void test(String p) {
        System.out.println("p = " + p);
    }
}

public class Client {
    public static void main(String[] args) {
        MethodRefTest methodRefTest = new MethodRefTest();
        StringPublisher stringPublisher = new StringPublisher();
        stringPublisher.subscribe(methodRefTest::test);
    }
}
```
- 위와 같이 `String` 제네릭의 경우 `String` 시그니처로 이용할 수 있다.
