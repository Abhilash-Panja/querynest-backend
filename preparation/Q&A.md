## 1. Question

How would you describe the purpose of this repository in one sentence, and what evidence in the code supports that description?

### What the Interviewer Is Testing

This question checks whether the candidate can connect the product concept to the actual implementation rather than relying on a generic readme description.

### Strong Answer

I would describe this repository as a backend API for a Q&A platform where users can create profiles, ask questions, answer questions, and vote on answers. The strongest evidence is in the package structure and endpoints: `UserController` manages user creation and profile flows, `QuestionController` handles question creation and search, `AnswerController` handles answer lifecycle and voting, and the `model` package contains `User`, `Question`, `Answer`, and `Vote` entities. The app is clearly centered on a classic Stack Overflow-like workflow rather than something else like a blog or marketplace. The main evidence is not just the names, but the relationships in the model layer: `Question` belongs to a `User`, `Answer` belongs to both a `Question` and a `User`, and `Vote` links a `User` to an `Answer`, which makes the feature set coherent and product-specific.

The design is intentionally simple and easy to defend as an MVP: it has the essential entities and service logic for a community-driven knowledge-sharing product, but it stops before adding more advanced features like notifications, reputation tiers, moderation, or a UI. If I were challenged on scope, I would say this is a backend-first Q&A MVP rather than a full production system, and I would be explicit that the repo does not include a frontend or deployment environment.

### Likely Follow-Up

- What makes you confident this is a Q&A product instead of a generic CRUD backend?
- Which piece of evidence would you use to justify that claim to a skeptical reviewer?

### Strong Follow-Up Direction

I would point to the entity relationships, the route names, and the actual business logic in the `service` layer. The existence of `vote_score`, `searchQuestions`, and ownership checks around user-created content makes the product intent much clearer than a generic CRUD app. I would also mention that the repository does not look like a generic admin system because it focuses on content creation, ownership, and community evaluation.

### Red Flags to Avoid

- Saying the repository is “a social media app” without evidence.
- Claiming a full production feature set that is not present in the code.
- Describing the project as scalable or mature without evidence.

## 2. Question

Why is the layered architecture in this project a reasonable design for the current repository, and where might it become overhead?

### What the Interviewer Is Testing

This question checks whether the candidate understands when a layered design adds clarity and when it may be too abstract for the project’s scale.

### Strong Answer

The repository follows a clean separation between controller, service, repository, model, and DTO layers. That is a strong design choice because the product is doing real domain work: user management, question lifecycle, answer updates, and voting logic. The controllers define the HTTP API surface, the services own business rules, and the repositories handle persistence. That separation makes it easier to test and reason about business behavior without mixing concerns. It is also visible in how `QuestionServiceImp` and `AnswerServiceImpl` manage validation and ownership rather than burying those rules inside the controller.

This is particularly justified in a backend where the business logic is not trivial. The code checks for duplicate emails, duplicate usernames, missing questions or users, ownership mismatches, and vote transitions. Those rules belong in the service layer because they encode the business meaning of the application. If those checks lived in controllers alone, the logic would be harder to test and easier to duplicate.

The trade-off is that the design adds some abstraction and indirection. For a small project, a single controller/service model could have been enough, but the current structure makes the code more readable and extensible. It becomes overhead when the project gets smaller or more trivial, and it may add more complexity than needed for a single-user demo. If the application remains at this scale, I would still defend the layering, but I would revisit it if the domain becomes much simpler or the team is prioritizing speed over structure.

### Likely Follow-Up

- What layer would you change first if you had to simplify the codebase?
- How would you decide whether the abstraction is worth it?

### Strong Follow-Up Direction

I would say I would not remove the layering unless the project dramatically shrank in scope or the team strongly needed a faster prototype. For a Q&A API, the clear separation is valuable because it supports authorization, validation, and persistence concerns independently. I would consider simplification only if we saw repeated boilerplate, a very small domain surface, or a team that was struggling with abstraction overhead.

### Red Flags to Avoid

- Acting as if every project must use a clean layered architecture.
- Claiming the architecture is automatically “best practice” without explaining the product trade-off.
- Ignoring the cost of indirection or the simplicity of a small MVP.

## 3. Question

The project uses MySQL with Spring Data JPA. Why is that a sensible fit for a Q&A backend, and what limitations does that choice create?

### What the Interviewer Is Testing

This question tests whether the candidate can justify the database choice and discuss the trade-offs of a relational model for a content-heavy app.

### Strong Answer

