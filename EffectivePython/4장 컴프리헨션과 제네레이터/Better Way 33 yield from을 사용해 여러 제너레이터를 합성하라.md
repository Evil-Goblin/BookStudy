## 제네레이터를 통한 애니메이션 예제
```Python
def move(period, speed):  
    for _ in range(period):  
        yield speed  
  
def pause(delay):  
    for _ in range(delay):  
        yield 0  
  
def animate():  
    for delta in move(4, 5.0):  
        yield delta  
    for delta in pause(3):  
        yield delta  
    for delta in move(2, 3.0):  
        yield delta  
  
def render(delta):  
    print(f'Delta: {delta:.1f}')  
# Render delta...  
  
def run(func):  
    for delta in func():  
        render(delta)  
  
run(animate)

# Delta: 5.0
# Delta: 5.0
# Delta: 5.0
# Delta: 5.0
# Delta: 0.0
# Delta: 0.0
# Delta: 0.0
# Delta: 3.0
# Delta: 3.0
```
- `for`와 `yield`가 반복되며 가독성이 줄어든다.
- 또한 코드가 명확하지 못해 유지보수가 어려워진다.

```Python
def animate_compounded():  
    yield from move(4, 5.0)  
    yield from pause(3)  
    yield from move(2, 3.0)  
  
run(animate_compounded)

# Delta: 5.0
# Delta: 5.0
# Delta: 5.0
# Delta: 5.0
# Delta: 0.0
# Delta: 0.0
# Delta: 0.0
# Delta: 3.0
# Delta: 3.0
```
- `yield from`은 `for`루프를 내장시키고 `yield`식을 처리하도록 하여 성능면에서 우수하다.
- 무엇보다 코드가 더 명확하고 직관적이 된다.

## 만약 제네레이터를 합성한다면 yield from을 사용하라
