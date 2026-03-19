# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BookMyShowApplicationTests

# Build the fat JAR and run it
java -jar target/BookMyShow-0.0.1-SNAPSHOT.jar
```

Prerequisites: PostgreSQL on `localhost:5432` (db: `BookMyShow`, user: `postgres`, pass: `password`) and Redis on `localhost:6379` must be running. The app starts on port `8080`.

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` when running.

## Architecture

This is a **single-module Spring Boot monolith** currently implementing only the Auth module. All source lives under `com.example.BookMyShow` with two top-level sub-packages: `auth/` and `config/`.

### Central Login Dispatch Pattern

The most important architectural concept is the `LoginUserCredRoleCheck` table (table: `login_user_cred_role_check`). Rather than a single `users` table with roles, each actor type has its **own entity table** (`users`, `vendor_profiles`, `admin_profiles`), and `LoginUserCredRoleCheck` acts as the central login lookup:

```
LoginUserCredRoleCheck { username (unique), role, entity_id }
         │
         ├─ ROLE_USER    → users.id
         ├─ ROLE_VENDOR  → vendor_profiles.id
         └─ ROLE_ADMIN   → admin_profiles.id
```

Login flow: lookup by username → get role + entity_id → switch on role → fetch entity from its table → verify BCrypt password → issue JWT.

Signup always writes two rows: one in the actor's own table, one in `LoginUserCredRoleCheck`.

### JWT — No UserDetailsService

Spring Security has **no `UserDetailsService`**. The `JwtAuthenticationFilter` (`config/`) reads the `Authorization: Bearer` header, validates the token via `JwtUtils`, extracts username + role claim directly, and constructs a `UsernamePasswordAuthenticationToken` with a single `SimpleGrantedAuthority`. There is no DB call on every request.

### Admin Zone Hierarchy

`AdminZone` is a self-referencing tree (PK: `zoneCode` String, e.g. `"WORLD"`, `"US"`, `"US-CA"`, `"US-CA-LA"`). Level 0 = root, 1 = country, 2 = state, 3 = city. `AdminProfile` belongs to one `AdminZone`.

`AdminServiceImpl.createAdmin()` enforces that the authenticated admin can only create admins **exactly one level below** their own zone, and only within their branch (checked via `targetZone.getZoneCode().startsWith(creatorZone.getZoneCode())`).

### Dev Seeding

`DevDataSeeder` (`auth/devseed/`) is a `CommandLineRunner` active only on `@Profile("dev")`. It seeds root + state + city admins on startup **if the admin table is empty**. It expects `AdminZone` rows to already exist in the DB (zones are not seeded in code — they must be inserted manually or via a migration).

## Known Issues

- **`bussinessName` / `bussinessLicenseNumber`** — intentional typo (double 's') present in `VendorProfile` entity, `VendorRepository`, `VendorSignUpRequest`, and `AuthServiceImpl`. Do not "fix" the spelling without migrating the DB column.
- **`DevDataSeeder` login username inconsistency** — seeder uses **email** as the `LoginUserCredRoleCheck.username` for admins; `AdminServiceImpl.createAdmin()` uses **adminName**. Keep this in mind when testing seeded admin login.
- **No global exception handler** — `RuntimeException`s return as unformatted 500s. A `@ControllerAdvice` is on the roadmap.
- **Roadmap items not yet implemented**: token refresh endpoint, MFA, login attempt tracking.
