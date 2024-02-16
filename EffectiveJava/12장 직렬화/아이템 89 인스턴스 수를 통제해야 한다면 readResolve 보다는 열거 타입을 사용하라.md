## 싱글턴과 직렬화
```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() {}
    
    public void leaveTheBuilding() {}
}
```
- 이 클래스는 바깥에서 생성자를 호출하지 못하게 막는 방식으로 인스턴스를 오직 하나만 만들어지도록 작성한 싱글턴 클래스이다.
- 하지만 `implements Serializable` 을 추가하게 되면 싱글턴이 아니게 된다.
- 어떤 `readObject` 를 사용하든 이 클래스가 초기화될 때 만들어진 인스턴스와는 별개인 인스턴스를 반환하게 된다.

### readResolve
- `readResolve` 기능을 이용하면 `readObject` 가 만들어낸 인스턴스를 다른 것으로 대체할 수 있다.
- 역직렬화한 객체의 클래스가 `readResolve` 메서드를 적절히 정의해뒀다면, 역직렬화 후 새로 생성된 객체를 인수로 이 메서드가 호출되고, 이 메서드가 반환한 객체 참조가 새로 생성된 객체를 대신해 반환된다.
- 대부분의 경우 이때 새로 생성된 객체의 참조는 유지하지 않으므로 가비지 컬렉션 대상이 된다.

```java
private Object readResolve() {
    return INSTANCE;
}
```
- 위의 `Elvis` 클래스가 `Serializable` 을 구현한다면 `readResolve` 메서드를 추가해 싱글턴을 유지할 수 있다.
- 역직렬화한 객체를 무시하기 때문에 `Elvis` 인스턴스의 직렬화 형태는 아무런 실 데이터를 가지지 않도록 모든 인스턴스 필드를 `transient` 으로 선언해야 한다.
- **`readResolve` 를 인스턴스 통제 목적으로 사용한다면 객체 참조 타입 인스턴스 필드는 모두 `transient` 로 선언해야 한다.**
  - 만약 싱글턴이 `transient` 가 아닌 참조 필드를 가지고 있다면 해당 필드의 내용이 역직렬화 되는 시점에 인스턴스의 참조를 훔쳐오는 공격이 가능해진다.
- **`readResolve` 메서드는 접근성이 매우 중요하다.**
  - `final` 클래스에서라면 `readResolve` 메서드는 `private` 이어야 한다.
  - `final` 이 아닌 클래스에서는 주의 사항이 있다.
    - `private` 로 선언하면 하위 클래스에서 사용할 수 없다.
    - `package-private` 으로 선언하면 같은 패키지에 속한 하위 클래스에서만 사용할 수 있다.
    - `protected` 나 `public` 으로 선언하면 이를 재정의하지 않은 모든 하위 클래스에서 사용할 수 있다.
    - `protected` 나 `public` 이면서 하위 클래스에서 재정의하지 않았다면, 하위 클래스의 인스턴스를 역직렬화하면 상위 클래스의 인스턴스를 생성하여 `ClassCastException` 을 일으킬 수 있다.

## enum 을 이용한 싱글턴
- 선언된 상수 외의 다른 객체가 존재하지 않음을 Java 가 보장해주기 때문에 `AccessibleObject.setAccessible` 같은 특권 메서드를 사용하는 경우를 제외하면 공격을 방어할 수 있게 된다.

```java
public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() { System.out.println(Arrays.toString(favoriteSongs)); }
}
```
- `Elvis` 를 열거타입으로 구현하게 되면 이전의 공격들이 통하지 않게 된다.
- 하지만 직렬화 가능 인스턴스 통제 클래스를 작성하는 경우, 컴파일타임에는 어떤 인스턴스들이 있는지 알 수 없는 상황에서는 열거 타입으로 표현할 수 없기 때문에 `readResolve` 를 이용해 인스턴스 통제를 수행해야 한다.

## 정리
- 불변식을 지키기 위해 인스턴스를 통제해야 한다면 가능한 한 열거 타입을 사용하자.
- 여의치 않은 상황에서 직렬화와 인스턴스 통제가 모두 필요하다면 `readResolve` 메서드를 작성해 넣어야 하고, 그 클래스에서 모든 참조 타입 인스턴스 필드를 `transient` 로 선언해야 한다.
