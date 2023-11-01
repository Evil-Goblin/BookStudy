## Object 의 equals
```java
public boolean equals(Object obj) {
  return (this == obj);
}
```
- 객체는 참조타입이기 때문에 `Object` 의 `equals` 에서 실제로 비교되는 값은 주소값이다.

## equals를 재정의하지 않는게 좋은 경우 (왠만하면 equals는 재정의하지 말자.)
- 각 인스턴스가 본질적으로 고유한 경우
  - 값이 아닌 동작을 표현하는 클래스들이 해당한다.
- 인스턴스의 '논리적 동치성'을 검사할 일이 없는 경우
- 상위 클래스에서 정의한 `equals` 가 하위 클래스에도 적용가능한 경우
  - `Collections` 가 대표적인 예시이다.
- 클래스가 `private` 이거나 `package-private` 이고 `equals` 를 호출할 일이 없는 경우
  - 만약 `equals` 가 호출되어서는 안되는 경우라면 `Exception` 을 던지도록 구현해놓을 수 있다.
  ```java
  @Override
  public boolean equals(Object o) {
    throw new AssertionError(); // 호출 금지
  }
  ```

## equals를 재정의하는 경우
- 객체 식별성(object identity; 두 객체가 무리적으로 같은가; 두 객체가 같은 메모리를 참조하고 있는가) 이 아닌 논리적 동치성을 확인해야하는데 상위 클래스의 `equals` 가 논리적 동치성을 비교하도록 재정의되지 않았을 때
  - 값 클래스들이 해당된다.(`Integer` , `String` 과 같은 값을 표현하는 클래스)
  - `equals` 는 객체가 같은지가 아닌 값이 같은지를 확인할 때 사용한다.
  - `equals` 의 재정의는 `Map` , `Set` 의 원소로도 사용할 수 있게 해준다.

## 인스턴스 통제 클래스(같은 값을 갖는 인스턴스가 둘 이상 만들어지지 않음을 보장) 의 경우 equals 의 재정의가 필요하지 않다.
- `Enum` 또한 여기에 해당된다.
- 논리적 동치성을 가지는 인스턴스가 중복해서 만들어지지 않기 때문에 논리적 동치성과 객체 식별성이 같은 의미를 지닌다. 

## equals 재정의할 때 지켜야할 일반 규약
> equals 메서드는 동치관계(equivalence relation) 을 구현하며, 다음을 만족한다.
> 
> - 반사성(reflexivity): `null` 이 아닌 모든 참조 값 `x` 에 대해, `x.equals(x)` 는 `true` 이다.
> - 대칭성(symmetry): `null` 이 아닌 모든 참조 값 `x`, `y` 에 대해, `x.equals(y)` 가 `true` 라면 `y.equals(x)` 도 `true` 이다.
> - 추이성(transitivity): `null` 이 아닌 모든 참조 값 `x`, `y`, `z` 에 대해, `x.equals(y)` 가 `true` 이고 `y.equals(z)` 도 `true` 이면 `x.equals(z)` 도 `true` 이다.
> - 일관성(consistency): `null` 이 아닌 모든 참조 값 `x`, `y` 에 대해, `x.equals(y)` 를 반복해서 호출하면 항상 `true` 를 반환하거나 항상 `false` 를 반환한다.
> - null-아님: `null` 이 아닌 모든 참조 값 `x` 에 대해, `x.equals(null)` 은 `false` 이다.

### 반사성
- 객체는 자기 자신과 같아야 한다.

### 대칭성
- 두 객체는 서로에 대해 동치 여부에 똑같이 답해야 한다.

### 추이성
- A 와 B 가 같고 B 와 C 가 같으면 A 와 C 도 같아야 한다. (삼단논법)
```java
public class Point {
  private final int x;
  private final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Point)) {
      return false;
    }

    Point p = (Point) obj;
    return p.x == x && p.y == y;
  }
}
```
- 좌표를 나타내는 클래스이다.
- 이를 확장하여 `color` 필드를 추가한 클래스를 만든다.

```java
public class ColorPoint extends Point {
    private final Color color;
    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
}
```
- 이때 `equals` 의 구현을 조심해야한다.

```java
// 대칭성 위배
@Override
public boolean equals(Object obj) {
  if (!(obj instanceof ColorPoint)) {
    return false;
  }
  return super.equals(obj) && ((ColorPoint) obj).color == color;
}
```
- 이 `equals` 는 `Point.equals(ColorPoint)` 와 `ColorPoint.equals(Point)` 두 결과가 다를 수 있다.

