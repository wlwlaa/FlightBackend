# FlightBooking (Microservices Backend) — Java Spring Boot + SwiftUI-ready API

Учебный проект: микросервисный бэкенд для приложения бронирования авиаперелётов.  
Сценарий клиента: **поиск → детали оффера → price-check → бронирование → мои поездки → отмена → mock-оплата → confirm**.  
Формат дат: **ISO-8601 (UTC)**.


## Архитектура

Сервисы (Spring Boot 3, Java 21):

- **identity-service** (порт `8081`)  
  Auth: login/refresh/logout + guest token + `/me`
- **catalog-service** (порт `8082`)  
  Locations autocomplete (IATA/город/аэропорт)
- **offers-service** (порт `8083`)  
  Search offers + offer details + price-check + TTL + пагинация cursor
- **booking-service** (порт `8084`)  
  Bookings CRUD (create/list/get/cancel/confirm) + mock payment intent  
  Поддержка **user** и **guest** режимов

Хранилища:
- Postgres **16** (отдельная БД на сервис)
- Flyway migrations (включая `org.flywaydb:flyway-database-postgresql`)
- JPA/Hibernate `ddl-auto=validate`


## Быстрый старт

### Требования
- Docker + Docker Compose

### Запуск
```bash
docker compose up --build
````

### Проверка статуса

```bash
docker compose ps
```


## Swagger UI (по сервисам)

* Identity: `http://localhost:8081/v1/swagger-ui.html`
* Catalog:  `http://localhost:8082/v1/swagger-ui.html`
* Offers:   `http://localhost:8083/v1/swagger-ui.html`
* Booking:  `http://localhost:8084/v1/swagger-ui.html`


## Режимы авторизации (User / Guest)

### User

1. Логин:

```bash
curl -s -X POST http://localhost:8081/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@demo.com","password":"demo12345"}'
```

2. Используй access token:

```bash
curl -s http://localhost:8081/v1/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Guest (логин не нужен)

Получить guest token:

```bash
curl -s -X POST http://localhost:8081/v1/auth/guest \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"ios-device-123"}'
```

> Гости в БД **не хранятся**. Guest subject детерминированный.

## Важные заголовки

* `X-Trace-Id` — опциональный trace id для корреляции логов (если не передан, сервис генерирует сам)
* `Idempotency-Key` — опционально для безопасных повторов write-запросов
* `X-Device-Id` — для guest-режима (особенно в booking-service)


## Основной API flow (пример)

### 1) Autocomplete локаций

```bash
curl "http://localhost:8082/v1/locations/autocomplete?query=hel&limit=10"
```

### 2) Поиск офферов

```bash
curl -s -X POST http://localhost:8083/v1/flights/search \
  -H "Content-Type: application/json" \
  -d '{
    "fromIATA":"HEL",
    "toIATA":"BCN",
    "departDate":"2026-01-10T00:00:00Z",
    "returnDate":null,
    "adults":1,
    "cabin":"economy"
  }'
```

### 3) Детали оффера

```bash
curl -s "http://localhost:8083/v1/offers/<offerId>"
```

### 4) Price-check (перед бронированием)

```bash
curl -s -X POST "http://localhost:8083/v1/offers/<offerId>/price-check"
```

### 5) Создать бронирование (Guest через X-Device-Id)

```bash
curl -s -X POST http://localhost:8084/v1/bookings \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: ios-device-123" \
  -H "Idempotency-Key: demo-create-001" \
  -d '{
    "offerId":"<offerId>",
    "contact":{"email":"a@b.com","phone":"+358..."},
    "passengers":[
      {
        "firstName":"Andrei",
        "lastName":"Gatsko",
        "birthDate":"2005-03-01T00:00:00Z",
        "documentNumber":"AB123456"
      }
    ]
  }'
```

> booking-service сам делает price-check через offers-service.

### 6) Создать mock payment intent

```bash
curl -s -X POST http://localhost:8084/v1/payments/intent \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: ios-device-123" \
  -H "Idempotency-Key: demo-pay-001" \
  -d '{"bookingId":"<bookingId>"}'
```

### 7) Confirm booking

```bash
curl -s -X POST http://localhost:8084/v1/bookings/<bookingId>/confirm \
  -H "X-Device-Id: ios-device-123" \
  -H "Idempotency-Key: demo-confirm-001"
```

### 8) My Trips (список бронирований)

```bash
curl -s "http://localhost:8084/v1/bookings?limit=20" \
  -H "X-Device-Id: ios-device-123"
```

### 9) Cancel

```bash
curl -s -X POST http://localhost:8084/v1/bookings/<bookingId>/cancel \
  -H "X-Device-Id: ios-device-123" \
  -H "Idempotency-Key: demo-cancel-001"
```


## База данных и миграции

* Flyway применяет миграции при старте сервисов (`db/migration`)
* Hibernate работает в режиме `validate` (схема обязана совпадать с Entity)

### Postgres 16 + Flyway

В каждом сервисе, который использует Flyway, подключено:

* `org.flywaydb:flyway-core`
* `runtimeOnly("org.flywaydb:flyway-database-postgresql")`
* `runtimeOnly("org.postgresql:postgresql")`


## Структура репозитория

```
/
  docker-compose.yml
  /services
    /identity-service
    /catalog-service
    /offers-service
    /booking-service
```


## Notes / учебные упрощения

* Оплата — **mock** (intent + confirm без настоящего провайдера)
* Поиск и офферы — **mock provider**, но с TTL/price-check как в “реальном мире”
* Guest режим — без хранения guest в БД (subject детерминированный от deviceId)
* Уведомления (push/email) не реализованы


## Лицензия

Код распространяется по лицензии MiT