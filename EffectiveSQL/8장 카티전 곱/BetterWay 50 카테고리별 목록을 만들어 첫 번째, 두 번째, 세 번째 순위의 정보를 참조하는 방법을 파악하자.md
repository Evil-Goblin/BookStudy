## 중요도별 순위
![연주회_예약_추적_데이터베이스_설계](https://github.com/Evil-Goblin/BookStudy/assets/74400861/055168e3-0197-4503-99bd-596bca4a2541)
- 예제에 사용할 데이터베이스 샘플이다.
- 고객과 연주회의 일정 관리를 처리하는 데이터베이스로 공연별로 연주하는 모든 종류의 음악 정보를 조회한다.
- Musical_preferences 테이블에는 일련번호를 사용해 고객이 선호하는 음악의 순위를 매긴 PreferenceSeq 컬럼이 있다.
  - 컬럼 값이 1이면 고객이 가장 선호하는 음악을 나타내고, 2면 두 번째로 선호하는 음악을 나타낸다.
- Entertainer_Styles 테이블에는 연주회에서 연주자가 상대적으로 잘 연주하는 음악의 유형 정보가 담겨 있다.

```sql
WITH CustStyles AS
  (SELECT C.CustomerID, C.CustFirstName, 
      C.CustLastName, MS.StyleName
   FROM Customers AS C INNER JOIN Musical_Preferences AS MP
     ON C.CustomerID = MP.CustomerID
   INNER JOIN Musical_Styles AS MS
     ON MP.StyleID = MS.StyleID),
EntStyles AS
  (SELECT E.EntertainerID, E.EntStageName, MS.StyleName
   FROM Entertainers AS E INNER JOIN Entertainer_Styles AS ES
     ON E.EntertainerID = ES.EntertainerID
   INNER JOIN Musical_Styles AS MS
     ON ES.StyleID = MS.StyleID)
SELECT CustStyles.CustomerID, CustStyles.CustFirstName, 
    CustStyles.CustLastName, EntStyles.EntStageName
FROM CustStyles INNER JOIN EntStyles
  ON CustStyles.StyleName = EntStyles.StyleName
GROUP BY CustStyles.CustomerID, CustStyles.CustFirstName,
     CustStyles.CustLastName, EntStyles.EntStageName
HAVING COUNT(EntStyles.StyleName) =
  (SELECT COUNT(StyleName) 
   FROM CustStyles AS CS1
   WHERE CS1.CustomerID = CustStyles.CustomerID)
ORDER BY CustStyles.CustomerID;
```
- 모든 고객이 선호하는 장르와 일치하는 연주회 정보를 조회하는 쿼리이다.
- 여러 속성(연주회가 잘 연주하는 장르) 집합과 일치하는 요건(고객의 선호 장르) 집합이 여러 개 있을 수 있기 때문에 GROUP BY 를 이용한 나누기 연산을 변형하여 사용한다.
  - 각 고객이 선호하는 장르의 개수만 세기 위해 장르 이름의 개수를 세는 HAVING 절의 서브쿼리에 WHERE 절을 추가한다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/53c1a0fe-26e0-4e12-922d-7e0a2842945d)
- 쿼리의 조회 결과이다.
- 주어진 고객별 선호하는 장르와 일치하는 연주회 및 고객 목록이 조회되었다.

```sql
WITH CustPreferences AS
(SELECT C.CustomerID, C.CustFirstName, C.CustLastName, 
       MAX((CASE WHEN MP.PreferenceSeq = 1  
                 THEN MP.StyleID 
                 ELSE Null END)) AS FirstPreference,
       MAX((CASE WHEN MP.PreferenceSeq = 2  
                 THEN MP.StyleID 
                 ELSE Null END)) AS SecondPreference,
       MAX((CASE WHEN MP.PreferenceSeq = 3  
                 THEN MP.StyleID 
                 ELSE Null END)) AS ThirdPreference
   FROM Musical_Preferences AS MP INNER JOIN Customers AS C
      ON MP.CustomerID = C.CustomerID 
   GROUP BY C.CustomerID, C.CustFirstName, C.CustLastName),
EntStrengths AS
(SELECT E.EntertainerID, E.EntStageName, 
       MAX((CASE WHEN ES.StyleStrength = 1 
                 THEN ES.StyleID 
                 ELSE Null END)) AS FirstStrength, 
       MAX((CASE WHEN ES.StyleStrength = 2 
                 THEN ES.StyleID 
                 ELSE Null END)) AS SecondStrength, 
       MAX((CASE WHEN ES.StyleStrength = 3 
                 THEN ES.StyleID 
                 ELSE Null END)) AS ThirdStrength 
   FROM Entertainer_Styles AS ES
   INNER JOIN Entertainers AS E
      ON ES.EntertainerID = E.EntertainerID 
   GROUP BY E.EntertainerID, E.EntStageName)
SELECT CustomerID, CustFirstName, CustLastName, 
   EntertainerID, EntStageName
FROM CustPreferences CROSS JOIN EntStrengths
WHERE (FirstPreference = FirstStrength
       AND SecondPreference = SecondStrength)
   OR (SecondPreference =FirstStrength
       AND FirstPreference = SecondStrength)
ORDER BY CustomerID;
```
- 고객이 선호하는 상위 장르 세 개를 각각 피벗하고, 연주회가 잘 연주하는 장르 또한 동일하게 처리하여 순서에 상관없이 일치하는 상위 2건을 찾음으로서 해결할 수도 있다.
  - WHERE 절의 조건을 변경해 수용할 만한 조합을 확장할 수 있다.
  - 고객의 첫 번째, 두 번째 선호 장르와 연주회의 세 번째 장르가 서로 일치하는 건을 추출할 수도 있다.

![조회_결과](https://github.com/Evil-Goblin/BookStudy/assets/74400861/ca4ea3e1-cbf2-4a45-aa8f-9be73789d9c9)
- 조회 결과이다.
- 이전의 쿼리와 다른점이라면 이전 쿼리는 선호 장르 두세 개가 일치하는 지를 보는 쿼리이고, 이 쿼리는 첫 번째, 두 번째 장르가 같은지를 보는 쿼리이기 때문에 결과가 조금 달라졌다.
- 나누기 연산으로 완전히 일치하는 건을 모두 찾을 수 있겠지만, 일부만 일치하는 건을 찾는 방법은 좀 더 모색해야 한다.

## 정리
- 나누기 연산은 완전히 일치하는 모든 건을 찾아낸다.
- 부분 일치 항목을 찾으려면 다른 기법을 적용해야 한다.
- 테이블에 있는 데이터에 순위를 매기면 일치하는 건을 찾는 최상의 대안을 결정하는 데 도움이 된다.
