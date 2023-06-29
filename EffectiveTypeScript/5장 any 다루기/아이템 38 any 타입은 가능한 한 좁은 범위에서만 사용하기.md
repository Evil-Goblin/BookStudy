## any 타입으로서의 스코프 최소화
```typescript
function processBar(b: Bar) { /* ... */ }

function f () {
  const x = expressionReturningFoo()
  processBar(x)
//           ^ 'Foo' 형식의 인수는 'Bar' 형식의 매개변수에 할당될 수 없습니다.
}
```
- 위의 예제에서 만약 `x`가 `Foo`와 `Bar`타입에 모두 할당 가능하다면 다음과 같이할 수 있다.

```typescript
function f1 () {
  const x: any = expressionReturningFoo()
  processBar(x)
}
```
- 이 경우 `x`변수는 `f1`스코프 내에서 `any`타입으로서 동작하게된다.
- 또한 `x`를 리턴하게되면 리턴된 결과는 `any`로 동작하기 때문에 그 여파를 측정하기 힘들다.

```typescript
function f2 () {
  const x = expressionReturningFoo()
  processBar(x as any)
}
```
- 그러니 이와 같이 매개변수에서만 `any`타입을 갖도록 스코프를 줄이는게 좋다.

```typescript
function f3 () {
  const x = expressionReturningFoo()
  //@ts-ignore
  processBar(x)
  return x
}
```
- `@ts-ignore`를 이용하여 다음줄 오류를 무시했다.
- 이로서 `processBar(x)`의 호출시 에러없이 호출된다.

## 객체에 대해서도 `any`의 범위를 줄여라
```typescript
const config: Config = {
  a: 1,
  b: 2,
  c: {
    key: value
//  ^^^ 타입오류가 발생했다고 가정
  }
}
```
- 위와 같이 한 속성에 대해서 타입오류가 발생했다고 가정한다.

```typescript
const config: Config = {
  a: 1,
  b: 2,
  c: {
    key: value
  }
} as any
```
- 위와 같이 아예 `any`로 만들어서 문제를 해결할 수도 있지만 앞서 언급한대로 `any`의 범위를 좁히는 것이 좋다.

```typescript
const config: Config = {
  a: 1,
  b: 2,
  c: {
    key: value as any
  }
}
```
- 위와 같이 최소한의 범위에 `any`를 적용할 수 있도록 한다.
