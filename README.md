# QueryNest Backend

A Spring Boot REST API for a Quora-style Q&A platform — users can post questions, answer them, and upvote/downvote answers.

> Package name in the codebase is `org.com.quora_backend` (project was originally scaffolded as "QuoraBackend").

---

## Features

- **User management** — create, fetch, update (full/partial), delete users; username/email availability checks; per-user question and answer counts.
- **Questions** — create, fetch, update, delete questions; keyword search; paginated + sorted listing of all questions.
- **Answers** — create, fetch, update, delete answers on a question; ownership is enforced via an `X-User-Id` header.
- **Voting** — upvote/downvote an answer, with a unique constraint per `(user, answer)` pair to prevent duplicate votes.
- **Centralized error handling** — consistent JSON error responses (timestamp, status, error, message, path) for not-found, conflict, and validation failures.
- **Auditing** — `createdAt` / `updatedAt` timestamps automatically maintained on every entity via JPA auditing.

---

## Tech Stack

| Layer            | Technology                                  |
|-------------------|---------------------------------------------|
| Language           | Java 17                                     |
| Framework          | Spring Boot 4.1.0                           |
| Web                | Spring Web (MVC)                            |
| Persistence        | Spring Data JPA / Hibernate                 |
| Database           | MySQL                                       |
| Validation         | Spring Validation (Jakarta Bean Validation) |
| Boilerplate        | Lombok                                      |
| Build Tool         | Gradle (wrapper included)                   |
| Testing            | JUnit 5 (via Spring Boot test starters)     |

---

## Project Structure

```
src/main/java/org/com/quora_backend/
├── QuoraBackendApplication.java     # Application entry point (@EnableJpaAuditing)
├── advice/
│   └── GlobalExceptionHandler.java  # Centralized exception -> HTTP response mapping
├── controller/
│   ├── UserController.java          # /api/v1/users endpoints
│   ├── QuestionController.java      # /api/v1/questions endpoints
│   └── AnswerController.java        # /api/v1/questions/{id}/answers, /api/v1/answers endpoints
├── dto/
│   ├── user/                        # Request/response DTOs for users
│   ├── question/                    # Request/response DTOs for questions
│   ├── answer/                      # Request/response DTOs for answers
│   ├── vote/                        # Vote request DTO
│   └── common/                      # Shared DTOs (ErrorResponse)
├── exception/                       # Custom domain exceptions
├── mapper/                          # Entity <-> DTO mapping (manual mappers)
├── model/                           # JPA entities (User, Question, Answer, Vote, BaseModel)
├── repository/                      # Spring Data JPA repositories
└── service/                         # Service interfaces + implementations
```

---

## Data Model

- **BaseModel** — abstract superclass providing `id`, `createdAt`, `updatedAt` to all entities.
- **User** — has many `Question`s and `Answer`s.
- **Question** — belongs to a `User`; has many `Answer`s (cascade delete on the question).
- **Answer** — belongs to a `Question` and a `User`; tracks a `voteScore`.
- **Vote** — links a `User` and an `Answer` with a `voteType` (`UPVOTE` / `DOWNVOTE`); unique per user/answer pair.

---

## Prerequisites

- JDK 17+
- MySQL Server (running locally or reachable)
- Gradle is not required to be installed separately — the repo includes the Gradle Wrapper (`gradlew` / `gradlew.bat`)

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

### 3. Configure the datasource

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
```

> **Note:** Storing plaintext credentials in `application.properties` is fine for local development only. For anything beyond local use, move these to environment variables (e.g. `${DB_USERNAME}`, `${DB_PASSWORD}`) and a `.env`/secrets manager.

### 4. Run the application

```bash
# macOS / Linux
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The API will be available at `http://localhost:8080`.

### 5. Run tests

```bash
./gradlew test
```

---

## API Reference

Base path: `/api/v1`

### Users — `/api/v1/users`

| Method | Endpoint                     | Description                          |
|--------|-------------------------------|---------------------------------------|
| POST   | `/`                            | Create a new user                     |
| GET    | `/{id}`                        | Get user by ID                        |
| PUT    | `/{id}`                        | Full update of a user                 |
| PATCH  | `/{id}`                        | Partial update of a user              |
| DELETE | `/{id}`                        | Delete a user                         |
| GET    | `/{id}/questions`              | List all questions posted by a user   |
| GET    | `/{id}/answers`                | List all answers posted by a user     |
| GET    | `/check-username?username=`    | Check if a username is available      |
| GET    | `/check-email?email=`          | Check if an email is available        |
| GET    | `/{id}/question-count`         | Count of questions posted by a user   |
| GET    | `/{id}/answer-count`           | Count of answers posted by a user     |

### Questions — `/api/v1/questions`

| Method | Endpoint            | Description                                            |
|--------|-----------------------|----------------------------------------------------------|
| POST   | `/`                    | Create a new question                                   |
| GET    | `/{id}`                | Get a question by ID                                     |
| PUT    | `/{id}`                | Update a question                                        |
| DELETE | `/{id}`                | Delete a question                                        |
| GET    | `/search?keyword=`     | Search questions by keyword (title, case-insensitive)    |
| GET    | `/`                    | Paginated list of all questions (default: 10/page, sorted by `createdAt` desc) |

### Answers

| Method | Endpoint                              | Description                              | Headers                 |
|--------|-----------------------------------------|--------------------------------------------|--------------------------|
| POST   | `/api/v1/questions/{questionId}/answers`| Post an answer to a question               | `X-User-Id: <id>`        |
| GET    | `/api/v1/answers/{id}`                  | Get an answer by ID                        | —                        |
| PUT    | `/api/v1/answers/{id}`                  | Update an answer (owner only)              | `X-User-Id: <id>`        |
| DELETE | `/api/v1/answers/{id}`                  | Delete an answer (owner only)              | `X-User-Id: <id>`        |
| POST   | `/api/v1/answers/{id}/vote`             | Upvote/downvote an answer                  | `X-User-Id: <id>`        |

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

| Exception                        | HTTP Status  |
|-----------------------------------|--------------|
| `UserNotFoundException`           | 404 Not Found |
| `QuestionNotFoundException`       | 404 Not Found |
| `AnswerNotFoundException`         | 404 Not Found |
| `ResourceNotFoundException`       | 404 Not Found |
| `UsernameAlreadyExistsException`  | 409 Conflict |
| `EmailAlreadyExistsException`     | 409 Conflict |
| `MethodArgumentNotValidException` | 400 Bad Request |

---

## Roadmap / Possible Improvements

- Add authentication (Spring Security + JWT) instead of the `X-User-Id` header
- Move DB credentials to environment variables / a `.env` file
- Add API documentation via springdoc-openapi (Swagger UI)
- Add pagination to `getUserQuestions` / `getUserAnswers` / search endpoints
- Dockerize the application and database for easier local setup

---

## License

_No license added yet. Add a `LICENSE` file (e.g. MIT) here if you want others to be able to use or contribute to this project._
