# Backend Frontend Handoff

## Base URL
- Local: `http://localhost:8080`

## Auth
- Login: `POST /api/auth/login`
- Register: `POST /api/auth/register`
- Header: `Authorization: Bearer <access_token>`

## Response Contract
- Success: `ApiResponse<T>`
```json
{
  "success": true,
  "message": "Success",
  "data": {}
}
```
- Error: `ErrorResponse`
```json
{
  "timestamp": "2026-04-02T21:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Validation failed",
  "path": "/api/user/profile",
  "validationErrors": {
    "fullName": "Full name is required"
  }
}
```

## CORS
- Config key: `CORS_ALLOWED_ORIGINS`
- Default: `http://localhost:5173,http://localhost:3000`

## Main FE Endpoints
- Public:
  - `GET /api/categories`
  - `GET /api/categories/{slug}`
  - `GET /api/products`
  - `GET /api/products/{slug}`
  - `GET /api/products/{productSlug}/variants`
  - `GET /api/products/{productSlug}/images`
- User:
  - `GET /api/user/profile`
  - `PUT /api/user/profile`
  - `GET /api/user/cart`
  - `POST /api/user/cart/items`
  - `PUT /api/user/cart/items/{itemId}`
  - `DELETE /api/user/cart/items/{itemId}`
  - `DELETE /api/user/cart/items`
  - `POST /api/user/orders`
  - `GET /api/user/orders`
  - `GET /api/user/orders/{orderId}`
- Admin:
  - `/api/admin/categories/**`
  - `/api/admin/products/**`
  - `/api/admin/orders/**`

## Pagination
- Query params: `page`, `size`, `sortBy`, `sortDir`
- Size is clamped to max `100` in backend.

## Suggested FE Flow
1. Login and store access token.
2. Load categories/products for home page.
3. Product detail: call product + variants + images.
4. Cart page: read cart then add/update/remove item.
5. Checkout: call create order from cart.
6. My Orders: list and detail.
