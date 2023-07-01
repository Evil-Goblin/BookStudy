## setdefault
```
setdefault(key[, default])
```
- `dict`의 메소드 `setdefault`를 이용하면 `key`값이 없을 시 해당 `key`에 `default`값을 넣어준다.

```Python
votes = {}  
  
names = votes.setdefault('철수', [])  
print(votes)

# {'철수': []}
```
- 하지만 메소드명이 `set`임에도 실제 기능은 `get`인 점 등에서 가독성이 좋지는 않다.
- 또한 `default`로 넘기는 값이 복사되어 세팅되는 것이 아닌 레퍼런스가 등록된다는 점에서 사용에 주의가 필요하다.

```Python
votes = {}  
item = []  
names = votes.setdefault('철수', item)  
print(votes)  
item.append('햄버거')  
print(votes)

# {'철수': []}
# {'철수': ['햄버거']}
```
- 이러한 단점이 있어서 `get`에 비해서 활용도가 높지 않다.