## DefaultDict
```Python
class collections.defaultdict(default_factory=None, [, ...])
```
- `default_factory`의 값은 `key`값이 없는 경우의 `default`값이 된다.
- `default_factory`는 `callable`해야한다.
- `get`을 통한 호출에는 `default`값을 리턴해주지 않는다.
	- `defaultdict`는 `__missing__(key)`라는 매직메소드를 가지고 있다.
	- 이 메소드는 `__getitem__()`을 통해 `key`가 없다면 호출되게 된다.
	- 때문에 `get`을 통해서는 호출이 되지 않는다.