A relational database is a natural fit for this project because the domain model is strongly relational: users own questions, questions contain many answers, and answers have one author and a vote score. A MySQL-backed JPA model gives us straightforward transactions, constraints, and query patterns without much operational complexity. The `@Entity` classes and repository methods like `findByUserId`, `countByUserId`, and `findByTitleContainingIgnoreCase` are consistent with a conventional relational structure. The project also benefits from a stable schema and clear relationships that can be enforced with foreign keys and uniqueness constraints, which is appropriate for a content platform that values integrity.

I would also say this choice is reasonable for an MVP because it keeps the codebase simple and accessible. The database can enforce uniqueness for email and username, maintain scores, and support basic searches without adding infrastructure like Elasticsearch or a NoSQL document store. That simplicity is a strength when the requirements are still evolving.

The limitation is that a relational model is not the only strong choice. This repository does not show features like full-text indexing, recommendation logic, or multi-table analytics, so the data model is intentionally simple. If the project scaled to heavy search traffic or required richer content modeling, I would consider a search index or denormalized read model. I would also be careful around N+1 query issues with lazy-loaded relationships if large result sets became common.

### Likely Follow-Up

- What would you do if the number of questions and answers grew much faster than the number of users?
- Which part of the design would become a bottleneck first?

### Strong Follow-Up Direction

I would say the likely bottleneck would not be the relational model itself, but the read pattern around content retrieval and search. A simple `findByTitleContainingIgnoreCase` is fine for an early-stage app, but a production system would likely need better indexing, search tuning, and maybe a dedicated search layer. I would also watch lazy-loading and large joins, especially when fetching user profiles with lots of question and answer records.

### Red Flags to Avoid

- Claiming MySQL is automatically more scalable than every other choice.
- Suggesting a “NoSQL is always better” argument without product-specific reasoning.
- Ignoring the importance of schema and data integrity in a Q&A app.

## 4. Question

The application uses JWTs and Spring Security. Why is that a good fit for this repository, and which security weaknesses are most obvious?

### What the Interviewer Is Testing

This question checks if the candidate can explain the security model, not just say “we used JWT,” and whether they can point to practical gaps.

### Strong Answer

This project is a stateless API backend, so JWT is a sensible choice. `SecurityConfig` disables CSRF because the app uses token-based authentication instead of server-side session state, and the JWT filter reads the `Authorization` header and populates the security context before the request is processed. `UserPrincipal` wraps the `User` entity and exposes role-based authorities, while `@PreAuthorize` checks enforce ownership or admin requirements on update operations. That gives the API a simple, explicit security model and works well for a backend that does not appear to maintain sessions or cookies.

I also think the design is understandable from an interview standpoint: the developer has separated the authentication pipeline from the domain logic, and each controller checks the right permissions in the right place. It keeps the API relatively easy to reason about.

The main weaknesses are also visible. The repository shows no refresh-token implementation, no token revocation or logout mechanism, no rate limiting, and no approach for rotating secrets or improving secret management beyond an external `jwt.secret` property. The app stores a password hash in the user entity and the code comments note that it stores the BCrypt hash, but I do not see evidence of password hashing being configured in the app setup itself. I would also want to see stronger protection against abuse, such as rate-limiting, logging, and request auditing, before calling this security model production-ready.

### Likely Follow-Up

- How would you improve the current authentication flow for a real production system?
- Which security controls would you add first?

### Strong Follow-Up Direction

I would prioritize token lifecycle management, stronger secret management, and rate limiting. I would also want to add secure password policy enforcement, audit logs for sensitive actions, and a clear strategy for expiration, refresh, and invalidation. In a production environment, I would not rely on the current stateless model alone; I would layer in operational controls and explicit security policy around access and abuse.

### Red Flags to Avoid

- Saying JWT is automatically secure because it is a token.
- Ignoring session and refresh management.
- Claiming the app has “enterprise security” without evidence.

## 5. Question

The project enforces ownership checks for user, question, and answer modifications. Why is that important, and what are the trade-offs?

### What the Interviewer Is Testing

This checks whether the candidate understands both business rules and permission boundaries in a content platform.

### Strong Answer

Ownership checks are essential in this kind of app because the domain is content-created by users. A user should not be able to modify another user’s profile, another person’s question, or another person’s answer without a specific rule allowing it. The repository reinforces that with `@PreAuthorize` expressions like `hasRole('ADMIN') or #id == authentication.principal.user.id` and method-level ownership checks such as `questionService.isOwnedByCurrentUser` and `answerService.isOwnedByCurrentUser`. That is a concrete expression of product safety and trust.

This is not just a technical requirement; it is the product’s governance model. Without this layer, a user could easily destroy or rewrite other people’s content, which would break the trust model of the platform. The authorization design is consistent with a community product, and it is an example of how domain logic and security rules align with the business reality.