```java
// 추이성 위배
@Override
public boolean equals(Object obj) {
  if (!(obj instanceof Point)) {
    return false;
  }
  // obj가 일반 Point 일 경우 색상을 무시하고 비교한다.
  if (!(obj instanceof ColorPoint)) {
    return obj.equals(this);
  }
  // obj가 ColorPoint 이면 색상까지 비교한다.
  return super.equals(obj) && ((ColorPoint) obj).color == color;
}
```
- 이는 `Point.equals(ColorPoint)` 와 `ColorPoint.equals(Point)` 두 결과가 같아 대칭성은 지켜진다.

```java
public class Client {
    public static void main(String[] args) {
        Point point = new Point(1, 2);
        ColorPoint redPoint = new ColorPoint(1, 2, Color.RED);
        ColorPoint bluePoint = new ColorPoint(1, 2, Color.BLUE);

        System.out.println("point.equals(redPoint) = " + point.equals(redPoint)); // true
        System.out.println("point.equals(bluePoint) = " + point.equals(bluePoint)); // true
        System.out.println("bluePoint.equals(redPoint) = " + bluePoint.equals(redPoint)); // false
    }
}
```
- 하지만 위와 같이 추이성은 지켜지지 않는다.
- 또한 `obj.equals(this)` 때문에 무한루프에 빠질 가능성이 있다. (`Point` 의 또 다른 하위클래스에서도 위와 같이 작성한 경우)

#### 구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.
- 구체 클래스의 하위 클래스로 값을 추가해나가는 방법이 아닌 `Point` 를 포함하는 클래스를 만들어 해결한다.
```java
public class ImproveColorPoint {
    private final Point point;
    private final Color color;

    public ImproveColorPoint(int x, int y, Color color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }
    
    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImproveColorPoint)) {
            return false;
        }
        ImproveColorPoint cp = (ImproveColorPoint) obj;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}

```
- 비슷한 예시로 자바 라이브러리의 `java.sql.Timestamp` 와 `java.util.Date` 가 있다.
- `java.sql.Timestamp` 는 `java.util.Date` 를 확장하여 `nanoseconds` 필드를 추가하였다.
- 결과 대칭성에 위배되어 `Timestamp` 와 `Date` 를 같은 컬렉션에 넣거나 섞어서 사용하는 경우 엉뚱한 동작을 할 가능성이 생긴다.

### 일관성
- 두 객체가 같다면 앞으로도 영원히 같아야 한다.

