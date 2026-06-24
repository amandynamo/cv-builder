# Spec: Project Setup

## Overview

This feature establishes the full foundational infrastructure for the Resume Builder backend.
The current scaffold is a bare-bones Spring Boot application with only `spring-boot-starter-web`
and no database, security, or tooling wired in.

Project Setup delivers:

- All required Maven dependencies (JPA, Security, PostgreSQL, Flyway, Lombok, JWT, WebClient, OpenAPI)
- Restructured package layout matching the Modular Monolith architecture defined in CLAUDE.md
- `application.yml` replacing the empty `application.properties`
- Docker Compose for local PostgreSQL
- Flyway baseline migration
- Spring Security skeleton (permit public endpoints, block everything else)
- OpenAPI / Swagger UI configuration
- Global exception handler skeleton
- Logging configuration

Without this feature no subsequent feature (Auth, Resume, AI, PDF) can be built.
IMPORTANT: pom.xml is authoritative ONLY after setup. During this feature, downgrade to Spring Boot 3.3.x even if scaffold uses 4.x.
> **Note — Java and Spring Boot versions:**
> `pom.xml` currently declares `<java.version>17</java.version>` and Spring Boot `4.1.0`.
> CLAUDE.md targets **Java 21** and **Spring Boot 3.x**.
> Implementation must align to CLAUDE.md: Java 21, Spring Boot 3.3.x (latest 3.x GA).
> Spring Boot 4.x is still pre-release and outside the stated tech stack.

---

## Depends On

None

---

## API Endpoints

```
GET  /actuator/health
Purpose: Liveness probe
Authentication Required: No

GET  /swagger-ui/**
Purpose: OpenAPI documentation UI
Authentication Required: No

GET  /v3/api-docs/**
Purpose: OpenAPI JSON spec
Authentication Required: No
```

No business endpoints are introduced in this feature.

---

## Request DTOs

None — no business endpoints in this feature.

---

## Response DTOs

None — no business endpoints in this feature.

---

## Database Changes

### Flyway Baseline Migration

File: `src/main/resources/db/migration/V1__baseline.sql`

Content: empty baseline comment — confirms Flyway connectivity and establishes the migration baseline.

```sql
-- Baseline migration: schema managed entirely by Flyway
```

No tables are created in this feature. Tables are introduced in the Auth and Resume feature specs.

---

## Security Considerations

Spring Security must be configured to:

| Endpoint pattern         | Access        |
|--------------------------|---------------|
| `/api/auth/**`           | Permit All    |
| `/swagger-ui/**`         | Permit All    |
| `/v3/api-docs/**`        | Permit All    |
| `/actuator/health`       | Permit All    |
| Everything else          | Authenticated |

- Session management: `STATELESS`
- CSRF: disabled (REST API with JWT)
- No default login form
- No HTTP Basic
- JWT filter placeholder registered (empty pass-through until Auth feature wires it)

---

## Services

None — no business services in this feature.

Configuration classes only:

| Class                     | Responsibility                                    |
|---------------------------|---------------------------------------------------|
| `SecurityConfig`          | Spring Security filter chain, CORS, CSRF, permits |
| `OpenApiConfig`           | SpringDoc OpenAPI bean, JWT security scheme        |
| `WebClientConfig`         | `WebClient.Builder` bean for AI integration        |
| `GlobalExceptionHandler`  | `@RestControllerAdvice` skeleton                   |

---

## Files To Modify

| File                                   | Change                                                                          |
|----------------------------------------|---------------------------------------------------------------------------------|
| `pom.xml`                              | Java 21, Spring Boot 3.3.x, add all required dependencies                      |
| `src/main/resources/application.properties` | Delete — replaced by `application.yml`                                   |

---

## Files To Create

### Configuration

```
src/main/java/com/aks/resume/config/SecurityConfig.java
src/main/java/com/aks/resume/config/OpenApiConfig.java
src/main/java/com/aks/resume/config/WebClientConfig.java
```

### Exception Handling

```
src/main/java/com/aks/resume/exception/GlobalExceptionHandler.java
src/main/java/com/aks/resume/exception/ResumeNotFoundException.java
src/main/java/com/aks/resume/exception/UserNotFoundException.java
src/main/java/com/aks/resume/exception/UnauthorizedAccessException.java
src/main/java/com/aks/resume/exception/AIServiceException.java
src/main/java/com/aks/resume/exception/ErrorResponse.java
```

### Resources

```
src/main/resources/application.yml
src/main/resources/db/migration/V1__baseline.sql
```

### Docker

```
docker/Dockerfile
docker/docker-compose.yml
```

### Package structure (empty `package-info.java` or placeholder classes)

The following packages must exist so imports compile in later features:

```
com.aks.resume.controller
com.aks.resume.service
com.aks.resume.repository
com.aks.resume.entity
com.aks.resume.dto.request
com.aks.resume.dto.response
com.aks.resume.mapper
com.aks.resume.security.jwt
com.aks.resume.security.filter
com.aks.resume.security.config
com.aks.resume.util
com.aks.resume.modules.auth
com.aks.resume.modules.user
com.aks.resume.modules.resume
com.aks.resume.modules.template
com.aks.resume.modules.ai
com.aks.resume.modules.pdf
```

