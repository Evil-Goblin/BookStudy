## `defaultdict`의 `__missing__`메소드
```Python
from collections import defaultdict  

class TestDict(defaultdict):  
	def __missing__(self, key):  
		print('missing')  
		return super().__missing__(key)  
  
	def __getitem__(self, key):  
		print('getitem')  
		return super().__getitem__(key)  
	  
	def __getattr__(self, item):  
		print('getattr')  
		return super().__getattr__(item)  
	  
	def __getattribute__(self, item):  
		print('getattribute')  
		return super().__getattribute__(item)  
	  
	def get(self, key, default=None):  
		print('get')  
		return super().get(key, default)

class Obj:  
	pass

test = TestDict(Obj)  
print(test['철수'])  
print(test.__getitem__('영희'))  
print(test.get('개똥이'))

# getitem
# missing
# <__main__.Obj object at 0x104e8fc10>
# getattribute
# getitem
# missing
# <__main__.Obj object at 0x104e8fbe0>
# getattribute
# get
# None
```
- `defaultdict`의 `default_factory`로 `callable` 해아하기 때문에 `Obj`클래스를 넣어주었다.
- 이후 각 연산마다 어떤 메소드를 호출하는지 출력하였다.
- 결론적으로 `__getitem__`을 통해 키값에 해당하는 값이 없다면 `default_factory`를 호출해 리턴해준다.

```Python
class Obj:  
	def __init__(self, *args, **kwargs):  
		print('init', args, kwargs)

# getitem
# missing
# init () {}
# <__main__.Obj object at 0x10db06c40>
```
- 하지만 위와 같이 `default_factory`에 매개변수를 넘겨주지 못한다.
- 이에 `__missing__`을 오버라이딩하여 직접 매개변수를 넘겨주도록 할 수 있다.

```Python
class Obj:  
	def __init__(self, name):  
		print('init', name)  
		self.name = name

class TestDict(defaultdict):  
	def __missing__(self, key):  
		self[key] = self.default_factory(key)  
		return self[key]

# getitem
# getattribute
# init 철수
# <__main__.Obj object at 0x10396fc10>
```
- 위와 같이 오버라이딩을 통해 원하는 동작을 하도록 변경할 수 있다.