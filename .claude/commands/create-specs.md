--
description: Create a specification and feature branch for the Resume Builder backend
argument-hint: "Feature number and title e.g. 01 authentication, 02 resume-management"
allowed-tools: Read, Write, Glob, Bash(git:*)
---------------------------------------------

You are a senior backend architect working on the Resume Builder backend.

Always follow the rules defined in CLAUDE.md.

This project follows Spec Driven Development.

Never implement code in this command.
This command only creates the specification and feature branch.

---

# Step 1 — Verify Working Directory

Run:

```bash
git status
```

If there are:

* unstaged changes
* staged but uncommitted changes
* untracked files

STOP immediately.

Tell the user:

```text
Working directory is not clean.

Please commit, stash, or remove all changes before creating a new feature specification.
```

Do not continue until the repository is clean.

---

# Step 2 — Parse Feature Information

From $ARGUMENT extract:

## Feature Number

Convert to two digits.

Examples:

```text
1  -> 01
2  -> 02
10 -> 10
```

## Feature Title

Human-readable title.

Examples:

```text
Authentication
Resume Management
AI Resume Enhancement
PDF Export
```

## Feature Slug

Requirements:

* lowercase
* letters
* digits
* hyphens only
* max length 50

Examples:

```text
authentication
resume-management
ai-resume-enhancement
pdf-export
```

## Branch Name

Format:

```text
feature/<feature-slug>
```

Example:

```text
feature/authentication
feature/resume-management
```

If any value cannot be confidently inferred,
ask the user before proceeding.

---

# Step 3 — Verify Branch Availability

Run:

```bash
git branch
```

If the branch already exists:

Append a suffix.

Examples:

```text
feature/authentication-01
feature/authentication-02
```

Choose the first available branch.

---

# Step 4 — Update Main Branch

Run:

```bash
git checkout main
git pull origin main
```

If pull fails:

Stop and notify the user.

---

# Step 5 — Create Feature Branch

Run:

```bash
git checkout -b <branch_name>
```

---

# Step 6 — Research Existing Project

Read:

```text
CLAUDE.md
pom.xml
application.yml
```

Read all existing specifications:

```text
.claude/specs/*.md
```

Read all modules:

```text
src/main/java/com/resumebuilder/**
```

Read Flyway migrations:

```text
src/main/resources/db/migration/**
```

Determine:

* existing APIs
* existing entities
* existing DTOs
* existing security configuration
* existing database schema
* completed roadmap items

If requested feature already exists:

Stop and inform the user.

---

# Step 7 — Create Specification

Generate a specification with the following structure.

---

# Spec: <feature_title>

## Overview

Describe:

* business purpose
* user value
* why this feature exists
* how it fits the MVP roadmap

---

## Depends On

List prerequisite features.

Example:

```text
Authentication
User Management
```

If none:

```text
None
```

---

## API Endpoints

List all endpoints.

Format:

```text
METHOD /path
Purpose
Authentication Required: Yes/No
```

Example:

```text
POST /api/auth/register
Create user account
Authentication Required: No
```

---

## Request DTOs

List every request DTO.

Example:

```text
RegisterRequest
CreateResumeRequest
```

Include key fields.

---

## Response DTOs

List every response DTO.

Example:

```text
AuthResponse
ResumeResponse
```

Include key fields.

---

## Database Changes

Specify:

* new tables
* columns
* indexes
* constraints
* relationships

Reference Flyway migrations.

If none:

```text
No database changes
```

---

## Security Considerations

Specify:

* public endpoints
* authenticated endpoints
* ownership validation
* role requirements

Example:

```text
Users may only access their own resumes.
```

---

## Services

List:

```text
ResumeService
AIResumeService
PdfService
```

Describe responsibilities.

---

## Files To Modify

List every existing file.

---

## Files To Create

List every new file.

---

## External Integrations

Examples:

```text
OpenAI
Claude API
OpenHTMLToPDF
```

If none:

```text
No external integrations
```

---

## Testing Strategy

Required:

### Unit Tests

List classes.

### Integration Tests

List endpoints.

### Security Tests

List authorization scenarios.

---

## Rules For Implementation

Always include:

* Java 21
* Spring Boot 3
* Constructor Injection Only
* DTO Pattern Required
* No Business Logic In Controllers
* Flyway For Schema Changes
* No ddl-auto=create
* No Hardcoded Secrets
* JWT Authentication Required
* Global Exception Handling Required
* OpenAPI Documentation Required
* Service Layer Must Be Unit Tested
* Minimum 80% Service Coverage

---

## Definition Of Done

Provide a checklist that can be manually verified.

Example:

```text
[ ] User can register
[ ] User can login
[ ] JWT token generated
[ ] Protected endpoint rejects anonymous users
[ ] Unit tests pass
[ ] Integration tests pass
[ ] Flyway migration executes successfully
[ ] Swagger documentation available
```

---

# Step 8 — Save Specification

Save to:

```text
.claude/specs/<feature-number>-<feature-slug>.md
```

Example:

```text
.claude/specs/01-authentication.md
```

---

# Step 9 — Report To User

Print:

```text
Branch:    <branch_name>
Spec File: .claude/specs/<feature-number>-<feature-slug>.md
Title:     <feature_title>
```

Then print:

```text
Review the specification before implementation.

Enter Plan Mode and review the architecture, DTOs, database changes, and security considerations before writing code.
```

Do not print the full specification unless explicitly requested.
