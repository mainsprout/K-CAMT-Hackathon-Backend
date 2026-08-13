# API 문서

## 공통 사항

- Base URL: `http://localhost:8080`
- 인증 방식: 세션 쿠키(`JSESSIONID`) 기반. **모든 요청에 `credentials: "include"` 필수**
- 인증이 필요한 API는 별도 토큰/식별자를 body나 header에 넣을 필요 없음 — 쿠키만 있으면 서버가 로그인한 사용자를 알아서 식별함
- CORS: `http://localhost:*`, `http://127.0.0.1:*` (모든 포트 허용)
- Content-Type: `application/json`
- 에러 응답 포맷은 아직 통일 안 됨 — 현재는 Spring 기본 에러 형식(500/400 등)으로 내려감

---

## 1. Google 로그인

```
POST /api/auth/google
```

- 인증: 불필요
- 프론트에서 Google Identity Services로 발급받은 ID 토큰(`credential`)을 그대로 전달

**Request Body**
```json
{
  "idToken": "<Google이 발급한 credential 문자열>"
}
```

**Response `200 OK`**
```json
{
  "memberId": 1,
  "email": "example@gmail.com",
  "name": "홍길동"
}
```

성공 시 응답과 함께 `Set-Cookie`로 세션이 발급됩니다. 이후 요청부터는 이 쿠키가 자동으로 전송되어 로그인 상태가 유지됩니다.

**예시 코드**
```js
const res = await fetch("http://localhost:8080/api/auth/google", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({ idToken: response.credential }),
});
const data = await res.json();
```

---

## 2. 로그아웃

```
POST /api/auth/logout
```

- 인증: 불필요 (로그인 안 된 상태로 호출해도 에러 없이 그냥 `200 OK`)
- 요청/응답 바디 없음
- 서버에 저장된 세션을 무효화함 — 호출 이후로는 이전에 발급된 `JSESSIONID` 쿠키가 더 이상 로그인 상태로 인식되지 않음

**예시 코드**
```js
await fetch("http://localhost:8080/api/auth/logout", {
  method: "POST",
  credentials: "include",
});
```

---

## 3. 내 계정 타입 조회

```
GET /api/auth/me
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- 로그인한 사용자가 **일반 회원(`MEMBER`)인지, 가게를 등록한 오너(`RESTAURANT`)인지** 판단해서 내려줌
- 별도의 "레스토랑 계정"이 있는 게 아니라, 로그인한 회원이 레스토랑을 하나라도 등록(4번 API)했으면 `RESTAURANT`, 아니면 `MEMBER`로 내려옴

**Response `200 OK`**
```json
{
  "memberId": 1,
  "type": "MEMBER"
}
```
`type`은 `"MEMBER"` 또는 `"RESTAURANT"` 둘 중 하나입니다.

**예시 코드**
```js
const res = await fetch("http://localhost:8080/api/auth/me", {
  method: "GET",
  credentials: "include",
});
const data = await res.json();
```

---

## 4. 레스토랑 등록

```
POST /api/restaurants
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- `ownerId`는 프론트에서 보낼 필요 없음 — 서버가 로그인 세션에서 자동으로 채움

**Request Body**
```json
{
  "name": "가게 이름",
  "location": "서울시 강남구 ...",
  "openTime": "09:00:00",
  "closeTime": "22:00:00"
}
```
`openTime`, `closeTime`은 `"HH:mm:ss"` 형식의 문자열입니다.

**Response `200 OK`**
```json
{
  "id": 1,
  "name": "가게 이름",
  "location": "서울시 강남구 ...",
  "openTime": "09:00:00",
  "closeTime": "22:00:00",
  "ownerId": 1
}
```

**예시 코드**
```js
const res = await fetch("http://localhost:8080/api/restaurants", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({
    name: "가게 이름",
    location: "서울시 강남구 ...",
    openTime: "09:00:00",
    closeTime: "22:00:00",
  }),
});
const data = await res.json();
```

---

## 5. 음식 게시물 등록

