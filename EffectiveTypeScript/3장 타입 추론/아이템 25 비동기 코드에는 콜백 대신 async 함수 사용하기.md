## 콜백 지옥의 개선
```typescript
fetchURL(url1, function (response1) {
  fetchURL(url2, function (response2) {
    fetchURL(url3, function (response3) {
      console.log(1)
    })
    console.log(2)
  })
  console.log(3)
})
console.log(4)
```

```typescript
const page1Promise = fetch(url1)
page1Promise
  .then(response1 => {
    return fetch(url2)
  })
  .then(response2 => {
    return fetch(url3)
  })
  .then(response3 => {
    // ...
  })
  .catch(error => {
    // ...
  })
```

```typescript
async function fetchPages() {
  const response1 = await fetch(url1)
  const response2 = await fetch(url2)
  const response3 = await fetch(url3)
  // ...
}
```
- 프로미스는 콜백에 비해 코드 작성, 타입 추론이 쉽다.
- `async/await` 는 보다 간결하고 직관적이며 항상 프로미스를 반환하도록 강제한다.

## `Promise`
- `Promise` 객체는 비동기 작업이 맞이할 미래의 완료 또는 실패와 그 결과 값을 나타낸다.
- `rust` 또는 타 언어의 `future`와 비슷하다고 생각할 수 있을 것 같다.
- `async/await`를 이용해 `then/catch`를 사용하는 것 보다 나은 것 같다.
- 내용이 너무 광범위하여 어떻게 정리해야할지 모르겠다....
