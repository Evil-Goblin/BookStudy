## 인접 리스트 모델
- 모든 직원에게는 관리자가 있다.
- 하지만 관리자도 실제로는 직원에 속하며, 이 관리자에게도 관리자가 있을 수 있다.
  - 따라서 Employees(직원) 테이블과 Supervisors(관리자) 테이블을 만들어 사용하는 것은 적절하지 않다.

![자기_참조를_하는_기본키](https://github.com/Evil-Goblin/BookStudy/assets/74400861/0297e679-a41e-4d4a-ae44-2964f821c4c4)
- 자기 참조를 해서 기본키에 대한 외래키 제약 조건이 있는 테이블에 컬럼을 하나 만들어 처리한다.
- 위 그림과 같이 동일한 테이블의 기본키를 참조하는 외래키를 생성해 단일 테이블로 깊이에 제한이 없는 계층 구조를 만들 수 있다.
  - SupervisorID 가 EmployeeID 를 참조한다.

```sql
CREATE TABLE Employees
(
    EmployeeID   int PRIMARY KEY,
    EmpName      varchar(255) NOT NULL,
    EmpPosition  varchar(255) NOT NULL,
    SupervisorID int NULL
);

ALTER TABLE Employees
    ADD FOREIGN KEY (SupervisorID)
        REFERENCES Employees (EmployeeID);
```
- 자기 참조 외래키가 포함된 테이블을 생성하는 쿼리이다.
- 이 모델은 구현하기 쉽고, 설계된 방식 때문에 앞뒤가 맞지 않는 계층형 정보를 만들기 불가능하다.
  - '앞뒤가 맞지 않는다'는 것은 직원을 엉뚱한 관리자에게 할당하지 않음을 보장하지 못한다는 것이 아니라, 특정 직원의 관리자로 엉뚱한 사람을 조회하지 않음을 보장하지 못한다는 것이다.
- 이 테이블은 어떤 메타데이터도 필요 없는 완전하고 적절하게 정규화된 모델이다.
  - 관리할 메타데이터가 없으므로 이 모델로 앞뒤가 맞지 않는 계층형 정보를 생성하는 것은 불가능하다.

```sql
SELECT e1.EmpName AS Employee,
       e2.EmpName AS Supervisor,
       e3.EmpName AS SupverisorsSupervsior
FROM Employees AS e1
         LEFT JOIN Employees AS e2 ON e1.SupervisorID = e2.EmployeeID
         LEFT JOIN Employees AS e3 ON e2.SupervisorID = e3.EmployeeID;
```
- 하지만 계층형 구조에서 임의의 깊이에 있는 데이터를 추출하는 쿼리를 작성할 때 이런 쿼리의 성능은 대체로 좋지 않다.
- 위의 쿼리는 3레벨의 깊이를 수행하는 쿼리이다.
- 만약 더 깊은 레벨의 데이터를 조회해야 한다면 쿼리를 수정해야 한다.
- 인접 리스트 모델을 사용해 여러 레벨에서 수행되는 쿼리는 느리고 비효율적으로 수행될 것이다.
  - 인접 리스트 모델은 뒤에서 설명할 다른 모델과 결합해서 사용하는 것이 좋다.
- **인접 리스트 모델로 일관된 계층형 정보를 만든 후 필요한 메타데이터를 사용해 다른 모델을 정확히 표현한다.**

## 정리
- 인접 리스트는 단순히 테이블에 컬럼을 추가하고 자기 참조 테이블의 기본키를 외래키로 사용한다.
  - 메타데이터가 필요 없다.
- 일관된 계층형 구조를 만들 때는 인접 리스트 모델을 사용한다.
  - 일관된 계층형 구조는 BetterWay 59~61에서 소개할 다른 모델에서 유용하게 사용할 수 있다.
