## Python 3.5 이하에서는 딕셔너리의 이터레이션과 삽입 순서가 일치하지 않는다.
```python
# Python 3.5
baby_names = {
	'cat': 'kitten',
	'dog': 'puppy',
}
print(baby_names)
# >>> {'dog': 'puppy', 'cat': 'kitten'}
```
- 해쉬함수의 `seed`로 인터프리터 시작시 초기화 되는 난수를 사용했기 때문이다.
`Python 3.6` 이상부터는 딕셔너리가 삽입 순서를 보존하도록 동작이 개선되었다.

## 딕셔너리의 메소드들은 이터레이션 순서에 의존한다.
```python
# Python 3.5
print(list(baby_names.keys()))
print(list(baby_names.values()))
print(list(baby_names.items()))
print(list(baby_names.pipitem())) # 임의의 원소 하나

# ['dog', 'cat']
# ['puppy', 'kitten']
# [('dog', 'puppy'), ('cat', 'kitten')]
# ('dog', 'puppy')
```

```python
print(list(baby_names.keys()))
print(list(baby_names.values()))
print(list(baby_names.items()))
print(list(baby_names.pipitem())) # 마지막에 삽입된 원소

# ['cat', 'dog']
# ['kitten', 'puppy']
# [('cat', 'kitten'), ('dog', 'puppy')]
# ('dog', 'puppy')
```

## 딕셔너리 기반에 대해서 동일하게 적용된다.
- 키워드 인자(**kwargs)
- `class` 필드

## 모든 딕셔너리 처리에서 순서 관련 동작이 항상 성립한다고 가정해서는 안 된다.
- `list`, `dict` 등의 표준 프로토콜을 흉내 내는 커스텀 컨테이너 타입을 쉽게 정의할 수 있다.
```Python
votes = {  
	'otter': 1281,  
	'polar bear': 587,  
	'fox': 863,  
}  
  
def populate_ranks(votes, ranks):  
	names = list(votes.keys())  
	names.sort(key=votes.get, reverse=True)  
	for i, name in enumerate(names, 1):  
		ranks[name] = i  
  
def get_winner(ranks):  
	return next(iter(ranks))  
  
ranks = {}  
populate_ranks(votes, ranks)  
print(ranks)  
winner = get_winner(ranks)  
print(winner)

# {'otter': 1, 'fox': 2, 'polar bear': 3}
# otter
```
- 득표수를 기반으로 정렬하여 가장 득표수가 높은 값을 출력하는 예제이다.
- 이 경우 `get_winner`함수가 단순 딕셔너리를 받는다고 가정하여 작성되었다는 점이 맹점이다.

```Python
from collections.abc import MutableMapping  
  
  
class SortedDict(MutableMapping):  
	def __init__(self):  
		self.data = {}  
	
	def __getitem__(self, key):  
		return self.data[key]  
	  
	def __setitem__(self, key, value):  
		self.data[key] = value  
	  
	def __delitem__(self, key):  
		del self.data[key]  
	  
	def __iter__(self):  
		keys = list(self.data.keys())  
		keys.sort()  
		for key in keys:  
			yield key  
	  
	def __len__(self):  
		return len(self.data)  
  
  
sorted_rank = SortedDict()  
populate_ranks(votes, sorted_rank)  
print(sorted_rank.data)  
winner = get_winner(sorted_rank)  
print(winner)

# {'otter': 1, 'fox': 2, 'polar bear': 3}
# fox
```
- 딕셔너리와 같은 자료구조는 `Collections.abc`를 통해 기능을 재구성할 수 있다.
- 위의 예제에서 `iter`메소드의 사양을 변경함에 따라 득표수가 가장 높은 값을 출력하는 `get_winner`의 결과가 달라지게 되었다.
- 문제는 `get_winner`메소드가 삽입 순서에 맞게 딕셔너리를 이터레이션 한다고 가정했기 때문이다.

위의 예제에서 `get_winner`가 일관성있는 결과를 내도록 하기 위한 세가지 해결법이 있다.
1. `ranks`딕셔너리가 특정 순서로 이터레이션 된다고 가정하지 않는다.
```Python
def get_winner(ranks):  
	for name, rank in ranks.items():  
		if rank == 1:  
			return name
```

2. 매개변수 `ranks`의 정확한 타입을 확인한다.
```Python
def get_winner(ranks):  
	if not isinstance(ranks, dict):  
		raise TypeError('must provide a dict instance')  
	return next(iter(ranks))
```

3. 타입 에노테이션을 사용해서 `get_winner`의 매개변수의 타입이 `MutableMapping`이 아닌 `dict`임을 강제한다. (Better way 90:'type과 정적 분석을 통해 버그를 없애라')
```Python
from typing import Dict, MutableMapping  
  
def populate_ranks(votes: Dict[str, int],  
	ranks: Dict[str, int]) -> None:  
	names = list(votes.keys())  
	names.sort(key=votes.get, reverse=True)  
	for i, name in enumerate(names, 1):  
		ranks[name] = i  
  
def get_winner(ranks: Dict[str, int]) -> str:  
	return next(iter(ranks))

class SortedDict(MutableMapping[str, int]):
	...
	...

# >>> python3 -m mypy --strict BetterWay15.py
# EffectivePython/2장/BetterWay15.py:73: error: Argument 2 to "populate_ranks" has incompatible type "SortedDict"; expected "dict[str, int]"  [arg-type]
# EffectivePython/2장/BetterWay15.py:75: error: Argument 1 to "get_winner" has incompatible type "SortedDict"; expected "dict[str, int]"  [arg-type]
```
