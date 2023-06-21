## 반환 타입의 설계
```typescript
function extent(nums: number[]) {
  let min, max
  for (const num of nums) {
    if (!min) {
      min = num
      max = num
    } else {
      min = Math.min(min, num)
      max = Math.max(max, num)
    }
  }
  return [min, max]
}
```
- 위의 경우 반환값이 `(number | undefined)[]`이 되기 때문에 `undefined`가 반환된 경우 예외처리가 힘들다.

```typescript
function extent(nums: number[]) {
  let result: [number, number] | null = null
  for (const num of nums) {
    if (!result) {
      result = [num, num]
    } else {
      result = [Math.min(num, result[0]), Math.max(num, result[1])]
    }
  }
  return result
}

const range = extent([0, 1, 2])
if (range) {
  const [min, max] = range
  const span = max - min
}
```
- 반면 위의 경우 반환타입이 `[number, number] | null`이기 때문에 리턴 값 자체에 대한 `null`체크 이후 언패킹을 할 수 있다.

## 클래스 생성시의 `Nullable`배제
- 외부 api를 통해 프로퍼티를 세팅하는 경우 비동기로 인해 프로퍼티가 `Nullable`일 수 있다.
```typescript
class UserPosts {
  user: UserInfo | null
  posts: Post[] | null

  constructor() {
    this.user = null
    this.posts = null
  }

  async init(userId: string) {
    return Promise.all([
      async () => (this.user = await fetchUser(userId)),
      async () => (this.posts = await fetchPostsForUser(userId)),
    ])
  }
}
```
- 우선 생성자를 통해 `null`로 초기화 한 뒤 외부 요청을 통해 프로퍼티 값을 갱신하게된다.

```typescript
class UserPosts {
  user: UserInfo
  posts: Post[]

  constructor(user: UserInfo, posts: Post[]) {
    this.user = user
    this.posts = posts
  }

  static async init(userId: string): Promise<UserPosts> {
    const [user, posts] = await Promise.all([fetchUser(userId), fetchPostsForUser(userId)])
    return new UserPosts(user, posts)
  }
}
```
- 하지만 정적 팩토리 메서드와 비슷한 방법을 통해 객체의 프로퍼티의 `Nullable`을 배제할 수 있다.