## 여러 이터레이터 연결하기
### Chain
- 여러 이터레이터를 하나의 순차적인 이터레이터로 합친다.
```Python
it = itertools.chain([1, 2, 3], [4, 5, 6])  
print(list(it))  
  
# [1, 2, 3, 4, 5, 6]
```

### repeat
- 한 값을 계속 반복해 내놓고 싶을 때 repeat를 사용한다.
- 두 번째 인자로 최대 횟수를 지정할 수 있다.
```Python
it = itertools.repeat('hello', 3)  
print(list(it))  
  
# ['hello', 'hello', 'hello']
```

### cycle
- 어떤 이터레이터가 내놓는 원소들을 반복하고 싶을 때 사용
```Python
it = itertools.cycle([1, 2])  
result = [next(it) for _ in range(10)]  
print(result)  
  
# [1, 2, 1, 2, 1, 2, 1, 2, 1, 2]
```

### tee
- 한 이터레이터를 병렬적으로 지정된 개수의 이터레이터로 만든다.
- 두 번째 인자에 해당하는 개수만큼 만들어진다.
```Python
it1, it2, it3 = itertools.tee(['first', 'second'], 3)  
print(list(it1))  
print(list(it2))  
print(list(it3))  
  
# ['first', 'second']  
# ['first', 'second']  
# ['first', 'second']
```

### zip_longest
- zip 내장 함수의 변종으로 여러 이터레이터 중 짧은 쪽 이터레이터의 원소를 다 사용한 경우 fillvalue로 지정한 값을 채워준다.
- 만약 fillvalue를 지정하지 않을 경우 None이 넣어진다.
```Python
keys = ['one', 'two', 'three']  
values = [1, 2]  
  
normal = list(zip(keys, values))  
print('zip: ', normal)  
  
it = itertools.zip_longest(keys, values, fillvalue='nope')  
longest = list(it)  
print('zip_longest:', longest)  
  
# zip: [('one', 1), ('two', 2)]  
# zip_longest: [('one', 1), ('two', 2), ('three', 'nope')]
```


## 이터레이터에서 원소 거르기
### islice
- 이터레이터를 복사하지 않으면서 원소 인덱스를 이용해 슬라이싱 하고 싶을 때 사용한다.
- 끝만 지정하거나, 시작과 끝을 지정하거나, 시작과 끝과 증가값을 지정할 수 있다. ( 슬라이싱과 비슷하다. )
```Python
values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
  
first_five = itertools.islice(values, 5)  
print('First five: ', list(first_five))  
  
middle_odds = itertools.islice(values, 2, 8, 2)  
print('Middle odds:', list(middle_odds))  
  
# First five: [1, 2, 3, 4, 5]  
# Middle odds: [3, 5, 7]
```

### takewhile
- 이터레이터에서 주어진 술어가 False를 반환하는 첫 원소가 나타날 때까지 원소를 돌려준다.
```Python
values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
less_than_seven = lambda x: x < 7  
it = itertools.takewhile(less_than_seven, values)  
print(list(it))  
  
# [1, 2, 3, 4, 5, 6]
```

### dropwhile
- 이터레이터에서 주어진 술어가 False를 반환하는 동안 이터레이터의 원소를 건너뛴다.
```Python
values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
less_than_seven = lambda x: x < 7  
it = itertools.dropwhile(less_than_seven, values)  
print(list(it))  
  
# [7, 8, 9, 10]
```

### filterfalse
- filter 내장함수의 반대
- 이터레이터에서 술어가 False를 반환하는 모든 원소를 리턴한다.
```Python
values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
evens = lambda x: x % 2 == 0  
  
filter_result = filter(evens, values)  
print('Filter: ', list(filter_result))  
  
filter_false_result = itertools.filterfalse(evens, values)  
print('Filter false:', list(filter_false_result))

# Filter:       [2, 4, 6, 8, 10]
# Filter false: [1, 3, 5, 7, 9]
```


## 이터레이터에서 원소의 조합 만들어내기
### accumulate
- accumulate는 파라미터를 두 개 받는 함수를 반족 적용하면서 이터레이터 원소를 값 하나로 줄여준다.
- 이 함수가 돌려주는 이터레이터는 원본 이터레이터의 각 원소에 대해 누적된 결과를 내놓는다.
- javascript Array.prototype.reduce 와 흡사한 것 같다.
```Python
values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  
sum_reduce = itertools.accumulate(values)  
print('Sum: ', list(sum_reduce))  
  
def sum_modulo_20(first, second):  
output = first + second  
return output % 20  
  
modulo_reduce = itertools.accumulate(values, sum_modulo_20)  
print('Modulo:', list(modulo_reduce))

# Sum:    [1, 3, 6, 10, 15, 21, 28, 36, 45, 55]
# Modulo: [1, 3, 6, 10, 15, 1, 8, 16, 5, 15]
```

