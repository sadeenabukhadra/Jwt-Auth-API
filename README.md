# JWT Auth API

## Overview

JWT Auth API is a RESTful authentication service built with Java and Spring Boot. It provides user registration, login, token-based authentication, refresh token management, and protected endpoints.

The project demonstrates how to implement stateless authentication using JSON Web Tokens (JWT), integrate Spring Security, and persist user and refresh token data in PostgreSQL.

## Features

- **User Registration:** Register new users with a username, email, and password.
- **User Authentication:** Authenticate users using email and password.
- **JWT Access Tokens:** Generate signed tokens for authenticated requests.
- **Refresh Tokens:** Issue and validate refresh tokens to obtain new access tokens.
- **Logout:** Remove stored refresh tokens.
- **Password Hashing:** Securely hash passwords using BCrypt.
- **Protected Endpoints:** Restrict access to authenticated users.
- **Database Persistence:** Store user accounts and refresh token records using Spring Data JPA and PostgreSQL.
- **Exception Handling:** Provide centralized handling for application exceptions.

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.6
- **Security:** Spring Security
- **Authentication:** JSON Web Tokens (JWT)
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA and Hibernate
- **Build Tool:** Maven
- **Validation:** Spring Boot Validation

## Architecture

The application follows a layered structure that separates HTTP requests, business logic, security, and data persistence.

```text
src/main/java/com/example/jwt_auth_api/
├── Config/
│   ├── JWTAuthenticationFilter.java
│   └── SecurityConfig.java
├── Controller/
│   └── AuthController.java
├── Exception/
│   ├── CustomException.java
│   └── GlobalExceptionHandler.java
├── Model/
│   ├── User.java
│   └── RefreshToken.java
├── Repository/
│   ├── UserRepository.java
│   └── RefreshTokenRepository.java
├── Service/
│   └── AuthService.java
├── Util/
│   └── JWTUtil.java
└── JwtAuthApiApplication.java
```

## API Endpoints

The authentication endpoints are available under `/api/auth`.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive tokens |
| POST | `/api/auth/refresh` | Generate a new access token |
| POST | `/api/auth/logout` | Remove a stored refresh token |
| GET | `/api/auth/me` | Retrieve the authenticated user's profile |

### Example: User Registration

**Request**

```http
POST /api/auth/signup
Content-Type: application/json
```

```json
{
  "username": "sadeen",
  "email": "sadeen@example.com",
  "password": "your-password"
}
```

### Example: Login

**Request**

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "sadeen@example.com",
  "password": "your-password"
}
```

A successful login returns an access token and a refresh token.

### Example: Accessing a Protected Endpoint

Send the access token in the HTTP Authorization header:

```http
GET /api/auth/me
Authorization: Bearer <access-token>
```

The endpoint returns the authenticated user's username, email, and role.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven (or the included Maven Wrapper)
- PostgreSQL database

### 1. Clone the Repository

```bash
git clone https://github.com/sadeenabukhadra/Jwt-Auth-API.git
cd Jwt-Auth-API
```

### 2. Configure Environment Variables

Configure the database connection and JWT secret before starting the application.

The application uses the following settings in `src/main/resources/application.properties`:

- `spring.datasource.url` — PostgreSQL JDBC connection URL
- `spring.datasource.username` — Database username
- `spring.datasource.password` — Database password, supplied through `DB_PASSWORD`
- `app.jwt.secret` — JWT signing secret, supplied through `JWT_SECRET`

Set `spring.datasource.url` to your actual PostgreSQL connection URL. Keep database credentials and the JWT signing secret outside version control.

For example, in a Linux or macOS terminal:

```bash
export DB_PASSWORD="your-database-password"
export JWT_SECRET="your-long-random-secret-key-at-least-32-characters"
```

Use a securely generated secret in real deployments.

### 3. Run the Application

Using the Maven Wrapper on Linux or macOS:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The application is configured to run on port `8080`.

## Authentication Flow

1. A user registers with their account details.
2. The application hashes the password before storing the user.
3. The user logs in with their email and password.
4. The server issues an access token and a refresh token.
5. The access token is used to authenticate requests to protected endpoints.
6. A valid refresh token can be exchanged for a new access token.
7. Logging out deletes the stored refresh token.

## Learning Outcomes

This project provides practical experience with:

- REST API development using Spring Boot.
- Authentication and authorization with Spring Security.
- JWT generation, parsing, and validation.
- Access and refresh token workflows.
- Password hashing with BCrypt.
- Database integration using PostgreSQL and JPA.
- Layered application architecture and exception handling.

## Security Notes

This project is intended as a learning and development project. Before using it in production, review token revocation and expiration behavior, input validation, CORS and CSRF configuration, rate limiting, and secret management. Ensure database schema management is configured appropriately for the deployment environment.

## Author

**Sadeen Abu Khadra**

- GitHub: [@sadeenabukhadra](https://github.com/sadeenabukhadra)

---

**Topics:** Java · Spring Boot · Spring Security · JWT · REST API · PostgreSQL · Authentication · Backend Development
