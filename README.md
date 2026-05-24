# Task Management Platform (TMP)

A production-realistic backend system built with Java 17, Spring Boot 3.5.x, and PostgreSQL.
Composed of two independent microservices communicating over REST.

## Architecture
auth-service  (port 8081)  →  Handles identity, issues JWTs
task-service  (port 8082)  →  Handles projects and tasks, validates JWTs locally
PostgreSQL                 →  Single instance, two isolated schemas (authdb, taskdb)

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Security (JWT stateless auth)
- Spring Data JPA + PostgreSQL
- JJWT 0.12.6
- Lombok
- Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

## Database Setup

Run the following in PostgreSQL before starting the services:

```sql
CREATE DATABASE task_platform;
\c task_platform
CREATE SCHEMA authdb;
CREATE SCHEMA taskdb;
```

## Environment Variables

Both services share the same JWT secret — this is how task-service validates
tokens locally without calling auth-service.

| Variable         | Description                        | Default                                          |
|------------------|------------------------------------|--------------------------------------------------|
| DB_HOST          | PostgreSQL host                    | localhost                                        |
| DB_PORT          | PostgreSQL port                    | 5432                                             |
| DB_NAME          | Database name                      | task_platform                                    |
| DB_USER          | Database username                  | postgres                                         |
| DB_PASSWORD      | Database password                  | postgres                                         |
| JWT_SECRET       | HS256 signing secret (min 32 chars)| dev-secret-key-change-in-production-minimum-256-bits |
| JWT_EXPIRY_MS    | Token expiry in milliseconds       | 86400000 (24 hours)                              |
| AUTH_SERVICE_URL | Auth service base URL              | http://localhost:8081                            |

## Running the Services

### Option 1 — Without environment variables (uses defaults)

```bash
# Terminal 1
cd auth-service
mvn spring-boot:run

# Terminal 2
cd task-service
mvn spring-boot:run
```

### Option 2 — With environment variables

```bash
# Terminal 1
cd auth-service
JWT_SECRET=your-secret-key DB_PASSWORD=yourpassword mvn spring-boot:run

# Terminal 2
cd task-service
JWT_SECRET=your-secret-key DB_PASSWORD=yourpassword mvn spring-boot:run
```

## API Overview

### Auth Service (port 8081)

| Method | Endpoint                  | Auth     | Description              |
|--------|---------------------------|----------|--------------------------|
| POST   | /api/v1/auth/register     | Public   | Register a new user      |
| POST   | /api/v1/auth/login        | Public   | Login and receive JWT    |
| GET    | /api/v1/users/me          | Any role | Get own profile          |
| GET    | /api/v1/users/{id}        | Any role | Get user by ID           |
| GET    | /api/v1/users             | ADMIN    | List all users           |

### Task Service (port 8082)

| Method | Endpoint                                        | Auth     | Description               |
|--------|-------------------------------------------------|----------|---------------------------|
| POST   | /api/v1/projects                                | ADMIN    | Create project            |
| GET    | /api/v1/projects                                | Any role | List all projects         |
| GET    | /api/v1/projects/{id}                           | Any role | Get project by ID         |
| PUT    | /api/v1/projects/{id}                           | ADMIN    | Update project            |
| DELETE | /api/v1/projects/{id}                           | ADMIN    | Delete project            |
| POST   | /api/v1/projects/{pid}/tasks                    | Any role | Create task               |
| GET    | /api/v1/projects/{pid}/tasks                    | Any role | List tasks (filter by status)|
| GET    | /api/v1/projects/{pid}/tasks/{tid}              | Any role | Get task by ID            |
| PUT    | /api/v1/projects/{pid}/tasks/{tid}              | Any role | Update task               |
| PATCH  | /api/v1/projects/{pid}/tasks/{tid}/status       | Any role | Transition task status    |
| PATCH  | /api/v1/projects/{pid}/tasks/{tid}/assign       | ADMIN    | Assign task to user       |
| DELETE | /api/v1/projects/{pid}/tasks/{tid}              | ADMIN    | Delete task               |
| GET    | /api/v1/tasks/my-tasks                          | Any role | Get my assigned tasks     |

## Task Status Transitions

Only the following transitions are valid. Any other returns HTTP 400.
TODO → IN_PROGRESS
IN_PROGRESS → DONE
DONE → IN_PROGRESS  (re-open)

## First Admin Setup

Registration always creates a USER by default. To create the first ADMIN:

1. Register normally
2. Run this SQL:
```sql
UPDATE authdb.users SET role = 'ADMIN' WHERE email = 'your@email.com';
```
3. After that, use the ADMIN token to register further admins via the register
   endpoint with `"role": "ADMIN"` in the request body.

## Known Limitations

- `GET /api/v1/users/{id}` is publicly accessible to allow service-to-service
  communication from task-service. In production this would use a service account
  token or internal API key.
- No refresh token implementation (out of scope for time constraint).
- Flyway migrations are a planned improvement — currently using `ddl-auto=update`.

