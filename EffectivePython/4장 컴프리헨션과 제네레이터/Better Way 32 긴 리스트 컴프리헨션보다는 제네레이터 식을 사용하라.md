## 길이가 긴 리스트 컴프리헨션
```Python
# 예시입니다.
value = [x for x in range(999999999)]
```
- 위와 같이 너무나도 긴 데이터에 대해서 리스트 컴프리헨션을 사용하면 너무 많은 메모리를 잡아먹는 등의 문제가 발생할 수 있다.
- 이를 제네레이터 식을 이용하면 전체가 실체화되지 않기 때문에 더 나은 선택이 된다.

```Python
it = (x for x in range(999999999))
print(it)

# <generator object <genexpr> at 0x107c327b0>
```
- 이후 `next`를 통해 원소를 호출하면 되기 때문에 메모리 이슈에서 자유롭다.

## 제네레이터 식의 합성
```Python
it = (x for x in range(99999999))  
print(it)  
  
roots = ((x, x**0.5) for x in it)  
print(next(roots))  
print(next(roots))  
print(next(roots))

# <generator object <genexpr> at 0x10ddb67b0>
# (0, 0.0)
# (1, 1.0)
# (2, 1.4142135623730951)
```
- 제네레이터식을 또 다른 제네레이터식에 적용하면 연쇄적으로 호출이 되며 메모리를 보다 효율적으로 사용할 수 있게 된다.
- 제네네이터를 함께 연결한 코드를 파이썬은 아주 빠르게 실행할 수 있다.
- 스트림등에 사용시 효율적이다.