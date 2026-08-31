# Project Interview Master Question Bank — QueryNest Backend

**Repo:** `github.com/Abhilash-Panja/querynest-backend`
**Stack claimed:** Java 17, Spring Boot 4.1.0, Spring Web MVC, Spring Data JPA/Hibernate, MySQL, Spring Security + JWT, `@PreAuthorize` ownership authorization, springdoc-openapi, Gradle, JUnit 5.

---

## ⚠️ CRITICAL FINDING — READ THIS FIRST

I fetched your **actual `build.gradle`** from the repo (main branch). Here are its real dependencies:

```
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.boot:spring-boot-starter-webmvc'
compileOnly 'org.projectlombok:lombok'
developmentOnly 'org.springframework.boot:spring-boot-devtools'
runtimeOnly 'com.mysql:mysql-connector-j'
annotationProcessor 'org.projectlombok:lombok'
annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
testImplementation 'org.springframework.boot:spring-boot-starter-validation-test'
testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
testCompileOnly 'org.projectlombok:lombok'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
testAnnotationProcessor 'org.projectlombok:lombok'
testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
```

### ⚠️ README/CODE MISMATCH

| README claims | Dependency required | Present in build.gradle? |
|---|---|---|
| "JWT-based login (Spring Security)" | `spring-boot-starter-security` | ❌ NOT PRESENT |
| JWT generation/validation | a JWT library (`io.jsonwebtoken:jjwt-*`, `auth0:java-jwt`, or `nimbus-jose-jwt`) | ❌ NOT PRESENT |
| `@PreAuthorize` ownership authorization | `spring-boot-starter-security` (method security) | ❌ NOT PRESENT |
| "interactive Swagger UI via springdoc-openapi" | `org.springdoc:springdoc-openapi-starter-webmvc-ui` | ❌ NOT PRESENT |

**Interview risk: this is a killer red flag if an interviewer notices it.** Three explanations are possible, and you need to know which one is true *before* you walk into an interview:

1. **The build.gradle I fetched is stale / you have a local uncommitted version** with the real dependencies added. → Fix: commit and push the real build.gradle immediately.
2. **The security/JWT/Swagger code exists in `src/` but the project doesn't actually compile**, because Gradle can't resolve `@Autowired SecurityFilterChain`, `JwtAuthenticationFilter extends OncePerRequestFilter` (needs `spring-boot-starter-security` for `OncePerRequestFilter` and Spring Security types), or `@PreAuthorize` (needs `spring-security-config`) without the dependency. → This means the repo as pushed **does not build**, which is worse than not having the feature at all.
3. **The README is aspirational** — written to describe intended/planned architecture rather than what's actually implemented, possibly assisted by AI or copied from a template README for a similar project. → This is the most damaging scenario in an interview: it means the README cannot be trusted as a source of truth about your own code, and an interviewer who clones the repo and runs `./gradlew build` will find out in under two minutes.

**Action item before any interview:** run `./gradlew clean build` locally right now and confirm it succeeds. Open `SecurityConfig.java` and confirm the import statements actually resolve. If it doesn't compile, you have a false claim on your README that WILL be caught.

I could not browse the `src/` tree directly (GitHub blocks automated directory browsing), so all Level 3+ (implementation-level) questions below are written **as if the security/JWT/Swagger code exists**, based on what the README claims and standard patterns for this feature set. You must personally verify each one against your real files — if a class doesn't exist as described, treat that question as **unanswerable and dangerous**, not as a script to memorize.

Everything else in this document (entities, controllers, REST structure, exception handling, voting, JPA auditing) is based on your README's explicit claims, which are internally consistent with the dependencies that ARE present (`data-jpa`, `validation`, `webmvc`, `mysql-connector-j`) and therefore far more likely to be real.

---

## 1. Project Understanding

**Q1.1 (🟢 Basic).** What does QueryNest do, in one sentence?
- **Testing:** Can you compress the project to its core value prop without rambling.
- **Strong answer:** "It's a Quora-style Q&A REST API — users register, post questions, answer them, and upvote or downvote answers, with JWT-secured endpoints and ownership-based authorization so only the creator or an admin can edit or delete a resource."
- **Why strong:** Names the domain, the auth model, and the authorization model in one breath — shows the candidate knows what's load-bearing in their own project.
- **Weak answer:** "It's a backend project I built with Spring Boot and MySQL for a college assignment."
- **Follow-ups:** Why Quora-style specifically? What's the hardest entity relationship in this domain? Why is voting modeled separately from answers instead of a score column?

**Q1.2 (🟡 Intermediate).** Why is the package named `org.com.quora_backend` when the project is called QueryNest?
- **Testing:** Attention to their own repo, not memorized talking points.
- **Strong answer:** "The project was originally scaffolded as 'QuoraBackend' — I renamed the product/repo to QueryNest later but never did a full package rename/refactor, since that's a purely cosmetic change with real risk of breaking imports for little benefit."
- **Weak answer:** "I'm not sure, I think it just generated that way." — Red flag: this is literally stated in your own README, so not knowing it looks like you didn't write the README either.
- **Follow-up:** Would you rename it now? What's the actual Gradle/IDE mechanism for a safe package rename?

**Q1.3 (🟡 Intermediate).** Walk me through why you chose *this* domain (Q&A platform) over a simpler CRUD app.
- **Testing:** Whether the scope was a deliberate choice to exercise specific skills (auth, relationships, voting/concurrency) or just copied because "everyone builds a Quora clone."
- **Strong answer:** Names specific skills the domain forces: multi-entity relationships (User→Question→Answer→Vote), ownership-based authorization (not just role-based), and a uniqueness constraint under concurrent writes (the vote table).
- **Weak answer:** "I saw it as a common interview project idea and built it too."

---

## 2. The 2-Minute Project Explanation (memorize your OWN version, not this one)

**Q2.1 (🟢 Basic → 🟠 Deep, depending on follow-ups).** "Give me a 2-minute walkthrough of this project — what it does, how it's built, and what you're most proud of."
- **What's being tested:** Structure under time pressure. Most candidates either ramble or read a rehearsed script that falls apart the moment a follow-up hits.
- **Strong answer skeleton (yours to fill in with real details, not mine):**
  1. One sentence: what it does and for whom.
  2. Core entities and relationship shape (User 1—N Question 1—N Answer, User 1—N Vote, Vote unique per (user, answer)).
  3. The auth story — is it *actually* JWT + Spring Security right now, or in progress? **Say this correctly — see the mismatch above.**
  4. One technical decision you'd defend under attack (e.g., "ownership authorization via `@PreAuthorize` instead of checking ownership manually in the service layer").
  5. One honest limitation (no refresh tokens, no pagination on search yet — both are in your own Roadmap section).
- **Why strong:** Ends on a self-identified weakness before the interviewer finds it — this reads as engineering maturity, not padding.
- **Interviewer follow-up:** "You mentioned JWT — walk me through what happens, packet by packet, from login to an authorized request." (This routes straight into Section 12.)

---