---

## Dependencies To Add (pom.xml)

| Dependency                                    | Purpose                            |
|-----------------------------------------------|------------------------------------|
| `spring-boot-starter-data-jpa`                | JPA / Hibernate ORM                |
| `spring-boot-starter-security`                | Spring Security                    |
| `spring-boot-starter-validation`              | Bean Validation (`@Valid`)         |
| `spring-boot-starter-webflux`                 | `WebClient` for AI calls           |
| `spring-boot-starter-actuator`                | Health endpoint                    |
| `postgresql`                                  | PostgreSQL JDBC driver             |
| `flyway-core`                                 | Database migrations                |
| `flyway-database-postgresql`                  | Flyway PostgreSQL dialect          |
| `lombok`                                      | Boilerplate reduction              |
| `springdoc-openapi-starter-webmvc-ui`         | Swagger UI + OpenAPI 3             |
| `jjwt-api`                                    | JWT API (io.jsonwebtoken)          |
| `jjwt-impl`                                   | JWT implementation (runtime)       |
| `jjwt-jackson`                                | JWT Jackson serialization (runtime)|

---

## application.yml Structure

```yaml
spring:
  application:
    name: resume-builder

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/resumebuilder}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: ${PORT:8080}

jwt:
  secret: ${JWT_SECRET:replace-in-production}
  expiration-ms: ${JWT_EXPIRATION_MS:86400000}

openai:
  api-key: ${OPENAI_API_KEY:}
  base-url: https://api.openai.com/v1

logging:
  level:
    root: INFO
    com.aks.resume: DEBUG
```

---

## Docker Compose

`docker/docker-compose.yml` must define:

- Service: `postgres`
  - Image: `postgres:16`
  - Environment: `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
  - Port: `5432:5432`
  - Volume: named volume for data persistence

- Service: `app` (optional for local dev; can be enabled later)
  - Depends on `postgres`
  - Env vars from `.env` file

---

## External Integrations

No live external calls in this feature.

`WebClient.Builder` bean is registered for use by the AI module in a later feature.

---

## Testing Strategy

### Unit Tests

| Class                    | What to test                                                   |
|--------------------------|----------------------------------------------------------------|
| `GlobalExceptionHandler` | Each handler method returns correct HTTP status and body shape |

### Integration Tests

| Scenario                                            | Expected                    |
|-----------------------------------------------------|-----------------------------|
| `GET /actuator/health`                              | 200 OK, `{"status":"UP"}`   |
| `GET /swagger-ui/index.html`                        | 200 OK                      |
| `GET /v3/api-docs`                                  | 200 OK, JSON body           |
| `GET /api/resumes` (unauthenticated, does not exist)| 401 Unauthorized            |

### Security Tests

| Scenario                              | Expected         |
|---------------------------------------|------------------|
| Request to protected path, no token   | 401              |
| Request to `/api/auth/**`, no token   | permitted (no 401)|

---

## Rules For Implementation

- Java 21
- Spring Boot 3.3.x (align to CLAUDE.md — NOT 4.x)
- Constructor Injection Only (`@RequiredArgsConstructor`)
- No field injection, no setter injection
- No `ddl-auto=create` or `ddl-auto=update` — use `validate`
- All schema changes via Flyway only
- No hardcoded secrets — all sensitive values from environment variables
- JWT secret and OpenAI key must come from `application.yml` env interpolation
- OpenAPI documentation must be available at `/swagger-ui/index.html`
- Global exception handler must be the single point of error responses
- `@Slf4j` for logging — never log passwords, tokens, or API keys
- Docker Compose must be the only way to start local PostgreSQL

---

## Definition Of Done

```
[ ] pom.xml uses Java 21 and Spring Boot 3.3.x
[ ] All required dependencies are declared in pom.xml
[ ] application.properties is deleted
[ ] application.yml is present with all config keys
[ ] All sensitive values use environment variable interpolation
[ ] Flyway V1__baseline.sql migration exists
[ ] docker/docker-compose.yml starts PostgreSQL successfully
[ ] Application starts with `mvn spring-boot:run` after `docker compose up -d`
[ ] GET /actuator/health returns 200 {"status":"UP"}
[ ] GET /swagger-ui/index.html returns 200
[ ] GET /v3/api-docs returns 200 with JSON
[ ] Unauthenticated request to a protected endpoint returns 401
[ ] Requests to /api/auth/**, /swagger-ui/**, /v3/api-docs/**, /actuator/health are not blocked
[ ] Package structure matches CLAUDE.md architecture
[ ] GlobalExceptionHandler is registered and returns structured error responses
[ ] Custom exception classes exist (Resume, User, Unauthorized, AIService)
[ ] Unit test for GlobalExceptionHandler passes
[ ] Integration test for health endpoint passes
[ ] mvn clean package succeeds with no errors
```