The trade-off is that the system is intentionally narrow. It treats ownership as a direct and simple rule, but it does not include richer moderation workflows, delegation, soft-deletion, or approval chains. That is acceptable in an MVP, but if the platform were to support complex moderation or content review, I would expect more sophisticated policy models. The current design is good for a tight product with clear ownership semantics, but it would likely feel too rigid as the product grows.

### Likely Follow-Up

- How would you handle moderation for abuse cases without compromising user trust?
- What if a question owner is inactive and the content becomes stale or harmful?

### Strong Follow-Up Direction

I would say moderation is a separate concern from ownership. Ownership protects user edits, while moderation handles abuse, policy violations, and stale or inappropriate content. I would likely add an admin-facing moderation flow, logging, and content status management. If the platform grows beyond a small forum, I would also model whether content can be soft-deleted, flagged, or reviewed by moderators before becoming visible to the wider community.

### Red Flags to Avoid

- Suggesting content ownership is optional or unimportant.
- Treating security checks as an afterthought rather than part of the product logic.
- Ignoring the difference between user ownership and moderation.

## 6. Question

How does the voting logic work in `AnswerServiceImpl`, and why is that a meaningful feature for this project?

### What the Interviewer Is Testing

This question tests whether the candidate understands business logic, state transitions, and data integrity in a content-ranking flow.

### Strong Answer

The voting logic is implemented in `AnswerServiceImpl.voteAnswer`, and it is more than just a simple increment. The code checks whether a user already voted on an answer and then either toggles the vote off if the same vote type is clicked again or switches from upvote to downvote when the vote type changes. The logic computes a score delta based on the previous vote contribution and the new vote contribution, then recalculates `answer.voteScore` before saving it back to the database.

This is a good product feature because it gives the community a way to surface better answers and, in a broader product, it would support reputation or sorting. It also demonstrates domain logic beyond CRUD. The repository uses a dedicated `Vote` entity with a unique constraint on `(user_id, answer_id)`, which is a strong design choice because it prevents duplicate votes from the same user on the same answer while minimizing ambiguity.

The trade-off is that the current implementation is still fairly simple. There is no evidence of vote penalties, time decay, anti-abuse rules, or user reputation integration. That is okay for an MVP, but it is also the kind of feature that becomes much more nuanced when the product becomes more active or more valuable. A senior engineer would likely want to challenge the system around spam prevention, sybil attacks, and vote fairness before treating it as a production-ready ranking model.

### Likely Follow-Up

- What would you do to prevent people from gaming the voting system?
- How would you decide whether the answer score should be weighted or capped?

### Strong Follow-Up Direction

I would say the first line of defense is to keep vote records immutable and explicit, and to prevent duplicate voting across the same answer. Beyond that, I would consider account validation, abuse detection, and moderation rules, especially if the platform started carrying meaningful reputation or content trust. I would not assume a simple score is enough to surface the best answers in a larger community.

### Red Flags to Avoid

- Treating vote scores as objective truth without considering manipulation.
- Forgetting uniqueness constraints or duplicate-vote prevention.
- Overstating the maturity of the ranking system without mentioning abuse and moderation.

## 7. Question

What is the most important design decision visible in the `model` package, and why would a senior engineer care about it?

### What the Interviewer Is Testing

This question checks whether the candidate sees data-model choices as product and engineering decisions rather than just database plumbing.

### Strong Answer

The most important design decision is that the data model treats content as relational and owned by users. `Question` belongs to a `User`, `Answer` belongs to both a `Question` and a `User`, and `Vote` records a singular relationship between a user and an answer. This maps well to the business problem: content is created by users, it is associated with one thread of discussion, and it can be evaluated. The `BaseModel` also adds `createdAt` and `updatedAt`, which matters because it gives the platform basic auditing and timeline behavior without needing a separate framework or event model.

A senior engineer would care because this design is simple but still correct for the domain. It gives the code a strong conceptual model and makes business rules clear, such as “only the author can edit” or “one user can only vote once per answer.” The data model is not just persistence; it is the product logic in a relational form.

The trade-off is that this is a deliberately minimal design. There are no comments, tags, moderation tables, or richer social graph concepts. That keeps the code easy to understand, but it also means the model would need expansion if the product eventually needed community features, ranking, or more nuanced content governance. This is a good example of architecture designed to keep the first version understandable, not to optimize for every future scenario.

### Likely Follow-Up

- What changes would you make if comments or tags were introduced tomorrow?
- How does the current model affect query complexity over time?

### Strong Follow-Up Direction

