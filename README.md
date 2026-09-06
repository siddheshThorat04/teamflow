# Teamflow

A full-stack, multi-tenant team collaboration and project management platform — a focused take on Jira + Slack + Trello, built end-to-end from backend architecture through deployment.

**Live App:** https://teamflow-frontend-1zj6.onrender.com
**API:** https://teamflow-4pug.onrender.com
**Frontend repo:** https://github.com/siddheshThorat04/teamflow-frontend
**Backend repo:** https://github.com/siddheshThorat04/teamflow

> Note: hosted on Render's free tier, so the first request after a period of inactivity may take 30-60 seconds to respond while the service spins back up.

## Stack

| Layer | Technology |
|---|---|
| Frontend | React + TypeScript + Tailwind CSS |
| Backend | Spring Boot (Java 17) |
| Database | PostgreSQL |
| Auth | JWT (stateless) + Spring Security |
| Containerization | Docker (multi-stage build) |
| Deployment | Render (separate frontend/backend services) |

## Features

- **Multi-tenant organizations** — users can belong to multiple organizations, each with independent membership and roles (`OWNER` / `ADMIN` / `MEMBER`)
- **Role-based authorization** — enforced at the service layer; e.g., only `OWNER`/`ADMIN` can add members to an organization
- **Projects** scoped to organizations, with unique per-org project keys (Jira-style: `WEB-1`, `WEB-2`, ...)
- **Tasks** with status (`TODO` / `IN_PROGRESS` / `IN_REVIEW` / `DONE`), priority, due dates, assignee/reporter distinction, and sequential per-project numbering
- **JWT authentication** — registration, login, BCrypt password hashing, stateless token validation on every request
- **React frontend** — protected routing, JWT session persistence, and a Kanban-style task board consuming the live API

## Architecture

The backend follows a **feature-first (package-by-feature)** structure, so each domain's entity, repository, service, controller, and DTOs live together:

com.teamflow.intial
├── auth/ # JWT generation/validation, login, filter
├── user/ # User entity, registration
├── organization/ # Organizations, membership, role-based auth checks
│ └── OrganizationAuthorizationService.java # shared membership/role checks
├── project/ # Projects (scoped to organizations)
├── task/ # Tasks (scoped to projects)
├── config/
│ └── SecurityConfig.java
└── common/
├── BaseEntity.java
└── exception/GlobalExceptionHandler.java


**Key design decisions:**
- Entities are never returned directly from controllers — dedicated request/response DTOs enforce a clean API boundary and prevent mass-assignment vulnerabilities.
- Authorization logic (organization membership, role checks) is centralized in `OrganizationAuthorizationService` and reused across Organization, Project, and Task modules, rather than duplicated per-service.
- Multi-tenancy is modeled via a join entity (`OrganizationMember`) rather than a simple many-to-many, since per-org roles require attributes on the relationship itself.
- Passwords are hashed with BCrypt; plaintext is never logged, stored, or returned.
- Authentication is fully stateless (JWT-based, no server-side sessions).
- Configuration (database credentials, JWT secret, port) is fully externalized via environment variables — no secrets committed to the repo.
- A global exception handler (`@RestControllerAdvice`) centralizes error formatting; Spring Security's own `accessDeniedHandler` is separately configured to ensure authorization failures also return structured JSON (Spring Security's filter-level exceptions bypass `@RestControllerAdvice` by default).

## Project Status

### Completed
- [x] Multi-module backend: Auth, Organizations, Projects, Tasks
- [x] JWT authentication with role-based, membership-scoped authorization
- [x] React frontend: auth flow, dashboard, org/project views, Kanban task board
- [x] Dockerized backend (multi-stage build) deployed to Render
- [x] Frontend deployed to Render as a static site
- [x] CORS configured for cross-origin frontend/backend communication

### Up Next
- [ ] Task detail view (comments, full edit)
- [ ] Real-time updates (WebSocket)
- [ ] Redis caching / online presence
- [ ] Kafka-based async notifications
- [ ] S3 file attachments

## Local Development Setup

### Prerequisites
- Java 17+, Node.js 18+
- Docker Desktop

### Backend

```bash
docker run --name teamflow-db \
  -e POSTGRES_DB=teamflow \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16

cd intial
./mvnw spring-boot:run
```

Runs on `http://localhost:8080`. Config is read from environment variables with local defaults baked into `application.yml` (see `DATABASE_URL`, `JWT_SECRET`, etc.).

### Frontend

```bash
cd teamflow-frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`, configured via `.env.local` to call the local backend.

### Quick API test

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## Known Environment Gotchas (Windows)

- **CRLF line endings** can silently corrupt values in `application.yml` (e.g., appending `\r` to a password). A `.gitattributes` (`* text=auto eol=lf`) prevents this.
- **JVM timezone naming**: Windows may report the timezone using a legacy ID (e.g., `Asia/Calcutta`) that PostgreSQL doesn't recognize. Fixed by forcing `TimeZone.setDefault(TimeZone.getTimeZone("UTC"))` at startup.
- **Port conflicts**: a native PostgreSQL Windows service can bind to port 5432 alongside Docker, causing misleading auth errors. Resolve via `sc config <service-name> start=demand`.

## License

Personal learning/portfolio project — not currently licensed for reuse.
