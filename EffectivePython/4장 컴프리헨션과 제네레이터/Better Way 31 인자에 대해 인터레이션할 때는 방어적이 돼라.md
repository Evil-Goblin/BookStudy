## 이터레이션은 state를 갖는다.
- 이 말은 리턴될 인덱스를 저장하고 값이 매번 바뀐다는 말이다.
- 이에 한번 사용한 이터레이션을 재활용할 수 없다.
```Python
def ten_iter():  
    for i in range(10):  
        yield i  
  
  
a = ten_iter()  
print(list(a))  
print(list(a))

# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
# []
```
- 소진된 이터레이션을 다시 사용할 수 없다.

## 이터레이션의 재활용
1. 이터레이션의 복사
```Python
a = ten_iter()  
a_copy = list(a)  
print(sum(a_copy))  
print(a_copy)

# 45
# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```
- 리스트로 복사해놓고 재활용하는 방법이다.
- 하지만 이터레이션의 크기가 얼마나 될 지 모르기 때문에 메모리 이슈가 발생할 수 있다.

2. 이터레이터를 반환받는 인자를 활용
```Python
# ten_iter를 인자로 받았다고 가정  
for_sum = sum(ten_iter())  
print(for_sum)  
print(list(ten_iter()))

# 45
# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```
- 원래 함수의 매개변수로 받는 경우를 상정하는 예제인데 적당히 넘어가자
- 이 경우 매개변수에 이터레이션을 반환하는 람다식을 전달할 수 있는 등의 유동적임이 있지만 가독성이 좋지 않다.

3. 이터레이터 프로토콜을 구현한 컨테이너 클래스
```Python
class Num_iter:  
    def __init__(self, num):  
        self.num = num  
  
    def __iter__(self):  
        print("iter")
        for i in range(self.num):  
            yield i  
  
  
num_iter = Num_iter(10)  
print(list(num_iter))  
print(list(num_iter))

# iter
# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
# iter
# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```
- `for x in foo`와 같은 구문에서 실제로는 `iter(foo)`가 실행되고 이는 `foo.__iter__`매직메소드를 호출하게 된다.
- 매 호출마다 새로운 이터레이션을 반환하기 때문에 재사용이 가능해진다.
- 각 이터레이션은 독립적인 인스턴스이기 때문에 내부 state도 별도로 소진된다.
- 단점으로는 입력 데이터에 의존적이며 여러번 읽게 된다는 점이다.
