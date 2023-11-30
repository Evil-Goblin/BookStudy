```java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    Function<A, R> finisher();
    BinaryOperator<A> combiner();
    Set<Characteristics> characteristics();
}
```
- `T` 는 수집될 스트림 항목의 제네릭 형식이다.
- `A` 는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
- `R` 은 수집 연산 결과 객체의 형식이다.

### Supplier : 새로운 결과 컨테이너 만들기
```java
public Supplier<List<T>> supplier() {
    return () -> new ArrayList<T>();
}

public Supplier<List<T>> supplier() {
    return ArrayList::new;
}
```

### accumulator : 결과 컨테이너에 요소 추가
```java
public BiConsumer<List<T>, T> accumulator() {
    return (list, item) -> list.add(item);
}

public BiConsumer<List<T>, T> accumulator() {
    return List::add;
}
```

### finisher : 최종 변환값을 결과 컨테이너로 적용
```java
public Function<List<T>, List<T>> finisher() {
    return Function.identity();
}
```

### combiner : 두 결과 컨테이너 병합
```java
public BinaryOperator<List<T>> combiner() {
    return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
    }
}
```

### Characteristics
- 컬렉션 연산을 정의하는 `Characteristics` 형식의 불변 집합을 반환
