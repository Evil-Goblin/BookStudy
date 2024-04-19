## 변환용 탤리 테이블
![학생_성적_데이터](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d89ebd69-340d-4ae9-989a-e9e1825d8add)
- 예제에 사용될 데이터이다.
- 점수가 같은 경우가 없어서 요약본을 생성하기 어렵다.
  - 만약 과목과 점수로 집계를 할 경우 각각에 대해서 한 개의 로우를 생성하기 때문에 유용한 결과를 낼 수 없다.
- 점수의 범위로 등급을 나눈 탤리 테이블을 이용하면 유의미한 집계가 가능하다.

![점수_범위_탤리_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/4e202011-eb7a-48cd-9ec9-b0ca535621ba)
- 점수 범위를 문자로 변환한 탤리 테이블이다.

```sql
WITH StudentGrades (Student, Subject, FinalGrade)
         AS
         (SELECT Stu.StudentFirstNM AS Student, Sub.SubjectNM AS Subject, SS.FinalGrade
          FROM StudentSubjects AS SS
                   INNER JOIN Students AS Stu
                              ON SS.StudentID = Stu.StudentID
                   INNER JOIN Subjects AS Sub
                              ON SS.SubjectID = Sub.SubjectID)
SELECT SG.Student, SG.Subject, SG.FinalGrade, GR.LetterGrade
FROM StudentGrades AS SG
         INNER JOIN GradeRanges AS GR
                    ON SG.FinalGrade >= GR.LowGradePoint
                        AND SG.FinalGrade <= GR.HighGradePoint
ORDER BY SG.Student, SG.Subject;
```
- 점수 범위 탤리 테이블인 GradeRanges 와 StudentGrads 테이블을 조인하여 학생의 성적을 문자로 표현한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/b53e9933-de57-4483-9014-7f9351573e14)
- 위 쿼리의 조회 결과이다.
- 이제 문자로 이루어진 등급별로 데이터를 요약할 수 있을 것이다.

```sql
WITH StudentGrades (Student, Subject, FinalGrade)
         AS
         (SELECT Stu.StudentFirstNM AS Student, Sub.SubjectNM AS Subject, SS.FinalGrade
          FROM StudentSubjects AS SS
                   INNER JOIN Students AS Stu
                              ON SS.StudentID = Stu.StudentID
                   INNER JOIN Subjects AS Sub
                              ON SS.SubjectID = Sub.SubjectID)
SELECT SG.Subject, GR.LetterGrade, COUNT(*) AS NumberOfStudents
FROM StudentGrades AS SG
         INNER JOIN GradeRanges AS GR
                    ON SG.FinalGrade >= GR.LowGradePoint
                        AND SG.FinalGrade <= GR.HighGradePoint
GROUP BY SG.Subject, GR.LetterGrade
ORDER BY SG.Subject, GR.LetterGrade;
```
![성적_요약](https://github.com/Evil-Goblin/BookStudy/assets/74400861/8471f153-7f2d-4a0e-99a7-91a82f3889a2)
- 성적을 등급으로 나누고 등급별로 요약한 쿼리와 결과이다.

### 변환용 탤리 테이블 설계
- 이전 예제와 같이 변환하려면 탤리 테이블을 설계할 때 몇 가지를 염두에 둬야 한다.
  - 모든 범위의 값이 처리되는지 확인해야 한다.
- 일부 값은 수용 가능한 값의 범위에 포함되지 않는다면 데이터 손실이 발생할 수 있다.
- 이를 처리하는 방법은 두 가지이다.
  - CHECK 제약 조건을 사용해 데이터가 입력될 때마다 유효하지 않은 값들은 입력을 제한한다.
  - 탤리 테이블에 유효하지 않은 범위의 값을 포함한 로우를 추가해 'Invalid Values' 라는 문자열을 반환한다.
- 요약 목적으로 그루핑하는 것이 의도라면, 범위 값들이 적절한 크기로 분할되어 있는지도 확인해야 한다.
  - 각 범위에 속한 값이 얼마 되지 않는다면 큰 이점을 제공하지 않을 것이다.
  - 때문에 각 범위 값의 크기를 동일하게 쪼갤 필요는 없다.

## 정리
- 변환용 탤리 테이블이 여러분의 데이터에 맞게 적절하게 설계되었는지 확인한다.
- 비동등 조인에서 사용된 비동등 연산자를 탤리 테이블에 맞게 사용했는지 확인한다.
