## `any`를 타입의 값 그대로 사용하지 말자
```typescript
function getLengthBad (array: any) {
  return array.length
}

function getLength (array: any[]) {
  return array.length
}
```
- 당연하지만 전자보다는 후자가 더 좋은 방법이다.
- 이중배열의 경우 `any[][]`로 선언한다.
- 객체의 경우 `{[key: string]: any}`로 선언한다.

## `any`를 사용했을 때의 리턴타입
```typescript
const numArgsBad = (...args: any) => args.length // 반환타입 any
const numArgsGood = (...args: any[]) => args.length // 반환타입 number
```
- `any`를 사용하더라도 보다 구체적으로 사용할 수 있도록 하자.