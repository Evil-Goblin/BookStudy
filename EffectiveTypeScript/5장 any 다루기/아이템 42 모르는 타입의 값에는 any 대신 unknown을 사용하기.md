## any와 unknown의 차이
### *`any`의 특징*
1. 어떠한 타입이든 `any`타입에 할당 가능하다.
2. `any`타입은 어떠한 타입으로도 할당 가능하다.(`never`제외)

### *`unknown`과 차이점*
1. 어떠한 타입이든 `unknown`타입에 할당 가능하다.
2. 하지만 `unknown`은 오직 `unknown` 또는 `any` 에만 할당 가능하다.

### `never`
1. 어떠한 타입도 `never`에 할당할 수 없다.
2. `never`는 어떠한 타입으로도 할당 가능하다.

## `any`와 동일하게 타입 좁히기가 가능하다.
```typescript
function processValue(val: unknown) {
  if (val instanceof Date) {
    val // val: Date
  }
}
```
- 타입가드([아이템 22](https://github.com/Evil-Goblin/BookStudy/blob/main/EffectiveTypeScript/3%EC%9E%A5%20%ED%83%80%EC%9E%85%20%EC%B6%94%EB%A1%A0/%EC%95%84%EC%9D%B4%ED%85%9C%2022%20%ED%83%80%EC%9E%85%20%EC%A2%81%ED%9E%88%EA%B8%B0.md)) 또한 가능하다.

## 정리
```
unknown 은 다른 타입으로 할당이 불가능하기 때문에 any보다 안전하게 사용이 가능하다.
```