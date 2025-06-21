# 🎟️ BookMyShow Backend – Auth Service

This is the **Authentication Module** of the `BookMyShow` backend system, implemented using **Java + Spring Boot** with a clean, scalable design built for real-world use cases.

## 🔐 Overview

The Auth module supports secure, role-based authentication and signup for three distinct actors:

- 👤 **User** – end customer booking tickets
- 🏢 **Vendor** – manages shows, venues
- 🛡️ **Admin** – creates other admins, controls by zone

Rather than stuffing all roles into a single table, this service separates concerns cleanly using:

- ✅ Separate entities: `User`, `VendorProfile`, `AdminProfile`
- ✅ Central login mapping: `LoginCredential` table with `userType`, `entityId`
- ✅ JWT-based authentication with stateless, token-secured APIs

---

## 🧭 Architecture

```
Client → AuthController → AuthService → Role Repositories
                                ↳ LoginCredentialRepository
                                ↳ JwtService
```

**Signup Flow (User/Vendor):**
- Save respective entity
- Add to `LoginCredential`

**Login Flow:**
- Fetch user by username
- Use `userType` to resolve repo
- Validate password → issue JWT

**Admin Creation Flow:**
- Admins are created by other admins only
- Zone validation logic to prevent cross-region creation

---

## 📂 Module Structure

```
📁 authservice
 ┣ 📁 controller         → REST endpoints for login and signup
 ┣ 📁 dto                → DTOs with validation annotations
 ┣ 📁 model              → Entity classes: User, Vendor, Admin, LoginCredential
 ┣ 📁 repository         → JPA repositories
 ┣ 📁 service
 ┃ ┣ 📄 AuthService      → Interface
 ┃ ┣ 📄 AuthServiceImpl  → Core login/signup logic
 ┃ ┗ 📄 JwtService       → JWT utility
 ┣ 📄 SecurityConfig     → JWT filters, security rules
 ┗ 📄 Application.java
```

---

## 🔧 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security (JWT)
- PostgreSQL
- Maven
- JPA/Hibernate

---

## 🔄 APIs

### 🔐 Login
```http
POST /auth/login
```
- Body:
```json
{
  "username": "john@bms.com",
  "password": "secure"
}
```
- Returns: `JWT Token` + userType

---

### 🧾 User Signup
```http
POST /auth/signup/user
```
- Body:
```json
{
  "name": "John",
  "email": "john@bms.com",
  "password": "secure"
}
```

---

### 🏢 Vendor Signup
```http
POST /auth/signup/vendor
```
- Body:
```json
{
  "vendorName": "PVR Cinemas",
  "email": "vendor@pvr.com",
  "password": "secure"
}
```

---

### 🛡️ Create Admin (Restricted)
```http
POST /admin/create
```
- Requires: Admin JWT token
- Validates zone of creator and new admin

---

## 🔒 Security

- JWT-based stateless authentication
- Passwords hashed with BCrypt
- Clean RBAC by splitting login/auth from entity roles
- Admin creation restricted and zone-validated

---

## 📌 Roadmap

- [x] JWT Authentication
- [x] Zone-based Admin Creation
- [ ] MFA support
- [ ] Token refresh endpoint
- [ ] Login attempt tracking / IP logging

---

## 👨‍💻 Author

**Salman Shaik**  
[GitHub Profile](https://github.com/salmanshaikssk007)

---

## 📜 License

MIT License