I would say the current model is good for the MVP but would likely need either additional relational entities or some denormalization as the product matured. Comments and tags would add more join-heavy queries, and a richer reputation system would likely need a separate table or event-driven model. I would also expect read patterns to become more important as content grows, so indexes and query optimization would matter more than they do today.

### Red Flags to Avoid

- Claiming the model is “perfect” because it is normalized.
- Forgetting the product context and discussing normalization in the abstract.
- Ignoring how business rules influence the table design.

## 8. Question

Where is the real business logic in this project, and why is it not all in the controllers?

### What the Interviewer Is Testing

This checks whether the candidate can explain the value of service-layer separation and the difference between transport logic and domain logic.

### Strong Answer

The real business logic lives primarily in the service layer: `UserServiceImp`, `QuestionServiceImp`, and especially `AnswerServiceImpl`. That is where the code validates user identity, checks author ownership, enforces duplicate username and email rules, updates vote scores, and interprets the state transitions within the domain. The controllers are responsible for receiving HTTP requests and returning HTTP responses, but they do not own the domain rules. That distinction is important.

This separation is practical because the same logic should not need to be reimplemented at each endpoint. For example, answer creation, answer deletion, and answer voting all involve consistent rules around user existence, question existence, ownership, and state updates. If those rules lived directly in each controller, the code would drift and tests would become brittle. By pushing the logic into services, the code becomes easier to validate, easier to reason about, and stronger against accidental duplication.

The trade-off is that some repository patterns can feel heavier than necessary in a small app. But the current architecture is still understandable and appropriate for an API-driven project. I would defend it because the domain rules are meaningful, not just CRUD operations, and the project clearly benefits from explicit service-level behaviors.

### Likely Follow-Up

- What would happen if the project grew to multiple controllers or multiple APIs?
- How would you keep the architecture from turning into an overly abstract system?

### Strong Follow-Up Direction

I would say the architecture would remain beneficial as the codebase grows, but the team would need to be disciplined about not creating unnecessary abstraction. The service layer is a good place to keep product rules because it centralizes them. I would resist over-engineering by keeping the domain boundary clear and using interfaces only when they solve a real maintenance problem.

### Red Flags to Avoid

- Saying controllers are “the important part” because they expose endpoints.
- Claiming all logic belongs in entities or repositories without considering domain rules.
- Ignoring the complexity of validation and authorization.

## 9. Question

How would you describe the project’s maturity level, and what would you say is its strongest technical decision versus its biggest limitation?

### What the Interviewer Is Testing

This checks whether the candidate can assess project maturity honestly and distinguish a working MVP from a production platform.

### Strong Answer

I would describe the project as an early-stage MVP or backend prototype. The repository clearly demonstrates the main product flow and the core domain model, but it is not a complete production product. The code includes user registration, authentication, question creation, answer creation, and vote handling, which are enough to prove the business concept works at a basic level. The project is not yet an end-to-end product because there is no frontend, no deployment config, no monitoring, and no broad operational tooling visible.

The strongest technical decision is the separation of concerns and the explicit domain model. A user’s ownership, a question’s author, an answer’s author, and a vote’s relationship are all modeled concretely, which makes the business rules understandable and defendable. The code is not just “crud with a database”; it reflects product logic and access control.

The biggest limitation is the lack of production maturity. There is no evidence of CI/CD, observability, environment configuration beyond local MySQL, or a real strategy for security hardening. This does not make the project weak; it means the project is honest and focused. It is a backend foundation, not a full, production-ready social product.

### Likely Follow-Up

- What is the first thing you would build if you had six months to evolve this project?
- What would you avoid doing until the basics were stable?

### Strong Follow-Up Direction

I would say the first major step is to harden the platform around operational readiness: monitoring, structured logging, security reviews, and environment configuration. After that, I would add the features that make the product useful at scale, such as moderation, notifications, and better search. I would avoid adding too much product complexity before the core system was stable and observable.

### Red Flags to Avoid

- Saying it is “production ready” without evidence.
- Calling it “scalable” based only on a neat design.
- Ignoring operational concerns that are not visible in the repository.

## 10. Question

The project includes availability checks for usernames and emails. Why does that matter, and how does it affect the user experience?

### What the Interviewer Is Testing

This question tests whether the candidate can connect product usability to backend validation and data integrity.

### Strong Answer

Availability checks matter because they reduce friction in the sign-up flow and prevent obvious conflicts in a user-generated system. In `UserController`, the API exposes `check-username` and `check-email` endpoints, and `UserServiceImp` turns those into explicit responses indicating availability. This is a strong user experience decision because the client can validate input before the user submits a form and before the backend has to reject a duplicate record later. That reduces wasted effort and makes the product feel more polished.

