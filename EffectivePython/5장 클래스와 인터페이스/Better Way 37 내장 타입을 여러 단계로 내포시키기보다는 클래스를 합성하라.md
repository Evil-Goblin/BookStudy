## 딕셔너리와 관련 내장 타입은 사용이 쉬운 만큼 과하게 확장하면 깨지기 쉬운 코드가 될 가능성이 있다.
- 만약 학생별 점수를 기록하는 코드를 작성한다고 하면 딕셔너리를 이용하여 쉽게 정의할 수 있다.
```python
class SimpleGradebook:  
    def __init__(self):  
        self._grades = {}  
          
    def add_student(self, name):  
        self._grades[name] = []  
          
    def report_grade(self, name, grade):  
        self._grades[name].append(grade)  
          
    def average_grade(self, name):  
        grades = self._grades[name]  
        return sum(grades) / len(grades)
```

- 하지만 만약 학생의 과목별 점수를 기록하고자 한다면 딕셔너리 내부 딕셔너리를 이용하여 작성하게 될 것 이다.
```python
class BySubjectGradebook:  
    def __init__(self):  
        self._grades = {}  
  
    def add_student(self, name):  
        self._grades[name] = defaultdict(list)  
  
    def report_grade(self, name, subject, grade):  
        by_subject = self._grades[name]  
        grade_list = by_subject[subject]  
        grade_list.append(grade)  
  
    def average_grade(self, name):  
        by_subject = self._grades[name]  
        total, count = 0, 0  
        for grades in by_subject.values():  
            total += sum(grades)  
            count += len(grades)  
        return total / count
```

- 만약 각 점수의 가중치를 함께 저장해서 중간고사와 기말고사가 다른 쪽지 시험 성적에 더 큰 영향을 미치게 하고 싶다면 더욱 복잡해진다.
```python
class WeightedGradebook:  
    def __init__(self):  
        self._grades = {}  
  
    def add_student(self, name):  
        self._grades[name] = defaultdict(list)  
      
    def report_grade(self, name, subject, grade, weight):  
        by_subject = self._grades[name]  
        grade_list = by_subject[subject]  
        grade_list.append((grade, weight))  
      
    def average_grade(self, name):  
        by_subject = self._grades[name]  
        total, count = 0, 0  
        for subject, grades in by_subject.items():  
            subject_avg, total_weight = 0, 0  
            for grade, weight in grades:  
                subject_avg += grade * weight  
                total_weight += weight  
            total += subject_avg / total_weight  
            count += 1  
        return total / count
```
- 이제는 2중 루프를 이용하게 되며 코드가 많이 복잡해졌다.
- 또한 클래스의 사용도 어려워졌다.
	- 호출자에서 각 매개변수가 의미하는 값을 정확히 알기 어렵다.

## 복잡도가 올라가면 내장 타입이 아닌 클래스 계층 구조를 만들어라
### 클래스를 이용하여 리팩토링
#### namedtuple
```python
Grade = namedtuple('Grade', ('score', 'weight'))
```
- 이 클래스의 인스턴스를 만들 때 위치  기반 인자를 사용해도 되고 키워드 인자를 사용해도 된다.
- 필드에 접근 시 애트리뷰트 이름을 사용할 수 있다.
- 이름이 붙은 애트리뷰트를 사용할 수 있기 때문에 요구 사항이 바뀌는 경우에 `namedtuple` 을 클래스로 변경하기도 쉽다.
	- 가변성을 지원해야 하거나 간단한 데이터 컨테이너 이상의 동작이 필요한 경우 `namedtuple` 을 쉽게 클래스로 바꿀 수 있다.

##### namedtuple 의 한계
- `namedtuple` 클래스에는 디폴트 인자 값을 지정할 수 없다.
	- 선택적인 프로퍼티가 많은 데이터에 `namedtuple` 을 사용하기 어렵다.
	- 만약 프로퍼티가 4~5개보다 많아지면 `dataclasses` 내장 모듈을 사용하는 편이 낫다.
- `namedtuple` 인스턴스의 애트리뷰트 값을 숫자 인덱스로 접근 가능하고 이터레이션도 가능하다.
	- 외부에 제공하는 API 의 경우 이런 특성 때문에 나중에 실제 클래스로 변경이 어려울 수 있다.
	- `namedtuple` 을 사용하는 모든 부분을 제어할 수 있는 상황이 아니라면 새로운 클래스를 정의하는 편이 낫다.

```python
class Subject:  
    def __init__(self):  
        self._grades = []  
  
    def report_grade(self, score, weight):  
        self._grades.append(Grade(score, weight))  
  
    def average_grade(self):  
        total, total_weight = 0, 0  
        for grade in self._grades:  
            total += grade.score * grade.weight  
            total_weight += grade.weight  
        return total / total_weight

class Student:  
    def __init__(self):  
        self._subjects = defaultdict(Subject)  
  
    def get_subject(self, name):  
        return self._subjects[name]  
      
    def average_grade(self):  
        total, count = 0, 0  
        for subject in self._subjects.values():  
            total += subject.average_grade()  
            count += 1  
        return total / count  
      
class Gradebook:  
    def __init__(self):  
        self._students = defaultdict(Student)  
      
    def get_student(self, name):  
        return self._students[name]
```
- 코드의 양은 늘어났지만 가독성이 좋아지고, 확장성이 좋아졌다.
- 하위 호환성을 제공하는 메서드를 작성해서 예전 스타일의 API 를 사용중인 코드를 새로운 객체 계층을 사용하는 코드로 쉽게 마이그레이션할 수도 있다.

## 정리
- 딕셔너리, 긴 튜플, 다른 내장 타입이 복잡하게 내포된 데이터를 값ㅇ즈로 사용하는 딕셔너리를 만들지 말라.
- 완전한 클래스가 제공하는 유연성이 필요하지 않고 가벼운 불변 데이터 컨테이너가 필요하다면 `namedtuple` 을 사용하라.
- 내부 상태를 표현하는 딕셔너리가 복잡해지면 이 데이터를 관리하는 코드를 여러 클래스로 나눠서 재작성하라.