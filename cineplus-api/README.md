# CinePlus API (MySQL + Express)

API base con MySQL y docker-compose. Soporta: películas, funciones, asientos, **bloqueo temporal (TTL)**, órdenes y tickets con QR simulado.

## Requisitos
- Node.js 18+
- Docker (para levantar MySQL) o una instancia MySQL propia

## 1) Levantar MySQL (Docker)
```bash
docker compose up -d
# DB: cineplusdb | User: cineplus | Pass: cineplus | Port: 3306
```

## 2) Configurar el proyecto
```bash
cp .env.example .env
npm install
npm run bootstrap   # aplica schema.sql
npm run seed        # inserta datos demo
npm run dev         # http://localhost:3000
```
Usuario demo: `demo@cineplus.com` / `demo123`

## Endpoints clave
- `POST /api/auth/register | /login`
- `GET  /api/movies`
- `GET  /api/showtimes?movieId=`
- `GET  /api/seats?showtimeId=`  → `FREE | LOCKED | SOLD`
- `POST /api/locks` (JWT)        → `{ showtimeId, seatId }`
- `DELETE /api/locks/:id` (JWT)
- `POST /api/orders` (JWT)       → `{ showtimeId, seatIds: [] }`
- `GET  /api/orders/mine` (JWT)

> El TTL de *locks* se controla con `LOCK_TTL_SECONDS` (default 180s).

## Notas
- El backend usa `mysql2/promise` y *pooling*.
- `schema.sql` es idempotente (CREATE IF NOT EXISTS) y se corre con `npm run bootstrap`.
- La colección Postman está en `postman/CinePlus-MySQL.postman_collection.json`.
