## 암시적 `any`타입은 후 대입에 의해 타입이 변화한다.
```typescript
const result = [] // result: any[]
result.push('a') // result: string[]
```
```typescript
const result = [] // result: any[]
result.push(1) // result: number[]
```
```typescript
const result = [] // result: any[]
result.push('a') // result: string[]
result.push(1) // result: (string | number)[]
```
- 타입을 `any`로 선언하지 않고 암시적으로 `any`로 추론된 경우 후에 타입이 변화된다.

```typescript
let val: any
val = "name" // val: any
val = 1 // val: any
```
- 명시적으로 선언한 경우 타입은 `any`로 고정된다.