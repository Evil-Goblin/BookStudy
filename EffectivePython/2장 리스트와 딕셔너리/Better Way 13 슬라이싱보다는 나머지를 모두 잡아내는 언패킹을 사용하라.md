
## 언패킹 ( Better Way 6 ) 은 리스트에도 사용 가능하다.
```python
a = [x for x in range(1, 4)]
one, two, three = a

print(one, two, three)
# 1 2 3
```

- 리스트의 크기가 너무 큰 경우는 모든 원소를 대응하는 변수를 만들기 쉽지않다.
```python
long_list = [x for x in range(1, 11)]  
  
one, two, three, four, five, six, seven, eight, nine, ten = long_list
```

## 별표 식을 사용한 언패킹
```python
long_list = [x for x in range(1, 11)]  
  
one, two, three, *rest = long_list  
  
print(one, two, three, rest)
# 1 2 3 [4, 5, 6, 7, 8, 9, 10]
```
- 별표 식을 사용하여 모든 값을 담는 언패킹을 할 수 있다.

```python
long_list = [x for x in range(1, 11)]  
  
one, *two_list, ten = long_list  
  
print(one, two_list, ten)
# 1 [2, 3, 4, 5, 6, 7, 8, 9] 10
```
- 중간부분에도 넣을 수 있다.

```python
long_list = [x for x in range(1, 11)]  
  
one, *two_list, seven, *last = long_list  
  
print(one, two_list, seven, last)

# one, *two_list, seven, *last = long_list
# ^
# SyntaxError: multiple starred expressions in assignment
```
- 별표 식은 대입에서 하나만 허용된다.

```python
long_list = [x for x in range(1, 11)]  
  
*last = long_list
# *last = long_list
# ^
# SyntaxError: starred assignment target must be in a list or tuple
```
- 별표 식만으로 언패킹 대입은 불가능하다.

## 별표 식은 리스트로 반환된다.