```
POST /api/foods
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- **`multipart/form-data`** 요청입니다 (JSON 아님) — 이미지 파일을 같이 보내야 하기 때문
- 로그인한 사용자가 **소유하지 않은 가게(`restaurantId`)로 등록하면 에러**가 납니다 (본인 가게에만 등록 가능)
- `closingTime`은 프론트에서 안 보냄 — 응답에서 연결된 가게의 `closeTime`을 그대로 내려줌
- 가격은 `price`가 아니라 **정가(`originalPrice`)와 할인율(`discountRate`)을 따로 저장**합니다. 최종 판매가(`discountedPrice`)는 서버가 `originalPrice * (100 - discountRate) / 100`으로 계산해서 응답에만 내려줍니다 (DB엔 저장 안 됨)
- `category`는 마일리지 적립률과 직결됩니다 (구매 등록 API 참고). 아래 5개 값 중 하나여야 하며, 잘못된 값이면 에러가 납니다: `MEAT`, `VEGE`, `BAKERY`, `PROCESSED`, `DEFAULT`

**Request (form fields)**

| 필드 | 타입 | 설명 |
|---|---|---|
| `restaurantId` | number | 게시물을 등록할 가게 ID |
| `title` | string | 게시물 제목 |
| `originalPrice` | number | 정가 |
| `discountRate` | number | 할인율 (0~100 사이 정수, %) |
| `description` | string | 음식 설명 |
| `category` | string | 카테고리. `MEAT` \| `VEGE` \| `BAKERY` \| `PROCESSED` \| `DEFAULT` |
| `image` | file | 이미지 파일 |

**Response `200 OK`**
```json
{
  "id": 1,
  "title": "마감 할인 도시락",
  "imageUrl": "/images/3f2504e0-...jpg",
  "originalPrice": 5000,
  "discountRate": 30,
  "discountedPrice": 3500,
  "description": "오늘 만든 도시락 마감세일합니다",
  "category": "MEAT",
  "sold": false,
  "restaurantId": 1,
  "closingTime": "22:00:00"
}
```
`imageUrl`은 상대 경로로 내려오므로, 프론트에서 이미지를 띄울 땐 `http://localhost:8080` + `imageUrl`로 접근하면 됩니다. (예: `http://localhost:8080/images/3f2504e0-...jpg`)

**예시 코드**
```js
const formData = new FormData();
formData.append("restaurantId", 1);
formData.append("title", "마감 할인 도시락");
formData.append("originalPrice", 5000);
formData.append("discountRate", 30);
formData.append("description", "오늘 만든 도시락 마감세일합니다");
formData.append("category", "MEAT");
formData.append("image", fileInput.files[0]); // <input type="file"> 에서 가져온 파일

const res = await fetch("http://localhost:8080/api/foods", {
  method: "POST",
  credentials: "include",
  body: formData, // Content-Type 헤더는 직접 설정하지 말 것 (브라우저가 boundary 포함해서 자동 설정)
});
const data = await res.json();
```

> **주의**: `FormData`를 body로 보낼 땐 `Content-Type` 헤더를 직접 지정하면 안 됩니다. 브라우저가 자동으로 `multipart/form-data; boundary=...` 형식을 채워주는데, 수동으로 지정하면 boundary가 빠져서 요청이 깨집니다.

---

## 6. 음식 게시물 전체 조회

```
GET /api/foods
```

- 인증: 불필요 (로그인 안 해도 둘러볼 수 있음)
- 최신 등록순(`id` 내림차순)으로 전체 목록 반환
- **이미 판매된(`sold: true`) 게시물은 목록에서 제외**됩니다. 즉 이 API 응답에 나오는 게시물은 전부 `sold: false`이며, 필드 자체도 항상 `false`로 내려옴 (판매완료 항목을 보려면 상세 조회 API 사용)

**Response `200 OK`**
```json
[
  {
    "id": 2,
    "title": "마감 할인 샌드위치",
    "imageUrl": "/images/9c858901-...jpg",
    "originalPrice": 4000,
    "discountRate": 50,
    "discountedPrice": 2000,
    "description": "오늘 마감 세일합니다",
    "category": "BAKERY",
    "sold": false,
    "restaurantId": 1,
    "closingTime": "22:00:00"
  },
  {
    "id": 1,
    "title": "마감 할인 도시락",
    "imageUrl": "/images/3f2504e0-...jpg",
    "originalPrice": 5000,
    "discountRate": 30,
    "discountedPrice": 3500,
    "description": "오늘 만든 도시락 마감세일합니다",
    "category": "MEAT",
    "sold": false,
    "restaurantId": 1,
    "closingTime": "22:00:00"
  }
]
```

