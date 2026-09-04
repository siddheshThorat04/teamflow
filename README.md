# Teamflow

A full-stack team collaboration and project management platform — a focused take on Jira + Slack + Trello — built as a deep-dive learning project into backend architecture, clean code, and production patterns.

## Stack

| Layer | Technology |
|---|---|
| Frontend | React + TypeScript |
| Backend | Spring Boot (Java) |
| Database | PostgreSQL |
| Caching / Presence | Redis |
| Messaging / Async | Kafka |
| File Storage | AWS S3 |
| Containerization | Docker |

## Project Status

 Under active development. Currently building the **Users & Authentication** module.

### Completed
- [x] Project scaffolding (feature-first package structure)
- [x] Docker-based PostgreSQL setup
- [x] `User` entity with JPA auditing (`createdAt`/`updatedAt`)
- [x] Registration endpoint (`POST /api/auth/register`) with validation and BCrypt hashing
- [x] Login endpoint (`POST /api/auth/login`) with Spring Security `AuthenticationManager`
- [x] JWT generation and validation (`JwtService`)
- [x] Stateless JWT authentication filter (`JwtAuthFilter`) protecting all non-auth routes

### In Progress / Up Next
- [ ] Role-based authorization (`@PreAuthorize`)
- [ ] Organization / Workspace module
- [ ] Projects module
- [ ] Tasks module (core domain)
- [ ] Real-time comments & notifications (WebSocket)
- [ ] Redis caching + online presence
- [ ] Kafka-based async notifications
- [ ] S3 file attachments
- [ ] Full Docker Compose setup

## Architecture

The backend follows a **feature-first (package-by-feature)** structure rather than layer-first, so each domain's entity, repository, service, controller, and DTOs live together:

com.teamflow.intial
├── auth/
│ ├── AuthController.java
│ ├── JwtService.java
│ ├── JwtAuthFilter.java
│ └── dto/
├── user/
│ ├── User.java
│ ├── Role.java
│ ├── UserRepository.java
│ ├── UserService.java
│ └── dto/
├── config/
│ └── SecurityConfig.java
├── common/
│ ├── BaseEntity.java
│ └── exception/
│ └── GlobalExceptionHandler.java
└── IntialApplication.java


**Key design decisions:**
- Entities are never returned directly from controllers — dedicated request/response DTOs enforce a clean API boundary and prevent mass-assignment vulnerabilities.
- Passwords are hashed with BCrypt; plaintext is never logged, stored, or returned.
- Authentication is fully stateless (JWT-based, no server-side sessions) for horizontal scalability.
- A global exception handler (`@RestControllerAdvice`) centralizes error formatting across the whole API.

## Local Development Setup

### Prerequisites
- Java 17+
- Maven (or use the included `./mvnw` wrapper)
- Docker Desktop

### 1. Start PostgreSQL via Docker

```bash
docker run --name teamflow-db \
  -e POSTGRES_DB=teamflow \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

> **Windows users:** if you have a native PostgreSQL service installed, make sure it isn't also bound to port 5432 (`netstat -ano | findstr :5432`), or Docker and the native service will conflict.

### 2. Configure `application.yml`

Located at `src/main/resources/application.yml`. Defaults assume the Docker setup above:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/teamflow
    username: postgres
    password: postgres
```

### 3. Run the app

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. On first run, Hibernate auto-creates the schema (`ddl-auto: update`) — this is fine for local development but will be replaced with versioned Flyway migrations before production use.

### 4. Test the API

**Register a user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User"}'
```

**Log in:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

**Access a protected route:**
```bash
curl http://localhost:8080/api/test/me \
  -H "Authorization: Bearer <token from login response>"
```

## Known Environment Gotchas (Windows)

A few environment-specific issues surfaced during setup, documented here in case they recur:

- **CRLF line endings** can silently corrupt values in `application.yml` (e.g., appending `\r` to a password). A `.gitattributes` (`* text=auto eol=lf`) is in place to prevent this going forward.
- **JVM timezone naming**: Windows may report the system timezone using a legacy ID (e.g., `Asia/Calcutta`) that PostgreSQL's timezone database doesn't recognize, causing a connection failure. Fixed by forcing `TimeZone.setDefault(TimeZone.getTimeZone("UTC"))` at application startup — also good practice for storing unambiguous timestamps regardless of platform.
- **Port conflicts**: a native PostgreSQL Windows service can bind to port 5432 alongside Docker's container, causing confusing authentication errors (the app may connect to the wrong Postgres instance entirely). Resolve by stopping the native service and setting it to manual start (`sc config <service-name> start=demand`).

## License

Personal learning project — not currently licensed for reuse.
