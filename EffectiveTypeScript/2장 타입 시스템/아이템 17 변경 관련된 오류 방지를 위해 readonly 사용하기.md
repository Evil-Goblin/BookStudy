## readonly 접근 제어자
- `const` , `final` 과 같이 변경 불가능하게 만들어주는 접근 제어자이다.
- 하지만 위의 경우와는 다르게 변수의 값만이 아닌 변수가 가리키는 정확한 데이터들의 변경도 불가능하게 한다.
- ~~객체의 프로퍼티 값 등을 변경할 수 없게 된다.~~
- 배열, 튜플 등에만 사용 가능
- `readonly` 또한 타입의 일종이다.
- 타입 단언문을 통해 `readonly`를 무시할 수 있다.
```typescript
const a: number[] = [1, 2, 3];
const b: readonly number[] = a; // 역대입은 불가능

console.log((b as number[]).pop())
```

## Readonly 와 readonly
```typescript
type Readonly<T> = {
    readonly [P in keyof T]: T[P];
};
```
- `Readonly`는 보다 복잡한 객체를 위한 `readonly`이다.

```typescript
interface Outer {
    inner: {
        x:number
    }
}

const o: readonly Outer = { inner: {x: 0}}
// TS1354: 'readonly' type modifier is only permitted on array and tuple literal types.
```
- 객체에 `readonly` 이용 불가

```typescript
class Simple {
    constructor(private readonly test: Outer) {
    }
}
```
- `nest.js` 를 하는 중 위와 같은 코드를 작서하는 경우가 있었기 때문에 객체에도 사용가능하다 생각되었다.
- 하지만 위의 코드는 `test` 변수의 내부 값까지 보장하는 것이 아닌 `const`와 같이 변수자체의 불변만 보장하였다.