#### equals 의 판단에 신뢰할 수 없는 자원이 끼어들게 해서는 안 된다.
- 순수함수?
- `java.net.URL` 의 `equals` 는 주어진 `URL`과 매핑된 호스트의 `IP` 주소를 이용해 비교한다.
  ![URL.equals](https://github.com/Evil-Goblin/DesignPattern/assets/74400861/28da63fd-8715-4d56-95f1-8b968f1314b8)
- 조건에 외부요인이 포함되어 있기 때문에 그 결과가 항상 같다고 보장할 수 없다.

### null-아님
- 모든 객체는 null과 같지 않아야 한다.

## equals 메서드 구현 방법
1. `==` 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.
   - 자기 자신이면 `true` 를 반환한다.
   - 단순한 성능 최적화 용도이다.
2. `instanceof` 연산자로 입력이 올바른 타입인지 확인한다.
   - 올바른 타입이란 `equals` 가 정의된 클래스인 것이 보통이다.
   - 또는 그 클래스가 구현한 특정 인터페이스인 경우도 있다.
   - 어떤 인터페이스는 자신을 구현한 서로다른 클래스끼리도 비교할 수 있도록 `equals` 규약을 수정하기도 한다.
     - 이런 인터페이스를 구현한 클래스는 `equals` 에서 클래스가 아닌 해당 인터페이스를 이용해야한다.
     - `Set`, `List`, `Map`, `Map.Entry` 등의 컬렉션 인터페이스들이 여기에 해당한다.
3. 입력을 올바른 타입으로 형변환한다.
   - `instanceof` 를 이용해 검사하기 때문에 성공을 보장한다.
4. 입력 객체와 자기 자신의 대응되는 핵심 필드들이 모두 일치하는지 하나씩 검사한다.
   - 모든 필드가 일치한다면 `true` 하나라도 다르다면 `false` 를 반환한다.

### 필드 비교
- `float`, `double` 을 제외한 기본 타입 필드는 `==` 연산자로 비교
- 참조 타입 필드는 각각의 `equals` 메서드로 비교한다.
- `float`, `double` 은 각각 정적 메서드인 `Float.compare(float, float)` 와 `Double.compare(double, double)` 로 비교한다.
  - `float`, `double` 은 `Float.NaN` , `-0.0f`, 특수한 부동소수 값등을 다뤄야하기 때문에 특별 취급한다.
  - `Float.equals`, `Double.equals` 메서드는 오토박싱을 수반할 수 있기 때문에 성능상 좋지 않다.
- 배열 필드는 원소 각각을 위와 같은 방법으로 비교한다.
  - 배열의 모든 원소가 핵심 필드라면 `Arrays.equals` 메서드들 중 하나를 사용한다.
    ![Arrays.equals](https://github.com/Evil-Goblin/DesignPattern/assets/74400861/db93ae3f-6875-481a-904e-556cb903bb8f)
    _Arrays.equals 의 boolean[] 타입 메서드_
- `null` 또한 정상 값으로 취급하는 참조 타입 필드의 경우 정적 메서드인 `Objects.equals(Object, Object)` 로 비교해 `NullPointerException` 발생을 예방한다.
- 비교하기 복잡한 필드를 가진 클래스의 경우 필드의 표준형을 저장해둔 후 표준형끼리 비교하면 훨씬 경제적이다.
  - 불변 클래스에 제격이다.
  - 가변 객체의 경우 값이 바뀔 때마다 표준형을 최신 상태로 갱신해줘야한다.
  ```java
  public class CaseInsensitiveString {
    private final String s;
    private final String standard_s;

    public CaseInsensitiveString(String s) {
      this.s = Objects.requireNonNull(s);
      this.standard_s = s.toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
      // .. 생략
      CaseInsensitiveString s1 = (CaseInsensitiveString) obj;
      return this.standard_s.equals(s1.standard_s);
    }
  }
  ```
  - 문자열의 대소문자를 구분하지 않는 문자열객체의 경우 `equals` 때 마다 강제 `Upper`, `Lower` 하여 비교하지 않고 미리 표준형을 만들어 둔다.
- 비교 순서에 따라 `equals` 의 성능이 좌우될 수 있다.
  - 다를 가능성이 크거나 비교하는 비용이 싼 필드를 먼저 비교한다.
- 동기화용 `lock` 필드와 같이 논리적 상태와 관련 없는 필드는 비교하지 않는다.
- 핵심 필드로부터 계산해낼 수 있는 파생 필드 역시 굳이 비교하지 않는다.
  - 파생 필드를 비교하는 쪽이 더 빠를 수 있다.
    - 파생 필드가 객체 전체의 상태를 대표하는 상황
    - 자신의 상태를 캐싱하여 저장하는 객체의 경우 캐싱된 값만을 비교하면 보다 빠른 처리가 가능하다.

### equals 의 구현 이후 대칭성, 추이성, 일관성을 검토하라
- 단위 테스트를 작성해서 테스트하라
- 반사성과 null-아님 또한 만족해야하지만 이 조건은 크게 문제되는 경우가 없다.

## 주의 사항
- `equals` 를 재정의할 땐 `hashCode` 도 반드시 재정의하라
- 너무 복잡하게 해결하려 들지 말자
  - 필드의 동치성만 검사해도 `equals` 규약을 지킬 수 있다.
  - 별칭(`alias`)은 비교하지 않는다.
    - `File` 클래스에서 심볼릭 링크를 비교하여 같은 파일을 가리키는지 확인하지 않는다.
- `Object` 외의 타입을 매개변수로 받는 `equals` 메서드는 선언하지 말자.
  ```java
  // 잘못된 예시
  public boolean equals(MyClass o) {
    // ...
  }
  ```
  - 입력 타입은 반드시 `Object` 여야 한다.
  - 위의 예시는 `Override` 가 아닌 `Overload` 이다.
  - `@Override` 어노테이션을 통해 방지할 수 있다.(`Overload` 에 `@Override` 어노테이션을 이용하는 경우 컴파일 에러가 발생한다.)

## AutoValue
- 구글에서 만든 프레임워크
- 어노테이션 기반으로 `equals` 와 같은 메서드를 작성해준다.
- 유사품 `Lombok` , `Immutables`

## 정리
- 꼭 필요한 경우가 아니면 `equals` 를 재정의하지 말자.
- 많은 경우 `Object` 의 `equals` 로 해결된다.
- 만약 재정의하는 경우 핵심 필드를 빠짐없이 다섯 가지 규약을 확실히 지켜가며 비교해야 한다. 