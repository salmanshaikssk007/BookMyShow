# BookMyShow – Backend

A Spring Boot backend clone of BookMyShow, implementing role-based authentication and a full movie booking domain.

---

## Tech Stack

- **Java 17**, Spring Boot 3.5.0, Maven
- **Spring Security** – stateless JWT (HS256, 24h expiry, BCrypt passwords)
- **PostgreSQL** – primary database (port 5432, db: `BookMyShow`)
- **Redis** – port 6379 (wired, used for JTI cache / future caching)
- **OpenAPI / Swagger** – available at `http://localhost:8080/swagger-ui/index.html`

---

## Prerequisites

| Service    | Default                                                                  |
|------------|--------------------------------------------------------------------------|
| PostgreSQL | `localhost:5432`, db: `BookMyShow`, user: `postgres`, pass: `password`   |
| Redis      | `localhost:6379`                                                         |

---

## Build & Run

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run (dev profile — seeds test data on startup)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test
```

App starts on **port 8080**.

---

## Architecture

### Central Login Dispatch

Rather than a single `users` table with roles, each actor type has its own entity table. `LoginUserCredRoleCheck` is the central login lookup:

```
LoginUserCredRoleCheck { username (unique), role, entity_id }
         |
         |-- ROLE_USER    --> users.id
         |-- ROLE_VENDOR  --> vendor_profiles.id
         └-- ROLE_ADMIN   --> admin_profiles.id
```

**Login flow:** lookup by username → get role + entity_id → fetch entity from its table → verify BCrypt password → issue JWT.

**Signup** always writes two rows: one in the actor's own table, one in `LoginUserCredRoleCheck`.

### JWT – No UserDetailsService

The `JwtAuthenticationFilter` reads the `Authorization: Bearer` header, validates via `JwtUtils`, extracts username + role claim, and constructs a `UsernamePasswordAuthenticationToken` with a `SimpleGrantedAuthority`. No DB call on every request.

### Admin Zone Hierarchy

`AdminZone` is a self-referencing tree (PK: `zoneCode`, e.g. `"WORLD"`, `"US"`, `"US-CA"`, `"US-CA-LA"`). Level 0 = root, 1 = country, 2 = state, 3 = city. An admin can only create admins **one level below** their own zone and within their branch.

---

## Module Structure

```
com.example.BookMyShow
├── auth/
│   ├── controller/        AuthController, AdminController
│   ├── dto/               SignUp/Login/Admin request + JWTResponse
│   ├── entity/            User, VendorProfile, AdminProfile, AdminZone, LoginUserCredRoleCheck, Role
│   ├── repository/        Per-entity JPA repositories
│   ├── service/           AuthService, AdminService (+ Impl)
│   ├── util/              JwtUtils
│   └── devseed/           DevDataSeeder (@Profile="dev")
├── booking/
│   ├── entity/            Movie, City, Theater, Screen, Seat, Show, ShowSeat, Booking, BookingSeat
│   ├── repository/        JPA repositories (ShowSeatRepository has availability query)
│   └── devseed/           BookingDataSeeder (@Profile="dev")
└── config/
    ├── SecurityConfig.java
    └── JwtAuthenticationFilter.java
```

---

## Auth APIs

### POST `/api/auth/user/signup`
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret"
}
```

### POST `/api/auth/vendor/signup`
```json
{
  "vendorName": "PVR Cinemas",
  "email": "pvr@example.com",
  "password": "secret",
  "phoneNumber": "9999999999",
  "bussinessName": "PVR Ltd",
  "bussinessLicenseNumber": "LIC123"
}
```

### POST `/api/auth/login`
```json
{
  "username": "john",
  "password": "secret"
}
```
Returns: `{ "token": "<JWT>", "role": "ROLE_USER" }`

### POST `/api/v1/admin/create` *(requires `ROLE_ADMIN` JWT)*
```json
{
  "adminName": "state-admin",
  "email": "admin@example.com",
  "password": "secret",
  "phoneNumber": "9999999999",
  "zoneCode": "US-CA"
}
```

---

## Booking Domain (entities ready, APIs in progress)

| Entity        | Purpose                                                                             |
|---------------|-------------------------------------------------------------------------------------|
| `City`        | Geographic grouping of theaters                                                     |
| `Theater`     | Belongs to a city                                                                   |
| `Screen`      | Belongs to a theater; type: STANDARD / IMAX / FOUR_DX                              |
| `Seat`        | Physical seat in a screen; type: REGULAR / PREMIUM / RECLINER / WHEELCHAIR_ACCESSIBLE |
| `Movie`       | Title, language, genre, duration                                                    |
| `Show`        | A movie on a screen at a time; status: SCHEDULED / ONGOING / CANCELLED / etc.      |
| `ShowSeat`    | Real-time seat availability per show (optimistic lock + TTL for temp locks)         |
| `Booking`     | A user's order for a show                                                           |
| `BookingSeat` | Individual seat line item in a booking                                              |

### Planned Booking APIs

| Method | Endpoint                              | Description                        |
|--------|---------------------------------------|------------------------------------|
| `GET`  | `/api/booking/cities`                 | List cities                        |
| `GET`  | `/api/booking/cities/{cityId}/movies` | Movies showing in a city           |
| `GET`  | `/api/booking/movies/{movieId}/shows` | Shows for a movie                  |
| `GET`  | `/api/booking/shows/{showId}/seats`   | Seat availability                  |
| `POST` | `/api/booking/lock-seats`             | Temporarily lock seats (TTL)       |
| `POST` | `/api/booking/confirm`                | Confirm booking                    |
| `GET`  | `/api/booking/my-bookings`            | User's booking history             |
| `POST` | `/api/booking/{bookingId}/cancel`     | Cancel a booking                   |
| `POST` | `/api/v1/vendor/shows`                | Create a show *(ROLE_VENDOR)*      |

---

## Dev Seed Data (`--spring.profiles.active=dev`)

**Auth seeder:**
- Root admin: `root@bms.com` / `rootpass` (zone: WORLD)
- State admins: CA, NY, TX, FL
- City admins: NYC, LA, Seattle

**Booking seeder:**
- 2 cities (Mumbai, Delhi), 10 theaters, 5 screens
- 100 seats (20/screen — rows A/B: REGULAR, C: PREMIUM, D: RECLINER)
- 2 movies: Inception, RRR
- 3 shows with all seats initialized as AVAILABLE

---

## Known Issues

- `bussinessName` / `bussinessLicenseNumber` — intentional double-'s' typo in `VendorProfile` (matches DB column, do not rename without migration)
- Dev seeder logs admins in with **email** as username; `AdminServiceImpl` saves **adminName** — inconsistency when testing seeded admin login
- No global exception handler — unhandled `RuntimeException`s return as 500s

---

## Roadmap

- [x] JWT authentication (access + refresh tokens)
- [x] Zone-based admin hierarchy
- [x] Booking domain entities + dev seed
- [ ] Booking service + controllers
- [ ] Seat lock/confirm flow with optimistic concurrency
- [ ] Vendor show management APIs
- [ ] Global exception handler (`@ControllerAdvice`)
- [ ] MFA support
- [ ] Login attempt tracking

---

## Author

**Salman Shaik**
[GitHub](https://github.com/salmanshaikssk007)

---

MIT License
