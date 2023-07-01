# Python 3.8 이상 사용 가능

## 특정 결과를 통해 특정 로직을 수행하려 할때
```python
fresh_fruit = {  
	'사과': 10,  
	'바나나': 8,  
	'레몬': 5,  
}  
  
def make_lemonade(count):  
	print(f'make_lemonade {count}')  
  
count = fresh_fruit.get('레몬', 0)  
if count:  
	make_lemonade(count)  
else:  
	print('레몬이 없습니다.')

# make_lemonade 5
```
- `fresh_fruit` 에서 레몬의 갯수를 구한 값이 조건문에도 함수의 매개변수로도 사용된다.
- 이 문장을 줄일 수 있다.

```python
if count := fresh_fruit.get('레몬', 0):
	make_lemonade(count)
else:
	print('레몬이 없습니다.')
```
- 대입식을 이용하여 이를 축약할 수 있다.
- 대입식을 통한 변수의 실제 스코프가 해당 블럭내 까지인 것은 아니지만 해당 블럭에서 사용하려는 변수라는 사실을 보일 수 있어서 보다 직관적이다.