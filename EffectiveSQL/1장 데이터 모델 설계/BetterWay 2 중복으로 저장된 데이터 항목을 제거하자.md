## 중복 데이터의 문제
- 데이터를 중복으로 젖아하면 일관되지 않은 데이터, 비정상적인 삽입, 갱신, 삭제 처리, 디스크 공간 낭비 등 많은 문제를 일으킨다.
- 정규화는 중복 데이터를 저장하면서 일으키는 문제점을 없애려고 정보를 주제별로 분할하는 프로세스를 의미한다.
  - 정규화의 한 가지 목표는 한 데이터베이스에서 동일한 테이블이든 다른 테이블이든 반복되는 데이터를 최소화하는 것이다.

**CustomerSales**

| SalesID | CustFirstName | CustLastName | address           | Model                       | SalesPerson   |
|---------|---------------|--------------|-------------------|-----------------------------|---------------|
| 1       | Amy           | Bacock       | 111 DoverLane     | Mercedes R231               | Mariam Castro |
| 2       | Tom           | Frank        | 7453 NE 20th St.  | Land Rover                  | Donald Ash    |
| 3       | Debra         | Smith        | 3223 SE 12th Pl.  | Toyota Camry                | Bill Baker    |
| 4       | Barney        | Killjoy      | 4655 Rainier Ave. | Subaru Outback              | Bill Baker    |
| 5       | Homer         | Tyler        | 1287 Grady Way    | Ford Mustang GT Convertible | Mariam Castro |
| 6       | Tom           | Frank        | 7435 NE 20th St.  | Cadillac CT6 Sedan          | Jessica Robin |
- `Tom Frank` 의 주소 데이터가 일관되지 않은 데이터 예제이다.
  - 2번 값의 주소와 6번 값의 주소가 다르다.
  - 이런 형태의 데이터 불일치는 어느 갈림에서나 나타날 수 있다.
- 고객 레코드와 함께 판매 정보가 입력되기 전까지는 주어진 자동차 모델 정보를 입력할 수 없으므로 비정상적인 데이터 입력이 존재한다.
  - 또한 자동차를 추가로 구매하는 경우 대부분의 데이터가 반복된다.
- 불필요한 데이터 입력은 디스크 공간, 메모리, 네트워크 리소스 낭비, 데이터를 입력하는 사람의 시간까지 낭비한다.
  - 오류 발생 가능성이 늘어난다.(위의 주소값 숫자를 잘못 입력하는 경우)
- 만약 판매자의 이름이 변경된 경우 전체 데이터에 대해 `UPDATE` 쿼리를 수행해야 한다.
  - 이 또한 데이터가 정확히 입력되었을 경우이다.
- 로우를 삭제할 때 제거하려고 하지 않은 데이터도 제거될 수 있다.

### 정규화
- 위의 데이터는 논리적으로 다음 테이블 네개로 분할할 수 있다.
  1. Customers 테이블
  2. Employees 테이블
  3. AutomobileModels 테이블
  4. SalesTransactions 테이블

**Customers**

| CustomerID | CustFirstName | CustLastName | Address           |
|------------|---------------|--------------|-------------------|
| 1          | Amy           | Bacock       | 111 DoverLane     |
| 2          | Tom           | Frank        | 7453 NE 20th St.  |
| 3          | Debra         | Smith        | 3223 SE 12th Pl.  |
| 4          | Barney        | Killjoy      | 4655 Rainier Ave. |
| 5          | Homer         | Tyler        | 1287 Grady Way    |


**Employees**

| EmployeeID | SalesPerson   |
|------------|---------------|
| 1          | Mariam Castro |
| 2          | Donald Ash    |
| 3          | Bill Baker    |
| 4          | Mariam Castro |


**AutomobileModels**

| ModelID | Model                       |
|---------|-----------------------------|
| 1       | Mercedes R231               |
| 2       | Land Rover                  |
| 3       | Toyota Camry                |
| 4       | Subaru Outback              |
| 5       | Ford Mustang GT Convertible |
| 6       | Cadillac CT6 Sedan          |


**SalesTransactions**

| SalesID | CustomerID | ModelID | SalesPersonID | PurchaseDate |
|---------|------------|---------|---------------|--------------|
| 1       | 1          | 1       | 1             | 2/14/2016    |
| 2       | 2          | 2       | 2             | 3/15/2016    |
| 3       | 3          | 3       | 3             | 1/20/2016    |
| 4       | 4          | 4       | 3             | 12/22/2015   |
| 5       | 5          | 5       | 1             | 11/10/2015   |
| 6       | 2          | 6       | 4             | 5/25/2015    |

- 부모 테이블 세 개(`Customers` , `AutomobileModels` , `Employees`) 의 기본키와 자식 테이블 `SalesTransactions` 의 외래키를 사용해 관계를 맺을 수 있다.
  ![ERD](https://github.com/Evil-Goblin/BookStudy/assets/74400861/d11f4fee-3e0d-444c-a409-41b6b94e8dd0)
```sql
SELECT st.SalesID, c.CustFirstName, c.CustLastName, c.Address, st.PurchaseDate, m.Model, e.SalesPerson
FROM SalesTransactions st
    INNER JOIN Customers c
        ON c.CustomerID = st.CustomerID
    INNER JOIN Employees e
        ON e.EmployeeID = st.SalesPersonID
    INNER JOIN AutomobileModels m
        On m.ModelID = st.ModelID;
```
- 이전 테이블을 반환하는 SQL

## 정리
- 데이터베이스 정규화의 목표는 중복 데이터를 제거해 데이터를 처리할 때 사용되는 자원을 최소화하는 것이다.
- 중복 데이터를 제거하면 비정상적인 삽입, 갱신, 삭제를 막을 수 있다.
- 중복 데이터를 제거하면 일관성 없는 데이터 발생을 최소화할 수 있다.