## 3. Architecture

**Q3.1 (🟡 Intermediate).** Draw the request flow for `POST /api/v1/questions/{questionId}/answers`.
- **Strong answer:** Client sends `Authorization: Bearer <token>` → `JwtAuthenticationFilter` intercepts before the security filter chain reaches the dispatcher servlet → filter validates signature + expiry → populates `SecurityContextHolder` with a `UserPrincipal` → request reaches `AnswerController` → controller extracts the authenticated principal (not a body field — this matters, see Q12.6) → delegates to `AnswerService` → service validates the question exists (`QuestionRepository.findById` or throws `QuestionNotFoundException`) → builds and persists an `Answer` entity via `AnswerRepository.save()` → `BaseModel`'s auditing fields (`createdAt`/`updatedAt`) are populated automatically by JPA auditing listeners → mapper converts entity to response DTO → 201/200 returned.
- **Copy-detection variant:** "At which exact point does Spring know which user is 'you'? Is it read from the request body, a path variable, or the Security Context?" A copied project's owner often can't answer this precisely and will guess "from the request" vaguely instead of naming `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`.
- **Follow-up:** What HTTP status and body do you get if `questionId` doesn't exist? (Should map to `QuestionNotFoundException` → 404, per your own exception table.)

**Q3.2 (🟠 Deep).** "If I remove `GlobalExceptionHandler`, what breaks?"
- **Strong answer:** Nothing stops working per se — exceptions still propagate — but every unhandled exception falls through to Spring Boot's default `/error` whitebox handler, returning its generic shape instead of your `{timestamp, status, error, message, path}` contract. Any client (frontend, tests, docs) written against your custom error shape breaks immediately. Validation errors would also lose their `validationErrors` field map.
- **Weak answer:** "The app would crash." (It wouldn't — this reveals the candidate doesn't understand `@ControllerAdvice` is a translation layer, not a safety net.)

**Q3.3 (🟠 Deep → 🔴 Killer).** "Where, precisely, do you validate that a user updating a Question is actually its owner? Is that check in the controller, the service, or a security annotation? What happens if two of those layers disagree?"
- **Testing:** Whether ownership enforcement is centralized and consistent, or duplicated/contradictory across layers (a common real bug: `@PreAuthorize` checks role but the service layer separately (and maybe incorrectly) checks ownership, or vice versa, and they can drift out of sync when only one is updated).
- **This is a "you must know your own code" question — I cannot answer it for you without seeing `QuestionController`/`QuestionService`.** If your `@PreAuthorize` expression calls a custom bean method (e.g., `@PreAuthorize("@questionSecurity.isOwner(#id, authentication)")`), be ready to explain exactly what that bean does, including its DB round trip, and whether it's N+1-prone if called per request.

**Q3.4 (🟠 Deep).** "10x traffic overnight. What's the first thing that falls over?"
- **Strong answer for this specific project:** Almost certainly the MySQL connection pool (default HikariCP pool size is small, ~10) under concurrent writes to `Answer`/`Vote`, especially the paginated question listing doing `ORDER BY createdAt DESC` on an unindexed or poorly-indexed column at scale, and unindexed `LIKE %keyword%` search on `Question.title` doing full table scans. There's no caching layer (no Redis in this project), so every read hits MySQL directly.
- **Follow-up:** How would you verify that hypothesis instead of guessing? (Answer should mention: connection pool metrics via Actuator, slow query log, `EXPLAIN` on the search query.)
- **Follow-up:** What's the actual index situation on `Question.title` for the keyword search? Is it a full LIKE scan?

---

## 4. Ownership Verification (general)

**Q4.1 (🔴 Killer).** "Show me the commit history for one feature — walk me through how the `Vote` uniqueness constraint evolved. Did you get it right the first time?"
- **Testing:** This can't be faked from memory of a tutorial. If you actually built this, you likely hit a real bug first (e.g., allowing duplicate votes, or a race condition producing two rows for the same user+answer before the unique constraint existed) and fixed it. If you copied it, you'll have no story here beyond "it just worked."
- **What to prepare:** Have a real, specific story about a bug you hit and fixed — ideally with a `git log`/`git blame` you can show live.

**Q4.2 (🔴 Killer, copy-detection).** "Why is `Vote` a separate entity instead of just an `upvotes`/`downvotes` integer counter on `Answer`?"
- **Strong answer:** A counter can't enforce "one vote per user per answer" — you need a row per (user, answer) pair with a unique constraint to prevent double-voting, and you need to be able to identify *which* user voted which way (e.g., to toggle/change a vote, or show "you upvoted this" in the UI). A bare counter is also not atomic-safe under concurrent increments without extra locking, whereas the DB unique constraint on `Vote` gives you a natural conflict signal (constraint violation → 409) for free.
- **Weak answer:** "Because that's how you're supposed to do voting systems." (No reasoning — clear tutorial-recall pattern.)
- **Follow-up:** So how do you compute an answer's current score for display — do you `SUM` on read every time, or maintain a denormalized counter that you update on each vote?
- **Follow-up (killer):** If it's `SUM` on every read, what happens to that query's cost as an answer accumulates 100k votes? If it's a denormalized counter, how do you keep it consistent with the underlying `Vote` rows if a vote update fails halfway?

**Q4.3 (🔴 Killer, copy-detection).** "What SQL does `voteRepository.findByUserIdAndAnswerId(...)` actually generate, and why did you name the method exactly that way?"
- **Testing:** Spring Data JPA method-name-derived query understanding. A copier can usually recite "Spring generates the query from the method name" as a slogan but can't explain the actual generated SQL or explain what breaks if the method name doesn't match field names exactly (e.g., `Vote.user` vs a field actually named `voter`).
- **Strong answer:** Spring Data parses the method name into query fragments (`findBy` + `UserId` + `And` + `AnswerId`) and maps each fragment to a property path on the entity graph — `UserId` resolves to `vote.user.id`, generating a query like `SELECT v FROM Vote v WHERE v.user.id = :userId AND v.answer.id = :answerId`, translated by Hibernate into a `SELECT ... FROM votes WHERE user_id = ? AND answer_id = ?` using the FK columns. If the entity field isn't literally named `user` (say it's `voter`), this method name would fail to compile/bootstrap with a `PropertyReferenceException` at application startup, not silently at runtime.
- **Follow-up:** Why is checking for an existing vote via `findBy...` before inserting *not* a safe way to prevent duplicate votes under concurrency, and what actually prevents the duplicate (the DB unique constraint, not this check)?

---

## 5. Technology Decisions

**Q5.1 (🟡 Intermediate).** Why Spring Boot 4.1.0 specifically — did you choose this version deliberately or is it just "whatever the initializer gave me"?
- **Note:** Spring Boot 4.x is a genuinely recent major line built on Spring Framework 7 and a Java 17+ baseline. Be ready to say whether you know what changed vs. Spring Boot 3.x for you specifically (Jakarta namespace was already true in 3.x; if you can't name *anything* that changed between 3.x and 4.x, don't claim you picked it deliberately — say honestly you used whatever the Spring Initializr defaulted to).
- **Weak answer:** Inventing specific 4.x features you can't actually describe. **Don't do this** — an interviewer who knows the framework better than you will catch a fabricated claim instantly, and it's worse than "I'm not sure, I used the initializr default."

