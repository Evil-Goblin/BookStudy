
## 언패킹을 이용한 예시
```python
item = ('호박엿', '식혜')  
first, second = item  
print(first, 'and', second)
```

```python
favorite_snacks = {  
	'짭조름한 과자': ('프레즐', 100),  
	'달콤한 과자': ('쿠키', 180),  
	'채소': ('당근', 20),  
}  
  
((type1, (name1, cals1)),  
 (type2, (name2, cals2)),  
 (type3, (name3, cals3))) = favorite_snacks.items()  
  
print(f'제일 좋아하는 {type1}은 {name1}로, {cals1} 칼로리입니다.')  
print(f'제일 좋아하는 {type2}은 {name2}로, {cals2} 칼로리입니다.')  
print(f'제일 좋아하는 {type3}은 {name3}로, {cals3} 칼로리입니다.')
```

```python
def bubble_sort(a):  
	for _ in range(len(a)):  
		for i in range(1, len(a)):  
			if a[i] < a[i-1]:  
				a[i-1], a[i] = a[i], a[i-1]  
  
names = ['프레즐', '당근', '쑥갓', '베이컨']  
bubble_sort(names)  
print(names)
```

```python
snacks = [('베이컨', 350), ('도넛', 240), ('머핀', 190)]  
for rank, (name, calories) in enumerate(snacks, 1):  
	print(f'#{rank}: {name}은 {calories} 칼로리입니다.')
```
- 이터러블에 사용하는것이 특히 좋다고 한다.