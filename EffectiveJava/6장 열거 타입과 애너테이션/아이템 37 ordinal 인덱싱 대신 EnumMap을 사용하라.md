## ordinal 을 이용한 인덱싱을 지양하자.
```java
public class Plant {
  enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

  final String name;
  final LifeCycle lifeCycle;

  public Plant(String name, LifeCycle lifeCycle) {
    this.name = name;
    this.lifeCycle = lifeCycle;
  }

  @Override
  public String toString() {
    return "Plant{" +
            "name='" + name + '\'' +
            '}';
  }
}

public class Client {
  public static void main(String[] args) {
    Plant[] garden = new Plant[]{
            new Plant("a", Plant.LifeCycle.ANNUAL),
            new Plant("b", Plant.LifeCycle.PERENNIAL),
            new Plant("c", Plant.LifeCycle.BIENNIAL)};

    Set<Plant>[] plantsByLifeCycle = new Set[Plant.LifeCycle.values().length];

    for (int i = 0; i < plantsByLifeCycle.length; i++) {
      plantsByLifeCycle[i] = new HashSet<>();
    }

    for (Plant p : garden) {
      plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
    }

    for (int i = 0; i < plantsByLifeCycle.length; i++) {
      System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
    }
  }
}
```
- 조금 극단적인 예시이다.
- 동작은 되지만 문제가 많다.
- 배열은 제네릭과 호환되지 않기 때문에 비검사 형변환을 수행해야하고 깔끔히 컴파일되지 않는다.
- 배열은 각 인덱스의 의미를 모르기 때문에 출력 결과에 직접 레이블을 달아야 한다.
  - `System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);`
- 가장 심각한 문제는 정확한 정숫값을 사용한다는 것을 직접 보증해야 한다는 점이다.
  - 정수는 열거 타입과 달리 타입 안전하지 않고 잘못된 값을 사용하면 잘못된 동작이 수행되거나 `ArrayIndexOutOfBoundsException` 이 발생한다.

## EnumMap 의 활용
```java
public class Client {
  public static void main(String[] args) {
    Plant[] garden = new Plant[]{
            new Plant("a", Plant.LifeCycle.ANNUAL),
            new Plant("b", Plant.LifeCycle.PERENNIAL),
            new Plant("c", Plant.LifeCycle.BIENNIAL)};

    Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
    for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
      plantsByLifeCycle.put(lc, new HashSet<>());
    }

    for (Plant plant : garden) {
      plantsByLifeCycle.get(plant.lifeCycle).add(plant);
    }

    System.out.println("plantsByLifeCycle = " + plantsByLifeCycle);
  }
}
```
- 타입 안전성이 지켜진다.
- 맵의 키가 열거 타입이기 때문에 그 자체로 출력용 문자열이 제공되어 출력 결과에 레이블을 달 필요가 없다.
- 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 없다.

### EnumMap
![EnumMap](https://github.com/Evil-Goblin/BookStudy/assets/74400861/3b2989e3-b86b-4b0a-964c-273d7251527b)
- `EnumMap` 은 내부적으로 배열을 사용한다.
- 내부 구현 방식을 안으로 숨겨서 `Map` 의 타입 안전성과 배열의 성능을 모두 얻었다.
- `EnumMap` 의 생성자가 받는 키 타입의 `Class` 는 한정적 타입 토큰으로, 런타임 제네릭 타입 정보를 제공한다.

#### 다차원 EnumMap
```java
public enum Phase {
  SOLID, LIQUID, GAS;

  public enum Transaction {
    MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
    BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
    SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

    private final Phase from;
    private final Phase to;

    Transaction(Phase from, Phase to) {
      this.from = from;
      this.to = to;
    }

    private static final Map<Phase, Map<Phase, Transaction>> m = Stream.of(values())
            .collect(groupingBy(t ->
                            t.from,
                    () -> new EnumMap<>(Phase.class),
                    toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));

    private static Transaction from(Phase from, Phase to) {
      return m.get(from).get(to);
    }
  }
}
```
- 다차원 `Enum` 을 활용하기 보다 `arr[enum.ordinal()][enum.ordinal()]` 과 같은 방법보다 `EnumMap` 을 연쇄하는 것이 좋다.
- 새로운 상태를 추가하고 싶다면 상수값만 추가해주면 다른 동작을 건드리지 않아도 된다.

## 정리
- 배열의 인덱스를 얻기 위해 `ordinal` 을 사용하는 것은 일반적으로 좋지 않기 때문에 `EnumMap` 을 사용하는 것이 좋다.
- 다차원 관계는 `EnumMap<..., EnumMap<...>>` 으로 표현하라.
- `Enum.ordinal` 은 왠만해선 사용하지 말아야 한다.
