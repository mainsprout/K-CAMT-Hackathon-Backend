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
