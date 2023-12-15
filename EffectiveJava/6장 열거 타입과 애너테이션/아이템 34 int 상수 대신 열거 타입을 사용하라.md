## 자바의 열거 타입
- 자바의 열거 타입은 클래스이다.
  - 때문에 추가적인 기능을 가질 수 있다.
- 상수 하나당 자신의 인스턴스를 하나씩 만들어 `public static final` 필드로 공개한다.
- 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 `final` 이다.
  - 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없기 때문에 열거 타입의 인스턴스는 하나씩만 존재하게 된다.
  - 인스턴스가 통제된다.
  - 싱글턴
- 컴파일타임 타입 안전성을 제공한다.
  - 열거 타입을 매개변수로 받는 메서드는 `null` 이 아닌 경우 해당 열거 타입의 값 중 하나임이 확실하다.
- 각자의 네임스페이스가 존재하여 이름이 같은 상수도 공존할 수 있다.
- `toString` 출력하기에 적합한 문자열을 만들어준다.
- 임의의 메서드나 필드를 추가할 수 있다.
  - 인터페이스의 구현도 가능하다.
- `Object` 메서드들이 이미 높은 품질로 구현되어있다.
- `Comparable` , `Serializable` 도 구현되어있다.
```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(4.869e+24, 6.052e6),
    MARS(4.869e+24, 6.052e6),
    JUPITER(4.869e+24, 6.052e6),
    SATURN(4.869e+24, 6.052e6),
    URANUS(4.869e+24, 6.052e6),
    NEPTUNE(4.869e+24, 6.052e6);
    
    private final double mass;
    private final double radius;
    private final double surfaceGravity;
    
    private static final double G = 6.67300E-11;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }
    
    public double mass() {
        return mass;
    }
    
    public double radius() {
        return radius;
    }
    
    public double surfaceGravity() {
        return surfaceGravity;
    }
    
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;
    }
}
```
- 열거 타입 예시이다.

```java
public class WeightTable {
    public static void main(String[] args) {
        double earthWeight = 100.0;
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
        }
    }
}
```
- 열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 `values` 를 제공한다.
- 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 `private` 또는 `package-private` 메서드로 구현한다.
  - 이렇게 구현한 상수는 자신을 선언한 클래스 혹은 패키지에서만 사용할 수 있는 기능을 담게 된다.
  - 기능을 노출해야할 합당한 이유가 없다면 `private` 또는 `package-private` 로 선언하라.
- 널리 사용되는 열거 타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만든다.

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }


    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```
- 각 상수별 기능을 가지는 상수별 메서드 구현 예시이다.
  - `apply` 메서드를 추상 메서드로 만들어서 구현하지 않는다면 컴파일에러를 발생시킨다.
- 열거 타입에는 상수 이름을 통해 상수를 반환하는 `valueOf(String)` 메서드가 제공된다.
  - `toString` 이 반환하는 문자열을 해당 상수로 바꿔주는 `fromString` 메서드를 제공하는 것도 고려하자.
```java
private static final Map<String , Operation> stringToEnum = Stream.of(values()).collect(toMap(Object::toString, e -> e));

public static Optional<Operation> fromString(String symbol) {
  return Optional.ofNullable(stringToEnum.get(symbol));
}
```
- **`Operation` 상수가 `stringToEnum` 맵에 추가되는 시점은 열거 타입 상수 생성 후 정적 필드가 초기화될 때 이다.**
  - 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 초기화되기 전이다.
    - **열거 타입 생성 후 정적 필드들의 초기화가 수행된다.**
  - 열거 타입의 생성자에서 정적 필드인 `stringToEnum` 맵에 접근할 수 없기 때문에 생성자에서 자신의 인스턴스를 맵에 넣는 행위를 할 수 없다.
    - 정적 필드 초기화 전이기 때문에 만약 컴파일이 가능했다면 `NullPointerException` 이 발생한다.
      ![Enum_StaticField](https://github.com/Evil-Goblin/BookStudy/assets/74400861/b05a05e1-ecf4-4261-a7b0-286e1470ecc4)
- 비슷하게 열거 타입 생성자에서 같은 열거 타입의 다른 상수에도 접근이 불가능하다.

### 단점
- 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다.
```java
public enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
        
        int overtimePay;
        switch (this) {
            case SATURDAY:
            case SUNDAY:
                overtimePay = basePay / 2;
                break;
            default:
                overtimePay = minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
        return basePay + overtimePay;
    }
}
```
- 일한 시간에 따라 일당을 계산해주는 메서드를 제공하는 열거 타입이다.
- 만약 휴가와 같은 새로운 타입이 추가가된다면 그 값이 `case` 에 추가되어야만 한다.
  - 만약 추가되지 않는다면 휴가 기간의 일당이 계산되지 않을 수 있다.

#### 개선법
1. 잔업수당 계산 코드를 모든 상수에 중복하여 넣는다.
2. 계산 코드를 평일용과 주말용으로 나눠 각각을 도우미 메서드로 작성한 후 각 상수가 자신에게 필요한 메서드를 적절히 호출하도록 한다.
- 두 방식 모두 각 상수별 구현이 필요하기 때문에 코드가 장황해져서 가독성이 크게 떨어지고 오류 발생 가능성을 높인다.
- 만약 평일 잔업수당 계산용 메서드 `overtimePay` 를 구현하고 주말 상수에서는 재정의해서 쓴다면 장황한 코드를 줄일 수는 있지만 `switch` 를 사용했을 때의 문제가 같은 문제가 발생한다.
  - 새로운 상수를 추가하면서 `overtimePay` 메서드를 재정의하지 않으면 평일용 코드를 그대로 물려받게 된다.
- 가장 좋은 방법은 새로운 상수를 추가할 때 잔업수당 `전략` 을 선택하도록 하는 것이다.
```java
public enum PayrollDay {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY), THURSDAY(WEEKDAY), FRIDAY(WEEKDAY), SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) {
        this.payType = payType;
    }
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };
        
        abstract int overtimePay(int mins, int patRate);
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
```
- 이와 같이 잔업수당 계산을 `전략` 으로 만들고 타입 생성자에서 주입받을 수 있도록 한다.

#### switch 가 더 좋은 상황
```java
public static Operation inverse(Operation operation) {
    return switch (operation) {
        case PLUS -> MINUS;
        case MINUS -> PLUS;
        case TIMES -> DIVIDE;
        case DIVIDE -> TIMES;
        default -> throw new AssertionError("Invalid Operation: " + operation);
    };
}
```
- 위와 같은 기존 열거 타입 상수별 동작을 혼합하는 경우 `switch` 가 좋은 선택일 수 있다.
  - 상수별 메서드로 구현하게 되면 코드가 복잡해진다.

## 열거 타입를 사용하는 경우
- 필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자.
  - 태양계 행성, 요일, 체스 말 과 같이 본질적으로 열거 타입인 타입은 당연히 포함된다.
  - 메유 아이템, 연산 코드, 명령줄 플래그 등 허용하는 값 모두를 컴파일타임에 이미 알고 있을 때도 쓸 수 있다.
- 열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.

## 정리
- 열거 타입은 정수 상수보다 뛰어나다.
  - 더 읽기 쉽고 안전하다.
- 대다수 열거 타입이 명시적 생성자나 메서드 없이 사용되지만, 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작하도록 하는 경우 필요하다.
- 하나의 메서드가 상수별로 다르게 동작해야 하는 경우 `switch` 문 대신 상수별 메서드 구현을 사용하는 것이 좋다.
- 만약 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자.
