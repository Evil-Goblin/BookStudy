## GeoJSON
```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          126.97505272087818,
          37.56153618199296
        ],
        "type": "Point"
      }
    }
  ]
}
```
- 시청역 부영빌딩의 `GeoJSON` 정보이다.

```json
{
  "type": "FeatureCollection",
  "features": [{
    "type": "Feature", 
    "properties": {},
    "geometry": { 
      "type": "GeometryCollection", 
      "geometries": [ 
        { 
          "type": "Point",
          "coordinates": [
            61.34765625,
            48.63290858589535
          ]
        },
        {
          "type": "Polygon",
          "coordinates": [
            [
              [
                59.94140624999999,
                50.65294336725709
              ],
              [
                59.94140624999999,
                50.65294336725709
              ]
            ]
          ]
        }
      ]
    }
  }]
}
```
- 출처: https://stackoverflow.com/questions/34044893/how-to-make-a-geometrycollection-in-geojson-with-a-single-point-polygon
- 책의 예시에서 문제가 되던 부분인 `geometry` 타입이 `GeometryCollection` 인 경우

```typescript
import { Feature } from 'geojson'

function calculateBoundingBox(f: Feature): BoundingBox | null {
  let box: BoundingBox | null = null

  const helper = (coords: any[]) => {
    // ...
  }

  const { geometry } = f
  if (geometry) {
    helper(geometry.coordinates)
    //              ~~~~~~~~~~~
    //              'Geometry' 형식에 'coordinates' 속성이 없습니다. 
    //              'GeometryCollection' 형식에
    //              'coordinates' 속성이 없습니다.
  }

  return box
}
```
- `GeometryCollection` 형식에는 `coordinates` 가 `geometries` 으로 한번 더 둘러 쌓여있다.
- 이에 위의 코드는 오류가 발생하게된다.

```typescript
const { geometry } = f
if (geometry) {
  if (geometry.type === 'GeometryCollection') {
    throw new Error('GeometryCollections are not supported.')
  }
  helper(geometry.coordinates)
}
```
- 위와 같은 타입 좁히기를 통해 오류를 제어할 수는 있지만 모든 타입에 대해서 동작하게 하려고한다면 위의 방법이 정답이 될 수 없다.

```typescript
const geometryHelper = (g: Geometry) => {
  if (geometry.type === 'GeometryCollection') {
    geometry.geometries.forEach(geometryHelper)
  } else {
    helper(geometry.coordinates)
  }
}

const { geometry } = f
if (geometry) {
  geometryHelper(geometry)
}
```
- 이렇게 해야 모든 타입에 대해서 작동한다고 할 수 있다.
- 하지만 이런 방식은 너무 api의 타입에 의존하게 되는 느낌이 강하게 든다.
- 그럼에도 api에 의존적일 수 밖에 없는건가...
- `geometryHelper`를 어뎁터패턴의 일종으로 생각해야하는가...
- 그러기보다 어뎁터를 직접 만들어놓고 아예 클라이언트 사이드와 분리해버리는게 더 나을 것 같은데...

## API 또는 데이터 형식에 대한 타입생성을 고려하라
## 데이터보다는 명세로부터 코드를 생성하라
- `graphql` 또한 쿼리로부터 타입을 생성해주는 라이브러리가 있기 때문에 해당 명세에 대한 생성된 코드를 사용하는 것이 좋다.
- 약간 `ORM`과 비슷한 느낌이 든다.
- 타입스크립트의 영역에서 추상화는 좀 어려운가? 하는 생각이 든다.
- 타입을 이용한 추상화가 분명 가능한 것 같은데 위와 같은 예시를 보면 추상화보다는 구현된 객체의 형식에 보다 의존하는 느낌
