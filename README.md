# Task Management Platform (TMP)

A production-realistic backend system built with Java 21, Spring Boot 3.5.x, and PostgreSQL.
Composed of two independent microservices communicating over REST.

## Architecture
auth-service  (port 8081)  →  Handles identity, issues JWTs
task-service  (port 8082)  →  Handles projects and tasks, validates JWTs locally
PostgreSQL                 →  Single instance, two isolated schemas (authdb, taskdb)

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security (JWT stateless authentication)
- Spring Data JPA + PostgreSQL
- Flyway (versioned database migrations)
- JJWT 0.12.6
- Lombok
- Docker + Docker Compose
- Maven

---

## Running with Docker (Recommended)

This is the easiest way. You only need Docker installed — no Java, Maven, or PostgreSQL required.

```bash
docker-compose up --build
```

This single command:
- Starts PostgreSQL on port 5432
- Creates authdb and taskdb schemas automatically
- Runs Flyway migrations for both services
- Starts auth-service on port 8081
- Starts task-service on port 8082

To stop everything:
```bash
docker-compose down
```

To stop and remove all data:
```bash
docker-compose down -v
```

---

## Running Locally (Without Docker)

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### Step 1 — Database Setup

Run this in PostgreSQL:

```sql
CREATE DATABASE task_platform;
\c task_platform
CREATE SCHEMA authdb;
CREATE SCHEMA taskdb;
```

### Step 2 — Start Auth Service

```bash
cd auth-service
mvn spring-boot:run
```

Runs on port 8081.

### Step 3 — Start Task Service

```bash
cd task-service
mvn spring-boot:run
```

Runs on port 8082.

### Step 4 — Build without running

```bash
mvn clean package
```

---

## Environment Variables

Both services share the same JWT secret. This is how task-service validates
tokens locally without calling auth-service.

| Variable | Description | Default |
|---|---|---|
| DB_HOST | PostgreSQL host | localhost |
| DB_PORT | PostgreSQL port | 5432 |
| DB_NAME | Database name | task_platform |
| DB_USER | Database username | postgres |
| DB_PASSWORD | Database password | postgres |
| JWT_SECRET | HS256 signing secret (min 32 chars) | dev-secret-key-change-in-production-minimum-256-bits |
| JWT_EXPIRY_MS | Token expiry in milliseconds | 86400000 (24 hours) |
| JWT_REFRESH_EXPIRY_MS | Refresh token expiry in ms | 604800000 (7 days) |
| AUTH_SERVICE_URL | Auth service base URL (task-service only) | http://localhost:8081 |

---

## API Overview

### Auth Service — port 8081

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/v1/auth/register | Public | Register new user |
| POST | /api/v1/auth/login | Public | Login and receive JWT |
| GET | /api/v1/users/me | Any role | Get own profile |
| GET | /api/v1/users/{id} | Any role | Get user by ID |
| GET | /api/v1/users | ADMIN only | List all users |
| POST | /api/v1/auth/refresh | Public | Get new access token using refresh token |


### Task Service — port 8082

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/v1/projects | ADMIN | Create project |
| GET | /api/v1/projects | Any role | List all projects |
| GET | /api/v1/projects/{id} | Any role | Get project by ID |
| PUT | /api/v1/projects/{id} | ADMIN | Update project |
| DELETE | /api/v1/projects/{id} | ADMIN | Delete project |
| POST | /api/v1/projects/{pid}/tasks | Any role | Create task |
| GET | /api/v1/projects/{pid}/tasks | Any role | List tasks (filter by ?status=) |
| GET | /api/v1/projects/{pid}/tasks/{tid} | Any role | Get task by ID |
| PUT | /api/v1/projects/{pid}/tasks/{tid} | Any role | Update task |
| PATCH | /api/v1/projects/{pid}/tasks/{tid}/status | Any role | Transition task status |
| PATCH | /api/v1/projects/{pid}/tasks/{tid}/assign | ADMIN | Assign task to user |
| DELETE | /api/v1/projects/{pid}/tasks/{tid} | ADMIN | Delete task |
| GET | /api/v1/tasks/my-tasks | Any role | Get my assigned tasks |
| GET | /api/v1/tasks/overdue | Any role | Get all overdue tasks |


---

## Task Status Transitions

Only the following transitions are valid. Any other returns HTTP 400.
TODO → IN_PROGRESS
IN_PROGRESS → DONE
DONE → IN_PROGRESS  (re-open)

---

## Error Response Format

All errors return a consistent JSON structure:

```json
{
    "timestamp": "2026-05-24T16:21:47",
    "status": 400,
    "message": "Email is required",
    "path": "/api/v1/auth/register"
}
```

---

## First Admin Setup

Registration always creates a USER by default. To bootstrap the first ADMIN:

1. Register normally via POST /api/v1/auth/register
2. Run this SQL:

```sql
UPDATE authdb.users SET role = 'ADMIN' WHERE email = 'your@email.com';
```

3. Login with that account to get an ADMIN token
4. Use that token to register further admins by passing `"role": "ADMIN"` in the request body

---

## Known Limitations

- GET /api/v1/users/{id} is publicly accessible to allow service-to-service
  communication from task-service. In production this would use a service
  account token or internal API key.
- No refresh token implementation.
- RestTemplate used for service-to-service calls — WebClient would be preferred
  in a fully reactive production setup.

---

## Postman Collection

A Postman collection is included at the root of the repository as
`postman_collection.json`. Import it into Postman to test all endpoints
immediately. The Login request auto-saves the token — all subsequent
requests use it automatically.