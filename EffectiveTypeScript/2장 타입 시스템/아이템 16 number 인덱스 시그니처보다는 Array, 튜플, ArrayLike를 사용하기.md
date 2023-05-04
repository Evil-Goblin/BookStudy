## 객체와 배열
- 객체는 키와 값으로 이루어져있다.
- 배열 또한 객체이다.
- 배열의 `key`는 `number`가 아닌 `string`이다.

## number 인덱스의 사용
```typescript
const keys = Object.keys(xs);
for (const key of keys) {
    const x = xs[key];
    console.log(x)
}
```
- 책에서는 위의 코드에서 `xs[key]`부분이 `number`타입이 될 것이라고 하였다.

```
item16/main.ts:7:18 - error TS7015: Element implicitly has an 'any' type because index expression is not of type 'number'.

7     const x = xs[key];
```
- 하지만 인덱스가 `number`가 아니라서 안되더라....
- 에러가 나는 것은 이해가 가지만 책에서는 왜 된다고 하였는지 설정의 차이인가...
- 어떤 설정을 해야 `key`값이 `number`로 인식이 되는 것인지...

```typescript
const keys = Object.keys(xs);
for (const key in keys) {
    const x = xs[key];
    console.log(x)
}
```
- 아...`for in` 과 `for of`의 차이였다...

```typescript
for (const x of xs) {
    console.log(typeof x);
}
```
- `for of`를 이용한 순회를 하는 경우 위와 같이 해야한다.

## `for of` 와 `for in` 차이
- `for in`의 경우 `key`값만을 순회한다.
- `for of`의 경우 `es6`에 추가된 문법으로 `value`값을 순회한다.
- `for in`루프의 경우 보편적인 `for(;;)` , `for of` 루프에 비해 많이 느리다.

