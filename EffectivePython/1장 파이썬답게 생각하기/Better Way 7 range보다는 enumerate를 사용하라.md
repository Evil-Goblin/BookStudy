
## list의 enumerate 적용 과정
```python
flavor_list = ['바닐라', '초콜릿', '피칸', '딸기']  
for flavor in flavor_list:  
	print(f'{flavor} 맛있어요.')  
  
for i in range(len(flavor_list)):  
	flavor = flavor_list[i]  
	print(f'{i + 1}: {flavor}')  
  
for i, flavor in enumerate(flavor_list):  
	print(f'{i + 1}: {flavor}')

for i, flavor in enumerate(flavor_list, 1):  
	print(f'{i}: {flavor}')
```
