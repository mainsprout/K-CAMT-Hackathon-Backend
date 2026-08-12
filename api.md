# API 문서

## 공통 사항

- Base URL: `http://localhost:8080`
- 인증 방식: 세션 쿠키(`JSESSIONID`) 기반. **모든 요청에 `credentials: "include"` 필수**
- 인증이 필요한 API는 별도 토큰/식별자를 body나 header에 넣을 필요 없음 — 쿠키만 있으면 서버가 로그인한 사용자를 알아서 식별함
- CORS: `http://localhost:3000` 오리진만 허용됨 (프론트 포트가 바뀌면 백엔드에 알려주세요)
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

## 2. 레스토랑 등록

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

## 3. 음식 게시물 등록

```
POST /api/foods
```

- 인증: **필요** (로그인 세션 쿠키). 로그인 안 된 상태로 호출하면 `401 Unauthorized`
- **`multipart/form-data`** 요청입니다 (JSON 아님) — 이미지 파일을 같이 보내야 하기 때문
- 로그인한 사용자가 **소유하지 않은 가게(`restaurantId`)로 등록하면 에러**가 납니다 (본인 가게에만 등록 가능)
- `closingTime`은 프론트에서 안 보냄 — 응답에서 연결된 가게의 `closeTime`을 그대로 내려줌

**Request (form fields)**

| 필드 | 타입 | 설명 |
|---|---|---|
| `restaurantId` | number | 게시물을 등록할 가게 ID |
| `title` | string | 게시물 제목 |
| `price` | number | 가격 |
| `description` | string | 음식 설명 |
| `image` | file | 이미지 파일 |

**Response `200 OK`**
```json
{
  "id": 1,
  "title": "마감 할인 도시락",
  "imageUrl": "/images/3f2504e0-...jpg",
  "price": 3000,
  "description": "오늘 만든 도시락 마감세일합니다",
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
formData.append("price", 3000);
formData.append("description", "오늘 만든 도시락 마감세일합니다");
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

## 4. 음식 게시물 전체 조회

```
GET /api/foods
```

- 인증: 불필요 (로그인 안 해도 둘러볼 수 있음)
- 최신 등록순(`id` 내림차순)으로 전체 목록 반환

**Response `200 OK`**
```json
[
  {
    "id": 2,
    "title": "마감 할인 샌드위치",
    "imageUrl": "/images/9c858901-...jpg",
    "price": 2000,
    "description": "오늘 마감 세일합니다",
    "restaurantId": 1,
    "closingTime": "22:00:00"
  },
  {
    "id": 1,
    "title": "마감 할인 도시락",
    "imageUrl": "/images/3f2504e0-...jpg",
    "price": 3000,
    "description": "오늘 만든 도시락 마감세일합니다",
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

## 5. 음식 게시물 상세 조회

```
GET /api/foods/{id}
```

- 인증: 불필요
- 존재하지 않는 `id`로 요청하면 현재는 에러 응답 포맷이 정리 안 돼 있어 500으로 내려옴 (추후 정리 예정)

**Response `200 OK`**
```json
{
  "id": 1,
  "title": "마감 할인 도시락",
  "imageUrl": "/images/3f2504e0-...jpg",
  "price": 3000,
  "description": "오늘 만든 도시락 마감세일합니다",
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
