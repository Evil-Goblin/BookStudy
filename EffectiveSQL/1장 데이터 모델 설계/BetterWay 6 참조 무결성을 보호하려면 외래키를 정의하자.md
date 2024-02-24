## 데이터베이스 스키마를 제대로 설계하려면 관련된 부모 테이블의 기본키 값을 포함하도록 테이블에 외래키를 정의하는 것이 좋다.
![Sales_Orders_테이블_설계](https://github.com/Evil-Goblin/BookStudy/assets/74400861/9c8836ce-a106-4b47-8312-7618c45426e5)
- 각 관계선 끝에 위치한 열쇠 기호가 한 테이블의 기본키와 관계를 맺고 있음을 의미한다.
- 무한대 기호로 '일대다'관계를 맺고 있음을 의미한다.
- `Orders` 테이블은 `Customers` 테이블의 기본키를 가리키는 `CustomerID` 나 `CustomerNumber` 컬럼을 정의해 이 컬럼으로 각 주문 고객 정보를 식별할 수 있게 해야 한다.

## 선언적 참조 무결성
- 기본키 제약, 유일제약, 참조키 제약, `Identity` 속성 등 액세스 로직을 데이터베이스 객체에 직접 삽입한다.
- 이런 관계를 정의하는 목적은 다음과 같다.
1. 그래픽 쿼리 디자이너를 사용하면 데이터베이스에서 새로운 뷰나 저장 프로시저를 생성할 때 쿼리 디자이너가 `JOIN` 절을 올바르게 만들 수 있도록 도와준다.
2. 일대다 관계에서 '다'에 해당하는 테이블에 데이터를 입력하고 변경하거나 '일'에 해당하는 테이블의 데이터를 변경하고 삭제할 때 데이터베이스 시스템이 데이터 무결성을 강화하는 데 도움을 준다.
   - `Orders` 테이블에 누락되거나 엉뚱한 `CustomerID` 값을 가진 로우를 입력하지 않도록 해야 하기 때문
- 로우의 변경등이 전파되도록 하고 싶다면 '다'관계에 있는 테이블을 생성할 때 `CREATE TABLE` 구문이나 `ALTER TABLE` 구문으로 `FOREIGN KEY` 제약 조건을 추가해야 한다.

```sql
CREATE TABLE Customers
(
    CustomerID        int NOT NULL PRIMARY KEY,
    CustFirstName     varchar(25) NULL,
    CustLastName      varchar(25) NULL,
    CustStreetAddress varchar(50) NULL,
    CustCity          varchar(30) NULL,
    CustState         varchar(2) NULL,
    CustZipCode       varchar(10) NULL,
    CustAreaCode      smallint NULL DEFAULT 0,
    CustPhoneNumber   varchar(8) NULL
);

CREATE TABLE Orders
(
    OrderNumber int NOT NULL PRIMARY KEY,
    OrderDate   date NULL,
    ShipDate    date NULL,
    CustomerID  int NOT NULL DEFAULT 0,
    EmployeeID  int NULL DEFAULT 0,
    OrderTotal  decimal(15, 2) NULL DEFAULT 0
);

ALTER TABLE Orders
    ADD CONSTRAINT Orders_FK99
        FOREIGN KEY (CustomerID)
            REFERENCES Customers (CustomerID);
```
- `ALTER TABLE` 을 이용하여 `FOREIGN KEY` 제약 조건을 추가한 경우
- 이 두 테이블을 생성하고 데이터를 입력한 후  `FOREIGN KEY` 제약 조건을 추가하면 `Orders` 테이블에 입력된 데이터가 참조 무결성을 위반할 때 `Orders` 테이블의 `ALTER TABLE` 구문은 실패할 것이다.
  - 일부 데이터베이스에서는 성공할 수 있지만, 데이터베이스 옵티마이저는 이 제약 조건을 신뢰할 수 없다고 판단해 더 사용하지 않을 것이다.
  - 단순히 이 제약 조건을 정의하는 것만으로는 이전에 입력된 데이터에 대한 참조 무결성이 꼭 보장된다고 장담할 수 없다.

```sql
CREATE TABLE Orders
(
    OrderNumber int NOT NULL PRIMARY KEY,
    OrderDate   date NULL,
    ShipDate    date NULL,
    CustomerID  int NOT NULL DEFAULT 0
        CONSTRAINT Orders_FK98 FOREIGN KEY
        REFERENCES Customers (CustomerID),
    EmployeeID  int NULL DEFAULT 0,
    OrderTotal  decimal(15, 2) NULL DEFAULT 0
);
```
- `CREATE TALBE` 에서 `FOREIGN KEY` 제약 조건을 정의한 경우
- 일부 데이터베이스 시스템에서는 참조 무결성 제약 조건을 정의하면 자동으로 외래키 컬럼에 인덱스를 만들어 조인을 수행할 때 성능 향상 효과가 있을 수 있다.
- `DB2` 와 같은 외래키 컬럼에 자동으로 인덱스를 만들지 않는 데이터베이스 시스템은 이 제약 조건을 검증하고 최적화 차원에서 외래키 컬럼에 인덱스를 만들면 좋다.

## 정리
- 명시적으로 외래키를 만들면 부모 테이블에 없는 로우를 가리키는 자식 테이블 로우가 없음을 보장할 수 있으므로 관련된 테이블 간에 데이터 무결성을 확인하는 데 좋다.
- 이미 데이터가 있는 테이블에 `FOREIGN KEY` 제약 조건을 추가할 때 이 제약 조건을 위반하는 데이터가 있다면 제약 조건을 생성하는 작업은 실패할 것이다.
- 일부 시스템에서는 `FOREIGN KEY` 제약 조건을 정의하면 자동으로 인덱스를 만들어 주므로 조인 성능이 향상될 수 있다.
  - 다른 시스템은 `FOREIGN KEY` 제약 조건이 걸린 컬럼에 수동으로 인덱스를 만들어야 한다.
  - 일부 시스템은 인덱스 없이도 옵티마이저가 해당 컬럼을 특별 취급해 더 나은 쿼리 실행 계획을 세우기도 한다.
