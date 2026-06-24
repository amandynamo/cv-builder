# CLAUDE.md

## Project Overview

Resume Builder Backend is a production-ready Spring Boot application that powers an AI-assisted resume generation platform.

The system allows users to:

* Register and authenticate using JWT
* Create and manage multiple resumes
* Generate AI-enhanced resume content
* Tailor resumes for job descriptions
* Generate ATS-friendly PDFs
* Store and retrieve resume templates
* Export resumes in multiple formats

The application follows a Modular Monolith architecture to enable rapid MVP development while maintaining clear separation of concerns.

---

## Architecture

```text
resume-builder-backend/
├── src/main/java/com/resumebuilder
│
├── config/                 # Spring configuration
├── controller/             # REST endpoints
├── service/                # Business logic
├── repository/             # JPA repositories
├── entity/                 # Database entities
├── dto/
│   ├── request/
│   └── response/
├── mapper/                 # Entity ↔ DTO mapping
├── security/
│   ├── jwt/
│   ├── filter/
│   └── config/
├── exception/
├── util/
│
├── modules/
│   ├── auth/
│   ├── user/
│   ├── resume/
│   ├── template/
│   ├── ai/
│   └── pdf/
│
└── ResumeBuilderApplication.java

src/main/resources/
├── application.yml
├── db/
│   └── migration/
└── templates/

docker/
├── Dockerfile
└── docker-compose.yml
```

---

## Modular Design Rules

### Auth Module

Responsible for:

* Registration
* Login
* JWT generation
* JWT validation
* User roles

Controllers:

```text
/api/auth/register
/api/auth/login
```

### Resume Module

Responsible for:

* Create resume
* Update resume
* Delete resume
* Resume retrieval

Controllers:

```text
/api/resumes
```

### AI Module

Responsible for:

* Summary generation
* Experience enhancement
* Skill suggestions
* Job description tailoring

Controllers:

```text
/api/ai
```

### PDF Module

Responsible for:

* HTML rendering
* PDF generation
* Resume export

Controllers:

```text
/api/export
```

---

## Technology Stack

### Backend

* Java 21
* Spring Boot 3.x
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway

### Documentation

* OpenAPI 3
* Swagger UI

### Authentication

* JWT

### AI Integration

* OpenAI API
* WebClient

### PDF Generation

* OpenHTMLToPDF

### Build

* Maven

### Containerization

* Docker
* Docker Compose

### Testing

* JUnit 5
* Mockito
* Spring Boot Test

---

## Code Style

### Java

Follow:

* SOLID principles
* Clean Code principles
* Constructor Injection only
* No field injection
* No setter injection

Example:

```java
@RequiredArgsConstructor
@Service
public class ResumeService {

    private final ResumeRepository repository;

}
```

### Naming

Classes:

```text
ResumeController
ResumeService
ResumeRepository
ResumeMapper
```

Variables:

```java
resumeId
userEmail
jobDescription
```

Constants:

```java
MAX_RESUME_SIZE
DEFAULT_TEMPLATE
```

---

## API Design Rules

### Controllers

Controllers should:

* Validate requests
* Delegate to services
* Return DTOs

Controllers must NOT:

* Contain business logic
* Contain repository calls

Example:

```java
@PostMapping
public ResponseEntity<ResumeResponse> createResume(
        @Valid @RequestBody ResumeRequest request) {

    return ResponseEntity.ok(
        resumeService.createResume(request));
}
```

---

## Service Layer Rules

All business logic belongs in services.

Services may:

* Call repositories
* Call external APIs
* Perform validations

Services must NOT:

* Return entities directly
* Expose database implementation details

Always return DTOs.

---

## Repository Rules

Repositories should only contain:

* Persistence operations
* JPA queries

Repositories must NOT:

* Contain business logic
* Call external APIs

---

## DTO Rules

Never expose entities directly.

Always use:

```text
Request DTO
Response DTO
```

Example:

```java
CreateResumeRequest
ResumeResponse
```

---

## Database Rules

Database:

```text
PostgreSQL
```

Migration Tool:

```text
Flyway
```

Never:

* Use ddl-auto=create
* Use ddl-auto=update

Allowed:

```yaml
ddl-auto: validate
```

All schema changes must be implemented through Flyway migrations.

---

## Security Rules

Authentication:

```text
JWT only
```

Password Encoding:

```text
BCrypt
```

Never:

* Store plain text passwords
* Disable security for convenience

Public Endpoints:

```text
/api/auth/**
/swagger-ui/**
/v3/api-docs/**
/actuator/health
```

Everything else must require authentication.

---

## AI Integration Rules

All AI functionality must go through:

```text
AIResumeService
```

Never call OpenAI directly from controllers.

Use:

```java
WebClient
```

API key must come from:

```yaml
OPENAI_API_KEY
```

Never hardcode keys.

---

## PDF Generation Rules

PDF generation must be isolated inside:

```text
PdfService
```

Preferred flow:

```text
Resume Data
→ HTML Template
→ PDF
→ Download
```

Controllers must not generate PDFs directly.

---

## Error Handling

Use:

```java
@RestControllerAdvice
```

Global exception handling only.

Create custom exceptions:

```java
ResumeNotFoundException
UserNotFoundException
UnauthorizedAccessException
AIServiceException
```

Never return stack traces.

---

## Logging

Use:

```java
@Slf4j
```

Log:

* Startup events
* Authentication failures
* AI API calls
* PDF generation

Never log:

* Passwords
* JWT tokens
* API keys

---

## Testing Rules

Every service must have tests.

Minimum:

```text
Service Tests
Repository Tests
Controller Tests
```

Use:

```text
JUnit 5
Mockito
MockMvc
```

Target:

```text
80%+ service layer coverage
```

---

## Commands

### Run PostgreSQL

```bash
docker compose up -d
```

### Run Application

```bash
mvn spring-boot:run
```

### Build

```bash
mvn clean package
```

### Run Tests

```bash
mvn test
```

### Run Single Test

```bash
mvn -Dtest=ResumeServiceTest test
```

### Generate Coverage

```bash
mvn verify
```

---

## MVP Scope

### Phase 1

Implemented:

* User Registration
* User Login
* JWT Authentication

### Phase 2

Implemented:

* Resume CRUD
* Resume Templates

### Phase 3

Implemented:

* AI Resume Enhancement
* Job Description Matching

### Phase 4

Implemented:

* PDF Export

Do not implement future phases unless explicitly requested.

---

## Things To Avoid

* No microservices
* No Kafka
* No Redis
* No GraphQL
* No event-driven architecture
* No premature optimization

This project is intentionally a Modular Monolith.

Focus on:

* Simplicity
* Maintainability
* Clean Architecture
* Fast MVP delivery

If a feature can be solved with Spring Boot and PostgreSQL, prefer that solution over introducing new technologies.
