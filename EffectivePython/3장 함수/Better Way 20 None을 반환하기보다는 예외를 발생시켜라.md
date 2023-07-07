```Python
def careful_divide(a, b):
	try:
		return a / b
	except ZeroDivisionError:
		return None
```
- 위의 함수는 나눗셈을 하는 함수이지만 리턴 값이 `int`가 아닐 수 있다.
- 실패의 경우도 있기 때문에 실패 또한 표시를 위해 함수의 성공 실패를 뜻하는 리턴값을 추가할 수 있다.

```Python
def careful_divide(a, b):
	try:
		return True, a / b
	except ZeroDivisionError:
		return False, None
```
- 하지만 이 또한 계산결과만을 바로 사용할 수 없고 매번 언패킹을 통해 함수의 성공 여부를 체크해야한다.
- 또한 `_, value = careful_divide(a, b)`와 같이 성공 여부를 무시해버리는 등이 가능하기 때문에 옳바른 예외처리라 할 수 없다.

```Python
def careful_divide(a, b):
	try:
		return a / b
	except ZeroDivisionError:
		raise ValueError('Invalid Argument')
```
- 위와 같이 실패의 경우 예외를 던짐으로서 입력, 출력, 예외를 모두 명확하게 할 수 있다.
- 이로서 함수가 잘못 사용될 문제 또한 제거할 수 있다.