### product
- 하나 이상의 이터레이터에 들어 있는 아이템들의 데카르트 곱을 반환한다.
- 리스트 컴프리헨션을 깊이 내포시키는 대신 이 함수를 사용하면 편리하다.
```Python
single = itertools.product([1, 2], repeat=2)  
print('Single: ', list(single))  
  
multiple = itertools.product([1, 2], ['a', 'b'])  
print('Multiple:', list(multiple))  
  
# Single: [(1, 1), (1, 2), (2, 1), (2, 2)]  
# Multiple: [(1, 'a'), (1, 'b'), (2, 'a'), (2, 'b')]  
  
triple = itertools.product([1, 2], ['a', 'b'], ['x', 'y'])  
print('Triple: ', list(triple))  
  
# Triple: [(1, 'a', 'x'), (1, 'a', 'y'), (1, 'b', 'x'), (1, 'b', 'y'), (2, 'a', 'x'), (2, 'a', 'y'), (2, 'b', 'x'), (2, 'b', 'y')]  
  
multiple_repeat = itertools.product([1, 2], ['a', 'b'], repeat=2)  
print('Multiple:', list(multiple_repeat))  
multiple_repeat = itertools.product([1, 2], ['a', 'b'], [1, 2], ['a', 'b'])  
print('Multiple:', list(multiple_repeat))  
  
# Multiple: [(1, 'a', 1, 'a'), (1, 'a', 1, 'b'), (1, 'a', 2, 'a'), (1, 'a', 2, 'b'), (1, 'b', 1, 'a'), (1, 'b', 1, 'b'), (1, 'b', 2, 'a'), (1, 'b', 2, 'b'), (2, 'a', 1, 'a'), (2, 'a', 1, 'b'), (2, 'a', 2, 'a'), (2, 'a', 2, 'b'), (2, 'b', 1, 'a'), (2, 'b', 1, 'b'), (2, 'b', 2, 'a'), (2, 'b', 2, 'b')]  
# Multiple: [(1, 'a', 1, 'a'), (1, 'a', 1, 'b'), (1, 'a', 2, 'a'), (1, 'a', 2, 'b'), (1, 'b', 1, 'a'), (1, 'b', 1, 'b'), (1, 'b', 2, 'a'), (1, 'b', 2, 'b'), (2, 'a', 1, 'a'), (2, 'a', 1, 'b'), (2, 'a', 2, 'a'), (2, 'a', 2, 'b'), (2, 'b', 1, 'a'), (2, 'b', 1, 'b'), (2, 'b', 2, 'a'), (2, 'b', 2, 'b')]
```

### permutations
- permutations는 이터레이터가 내놓는 원소들로부터 만들어낸 길이 N인 수열을 돌려준다.
- 이거는 순열조합 사용하는 알고리즘 풀이에 매우 효율적일 것 같다.
```Python
it = itertools.permutations([1, 2, 3, 4], 2)  
print(list(it))  
  
# [(1, 2), (1, 3), (1, 4), (2, 1), (2, 3), (2, 4), (3, 1), (3, 2), (3, 4), (4, 1), (4, 2), (4, 3)]
```

### combinations
- combinations는 이터레이터가 내놓는 원소들로부터 만들어낸 길이가 N인 조합을 돌려준다.
- permutations와 마찬가지로 순열조합 문제에 매우 효과적이라 생각된다.
```Python
it = itertools.combinations([1, 2, 3, 4], 2)  
print(list(it))  
  
# [(1, 2), (1, 3), (1, 4), (2, 3), (2, 4), (3, 4)]
```

### combinations_with_replacement
- combinations_with_replacement 는 combinations와 같지만 원소의 반복을 허용한다. (중복 조합)
```Python
it = itertools.combinations_with_replacement([1, 2, 3, 4], 2)  
print(list(it))  
  
# [(1, 1), (1, 2), (1, 3), (1, 4), (2, 2), (2, 3), (2, 4), (3, 3), (3, 4), (4, 4)]
```

## 순열조합 주의점
- permutations, combinations, combinations_with_replacement 의 경우 원본 이터레이터의 원소가 모두 다르다고 가정하고 순열조합을 생성한다.
```Python
it = itertools.permutations([1, 1, 1, 1], 2)  
print(list(it))  
  
# [(1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1)]  
  
  
it = itertools.combinations([1, 1, 1, 1], 2)  
print(list(it))  
  
# [(1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1)]  
  
  
it = itertools.combinations_with_replacement([1, 1, 1, 1], 2)  
print(list(it))  
  
# [(1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1), (1, 1)]
```