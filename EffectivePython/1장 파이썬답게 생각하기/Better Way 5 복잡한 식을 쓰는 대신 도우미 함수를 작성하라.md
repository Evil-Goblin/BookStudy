```python
from urllib.parse import parse_qs  
  
my_values = parse_qs('red=5&blue=0&green=', keep_blank_values=True)  
print(repr(my_values))  
  
red = my_values.get('red', [''])[0] or 0  
green = my_values.get('green', [''])[0] or 0  
opacity = my_values.get('opacity', [''])[0] or 0  
  
print(f'Red: {red!r}')  
print(f'Green: {green!r}')  
print(f'Opacity: {opacity!r}')
```

```python
def get_first_int(values, key, default=0):
    try:
        return int(values.get(key))  
    except (TypeError, ValueError):  
        return default
```
- 책에서 추천하는 방법보다 위의 방법이 더 낫지 않나 생각이 든다.