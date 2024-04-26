## 계통 폐쇄(Ancestry Closure) 테이블
- 계층형 데이터를 관리하는 마지막 방법은 계총 폐쇄(Ancestry Closure) 테이블을 사용하는 것이다.
- 기본적으로는 '구체화된 경로'에서 언급된 구체화된 뷰에 대한 관계형 접근법이다.
  - 테이블의 컬럼에 저장된 문자열 대신 별도의 테이블을 사용하고, 노드 간 '연결' 각각에 대해서는 메타데이터 레코드를 생성한다.
  - 노드의 개수에 상관없이 두 노드 간에 가능한 모든 연결 정보를 만든다.

```sql
CREATE TABLE Employees
(
  EmployeeID   int          NOT NULL PRIMARY KEY,
  EmpName      varchar(255) NOT NULL,
  EmpPosition  varchar(255) NOT NULL,
  SupervisorID int NULL
);

CREATE TABLE EmployeesAncestry
(
  SupervisedEmployeeID  int NOT NULL,
  SupervisingEmployeeID int NOT NULL,
  Distance              int NOT NULL,
  PRIMARY KEY (SupervisedEmployeeID, SupervisingEmployeeID)
);

ALTER TABLE EmployeesAncestry
  ADD CONSTRAINT FK_EmployeesAncestry_SupervisingEmployeeID
    FOREIGN KEY (SupervisingEmployeeID)
      REFERENCES Employees (EmployeeID);

ALTER TABLE EmployeesAncestry
  ADD CONSTRAINT FK_EmployeesAncestry_SupervisedEmployeeID
    FOREIGN KEY (SupervisedEmployeeID)
      REFERENCES Employees (EmployeeID);
```
- 계통 테이블을 포함한 테이블을 생성한다.
- 다른 모델과 달리 메타데이터가 EmployeesAncestry 라는 별도의 테이블에 저장된다.

![계통_메타데이터_레코드를_가진_Employees_테이블](https://github.com/Evil-Goblin/BookStudy/assets/74400861/a895fe5f-8eb4-48f0-8199-ac85ae6f6a77)
- 모든 데이터를 나타내지는 않았다.
- 그림에서 표시된 것 처럼 'Nya Maeng' 과 그 관리자인 'Tom LaPlante', 'Tom LaPlante' 와 그 관리자인 'Amy Kok' 의 연결 정보가 두 개 있다.
- 'Nya' 에 대해 가능한 연결 정보를 모두 보여 줘야 한다면 레코드를 세 개 만들어야 한다.
  - 'Nya' 자신을 식별하는 재귀 레코드(Reflexive Record)가 필요하다.
    - 이 레코드는 관리 대상자와 관리자가 자기 자신이고, 둘 간의 거리는 0이다.
  - 'Nya' 의 직속 관리자인 'Tom' 을 식별하는 레코드가 필요하다.
    - 둘 사이의 거리는 1이다.
  - 'Amy' 를 식별하는 레코드가 필요하다.
    - 'Nya' 와의 거리는 2이다.
- 가능한 모든 연결이 계통 테이블에 있다.
  - 관심 있는 노드가 무엇이든 주어진 노드에서 완전한 경로를 추적하려면 이 계통 테이블과 데이터 테이블을 조인하면 된다.

### 특징
- 구체화된 경로와 마찬가지로 보편적으로 준수해야 할 사항이 따로 없다.
  - 깊이 대신 거리를 사용했다.
- 계통 테이블에 있는 컬럼은 보통 조상(Ancestor)과 자손(Descendant) 이라고 하지만, 이것도 상대적인 개념이다.
  - 한 로우는 다른 로우의 조상이자 다른 로우의 자손이 될 수 있다.
- 재귀 레코드를 포함할지 고려해야 한다.
  - 자기 자신을 관리하는 레코드가 불필요해 보일 수 있지만, 만약 재귀 레코드를 포함하지 안흔ㄴ다면 결과에 찾는 직원을 표시할 때 쿼리가 더 복잡해지게 된다.

### 단점
- 테이블 관리에 많은 노력이 필요하다.
  - 계층 정보가 변경될 때마다 여러 레코드에서 삽입과 삭제가 필요해진다.
  - 저장 프로시저로 구현하거나, 인접 리스트 모델을 계속 사용한다면 Employees 테이블의 SupervisorID 컬럼을 모니터링하면서 자동으로 계통 테이블 값을 갱신하는 트리거를 사용할 수도 있다.

### 활용
```sql
SELECT e.*
FROM Employees AS e
       INNER JOIN EmployeesAncestry AS a
                  ON e.EmployeeID = a.SupervisedEmployeeID
WHERE a.SupervisingEmployeeID = @EmployeeID
  AND a.Distance > 0;
```
- 한 노드의 모든 자식 노드를 찾는 쿼리이다.
- 다른 방법과 달리 깊이를 제한하기가 더 쉽다.
  - 찾으려는 깊이에 따라서 Distance 의 범위룰 조정하면 된다.

```sql
SELECT e.*
FROM Employees AS e
       INNER JOIN EmployeesAncestry AS a
                  ON e.EmployeeID = a.SupervisingEmployeeID
WHERE a.SupervisedEmployeeID = @EmployeeID
  AND a.Distance > 0;
```
- 한 노드의 모든 조상을 찾는 쿼리이다.
- 이전의 구체화된 경로가 조상을 찾을 때 사거블하지 않은 쿼리가 된 것과 달리 이 쿼리는 여전히 사거블 쿼리이다.

```sql
SELECT e.*
FROM Employees AS e
WHERE NOT EXISTS (SELECT NULL
                  FROM EmployeesAncestry AS a
                  WHERE e.EmployeeID = a.SupervisingEmployeeID
                    AND a.Distance > 0);
```
- 자식이 없는 모든 노드를 찾는 쿼리이다.
- 계통 테이블에 재귀 레코드가 포함되었기 때문에 재귀 레코드 이외의 노드를 검색할 때 재귀 레코드를 제외해야 한다.

## 정리
- 계통 테이블을 관리하는 노력이 추가로 필요하지만 갱신이 빈번하고 검색을 쉽게 해야 할 때, 특히 트리의 중간에서 탐색하는 상황에서는 계통 순회 폐쇄 모델을 사용하는 방안을 검토한다.
- 계통 테이블의 메타데이터를 최신 상태로 유지하지 못하면 잘못된 결과가 나올 수 있다.
  - 이 문제는 Employees 테이블에 트리거를 추가해 자동으로 계통 테이블 정보를 갱신하는 방식으로 해결할 수 있다.
  - 하지만 그에 따른 추가 노력은 감수해야 한다.