**Q5.2 (🟠 Deep).** Why MySQL over PostgreSQL for this project?
- **Strong answer (early-career-appropriate):** "Familiarity and simplicity for a project this size — MySQL's default tooling and local setup were fastest to get running. Postgres would have been a fine choice too; nothing in this schema (no arrays, no JSON columns, no need for advanced indexing like GIN/GiST) actually depends on MySQL-specific features."
- **Weak answer:** Claiming MySQL has some specific advantage for this project that isn't true (e.g., "MySQL is better for relational data" — both are equally relational; this reveals shallow DB knowledge).
- **Follow-up:** What MySQL-specific behavior have you actually had to think about — collation, `utf8mb4`, transaction isolation level defaults (MySQL's default is `REPEATABLE READ`, unlike Postgres' `READ COMMITTED`)? This is a real trap: if you haven't thought about isolation level, say so — don't bluff.

**Q5.3 (🟡 Intermediate).** Why Gradle instead of Maven?
- **Strong answer:** Either a genuine preference reason (Groovy/Kotlin DSL is more concise than Maven's XML, faster incremental builds via the build cache) or an honest "the Spring Initializr default / what I'm more comfortable with." Both are acceptable — what's *not* acceptable is inventing a technical superiority claim you can't back up.
- **Follow-up:** What does `./gradlew bootRun` actually do differently from `./gradlew build` then running the jar?

---

## 6. Java

**Q6.1 (🟡 Intermediate).** Your entities use Lombok (`compileOnly` + `annotationProcessor` in build.gradle). What does `@Data` (or whatever combination you used) actually generate, and why is Lombok scoped as `compileOnly` rather than `implementation`?
- **Strong answer:** Lombok annotations are processed at compile time to generate bytecode (getters/setters/equals/hashCode/toString/constructors) directly into the `.class` files — the Lombok library itself isn't needed at runtime because nothing in the generated bytecode references the Lombok jar; it's purely an annotation processor. `compileOnly` keeps it off the runtime classpath, avoiding shipping a dependency you don't need in production and avoiding classpath bloat.
- **Copy-detection follow-up:** "You have a `Vote` entity linking `User` and `Answer` with `@ManyToOne` relationships. If you used `@Data` (which generates `equals`/`hashCode`) directly on that entity, what breaks with Hibernate lazy loading and bidirectional relationships?" — real risk: `@Data`'s generated `equals`/`hashCode`/`toString` can trigger infinite recursion on bidirectional associations, or trigger lazy-load exceptions inside `toString()`. If you used `@Getter`/`@Setter` individually instead of `@Data` for entities, that's actually the *more correct* choice — be ready to explain why you did or didn't do that.

**Q6.2 (🟠 Deep).** Explain `Optional` usage in your repository/service layer — where do you use it, and where do you deliberately avoid it?
- **Testing:** Whether `Optional` is used correctly (as a return type signaling "may be absent," never as a field or method parameter) or cargo-culted.
- **Follow-up:** `UserRepository.findById()` returns `Optional<User>` from Spring Data JPA by default. Show me exactly how you unwrap it into a `UserNotFoundException` — is it `.orElseThrow(...)`, and what exception type/message does it construct?

**Q6.3 (🟢 Basic → 🟡).** Java 17 — do you use any Java 17 features specifically (records, sealed classes, pattern matching for switch), or is this just "the JDK version I had installed"?
- **Honest answer expected:** Most likely this is just the toolchain version and no 17-specific language features are used beyond what 8-11 already offered (var, streams are older). Say so plainly rather than inventing usage.

---

## 7. Spring Boot

**Q7.1 (🟡 Intermediate).** `@EnableJpaAuditing` is on your main application class. What does it actually turn on, mechanically?
- **Strong answer:** It registers an `AuditingEntityListener`-aware infrastructure that Spring Data JPA hooks into via JPA's `@EntityListeners`. On your `BaseModel`, fields annotated `@CreatedDate`/`@LastModifiedDate` get populated automatically by an `AuditingEntityListener` that intercepts `@PrePersist` (sets `createdAt` and `updatedAt`) and `@PreUpdate` (updates `updatedAt`) lifecycle callbacks — before Hibernate flushes the entity to the DB.
- **Follow-up (copy-detection):** Does `BaseModel` need `@EntityListeners(AuditingEntityListener.class)` explicitly, or does `@EnableJpaAuditing` alone handle it? (It does NOT alone — the entity/superclass must be annotated with `@EntityListeners(AuditingEntityListener.class)`, or auditing silently does nothing. If you can't explain this wiring, you likely copied the annotation without understanding the mechanism.)
- **Follow-up:** What happens to `updatedAt` on a `PATCH` request that changes nothing? Does it still bump the timestamp? Why or why not?

**Q7.2 (🟠 Deep).** You have separate `PUT` (full update) and `PATCH` (partial update) endpoints for User. How are they actually implemented differently at the service layer?
- **Strong answer:** `PUT` typically deserializes a full request DTO and overwrites all mutable fields on the fetched entity, while `PATCH` needs field-level null-checking (only overwrite a field if the incoming DTO's field is non-null) — or a `JsonPatch`/`MergePatch` mechanism if done "properly" per HTTP semantics. Ask directly: **is your `PATCH` implementation actually field-null-checking, or did you implement `PUT` and `PATCH` identically** (a very common tutorial-copy tell — many beginner projects have `PATCH` that behaves exactly like `PUT`, which is semantically wrong and will confuse a careful interviewer testing it live)?
- **Killer follow-up:** "If I send a PATCH request with `{"email": null}` explicitly, does that clear the user's email, or is it ignored because your null-check treats 'not provided' and 'explicitly set to null' identically?" Most naive `PATCH` implementations cannot distinguish these two cases — that's a real, known limitation worth acknowledging rather than being caught off guard by.

**Q7.3 (🟡 Intermediate).** What profile/config setup do you have for local vs. anything-beyond-local? Your README explicitly says plaintext DB credentials and JWT secret are fine for local dev only, and recommends env vars beyond that.
- **Strong answer:** Currently single `application.properties`, no Spring Profiles (`application-dev.properties` / `application-prod.properties`) yet — that's an acknowledged gap, listed in your own Roadmap.
- **Follow-up:** How would you actually implement profile-based config switching in Spring Boot? (`spring.profiles.active`, `application-{profile}.properties`, `@Profile` on beans.)

---

## 8. Spring Data JPA / Hibernate

**Q8.1 (🟠 Deep, copy-detection).** `Question` has many `Answer`s with "cascade delete on the question" per your README. Is that cascade implemented via `CascadeType.REMOVE` on the JPA relationship, or via `ON DELETE CASCADE` at the MySQL FK level, or `orphanRemoval = true`? These are NOT the same thing.
- **Testing:** This is one of the sharpest copy-detection questions in JPA. A huge number of tutorial-copiers say "cascade delete" without knowing which layer enforces it.
- **Strong answer:** If it's `@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)`, deleting a `Question` via the JPA entity manager triggers Hibernate to first load and individually delete each associated `Answer` (N+1 delete statements, potentially), all within the same transaction — this only works if you delete *through* Hibernate (e.g., `questionRepository.delete(question)`), NOT if you ran a raw `DELETE FROM questions WHERE id = ?` bypassing the persistence context. If instead the FK has `ON DELETE CASCADE` at the DB level (via `@JoinColumn(foreignKey = @ForeignKey(...))` with DDL, or the DDL was hand-written), the DB itself performs a single cascading delete regardless of how the delete was issued — more efficient, but invisible to Hibernate's persistence context/cache, risking stale in-memory state.
- **Follow-up:** Given `ddl-auto=update` in your properties (see setup section), which of these two mechanisms did Hibernate actually generate? Have you *checked* the generated DDL, or are you assuming?

**Q8.2 (🔴 Killer).** Where in this codebase could an N+1 query problem occur?
- **Strong answer, project-specific:** Fetching a paginated list of Questions (`GET /api/v1/questions`) where each Question's response DTO includes owner info (username) — if `Question.user` is `@ManyToOne(fetch = FetchType.EAGER)` or accessed lazily per-row inside a loop while mapping to DTOs outside a single query, that's N+1: 1 query for the page of questions + N queries (one per question) to fetch each owner. Same risk on `GET /{id}/questions` and `GET /{id}/answers` for a user, and on `Answer` fetching its parent `Question` and `User` per answer.
- **Follow-up:** How would you actually detect this happening? (`spring.jpa.show-sql=true` + counting statements per request, or Hibernate statistics, or a tool like p6spy/datasource-proxy.) Note your README's local config already has `show-sql=true` — have you actually watched the console output during a paginated question list request?
- **Follow-up:** How would you fix it? (`JOIN FETCH` in a custom `@Query`, or `@EntityGraph`, or a projection/DTO query that selects only needed columns directly.)

**Q8.3 (🟠 Deep).** Why `LAZY` vs `EAGER` on your `@ManyToOne`/`@OneToMany` relationships — did you set this explicitly, or leave JPA defaults (which are `EAGER` for `@ManyToOne`/`@OneToOne` and `LAZY` for `@OneToMany`/`@ManyToMany` by JPA spec default)?
- **Copy-detection risk:** Most beginners never touch this and don't know the *default* differs by relationship type. If you say "everything's LAZY" without having explicitly set it on your `@ManyToOne` fields, you're wrong about your own code — JPA's default for `@ManyToOne` is `EAGER` unless you explicitly override it with `fetch = FetchType.LAZY`.
- **Follow-up:** What breaks if `Answer.user` is EAGER and you fetch a paginated list of 50 answers? (Each triggers an eager join or extra select for its user — compounding the N+1 risk above.)

**Q8.4 (🟠 Deep).** Explain `ddl-auto=update` — why is this acceptable for local dev and dangerous for production?
- **Strong answer:** `update` tells Hibernate to inspect your entity mappings on startup and issue `ALTER TABLE`/`CREATE TABLE` statements to reconcile the schema — convenient for solo local iteration, but it never drops columns, can silently fail to apply certain changes (e.g., renaming a column just adds a new one and leaves the old orphaned), has no rollback/versioning, and running it against a production DB with real data is a well-known way to cause data loss or an inconsistent schema no one can reason about. Production needs a migration tool (Flyway/Liquibase) with `ddl-auto=validate` or `none`.
- **Follow-up:** This is explicitly listed as a "local only" caveat in your own README's setup section, but it's NOT listed in your Roadmap as a planned fix — is that an oversight, or a deliberate scope decision?

---

## 9. Database

**Q9.1 (🟠 Deep).** Draw the schema: tables, FKs, and the unique constraint on `Vote`.
- **Strong answer:** `users(id, username, email, password_hash, role, created_at, updated_at)`, `questions(id, title, body, user_id FK→users, created_at, updated_at)`, `answers(id, body, question_id FK→questions, user_id FK→users, vote_score?, created_at, updated_at)`, `votes(id, user_id FK→users, answer_id FK→answers, vote_type, created_at, updated_at)` with a composite unique constraint on `(user_id, answer_id)`.
- **Follow-up:** Is that unique constraint enforced via `@Table(uniqueConstraints = ...)` (DB-level) or a manual existence-check in the service before insert (application-level, race-condition-prone)? **This is the single most important concurrency question in this project — see Section 18.**

**Q9.2 (🟡 Intermediate).** Is `Question.title` (used in `GET /questions/search?keyword=`) indexed?
- **Strong, honest answer if not verified:** "I haven't explicitly added an index — Spring Data derives a `LIKE %keyword%` query from that endpoint, which by default does a full table scan since a leading wildcard can't use a standard B-tree index." Then explain what you'd do: a MySQL FULLTEXT index for real search relevance, or at minimum a plain index if the search pattern were prefix-only (`keyword%` instead of `%keyword%`).
- **Follow-up:** At 1 million questions, what happens to this endpoint's latency? What would you change first?

**Q9.3 (🟠 Deep).** Foreign key `ON DELETE` behavior for `answers.question_id` and `votes.answer_id` — what happens when a Question is deleted? What happens to its Answers' Votes?
- **Testing:** Whether the cascade chain (Question → Answer → Vote) is fully thought through, or only the first hop (Question → Answer) was considered, leaving orphaned `Vote` rows referencing a deleted `Answer`.
- **This is a very likely gap** — many student projects cascade one level and forget the second. Check your actual schema for this before an interview.

---

## 10. REST APIs

**Q10.1 (🟡 Intermediate).** Is `POST /api/v1/answers/{id}/vote` idempotent? Should it be?
- **Strong answer:** As designed (submit a vote type, presumably upsert-or-toggle), it's *not* naturally idempotent under naive semantics — calling it twice with `UPVOTE` could either (a) fail the second time due to the unique constraint (409), (b) silently no-op if you check-then-skip, or (c) toggle/remove the vote on the second call. **You need to know which of these three your code actually does** — this is a live-demo question an interviewer can test in Postman in 30 seconds.
- **Follow-up:** What's the "correct" REST semantics here — should re-voting the same way be a no-op, an error, or should it un-vote (toggle)? Defend your choice.

**Q10.2 (🟡 Intermediate).** Why is username/email availability check (`GET /check-username`, `GET /check-email`) a `GET` and not folded into the `POST /users` validation response?
- **Strong answer:** Separate `GET` endpoints let a frontend do live validation (e.g., on-blur field check) before the user submits the whole registration form, improving UX — it's a read-only existence check, correctly modeled as `GET` since it has no side effects.
- **Follow-up:** Is there a race condition between "username is available" (GET) and the actual "create user" (POST) — i.e., a TOCTOU (time-of-check-to-time-of-use) bug where two users check simultaneously, both see "available," and both submit? What actually prevents two users signing up with the same username in that case? (Should be the DB `UNIQUE` constraint on `username`, causing the second `POST` to fail — the `GET` check is UX sugar, not the real guarantee.)

**Q10.3 (🟡 Intermediate).** Why does `GET /api/v1/questions` default to 10/page sorted by `createdAt desc`, and how is that implemented — Spring Data's `Pageable`?
- **Follow-up:** What happens if a client requests `?page=-1` or `?size=10000`? Is there any bound-checking, or does Spring Data `Pageable` just pass those through?

---

## 11. Security / JWT — **Answer these only after resolving the build.gradle mismatch above**

**Q11.1 (🟠 Deep).** Walk me through JWT generation on login, field by field.
- **What you need to know cold:** What library generates the token, what claims are in the payload (subject/username, roles, issued-at, expiration — anything else?), what algorithm signs it (HS256 is most common for a solo project — symmetric, single shared secret), and where `jwt.secret` is read from (`application.properties`, defaulting to a hardcoded local value per your own README — `dev-only-local-secret-change-me`).
- **Killer follow-up:** Your README shows `jwt.secret=${JWT_SECRET:dev-only-local-secret-change-me}` — if `JWT_SECRET` is never set as an env var (e.g., in a demo/interview environment), your app silently signs tokens with a publicly-visible-on-GitHub default secret. **Anyone who reads your public README can forge a valid admin JWT against your app if you ever deploy it with defaults.** Is this in your Roadmap? (It should be — flag it as a real vulnerability if asked "what security vulnerabilities exist.")

**Q11.2 (🟠 Deep).** Where exactly does `JwtAuthenticationFilter` sit in the filter chain, and why does that placement matter?
- **Strong answer:** It's a custom filter, likely extending `OncePerRequestFilter`, registered via `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)` in `SecurityConfig`'s `SecurityFilterChain` bean — it must run *before* Spring Security's authorization checks so that `SecurityContextHolder` is populated with the authenticated principal before `@PreAuthorize` expressions evaluate.
- **Follow-up:** What happens if the `Authorization` header is missing entirely vs. present but malformed vs. present but expired? Do these produce different HTTP responses, and where is that difference implemented — inside the filter itself, or delegated to an `AuthenticationEntryPoint`/`AccessDeniedHandler` (your README's project structure lists both under `security/`)?

**Q11.3 (🔴 Killer).** Explain exactly how `@PreAuthorize` enforces "only the owner or ADMIN" — what expression did you write, and what does it evaluate against?
- **Strong answer pattern:** Something like `@PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")` on update/delete methods, OR a call to a custom `@Component` security bean like `@PreAuthorize("@questionSecurity.canModify(#id, authentication)")` that fetches the resource and compares `resource.getUser().getId()` to the authenticated principal's id.
- **Follow-up (real trap):** `@PreAuthorize` runs via a proxy around the *service* method (Spring AOP method security) — does it run before or after the controller has already fetched/loaded the entity? If your ownership check needs to load the `Question` to know its owner, does that mean the entity gets loaded twice (once for the security check, once for the actual update)? How would you avoid that redundant query?
- **Follow-up:** What HTTP status does a failed `@PreAuthorize` check produce by default (403), and is that translated through your `GlobalExceptionHandler`, or handled separately via `AccessDeniedHandler`?

**Q11.4 (🔴 Killer, security depth).** Someone tampers with their JWT payload to change `role: USER` to `role: ADMIN`. What actually stops that from working?
- **Strong answer:** The signature. A JWT's payload is base64-encoded, not encrypted — anyone can decode and read/edit it — but the token is only valid if the signature (HMAC-SHA256 of header+payload using the secret) matches. Editing the payload without re-signing with the correct secret produces a signature mismatch, which `jwtGenerator.validateToken()` (or equivalent) rejects, so the filter never populates an authenticated context, and the request is rejected with 401 before it reaches any role check.
- **Follow-up:** Given Q11.1's finding about the hardcoded default secret — if someone knows the default local secret from your public README, can they forge a token with `role: ADMIN` themselves and sign it correctly? (Yes, if the deployed instance is running on the default secret. This is why the vulnerability in Q11.1 is real and not theoretical.)

---

## 12. Redis

**This is NOT currently implemented in the repository.** Your README, dependency list, and project structure contain no mention of Redis. If an interviewer asks about caching, do not claim you implemented it — say plainly it's not part of this project, and if you want, describe *where* you'd add it (caching `GET /questions/{id}` reads, or a Spring `@Cacheable` layer) as a forward-looking answer, clearly labeled as "if I were to add it."

---

## 13. Kafka

**This is NOT currently implemented in the repository.** Same rule as Redis — there is no messaging/eventing layer in this project (no async question/answer notification pipeline, no event sourcing). Do not improvise a Kafka story for this specific repo. If asked why not, an honest answer is: "The project is synchronous and small enough that request/response REST was sufficient — I'd introduce Kafka if I needed to decouple, e.g., sending a notification when someone answers your question, without blocking the answer-creation request."

---

## 14. WebSocket

**This is NOT currently implemented in the repository.** No STOMP/WebSocket config appears in your README's project structure or dependencies. Do not claim real-time features exist.

---

## 15. Microservices / Service Discovery

**This is a monolith**, per your own project structure (single Spring Boot app, one `build.gradle`, no Eureka/Config Server/Gateway modules). Do not claim microservices architecture. If asked "why not microservices," a strong early-career answer is: "The domain is small and tightly coupled (users/questions/answers/votes all reference each other constantly) — splitting it into services now would mean most requests fan out across service boundaries for basic joins, adding network latency and distributed-transaction complexity for no real benefit at this scale. I'd consider it only if a specific subdomain (e.g., notifications) needed independent scaling."

---

## 16. Concurrency

**Q16.1 (🔴 Killer).** Two requests hit `POST /api/v1/answers/{id}/vote` for the same user and answer at the exact same millisecond. Walk me through what happens at the database level.
- **Strong answer:** If the uniqueness is enforced by a DB-level `UNIQUE` constraint on `(user_id, answer_id)`, both `INSERT` statements race — whichever transaction commits first succeeds, and the second gets a constraint-violation exception (MySQL: duplicate key error) at commit/flush time. That exception needs to be caught in your service/exception-handler layer and translated into a sane HTTP response (ideally 409 Conflict) rather than leaking a raw `DataIntegrityViolationException` as a 500.
- **Killer follow-up:** **Does your `GlobalExceptionHandler` actually catch `DataIntegrityViolationException` and map it to 409?** Check your exception table in the README — it lists `UsernameAlreadyExistsException`/`EmailAlreadyExistsException` → 409, but does NOT list a generic `DataIntegrityViolationException` mapping. If you're relying on an application-level "check if vote exists, then insert" pattern instead of trusting the DB constraint, **that check is itself race-prone** — two concurrent requests can both pass the "does not exist" check before either has inserted, producing a genuine duplicate-vote bug in production under real concurrent load, if the DB constraint isn't there as the actual backstop.
- **This is the single most important question in this whole project to be able to answer under pressure.** Verify, in your real code, whether the unique constraint is at the DB schema level or only checked in application code.

**Q16.2 (🟠 Deep).** Two admins simultaneously `PUT` different full-update payloads to the same `Question`. What happens — is this a lost update?
- **Strong answer:** With default JPA/Hibernate optimistic locking absent (no `@Version` field mentioned anywhere in your README's data model), this is a classic **lost update**: last-write-wins, no conflict detection. Whoever's transaction commits second silently overwrites the first writer's changes with no error.
- **Follow-up:** How would you fix this? (`@Version` column for optimistic locking — a failed update throws `OptimisticLockException`, translatable to a 409/412 "resource was modified" response.) **Do you have a `@Version` field on any entity? If not, say so honestly — this is a real, known gap, not a hidden implementation detail.**

---

## 17. Transactions

**Q17.1 (🟠 Deep).** Which of your service methods are `@Transactional`, and why those specifically?
- **Strong answer pattern:** Multi-step writes that must succeed or fail atomically — e.g., deleting a `Question` (which cascades to `Answer`s and potentially their `Vote`s) should be one transaction so a partial cascade failure doesn't leave orphaned rows. Simple single-repository-call reads/writes often don't need explicit `@Transactional` since Spring Data JPA repository methods are transactional by default at the method level.
- **Follow-up:** What's the default propagation and isolation level if you don't specify anything on `@Transactional`? (`REQUIRED` propagation, and isolation defaults to whatever the underlying DB's default is — for MySQL/InnoDB that's `REPEATABLE READ`, which is worth knowing since it's *not* the same as Postgres' default `READ COMMITTED`.)
- **Killer follow-up:** If a service method is annotated `@Transactional` and calls another `@Transactional` method *on the same class* (self-invocation), does the inner method's transactional boundary actually apply? (No — Spring's proxy-based AOP means self-invocation bypasses the proxy entirely, so the "inner" transactional annotation is silently ignored. This is one of the most common real Spring bugs and a strong signal of genuine understanding if you know it unprompted.)

---

## 18. Error Handling

**Q18.1 (🟡 Intermediate).** Walk through what happens, end to end, when someone `POST`s a question with a blank title.
- **Strong answer:** Jakarta Bean Validation annotation (e.g., `@NotBlank`) on the request DTO field → Spring MVC's argument resolver triggers validation on `@Valid @RequestBody` → validation failure throws `MethodArgumentNotValidException` before the controller method body ever executes → caught by `@ExceptionHandler(MethodArgumentNotValidException.class)` in `GlobalExceptionHandler` → mapped to 400 with your standard error shape plus the `validationErrors` field-message map, per your README's documented format.
- **Follow-up:** Is validation only on `POST`, or also enforced on `PUT`/`PATCH`? If `PATCH` allows partial updates, does `@NotBlank` on the DTO break partial updates (since a partial-update DTO might legitimately have a null title meaning "don't change this field," but `@NotBlank` would reject a null/blank value even when the intent was "not provided")? **This is a real design tension between validation-for-create and validation-for-partial-update that many student projects get wrong by sharing one DTO for both.**

**Q18.2 (🟡 Intermediate).** Why do `UserNotFoundException`, `QuestionNotFoundException`, and `AnswerNotFoundException` exist as separate classes instead of one generic `ResourceNotFoundException` (which your README shows you ALSO have)?
- **Strong answer:** Distinct exception types let you attach resource-specific context/messages cleanly and let the exception handler map them individually if their HTTP semantics ever diverge — right now they all map to 404, but having them separate means you could later decide, say, a missing nested resource behaves differently without a large refactor. The generic `ResourceNotFoundException` likely exists as a catch-all for cases not worth a dedicated type yet.
- **Follow-up (mild inconsistency risk):** Given you have both specific AND generic not-found exceptions, is there any inconsistency in your codebase about which one gets thrown where? This is worth auditing before an interview.

---

## 19. Testing

**Q19.1 (🟠 Deep).** Your build.gradle includes `spring-boot-starter-data-jpa-test` and `spring-boot-starter-webmvc-test` but only `junit-platform-launcher` for the runtime — no Mockito starter is explicitly listed (though `spring-boot-starter-webmvc-test`/`data-jpa-test` typically pull Mockito transitively via `spring-boot-starter-test`-style umbrellas in Boot 4.x's modularized test starters). **What do your tests actually test — do you have real test classes at all, or is this dependency scaffolding with no tests written?**
- **Honest-answer expectation:** If there are few/no real test files, say so plainly. Claiming "full test coverage" for a project where the test dependencies are present but no meaningful test classes exist is an easily-checked lie (`grep -r "@Test" src/test`).
- **Follow-up if tests exist:** For one controller test, is it a full `@SpringBootTest` (full context, real DB or embedded) or a slice test (`@WebMvcTest`, mocking the service layer)? What's the tradeoff?
- **Follow-up:** How would you test the `Vote` uniqueness constraint / concurrent voting behavior? (This needs either a repository-layer integration test hitting a real DB and asserting the second insert throws, or a concurrency test spinning up two threads — most student projects don't have this. If you don't, say so and describe how you'd write it.)

---

## 20. Performance & Scalability

**Q20.1 (🟠 Deep).** No caching layer exists in this project (confirmed — no Redis dependency). Where would caching help most, and where would it be dangerous?
- **Strong answer:** Helpful: `GET /questions/{id}` and `GET /answers/{id}` for popular/hot questions — read-heavy, changes relatively infrequently. Dangerous: caching vote counts/scores naively risks serving stale scores right after a vote, and caching user session/auth state incorrectly could serve stale role/permission data after a role change, creating a security gap (a demoted admin still treated as admin from cache).
- **Follow-up:** How would you invalidate a cached question on update? (Cache-aside with explicit eviction on write, e.g., `@CacheEvict` alongside `@CachePut`/`@Cacheable`.)

**Q20.2 (🟠 Deep).** Which endpoint in this API is the least scalable as written, and why?
- **Strong, project-specific answer:** `GET /questions/search?keyword=` — a `LIKE %keyword%` query is a full table scan with no usable B-tree index due to the leading wildcard; at scale this degrades linearly with table size and will be the first endpoint to time out under load. Second candidate: any per-user question/answer count endpoints (`/{id}/question-count`, `/{id}/answer-count`) if implemented as `COUNT(*)` over the full table each request instead of a maintained counter — cheap at small scale, real cost at large scale.

---

## 21. Failure Scenarios

**Q21.1 (🟠 Deep).** MySQL becomes unavailable mid-request. What does the caller actually see?
- **Strong answer:** Without explicit handling, a `DataAccessResourceFailureException`/`CannotCreateTransactionException` propagates up, and unless your `GlobalExceptionHandler` has a catch-all `@ExceptionHandler(Exception.class)`, it falls through to Spring Boot's default error handling — likely a generic 500 with Spring Boot's default whitebox error body, NOT your custom `{timestamp, status, error, message, path}` shape, since that specific exception type isn't in your documented exception table. **Does your GlobalExceptionHandler have a catch-all handler, or only handlers for the specific domain exceptions listed in the README? This is worth checking — an uncaught infra failure currently likely breaks your API contract.**
- **Follow-up:** What would you add to make this resilient — connection retry, circuit breaker, or just a clean catch-all 500 mapping to preserve the error contract? For a project this size, the honest, non-overengineered answer is: at minimum, a catch-all exception handler that preserves your JSON error shape even for unexpected exceptions.

**Q21.2 (🟡 Intermediate).** JWT validation starts failing for all users simultaneously (e.g., the signing secret got rotated without warning). What do users experience, and how would you diagnose it fast?
- **Strong answer:** Every authenticated endpoint returns 401 (or whatever your `AuthenticationEntryPoint` sends) — public endpoints (question search, get-by-id, etc.) keep working, which is actually a useful diagnostic signal (isolates the failure to the security filter, not the whole app). Diagnosis: check logs for signature-verification exceptions in the JWT filter, confirm the deployed `JWT_SECRET` env var actually matches what tokens were signed with (a redeploy that regenerates a default secret, per Q11.1's hardcoded-default risk, is a very plausible real cause of exactly this incident).

---

## 22. Debugging

**Q22.1 (🟠 Deep, realistic bug).** "A user reports they can see and edit another user's question. Reproduce this for me, mentally — what are the top 3 places you'd check first?"
1. Is the `@PreAuthorize` expression on `updateQuestion` actually comparing the *authenticated* user's id to the question's owner id, or accidentally comparing against a value from the request body (which an attacker fully controls and could spoof)?
2. Is the ownership check bypassed for `ADMIN` in a way that's leaking to all users (e.g., a bug where `hasRole('ADMIN')` was written as `hasAuthority('ADMIN')` and role storage doesn't match, causing the check to fail open rather than fail closed)?
3. Is the JWT principal being correctly re-resolved per request, or is there a stale/shared security context bug (rare, but possible with certain thread-pool/filter misconfigurations)?
- **This exact class of bug (authorization comparing against attacker-controlled input, or failing open) is the single most common real vulnerability in early-career ownership-check implementations — know this pattern cold.**

---

## 23. Code-Level Questions

**Q23.1 (🟡 Intermediate).** Why do you have a `mapper/` package for manual entity↔DTO mapping instead of using MapStruct or ModelMapper?
- **Strong answer:** Manual mapping is fully explicit and debuggable — no reflection-based magic, no annotation-processor build step to configure, and for a project this size the mapping logic is simple enough that a library adds more overhead (learning curve, generated-code debugging) than it saves. This is a legitimate, defensible engineering tradeoff for a solo/learning project.
- **Follow-up:** What's the actual cost of manual mapping as the project grows — e.g., every time you add a field to `Question`, how many places do you have to remember to update it (entity, request DTO, response DTO, mapper)? Is that a maintenance risk you're aware of?

**Q23.2 (🟠 Deep).** Why does `BaseModel` exist as an abstract superclass instead of each entity independently declaring `id`/`createdAt`/`updatedAt`?
- **Strong answer:** DRY — avoids repeating identical boilerplate (and JPA annotations) across `User`/`Question`/`Answer`/`Vote`. Needs `@MappedSuperclass` (not `@Entity`) so JPA knows it's not itself a persisted table but contributes columns to subclasses.
- **Follow-up (copy-detection):** Is `BaseModel` annotated `@MappedSuperclass`? What would happen if it were annotated `@Entity` instead (Hibernate would expect a separate strategy — table-per-class, single-table, or joined-table inheritance — and without one explicitly configured, this would likely fail to bootstrap or create an unwanted extra table).

---

## 24. Design Trade-offs

**Q24.1 (🟠 Deep).** What's the biggest thing you'd do differently if you rebuilt this from scratch?
- **A genuinely strong, specific answer beats a vague one.** E.g.: "I'd add the DB-level unique constraint and optimistic locking (`@Version`) from day one instead of an afterthought, because concurrency bugs like duplicate votes or lost updates are exactly the kind of thing that's invisible in manual single-user testing and only shows up under real concurrent load — I'd rather design for it upfront than retrofit it."

---

## 25. Requirement-Change Questions

**Q25.1.** "What if users need multiple active sessions across devices?" → Refresh-token rotation, or per-device token tracking (a `tokens`/`sessions` table), since a single stateless JWT with no revocation list can't selectively invalidate one device's session without invalidating all of them (or maintaining a blocklist, which reintroduces state you were trying to avoid with JWTs).

**Q25.2.** "What if we need to support duplicate-request-safe voting from a flaky mobile network retrying requests?" → Need true idempotency: an idempotency key header, or making the vote endpoint an idempotent PUT-style "set my vote to X" rather than "add a vote event," relying on the (user, answer) unique constraint as a natural upsert key.

**Q25.3.** "What if 100x traffic hits the search endpoint specifically?" → Move off `LIKE` entirely: MySQL FULLTEXT index at minimum, or a dedicated search engine (Elasticsearch/OpenSearch) if relevance ranking matters, decoupled from the primary transactional DB.

---

## 26. Behavioral / Ownership Questions

- What was the hardest part of this project, specifically (not generically — name the actual bug or design decision)?
- Tell me about a bug you introduced and how you found it. (Prepare a *real*, specific one — vague answers here are the single biggest tell of a copied project.)
- What are you least confident about in this codebase, right now, today? (A confident, specific answer — e.g., "the vote race condition, I'm honestly not 100% sure the DB constraint is doing the enforcement vs. an app-level check" — is a MUCH stronger signal than false confidence.)
- If another engineer joined tomorrow, what part of this would be hardest for them to understand? Why?

---

## 27. Copy-Detection Questions (concentrated list)

1. Why is the package `org.com.quora_backend` but the product is "QueryNest"? (Section 1)
2. Walk me through the exact SQL your `Vote` unique constraint enforces, and whether it's DB-level or app-level. (Section 16 — the most important one)
3. `LAZY` vs `EAGER` on your `@ManyToOne` fields — what's the JPA *default* you'd get if you never touched it, vs. what you actually set? (Section 8)
4. Why does `BaseModel` need `@MappedSuperclass` specifically, not `@Entity`? (Section 23)
5. What SQL does a specific derived-query repository method (e.g., `findByUserIdAndAnswerId`) generate? (Section 4)
6. What happens on self-invocation of a `@Transactional` method within the same class? (Section 17)
7. Does your `GlobalExceptionHandler` have a catch-all handler, or only the specific exceptions in your README's table? (Section 21)
8. Why is your build.gradle missing Spring Security/JWT/springdoc dependencies your README claims are implemented? (**Top of document — the single sharpest question available against this specific repo.**)
9. Is your `PATCH` implementation actually field-null-checking, or identical to `PUT`? (Section 7)
10. Where exactly is the authenticated user's identity read from on a write request — SecurityContext, or (incorrectly) a client-supplied field? (Section 22)

---

## 28. Killer Questions (top 20 — prepare these specifically)

1. Your `build.gradle` has no Spring Security, no JWT library, and no springdoc-openapi dependency. Does this project actually compile with the auth features your README describes?
2. Is the `Vote` (user, answer) uniqueness enforced by a database constraint or an application-level check? Prove it.
3. Two concurrent `PUT` requests to the same `Question` — is there a lost-update bug? Do you have optimistic locking (`@Version`)?
4. What SQL does `findByUserIdAndAnswerId` (or your equivalent) actually generate, and why does the method name have to match your entity fields exactly?
5. Walk me through JWT validation, byte by byte, from header parsing to `SecurityContextHolder` population.
6. What's the actual risk of your README publicly documenting the default JWT secret?
7. Does `@PreAuthorize` re-fetch the entity separately from the controller/service, causing a redundant query?
8. Is your `Question.title` search indexed? What happens to that query at 1M rows?
9. Does cascading delete on `Question` → `Answer` also correctly cascade to `Vote` rows, or do you leave orphans?
10. What happens on self-invocation of a `@Transactional` method?
11. Does your `GlobalExceptionHandler` have a catch-all, or does a raw DB outage leak Spring's default error format instead of your documented contract?
12. Is `PATCH` actually different from `PUT` in your implementation, or copy-pasted logic?
13. What does `ddl-auto=update` NOT protect you from, and why is that dangerous beyond "just don't use it in prod"?
14. Show me a real git commit where you fixed a bug you introduced yourself.
15. What MySQL transaction isolation level are you running under, and how does it differ from Postgres' default?
16. Is voting idempotent? What happens if the same vote request is sent twice?
17. Where's the TOCTOU race between your username-availability check and actual user creation — what really prevents duplicates?
18. If you removed `@EnableJpaAuditing`, what specifically stops working, mechanically?
19. What's the least scalable endpoint in this whole API, and why, specifically?
20. What would you personally flag as the weakest part of this codebase if you were reviewing someone else's PR of it?

---

## 29. Resume / README Inconsistencies

- 🔴 **README claims Spring Security + JWT auth is implemented; `build.gradle` has no security or JWT dependency.** Verify and fix before any interview — this is the top risk in the entire document.
- 🔴 **README claims Swagger UI via springdoc-openapi; `build.gradle` has no springdoc dependency.** Same category of risk.
- 🟡 README documents `jwt.secret` with a hardcoded fallback default committed to a public repo — a real vulnerability if ever deployed without setting the env var, not just a style nitpick.
- 🟡 No `@Version`/optimistic locking mentioned anywhere in the documented data model, but concurrent-update scenarios are a near-certain interview topic given the domain (voting).
- 🟡 Roadmap lists "move DB credentials and JWT secret fully to environment variables for all environments" as still outstanding — consistent with the local-only plaintext note, at least this part of the README is self-consistent and honest.

---

## 30. Final Readiness Score (0–10)

| Dimension | Score | Why |
|---|---|---|
| Technical depth | 5/10 | README describes solid REST/JPA fundamentals, but the security/Swagger gap undermines confidence in the whole document until resolved. |
| Architecture understanding | 6/10 | Clean layered monolith is a reasonable, defensible shape for this scope — score reflects unverified implementation details, not the design itself. |
| Java understanding | Unscored | Insufficient source access to assess directly — verify with real code. |
| Spring understanding | 5/10 | Core Spring Data JPA / MVC / validation usage is plausible and consistent with real dependencies; Security layer is unverified/possibly non-functional. |
| Database understanding | 6/10 | Schema design (Vote uniqueness, BaseModel, auditing) shows real relational thinking; indexing and cascade depth are open questions. |
| Distributed systems understanding | N/A | Out of scope — this is a monolith with no Redis/Kafka/microservices, correctly so for this project size. |
| Security understanding | 3/10 | Cannot be scored higher until the build.gradle mismatch is resolved and JWT internals are demonstrably understood, not just described. |
| Testing | Unscored | Could not verify actual test coverage — audit `src/test` before your interview. |
| Debugging | Unscored | Depends entirely on real incident stories you can only supply yourself. |
| Scalability | 5/10 | The domain naturally surfaces real scalability discussion points (search, N+1, connection pool) even without load-testing evidence. |
| Ownership credibility | Unscored until you answer Section 26/27/28 questions with real specifics. |
| Interview readiness | **Not ready until the build.gradle issue is resolved.** Everything else is workable with 3–5 days of focused prep. |

---

## 31. 7-Day Preparation Plan (based on what this repo review actually found)

**Day 1 — Fix the critical mismatch.**
Run `./gradlew clean build` locally. Confirm whether Spring Security/JWT/springdoc actually compile and run. If they don't, either (a) add the missing dependencies and get it building for real, or (b) rewrite the README to accurately describe what's implemented today. Do not walk into an interview with this unresolved.

**Day 2 — Concurrency audit.**
Open your `Vote` entity and confirm, with your own eyes, whether the (user, answer) uniqueness is a `@Table(uniqueConstraints=...)` DB constraint or an app-level check. Same for any lost-update risk on `Question`/`Answer` updates — do you have `@Version` anywhere? Fix or document honestly.

**Day 3 — Security internals.**
Trace your actual `JwtAuthenticationFilter`, `SecurityConfig`, and `@PreAuthorize` expressions line by line. Be able to explain claim structure, signing algorithm, filter chain placement, and exactly what stops role-tampering, from memory, without looking at the code.

**Day 4 — N+1 and indexing.**
Turn on `spring.jpa.show-sql=true` (already default per your README), hit `GET /questions` and `GET /users/{id}/questions`, and count the actual SQL statements printed. Confirm or refute the N+1 hypothesis in Section 8 with real evidence, not guesses.

**Day 5 — Exception handling completeness.**
Read `GlobalExceptionHandler` end to end. List every exception type it handles vs. every exception type that could realistically occur (DB constraint violations, DB connection failures, `@PreAuthorize` denials). Add a catch-all handler if missing.

**Day 6 — Testing pass.**
Check `src/test` for what actually exists. Write at least one concurrency test for the vote race condition and one test proving ownership authorization actually rejects a non-owner — these directly answer two of the Killer Questions with a live demo instead of a claim.

**Day 7 — Mock interview.**
Have someone (or re-run yourself) through Sections 27–28 (Copy-Detection and Killer Questions) cold, out loud, with your laptop open to the real code the whole time. If you can't answer a question by pointing at a specific line, that's your remaining gap — go fix it, not just memorize an answer.

---

*This document was built from your actual README (verbatim) and your actual `build.gradle` (fetched directly from GitHub). Deeper source files (controllers, security classes, entities, mappers) could not be automatically browsed due to GitHub blocking automated directory access — paste them in and I'll do a second, code-cited pass with exact-line-level questions.*