In terms of product design, it also prevents a class of user experience problems where a user fills out a registration form, submits it, and gets an error after the fact. A pre-validation check is a good fit for a backend API that may serve a frontend or mobile client, because the logic is centralized and simple to reuse.

The trade-off is that it is still a very simple implementation. There is no evidence of async verification, social sign-in, or identity proofing. That is fine for a small app, but it would need more sophistication if the platform had more security-sensitive flows or a larger user base. I would still defend it as a good early-stage decision: it improves usability without introducing much complexity.

### Likely Follow-Up

- What happens if the user changes a username or email after signup?
- What security concerns exist with username and email validation endpoints?

### Strong Follow-Up Direction

I would say the same validation rules are reused during updates and profile changes. The current design is intentionally simple, but I would want to consider whether a username or email check endpoint should be rate-limited or protected against enumeration in a production system. In a broader app, leaking whether an email is taken can be a privacy or abuse issue, so the final design should be deliberate about when and how to reveal that information.

### Red Flags to Avoid

- Ignoring the value of pre-validation in user flows.
- Overselling the system as secure without protecting the endpoint from abuse.
- Treating validation checks as purely cosmetic rather than product logic.

## 11. Question

The code uses `@PreAuthorize` and role checks for user and content ownership. Why is that a good pattern here, and what would you improve if the application grew?

### What the Interviewer Is Testing

This question tests whether the candidate understands declarative authorization, product rules, and the limits of a simple role model.

### Strong Answer

The use of `@PreAuthorize` is a good pattern here because it keeps authorization decisions close to the endpoint while still allowing business-level logic to be evaluated cleanly. In `QuestionController` and `AnswerController`, the application checks whether the current user is an admin or whether the user is the owner. That makes the API contract clear and centralizes the permission policy around the action, not just the data. It also works nicely with `UserPrincipal`, which contains the role and user identity.

I would defend the approach because it is explicit and readable. It is trivial to understand why a user cannot modify another user’s resource, and the same pattern can be reused across many endpoints. This is an especially good pattern for a backend API because the rules are not secret—they are encoded in the code and can be reviewed by engineers.

If the system grew, I would want to go beyond a role-by-owner model and consider richer policy evaluation. A simple admin-or-owner expression is fine for a small community, but a larger system might need resource-specific permissions, team-based access, moderation workflows, or external authorization policies. I would also want to separate the application’s authorization language from the code in a clearer policy layer if the system became more complex.

### Likely Follow-Up

- What if a user can be part of a team or workspace rather than just a single owner?
- How would you keep the rules maintainable as the app grows?

### Strong Follow-Up Direction

I would say the model should eventually reflect actual organizational or social rules rather than only a binary owner/admin distinction. That might mean explicit permission objects, policy services, or a richer domain model. I would keep the current pattern while the app is small, but I would not assume it remains sufficient if the product starts modeling collaborative or moderated access.

### Red Flags to Avoid

- Treating role checks as a full authorization system.
- Ignoring the difference between ownership and broader access control.
- Claiming a simple `@PreAuthorize` expression is enough for every future requirement.

## 12. Question

What is the strongest evidence that this project is designed for a product rather than just a set of CRUD endpoints?

### What the Interviewer Is Testing

This question checks whether the candidate can distinguish a generic DAO layer from a product with intentional business behavior.

### Strong Answer

The clearest evidence is the combination of domain-specific behavior and data relationships. It is not simply a `UserRepository`, `QuestionRepository`, and `AnswerRepository` with CRUD methods. The application includes `vote_score`, duplicate protection, ownership authorization, question searches, and user profile counting. The repository and services also encode business logic rather than just persistence operations. That is a key sign that the project is not a trivial CRUD exercise.

The product intent is also visible in how the service layer transforms requests into domain actions. For example, `QuestionServiceImp.createQuestion` takes a user ID and maps it to a `Question` tied to a `User`, while `AnswerServiceImpl.voteAnswer` works through the user’s existing vote state to update the score. These are not generic database actions; they are domain-specific operations.

The limitation is that the project remains intentionally small and product-scope is still narrow. It does not show a large user graph, recommendations, moderation, or notifications. But the code absolutely shows that the developer was thinking in product terms: who owns content, how content is ranked, and how users interact with the platform.

### Likely Follow-Up

- What would you add to make the product feel more “social” and less like a backend exercise?
- Which product features are still missing from the current model?

### Strong Follow-Up Direction

I would call out moderation, notifications, reputation systems, and richer discovery features as the next layer. Those would make the product feel like a live community rather than a simple transactional backend. I would also mention that a user profile is present, but the product does not yet show a social graph or engagement model beyond the core Q&A flow.

