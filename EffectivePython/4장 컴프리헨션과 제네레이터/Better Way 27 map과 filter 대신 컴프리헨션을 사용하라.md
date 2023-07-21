```Python
a = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
squares = []  
for x in a:  
    squares.append(x ** 2)  
print(squares)
```
- 위와 같은 코드에 대해서

```Python
squares = [x**2 for x in a]  
print(squares)
```
- 위와 같이 작성하는 것이 낫다.
- `map`과 같은 경우 위와 같이 작성할 수 있지만 `filter`의 경우는 다음과 같다.

```Python
even_squares = [x**2 for x in a if x % 2 == 0]  
print(even_squares)
```
- `if`문을 추가함으로서 `filter`의 역할을 할 수 있다.
