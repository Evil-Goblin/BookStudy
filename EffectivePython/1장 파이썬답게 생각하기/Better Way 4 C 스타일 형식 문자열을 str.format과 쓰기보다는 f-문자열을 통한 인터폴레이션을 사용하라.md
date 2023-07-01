```python
key = 'my_var'  
value = 1.234  
  
f_string = f'{key:<10} = {value:.2f}'  
  
c_tuple = '%-10s = %.2f' % (key, value)  
  
str_args = '{:<10} = {:.2f}'.format(key, value)  
  
str_kw = '{key:<10} = {value:.2f}'.format(key=key, value=value)  
  
c_dict = '%(key)-10s = %(value).2f' % {'key': key, 'value': value}  
  
assert c_tuple == c_dict == f_string == str_args == str_kw
# >>> my_var     = 1.23
```

```python
places = 3  
number = 1.23456  
print(f'My number is {number:.{places}f}')
# >>> My number is 1.235
```