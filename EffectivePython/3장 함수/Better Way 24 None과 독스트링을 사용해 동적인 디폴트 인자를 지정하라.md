## 디폴트 인자의 동작
```Python
from time import sleep
from datetime import datetime

def log(message, when=datetime.now()):
	print(f'{when}: {message}')

log('hi')
sleep(0.1)
log('hello')

# 2023-07-06 09:12:12.988734: hi
# 2023-07-06 09:12:12.988734: hello
```
- `when`을 입력하지 않은 경우 현재시간이 출력되도록 하고 싶어서 디폴트 인자로 `datetime.now()`를 사용하였으나 결과는 동일한 값이 출력된다.
- 이는 함수가 정의되는 시점에 `datetime.now()`가 한 번만 호출되기 때문이다.

```Python
def log(message, when=None):  
	"""Log a message with a timestamp.  
	  
	Args:  
		message: Message to print.  
		when: datetime of when the message occurred.  
	"""
	if when is None:  
		when = datetime.now()  
	print(f'{when}: {message}')

# def log(message: Any,
#         when: Any = None) -> None
# Log a message with a timestamp.
# 매개변수:
#     message – Message to print.
#     when – datetime of when the message occurred.
```
- 고로 이렇게 작성해야한다.

```Python
import json  
  
  
def decode(data, default={}):  
	try:  
		return json.loads(data)  
	except ValueError:  
		return default
```
- 이 또한 위와 비슷한 경우이다.

```Python
foo = decode('bad data')  
foo['stuff'] = 5  
bar = decode('also bad')  
bar['meep'] = 1  
print('Foo:', foo)  
print('Bar:', bar)

# Foo: {'stuff': 5, 'meep': 1}
# Bar: {'stuff': 5, 'meep': 1}
```
- `default`의 가 함수 정의시점에 한 번만 호출되기 때문에 `default`를 리턴하게 되면 리턴된 모든 값은 같은 레퍼런스를 갖게 된다.

```Python
def decode(data, default=None):  
	"""Load JSON data from a string.  
	  
	Args:  
	data: JSON data to decode.  
	default: Value to return if decoding fails.  
	"""  
	try:  
		return json.loads(data)  
	except ValueError:  
		if default is None:  
			default = {}  
		return default
```
- 이와 같이 고치는 것이 옳다.