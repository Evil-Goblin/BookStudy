```Python
stock = {  
    'nails': 125,  
    'screws': 35,  
    'wingnuts': 8,  
    'washers': 24,  
}  
  
order = ['screws', 'wingnuts', 'clips']  
  
def get_batches(count, size):  
    return count // size  
  
result = {}  
for name in order:  
    count = stock.get(name, 0)  
    batches = get_batches(count, 8)  
    if batches:  
        result[name] = batches  
  
print(result)

# {'screws': 4, 'wingnuts': 1}
```
- 위와 같은 반복문을 컴프리헨션을 통해 보다 간단히 만들 수 있다.

```Python
found = {name: get_batches(stock.get(name, 0), 8)  
        for name in order  
        if get_batches(stock.get(name, 0), 8)}  
  
print(found)

# {'screws': 4, 'wingnuts': 1}
```
- 하지만 조건에 맞는 값을 출력해야하기 때문에 한 루프에 같은 함수를 두번 호출하게 된다.
- 이에 대입식을 이용해서 한번만 호출되도록 변경한다.

```Python
found = {name: batches for name in order  
        if (batches := get_batches(stock.get(name, 0), 8))}

print(found)

# {'screws': 4, 'wingnuts': 1}
```
- 대입 식을 통해 간략화한 결과이다.

```Python
result = {name: (tenth := count // 10)  
            for name, count in stock.items()  
            if tenth > 0}  
#              ^^^^^ NameError: name 'tenth' is not defined
  
result = {name: tenth for name, count in stock.items()  
            if (tenth := count // 10) > 0}  
  
print(result)
```
- 컴프리헨션과 대입식을 사용할 시 순선에 주의해야한다.
- 컴프리헨션이 평가되는 순서에 의해 뒤와 같은 에러가 발생한다.

```Python
found = ((name, batches) for name in order  
        if (batches := get_batches(stock.get(name, 0), 8)))  

print(next(found))  
print(next(found))

# ('screws', 4)
# ('wingnuts', 1)
```
- 같은 방법으로 제네레이터를 리턴시킬 수 있다.