### Red Flags to Avoid

- Saying the product is “fully social” where the repo shows only a minimal Q&A flow.
- Ignoring the difference between a domain model and a complete product experience.
- Claiming advanced features exist without repository evidence.

## 13. Question

What are the most important reliability and failure-handling patterns in this project, and what is still missing?

### What the Interviewer Is Testing

This question checks whether the candidate can assess operational stability and error handling realistically rather than just naming common Spring patterns.

### Strong Answer

The project demonstrates a few meaningful reliability patterns. Domain-specific exceptions such as `UserNotFoundException`, `QuestionNotFoundException`, and `UnauthorizedAccessException` make application failures explicit rather than generic. I also see validation in the controller layer and checks in the service layer before writes happen. That means a large portion of common failure modes is handled before the database update occurs. On top of that, `GlobalExceptionHandler` centralizes error handling, which makes the API easier to consume and reason about.

The code does a good job of turning database and business issues into controlled responses, which is important for an API. It is also good that duplicate usernames and emails are prevented before the database writes bad data. Those are concrete examples of reliability and data integrity decisions.

What is still missing is the broader production picture: there are no retry patterns, no queue-based decoupling, no circuit breakers, no health endpoints, and no metrics or log aggregation. The repository also does not show any degraded-mode behavior when dependencies fail. That is not a criticism of the current project; it is simply an honest statement that the code is in an early engineering maturity stage. It is a solid backend API, but it is not yet a resilient, observable production platform.

### Likely Follow-Up

- How would you measure whether the API is healthy in production?
- Which failure mode would you address first if the app had real traffic?

### Strong Follow-Up Direction

I would start with observability and failure classification: logs, response times, 4xx versus 5xx patterns, and a simple health endpoint. Then I would look at attack and abuse protection and the quality of the error contracts. Operational readiness is a bigger gap than feature polish in a backend like this.

### Red Flags to Avoid

- Saying “the app handles errors” without looking at the actual exception strategy.
- Treating database errors as equivalent to domain errors.
- Ignoring the difference between validation and production resilience.

## 14. Question

What would a senior engineer likely challenge about this repository, and how would you answer that challenge confidently?

### What the Interviewer Is Testing

This question checks for honest self-awareness and the ability to defend trade-offs without pretending the project is perfect.

### Strong Answer

A senior engineer would likely challenge the balance between simplicity and production readiness. The repository is very easy to understand, which is a strength, but it also lacks the operational maturity of a larger system. I would expect a challenge around whether the project actually has enough safeguards for security, observability, and abuse prevention to be called production-ready. My answer would be straightforward: it is a strong backend MVP, but not a mature production platform.

I would also expect a challenge around the authorization model. The `@PreAuthorize` checks are sensible for the current scale, but if the product matured into a collaborative environment with roles beyond admin and owner, the simple model would not be sufficient. I would answer that directly: the logic is intentionally simple and appropriate to the current product, but it sets a clear boundary for future extension rather than pretending to solve a larger policy problem.

I think the honest defense is to frame the project around what it does well and what it intentionally does not handle. The design is not trying to be broad or abstract; it is trying to implement the core Q&A flow clearly. That is a valid engineering goal, especially for an early-stage project. The risk is not in the core architecture, but in assuming that early simplicity is enough for a much larger product.

### Likely Follow-Up

- What would you change if a senior engineer said the project is too naive for production?
- Where is the first place you would add complexity for scale?

### Strong Follow-Up Direction

I would say I would start by adding better observability, validation, logging, and abuse protection. The next layer would be more robust access control and a clearer framework for moderation and operational metrics. I would not broaden the app before making the core system safer and more measurable.

### Red Flags to Avoid

- Acting defensive instead of acknowledging the limitations.
- Saying complexity is “always necessary” without a product reason.
- Claiming the project is better than it is because the code is organized.

## 15. Question

How would you test the critical user flow in this project, and what kinds of tests matter most?

### What the Interviewer Is Testing

This checks whether the candidate understands how to validate both business behavior and API contract quality in a backend application.

### Strong Answer

The critical workflow is the user -> auth -> question -> answer -> vote path. I would test that flow from the API boundary, because the product is most meaningful when the request and response contract works end-to-end. The most important tests would include: successful user registration, failed duplicate username or email, successful login and JWT generation, question creation tied to the authenticated user, answer creation under a valid target question, and voting that updates the stored `voteScore` correctly.

I would also want tests that confirm authorization boundaries: a user cannot edit another user’s question or answer, and an admin can perform the same action if that policy is intended. Those are the kinds of tests that protect the business rules from regressions and ensure that the API is not just compiling but behaving correctly.

