# QueryNest Backend

A Spring Boot REST API for a Quora-style Q&A platform — users can post questions, answer them, and upvote/downvote answers, all protected behind JWT authentication with ownership-based authorization.

> Package name in the codebase is `org.com.quora_backend` (the project was originally scaffolded as "QuoraBackend").

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![Gradle](https://img.shields.io/badge/Build-Gradle-blue)
![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1)

---

## Features

- **Authentication** — JWT-based login (`Spring Security`); tokens are validated on each request via a custom `JwtAuthenticationFilter`.
- **Authorization** — ownership-based access control via `@PreAuthorize`; only a resource's owner or an `ADMIN` can update/delete it.
- **User management** — create, fetch, update (full/partial), delete users; username/email availability checks; per-user question and answer counts.
- **Questions** — create, fetch, update, delete questions; keyword search; paginated + sorted listing of all questions.
- **Answers** — create, fetch, update, delete answers on a question; ownership enforced through the authenticated JWT principal.
- **Voting** — upvote/downvote an answer, with a unique constraint per (user, answer) pair to prevent duplicate votes.
- **Centralized error handling** — consistent JSON error responses (timestamp, status, error, message, path) for not-found, conflict, and validation failures.
- **Auditing** — `createdAt` / `updatedAt` timestamps automatically maintained on every entity via JPA auditing.
- **API documentation** — interactive Swagger UI via springdoc-openapi.

---

## Tech Stack

| Layer         | Technology                              |
|---------------|-------------------------------------------|
| Language       | Java 17                                    |
| Framework      | Spring Boot 4.1.0                          |
| Web            | Spring Web (MVC)                           |
| Security       | Spring Security + JWT                      |
| Persistence    | Spring Data JPA / Hibernate                |
| Database       | MySQL                                       |
| Validation     | Spring Validation (Jakarta Bean Validation)|
| API Docs       | springdoc-openapi (Swagger UI)             |
| Boilerplate    | Lombok                                      |
| Build Tool     | Gradle (wrapper included)                  |
| Testing        | JUnit 5 (via Spring Boot test starters)    |

---

## Project Structure

```
src/main/java/org/com/quora_backend/
├── QuoraBackendApplication.java     # Application entry point (@EnableJpaAuditing)
├── advice/
│   └── GlobalExceptionHandler.java  # Centralized exception -> HTTP response mapping
├── config/
│   ├── SecurityConfig.java          # Spring Security filter chain
│   ├── AuthenticationConfig.java    # AuthenticationManager bean
│   ├── PasswordEncoderConfig.java   # Password hashing config
│   ├── JpaAuditingConfig.java       # Enables createdAt/updatedAt auditing
│   └── OpenApiConfig.java           # Swagger/OpenAPI config
├── controller/
│   ├── AuthController.java          # /api/v1/auth/login
│   ├── UserController.java          # /api/v1/users endpoints
│   ├── QuestionController.java      # /api/v1/questions endpoints
│   └── AnswerController.java        # /api/v1/questions/{id}/answers, /api/v1/answers endpoints
├── dto/
│   ├── auth/                        # Login request/response DTOs
│   ├── user/                        # Request/response DTOs for users
│   ├── question/                    # Request/response DTOs for questions
│   ├── answer/                      # Request/response DTOs for answers
│   ├── vote/                        # Vote request DTO
│   └── common/                      # Shared DTOs (ErrorResponse)
├── exception/                       # Custom domain exceptions
├── mapper/                          # Entity <-> DTO mapping (manual mappers)
├── model/                           # JPA entities (User, Question, Answer, Vote, Role, VoteType, BaseModel)
├── repository/                      # Spring Data JPA repositories
├── security/                        # JWT filter, UserPrincipal, entry point, access-denied handler
└── service/                         # Service interfaces + implementations
```

---

## Data Model

- **BaseModel** — abstract superclass providing `id`, `createdAt`, `updatedAt` to all entities.
- **User** — has many Questions and Answers; carries a `Role` (e.g. `USER` / `ADMIN`).
- **Question** — belongs to a User; has many Answers (cascade delete on the question).
- **Answer** — belongs to a Question and a User; tracks a vote score.
- **Vote** — links a User and an Answer with a `VoteType` (`UPVOTE` / `DOWNVOTE`); unique per user/answer pair.

---

## Prerequisites

- JDK 17+
- MySQL Server (running locally or reachable)
- Gradle is not required separately — the repo includes the Gradle Wrapper (`gradlew` / `gradlew.bat`)

---

## Setup & Running Locally

### 1. Clone the repository
```bash
git clone https://github.com/Abhilash-Panja/querynest-backend.git
cd querynest-backend
```

### 2. Create the database
```sql
CREATE DATABASE quoraDB;
```

### 3. Configure the datasource and JWT secret
Update `src/main/resources/application.properties` with your local MySQL credentials if they differ from the defaults:

```properties
spring.application.name=QuoraBackend
spring.datasource.url=jdbc:mysql://localhost:3306/quoraDB
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

jwt.secret=${JWT_SECRET:dev-only-local-secret-change-me}
jwt.expiration-ms=${JWT_EXPIRATION_MS:3600000}
```

> **Note:** Storing plaintext credentials/secrets in `application.properties` is fine for local development only. For anything beyond local use, set `JWT_SECRET`, `DB_USERNAME`, and `DB_PASSWORD` as environment variables rather than committing real values.

### 4. Run the application
```bash
# macOS / Linux
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The API will be available at `http://localhost:8080`.

### 5. Explore the API
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

### 6. Run tests
```bash
./gradlew test
```

---

## API Reference

Base path: `/api/v1`

### Auth
| Method | Endpoint      | Auth   | Description               |
|--------|----------------|--------|-----------------------------|
| POST   | `/auth/login` | Public | Log in and receive a JWT   |

### Users — `/api/v1/users`
| Method | Endpoint                    | Auth              | Description                             |
|--------|-------------------------------|--------------------|--------------------------------------------|
| POST   | `/`                            | Public             | Create a new user                          |
| GET    | `/{id}`                        | Public             | Get user by ID                             |
| PUT    | `/{id}`                        | Owner / Admin      | Full update of a user                      |
| PATCH  | `/{id}`                        | Owner / Admin      | Partial update of a user                   |
| DELETE | `/{id}`                        | Owner / Admin      | Delete a user                              |
| GET    | `/{id}/questions`              | Public             | List all questions posted by a user        |
| GET    | `/{id}/answers`                | Public             | List all answers posted by a user          |
| GET    | `/check-username?username=`   | Public             | Check if a username is available           |
| GET    | `/check-email?email=`         | Public             | Check if an email is available             |
| GET    | `/{id}/question-count`        | Authenticated      | Count of questions posted by a user        |
| GET    | `/{id}/answer-count`          | Authenticated      | Count of answers posted by a user          |

### Questions — `/api/v1/questions`
| Method | Endpoint            | Auth              | Description                                          |
|--------|-----------------------|--------------------|---------------------------------------------------------|
| POST   | `/`                    | Authenticated      | Create a new question                                   |
| GET    | `/{id}`                | Public             | Get a question by ID                                    |
| PUT    | `/{id}`                | Owner / Admin      | Update a question                                        |
| DELETE | `/{id}`                | Owner / Admin      | Delete a question                                        |
| GET    | `/search?keyword=`    | Public             | Search questions by keyword (title, case-insensitive)   |
| GET    | `/`                    | Public             | Paginated list of all questions (default: 10/page, sorted by `createdAt` desc) |

### Answers
| Method | Endpoint                                | Auth              | Description                       |
|--------|--------------------------------------------|--------------------|--------------------------------------|
| POST   | `/api/v1/questions/{questionId}/answers`  | Authenticated      | Post an answer to a question        |
| GET    | `/api/v1/answers/{id}`                    | Public             | Get an answer by ID                 |
| PUT    | `/api/v1/answers/{id}`                    | Owner / Admin      | Update an answer                    |
| DELETE | `/api/v1/answers/{id}`                    | Owner / Admin      | Delete an answer                    |
| POST   | `/api/v1/answers/{id}/vote`               | Authenticated      | Upvote/downvote an answer            |

All authenticated endpoints require an `Authorization: Bearer <token>` header, obtained from `POST /api/v1/auth/login`.

**Vote request body:**
```json
{
  "voteType": "UPVOTE"
}
```

---

## Error Response Format

All handled exceptions return a consistent JSON shape:

```json
{
  "timestamp": "2026-08-02T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 1",
  "path": "/api/v1/users/1"
}
```

Validation errors additionally include a `validationErrors` map of field → message.

| Exception                          | HTTP Status     |
|--------------------------------------|-------------------|
| `UserNotFoundException`             | 404 Not Found     |
| `QuestionNotFoundException`         | 404 Not Found     |
| `AnswerNotFoundException`           | 404 Not Found     |
| `ResourceNotFoundException`          | 404 Not Found     |
| `UsernameAlreadyExistsException`     | 409 Conflict      |
| `EmailAlreadyExistsException`        | 409 Conflict      |
| `UnauthorizedAccessException`        | 403 Forbidden     |
| `MethodArgumentNotValidException`   | 400 Bad Request   |

---

## Roadmap / Possible Improvements

- Refresh tokens for JWT session renewal
- Move DB credentials and JWT secret fully to environment variables / a `.env` file for all environments
- Add pagination to `getUserQuestions` / `getUserAnswers` / search endpoints
- Comment support on answers
- Question tags/categories
- Dockerize the application and database for easier local setup

---

## License

No license added yet. Add a `LICENSE` file (e.g. MIT) here if you want others to be able to use or contribute to this project.
