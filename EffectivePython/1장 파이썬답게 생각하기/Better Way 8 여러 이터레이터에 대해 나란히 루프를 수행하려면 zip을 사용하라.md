
## 이름과 길이 두 리스트를 이용하는 경우
- 두 리스트를 같은 인덱스로 순회하는 것은 지저분하다.
```python
names = ['Cecilia', '남궁민수', '毛泽东']  
counts = [len(n) for n in names]  
  
longest_name = None  
max_count = 0  
  
for i in range(len(names)):  
	count = counts[i]  
	if count > max_count:  
		longest_name = names[i]  
		max_count = count

print(counts)
# [7, 4, 3]
```

- 이 경우는 enumarate 또한 마찬가지 ( Better Wey 7 )
```python
for i, name in enumerate(names):  
	count = counts[i]  
	if count > max_count:  
		longest_name = name  
		max_count = count
```

- 이런 경우 `zip`을 통해 동시 순회가 가능하다.
```python
for name, count in zip(names, counts):
	if count > max_count:  
		longest_name = name  
		max_count = count
```

## zip
```python
zip(*iterables, strict=False)
```
- `iterables` 들을 병렬로 반복하여 결과를 튜플로 리턴한다.
- `strict` 가 `True` 인 경우 `iterables` 들 중 `next`에 실패하는 경우 에러를 발생시킨다.
	- `ValuError`
- `strict`가 `False`인 경우 `iterables` 중 가장 짧은 순회까지만 수행된다.
- 만약 가장 긴 `iterable`까지 순회하려한다면 `itertools.zip_longest`를 사용하면 된다.
	- 더 이상 순회가 불가능한 `iterable`은 `None`이 리턴된다.
```python
import itertools  
  
names = ['Cecilia', '남궁민수', '毛泽东']  
counts = [len(n) for n in names]  
counts.append(1)  
  
for name, count in itertools.zip_longest(names, counts):  
	print(f'{name}: {count}')

# Cecilia: 7
# 남궁민수: 4
# 毛泽东: 3
# None: 1
```