The gap in the repository is that the tests do not appear to cover the core flows extensively. That is a realistic early-stage limitation. If I were improving the project, I would prioritize integration tests around the controllers and service layer, because that is where the business behavior resides. A unit-test-only approach would miss important data and authorization interactions.

### Likely Follow-Up

- Which tests would you write before changing the voting logic?
- How would you validate the auth flow without relying only on mock-based tests?

### Strong Follow-Up Direction

I would focus on integration-style tests that exercise the real data flow with a test database, because the logic relies on repository behavior and authorization context. I would not rely solely on mocking because that can hide the exact behavior that matters in this domain. The real risk is not whether a mock returns a value; it is whether the system correctly updates the database and enforces the policy.

### Red Flags to Avoid

- Writing tests that only assert mock calls instead of real behavior.
- Testing only happy-path scenarios and ignoring authorization failures.
- Assuming a large unit-test suite is enough without integration coverage.

## 16. Question

What technical debt is visible in this project, and which debt would you address first?

### What the Interviewer Is Testing

This question checks whether the candidate can identify real debt in the code as opposed to generic “we need better architecture” commentary.

### Strong Answer

The clearest technical debt is that the project is intentionally minimal but not yet hardened for real-world use. There is no evidence of a broader observability strategy, no structured deployment configuration, no rate limiting, and no full testing coverage around the main flows. The code is readable and well-labeled, but there are also a few places where the domain could become fragile if the product grows: the vote model, the simple search, and the resource authorization logic are all valid for an MVP but not necessarily sufficient for a large online community.

The first debt I would address is the lack of test coverage around the critical workflows. That is the most immediate risk because the core product behavior is the thing that matters most. If the registration, authentication, question, and voting flows are not well protected, a future change is more likely to break the product in subtle ways.

I would also want to strengthen observability and security. Those are not just “nice-to-haves”; they are what make a product safe and maintainable. In a small project, those may be deferred, but they would be among the first things I would address if the platform had any real user traffic or business value.

### Likely Follow-Up

- Why would you prioritize tests over additional features?
- Which type of debt creates the most risk in a backend like this?

### Strong Follow-Up Direction

I would say product trust is the highest-risk issue. If the core flows are not protected by tests and the system lacks operational visibility, the project can fail in a way that is hard to diagnose. Features can always be added later, but without reliable core behavior and observability, the product is fragile.

### Red Flags to Avoid

- Claiming “technical debt” is only a code cleanliness issue.
- Ignoring the difference between structural debt and operational debt.
- Treating all debt as equally important.

## 17. Question

If you had more time, what would you redesign first in this repository, and why?

### What the Interviewer Is Testing

This checks whether the candidate can prioritize a redesign based on real product and architecture constraints rather than chasing abstract improvements.

### Strong Answer

The first thing I would redesign is the operational and security foundation rather than the domain model itself. The current domain model is actually quite good for a small app, but the project is missing the operational structures that you need once the software is valuable. I would improve the configuration and resilience story first: better environment management, monitoring, structured logging, rate limiting, and a clearer token lifecycle. I would also want to revisit how identity and authorization are handled if the product required more nuanced permissions.

I would not start by rewriting the `User`, `Question`, and `Answer` model because that model is understandable and consistent with the business problem. The bigger issue is not the concept of a Q&A platform; it is the risk that a simple backend will become brittle without operational tooling and security discipline. A good redesign would preserve the product concept while adding the missing guardrails.

So the answer is that I would not throw away the domain model, but I would invest in the infrastructure and governance pieces around it. That includes stronger validation, better metrics, and a more intentional auth lifecycle. The project currently makes good product decisions; it is the production-readiness layer that is thin.

### Likely Follow-Up

- What would you keep unchanged even if you had the time to redesign the system?
- Which design is most worth preserving?

### Strong Follow-Up Direction

I would keep the core entity model and the service-oriented business flow. The system’s basic idea is sound, and many of the key product concepts are already there. The redesign would focus on the platform around the product rather than the product idea itself.

### Red Flags to Avoid

- Suggesting a rewrite for style rather than necessity.
- Forgetting that the domain model is already aligned with the problem.
- Treating operational and security work as secondary to the feature set.

## 18. Question

What would you say is the biggest product or engineering trade-off in this project, and why is it acceptable today?

### What the Interviewer Is Testing

This question tests whether the candidate can identify the central compromise between simplicity and completeness.

### Strong Answer

