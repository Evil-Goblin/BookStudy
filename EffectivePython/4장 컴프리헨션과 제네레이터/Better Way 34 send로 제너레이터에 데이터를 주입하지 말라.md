```Python
import math  
  
  
def transmit(output):  
    if output is None:  
        print(f'출력: None')  
    else:  
        print(f'출력: {output:>5.1f}')  
  
  
def wave_modulating(step):  
    step_size = 2 * math.pi / 180 * step  
    amplitude = yield  
    for step in range(step):  
        radians = step * step_size  
        fraction = math.sin(radians)  
        output = amplitude * fraction  
        amplitude = yield output  
  
  
def run_modulating(it):  
    amplitude = [None, 7, 7, 7, 2, 2, 2, 2, 10, 10, 10, 10, 10]  
    for amplitude in amplitude:  
        output = it.send(amplitude)  
        transmit(output)  
  
  
run_modulating(wave_modulating(12))

# 출력: None
# 출력:   0.0
# 출력:   2.8
# 출력:   5.2
# 출력:   1.9
# 출력:   2.0
# 출력:   1.7
# 출력:   1.2
# 출력:   2.1
# 출력:  -2.1
# 출력:  -5.9
# 출력:  -8.7
# 출력:  -9.9
```
- 파이썬 제네레이터는 `send`메서드를 지원한다.
- 이 메서드를 통해 양방향 채널로 이용이 가능하다.
- 일반적으로 제네레이터를 이터레이션할 때 `yield`식이 반환하는 값은 `None`이다.

## None이 리턴되는 경우를 대비하여 제네레이터의 입력으로 send가 아닌 이터레이터를 전달하는 방식이 더 낫다.
