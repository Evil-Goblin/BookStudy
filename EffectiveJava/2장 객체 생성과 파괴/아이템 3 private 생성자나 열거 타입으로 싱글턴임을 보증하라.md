![Singleton_UML_class_diagram](https://user-images.githubusercontent.com/74400861/235357251-0cfcde86-d23f-4b8b-87cd-fcb265721d3f.svg)
- 소프트웨어 디자인 패턴에서 **싱글턴 패턴**(Singleton pattern)을 따르는 클래스는, 생성자가 여러 차례 호출되더라도 실제로 생성되는 객체는 하나이고 최초 생성 이후에 호출된 생성자는 최초의 생성자가 생성한 객체를 리턴한다. 이와 같은 디자인 유형을 **싱글턴 패턴**이라고 한다. 주로 공통된 객체를 여러개 생성해서 사용하는 DBCP(DataBase Connection Pool)와 같은 상황에서 많이 사용된다.
	- 출처 : https://ko.wikipedia.org/wiki/%EC%8B%B1%EA%B8%80%ED%84%B4_%ED%8C%A8%ED%84%B4

## 싱글턴을 만드는 방법
```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }

    public void leaveTheBuilding() { ... }
}
```
- 생성자를 `private`로 만들고 `INSTANCE`를 초기화할 때 한번만 호출되기 때문에 인스턴스가 하나뿐임을 보증할 수 있다.
- 간결하면서도 싱글턴 클래스임을 명백히 드러낼 수 있다.
- 하지만 리플렉션을 이용해 `private`생성자를 호출할 수 있다.
	- 이를 방지하기 위해 생성자가 두번째 호출되는 시점에 예외를 던지도록 할 수 있다.

```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    public static Elvis getInstance() { return INSTANCE; }

    public void leaveTheBuilding() { ... }
}
```
- `getInstance`메소드를 통해 항상 `INSTANCE`를 반환시키도록 한다.
	- 위와 같이 리플렉션 예외에 대한 처리는 필요하다.
- 싱글턴에서 싱글턴이 아니도록 변경하기 간편하다.
	- 인스턴스를 반환하는  팩토리 메소드를 스레드별 다른 인스턴스를 리턴하는 등으로 변경이 가능하기 때문이다.
	- 클라이언트 코드의 변경없이 팩토리 메소드만 변경하면 된다.
- 정적 팩토리를 제네릭 싱글턴 팩토리로 만들 수 있다.
- 정적 팩토리 메소드를 `Supplier`로 사용할 수 있다. ( 자바 1.8 부터 )
	- `Supplier<Elvis> elvis = Elvis::getInstance`

## Supplier
```
Interface Supplier<T>

Type Parameters:
	T - the type of results supplied by this supplier

Functional Interface:
	This is a functional interface and can therefore be used as the assignment target for a lambda expression or method reference.
```
- 함수의 직접적인 호출이 아닌 함수 자체를 이용할 수 있기 때문에 `Lazy Evaluation` ( 느긋한 계산법 ) 등에 사용된다고 한다.

## Lazy Evaluation
- 불필요한 연산을 줄인다.
- 일반적인 `if`문의 `&&`연산과 비슷하다.
- `&&`연산의 경우 앞선 조건이 거짓일 경우 뒤의 조건은 수행하지 않는다.
```java
public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    getValueUsingMethodResult(true, getExpensiveValue());
    getValueUsingMethodResult(false, getExpensiveValue());
    getValueUsingMethodResult(false, getExpensiveValue());
    System.out.println("passed Time: "+ (System.currentTimeMillis()-startTime)/1000+"sec" );
}

private static void getValueUsingMethodResult(boolean valid, String value) {
    if(valid)
        System.out.println("Success: The value is "+value);
    else
        System.out.println("Failed: Invalid action");
}

private static String getExpensiveValue() {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "Hello World";
}
// https://dororongju.tistory.com/137
```
- 위와 같은 코드의 경우 함수의 결과를 매개변수로 받는다.
- `getValueUsingMethodResult`함수에서 `valid`의 값에 따라 `value`값이 사용되지 않음에도 함수의 결과를 얻기 위해 `sleep`이 수행된다.

```java
public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    getValueUsingSupplier(true, () -> getExpensiveValue());
    getValueUsingSupplier(false, () -> getExpensiveValue());
    getValueUsingSupplier(false, () -> getExpensiveValue());
    System.out.println("passed Time: "+ (System.currentTimeMillis()-startTime)/1000+"sec" );
}
private static void getValueUsingSupplier(boolean valid, Supplier<String> valueSupplier) {
    if(valid)
        System.out.println("Success: The value is "+valueSupplier.get());
    else
        System.out.println("Failed: Invalid action");
}
// https://dororongju.tistory.com/137
```
- 위와 같은 경우 `valid`여하에 따라 `Supplier`로 부터 호출하기 때문에 필요에 의한 경우에만 사용할 수 있다.

## Serializable
- 위의 두 방식으로 만들어진 싱글턴 클래스를 직렬화 하려면 `Serializable` 뿐만 아닌 `readResolve` 메소드를 제공해야한다.
- 그렇지 않으면 역직렬화시 새로운 인스턴스가 만들어진다.
- https://www.oracle.com/technical-resources/articles/java/serializationapi.html

## ENUM을 통한 싱글턴 선언
```java
public enum Elvis {
    INSTANCE;

    public void leaveTheBuilding() { ... }
}
```
- 간결하고 직렬화 문제도, 리플렉션 공격에도 방어가 가능하다.
- 원소가 하나뿐인 열거 타입으로 싱글턴을 만드는 것이 가장 좋은 방법이다.
- 하지만 `Enum` 외의 클래스를 상속해야 한다면 이 방법을 사용할 수 없다.