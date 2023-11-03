## 객체의 정보를 간결하며 읽기 쉽도록 toString 을 재정의하라
![Object.toString](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8f488049-952c-4795-a522-d843978dd650)
- `Lombok` 을 이용해 간결하게 재정의 가능하다.
```java
@ToString
@AllArgsConstructor
public class Member {
    private String name;
    private String address;
    private String phone;
}
// member = Member(name=ForExample, address=Address, phone=707-867-5309)
```

## 반환값의 포맷을 문서화할지 정해라.
- 포맷을 명시하면 그 객체는 표준적이고, 명확하고, 사람이 읽을 수 있게 된다.
- 따라서 데이터 객체로 저장할 수도 있다.
- 이를 `CSV` 파일등으로 만들 수 있다.
- 명시한 표맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공하면 좋다.(`Python` 의 `__repr__`)
- 하지만 한번 포맷을 명시하면 평생 그 포맷에 얽매이게 된다.
- 데이터 객체로서 파싱하여 사용중이라면 포맷이 바뀌는 순간 기존 코드를 사용하지 못하게 된다.

## toString 이 반환하는 값에 포함된 정보를 얻어올 수 있는 API를 제공하라.
- 위의 예시에서 `name`, `address`, `phone` 의 값을 가져올 수 있는 `getter` 를 제공해야 한다.

## 정리
- 모든 구체 클래스에서 `Object` 의 `toString` 을 재정의하자.
- 상위 클래스에서 이미 알맞게 재정의한 경우는 예외이다.
- `toString` 을 재정의 하면 시스템의 디버깅이 쉬워진다.
- `toString` 은 해당 객체에 대해 명확하고 유용한 정보를 읽기 좋은 형태로 반환해야 한다.