The biggest trade-off is that the project chooses simplicity and clarity over full product maturity. The API is easy to understand, the domain model is direct, and the main business flows are visible and well-separated. That is a strong engineering choice for an early-stage project, but it means the app is not yet complete in areas like moderation, reliability, operational telemetry, or broader authorization semantics.

I would say this is acceptable today because the project is not trying to solve a huge or highly regulated product problem. It is implementing the essential Q&A workflow with a backend-first design. That is often the correct trade-off at the beginning: solve the core workflow cleanly, then add complexity where it becomes necessary. The repository is a good example of prioritization: the product’s core use cases are clearly implemented, while the surrounding system concerns are not overbuilt.

The risk is not that the code is simplistic; the risk is when that simplicity is mistaken for production completeness. The project is a useful starting point, and it would need more operational and security work before I would treat it as a full production product. That is a fair and honest assessment.

### Likely Follow-Up

- What makes you confident that this trade-off was the right one for the project stage?
- When would you stop defending the simplicity?

### Strong Follow-Up Direction

I would answer that the right time to stop defending the simplicity is when product value depends on scale, monitoring, abuse prevention, or policy complexity. Until then, the code’s clarity is a real asset. The project behaves like an MVP and should be evaluated as such, not as a large community platform with all operating concerns solved.

### Red Flags to Avoid

- Claiming simplicity is always the best design.
- Pretending the lack of operational maturity is unimportant.
- Failing to distinguish an MVP from a production platform.

## 19. Question

How would you measure whether this project is “working well,” and which metrics matter most at the current stage?

### What the Interviewer Is Testing

This checks whether the candidate can talk about success metrics realistically and in a product-aware way instead of making generic claims about scalability or performance.

### Strong Answer

At this stage, I would measure success in terms of whether the core product flow is reliable and understandable, not in terms of large-scale throughput or production metrics that the repository simply does not show. The most important measures are correctness and stability: can users register, authenticate, create questions, answer them, and vote without breaking the business rules? I would also evaluate whether the API returns clear failure states for invalid inputs and unauthorized actions.

From an engineering perspective, the most valuable metrics would be basic API health: response success rates, error rates, request latency, and whether security checks behave as expected. I would also look at the data integrity of the application—duplicate entries, vote consistency, and correct ownership enforcement. Those are the signals that tell me the MVP is working.

The repository does not include deployment telemetry, so I would be careful not to invent numbers or production claims. I would say the project’s success is primarily measured by the correctness of its core behaviors and the clarity of its domain model. If it were to mature, I would add traffic, reliability, and security metrics in a production environment.

### Likely Follow-Up

- What would you monitor after the project becomes public-facing?
- Which metrics would tell you the system is failing even before users complain?

### Strong Follow-Up Direction

I would focus on 5xx error rates, authentication failures, duplicate-record errors, and user flow completion rates. Over time, latency and resource usage become important too, but for an MVP, data correctness and endpoint reliability matter more than scale metrics.

### Red Flags to Avoid

- Claiming success without measurable signals.
- Inventing user numbers or traffic assumptions.
- Ignoring the difference between product feedback and operational metrics.

## 20. Question

How do you think this project should evolve over the next six months, and what would the highest-value next steps be?

### What the Interviewer Is Testing

This question checks prioritization and product judgment: whether the candidate can identify the next meaningful improvements without jumping ahead to unnecessary complexity.

### Strong Answer

The highest-value next steps would be around hardening the platform rather than adding more surface area. I would start by improving operational readiness: structured logging, health checks, environment configuration, and a better view of API reliability. I would also want to test the main flows more thoroughly, especially authentication and voting, because those are the critical pieces that determine whether the product can be trusted.

After that, I would likely improve moderation and security policies, because content ownership is already handled but moderation and abuse protections are not clearly modeled. The project is already a credible Q&A backend, so the next step is to make it safer and more measurable, not necessarily to add many new features. I would also keep the API contract stable while I added improvements to the underlying behavior.

If the project were to become a real application, I would expect a frontend, deployment pipeline, and clearer environment management. I would probably not add new domain complexity before the core platform was reliable. The idea is to keep the product valuable without creating a fragile system that looks impressive but is hard to maintain.

### Likely Follow-Up

- What would you avoid adding until the basics are stable?
- Which improvement would most likely improve product trust?

### Strong Follow-Up Direction

I would prioritize security, observability, and testing because those are the things that make users trust the product and engineers feel confident changing it. I would avoid adding broad social features or complex ranking mechanics until the core product had become stable and observable.

### Red Flags to Avoid

- Pretending the next step is an entirely new product rather than a maturity step.
- Adding features before the core system becomes safe and observable.
- Ignoring that trust and reliability are often more valuable than new functionality.