**예시 코드**
```js
const res = await fetch("http://localhost:8080/api/foods", {
  method: "GET",
  credentials: "include",
});
const data = await res.json();
```

---

## 7. 음식 게시물 상세 조회

```
GET /api/foods/{id}
```

- 인증: 불필요
- 존재하지 않는 `id`로 요청하면 현재는 에러 응답 포맷이 정리 안 돼 있어 500으로 내려옴 (추후 정리 예정)
- 목록 조회(6번)와 달리 **판매완료된 게시물도 조회 가능**합니다 — 실제 `sold` 값(`true`/`false`)이 그대로 내려옴

**Response `200 OK`**
```json
{
  "id": 1,
  "title": "마감 할인 도시락",
  "imageUrl": "/images/3f2504e0-...jpg",
  "originalPrice": 5000,
  "discountRate": 30,
  "discountedPrice": 3500,
  "description": "오늘 만든 도시락 마감세일합니다",
  "category": "MEAT",
  "sold": false,
  "restaurantId": 1,
  "closingTime": "22:00:00"
}
```

**예시 코드**
```js
const res = await fetch(`http://localhost:8080/api/foods/${foodId}`, {
  method: "GET",
  credentials: "include",
});
const data = await res.json();
```

---

## 8. 구매 등록

```
POST /api/purchases
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- `buyerId`는 프론트에서 보낼 필요 없음 — 서버가 로그인 세션에서 자동으로 채움
- `price`는 프론트에서 보낼 필요 없음 — 구매 시점의 `discountedPrice`(정가 × (100 - 할인율) / 100)를 서버가 스냅샷으로 저장. 이후 게시물 가격이 바뀌어도 이미 한 구매 기록의 가격은 바뀌지 않음
- `pickupTime`은 해당 음식이 속한 가게의 영업 시간(`openTime`~`closeTime`) 이내여야 함. 벗어나면 에러
- **구매가 완료되면 마일리지가 자동으로 적립됩니다.** 적립액은 `price × 카테고리별 적립률`이며 소수점은 버림(내림) 처리됩니다. 적립된 마일리지는 구매자의 마일리지 잔액에 즉시 누적되고, 응답의 `earnedMileage`로 이번 구매에서 얼마가 적립됐는지 내려줍니다.

카테고리별 적립률:

| category | 적립률 |
|---|---|
| `MEAT` | 2.5% |
| `VEGE` | 1.2% |
| `BAKERY` | 1.5% |
| `PROCESSED` | 1.8% |
| `DEFAULT` | 1.65% |

**Request Body**
```json
{
  "foodId": 1,
  "pickupTime": "19:30:00"
}
```
`pickupTime`은 `"HH:mm:ss"` 형식의 문자열입니다.

**Response `200 OK`**
```json
{
  "id": 1,
  "foodId": 1,
  "foodTitle": "마감 할인 도시락",
  "price": 3500,
  "pickupTime": "19:30:00",
  "purchasedAt": "2026-08-13T18:02:11.123",
  "earnedMileage": 87
}
```
> 위 예시는 `MEAT` 카테고리(2.5%) 기준: 3500 × 0.025 = 87.5 → 내림 → 87
- **이미 판매된(`sold: true`) 게시물을 구매하려 하면 에러**가 납니다 (한 게시물은 한 번만 구매 가능)

**예시 코드**
```js
const res = await fetch("http://localhost:8080/api/purchases", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({
    foodId: 1,
    pickupTime: "19:30:00",
  }),
});
const data = await res.json();
```

---

## 9. 마일리지 잔액 조회

```
GET /api/members/{memberId}/mileage
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- **본인의 잔액만 조회 가능**합니다. 로그인한 사용자와 `{memberId}`가 다르면 에러가 납니다 (다른 사람 잔액 조회 불가)

**Response `200 OK`**
```json
{
  "memberId": 1,
  "balance": 312
}
```

**예시 코드**
```js
const res = await fetch(`http://localhost:8080/api/members/${memberId}/mileage`, {
  method: "GET",
  credentials: "include",
});
const data = await res.json();
```
