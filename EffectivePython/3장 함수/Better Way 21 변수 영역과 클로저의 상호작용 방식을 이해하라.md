### 클로저
자신이 정의된 영역 밖의 번수를 참조하는 함수

### 파이썬의 함수는 일급 시민 객체이다.
일급 시민(first-class citizen) 객체란 이를 직접 가리킬 수 있고, 변수에 대입하거나 다른 함수에 인자로 전달할 수 있으며 `if`문에서 함수를 비교하거나 함수를 반환하는 등이 가능하다는 뜻이다.

```Python
def sort_priority2(numbers, group):  
	found = False  
	def helper(x):  
		if x in group:  
			found = True  
			return (0, x)  
		return (1, x)  
	numbers.sort(key=helper)  
	return found  
  
found = sort_priority2(numbers, group)  
print('Found:', found)
print(numbers)

# False
# [2, 3, 5, 7, 1, 4, 6, 8]
```
- 정렬 결과는 맞고 `group`안의 값도 찾았으니 `True`가 리턴될 것이라 예상할 수 있지만 `False`가 리턴되었다.

## Python 인터프리터의 참조 해결
파이썬 인터프리터의 참조 해결을 위해 영역을 뒤지는 순서
1. 현재 함수의 영역
2. 현재 함수를 둘러싼 영역(현재 함수를 둘러싸고 있는 함수 등)
3. 현재 코드가 들어 있는 모듈의 영역(전역 global scope)
4. 내장 영역 (built-in scope) (`len`, `str`등의 함수가 들어 있는 영역)

만약 위의 네 가지 영역에 없으면 `NameError`예외를 발생시킨다.

변수에 값 대입은 다른 방식으로 작동한다.\
변수가 이미 정의되어 있다면 변수의 값만 바뀐다.(변수 대입)\
하지만 변수가 `현재 영역`에 정의되어 있지 않다면 변수 대입이 아닌 `변수 정의`로 취급한다.

## 위 예제의 문제점
- `helper`함수 내에 `found`라는 번수가 없기 때문에 지역변수로 생성된다.
- 즉 `helper`함수에서 변경한 `found`변수와 `sort_priority2`함수의 `found`변수는 서로 다른 값인 것이다.

## nonlocal , global 키워드
- 변수참조의 범위 특정하는 키워드
- `nonlocal`은 전역 전까지
- `global`은 전역변수

```Python
def sort_priority_nonlocal(numbers, group):  
	found = False  
	def helper(x):  
		nonlocal found  
		if x in group:  
			found = True  
			return (0, x)  
		return (1, x)  
	numbers.sort(key=helper)  
	return found
```
- 이 경우 전역이 아닌 상위 함수의 `found`를 가져오기 때문에 원하는 값인 `True`가 출력된다.

```Python
def sort_priority_global(numbers, group):  
	found = False  
	def helper(x):  
		global found  
		if x in group:  
			found = True  
			return (0, x)  
		return (1, x)  
	numbers.sort(key=helper)  
	return found
```
- 이 경우는 전역의 `found`를 찾지만 존재하지 않아 값의 정의가 일어나게 되고 원하지 않는 결과인 `False`가 출력된다.