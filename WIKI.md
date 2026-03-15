# Write In App - Project Wiki

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Data Model](#data-model)
- [API Reference](#api-reference)
  - [Authentication](#authentication-api)
  - [Elections](#elections-api)
  - [Offices](#offices-api)
  - [Counting Boards](#counting-boards-api)
  - [Users](#users-api)
  - [Admin Users](#admin-users-api)
  - [Write-In Records](#write-in-records-api)
  - [Records & Reports](#records--reports-api)
  - [Test Endpoints](#test-endpoints)
- [Security](#security)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Local Development](#local-development)

---

## Overview

The **Write In App** is a Java-based backend service built on the Spring Boot framework. It provides a secure, stateless REST API designed to work with a companion React.js frontend. The system manages election write-in records, supporting data entry, user management, and report generation (PDF and CSV exports).

The application is designed for election administrators who need a centralized system for managing write-in ballot data, including elections, offices, counting boards, users, and individual write-in records.

---

## Architecture

The application follows a layered Spring Boot architecture:

```
Client (React.js Frontend)
        |
        v
  REST Controllers  ──>  Security Filter Chain (JWT)
        |
        v
  Service / Utility Layer (PDF/CSV generation)
        |
        v
  Spring Data JPA Repositories
        |
        v
  MariaDB Database
```

Key architectural decisions:
- **Stateless authentication** using JSON Web Tokens (JWT)
- **JPA auditing** for automatic `createdAt` / `updatedAt` timestamps on entities
- **Multi-stage Docker build** for lean production images
- **CORS** enabled for all origins to support frontend integration

---

## Technology Stack

| Technology | Version | Purpose |
|:---|:---|:---|
| Java | 17 | Language runtime |
| Spring Boot | 3.5.x | Application framework |
| Spring Security | (managed) | Authentication and authorization |
| Spring Data JPA | (managed) | Database access layer |
| Hibernate | (managed) | ORM / schema management |
| MariaDB | - | Relational database |
| JJWT | 0.12.6 | JWT token creation and validation |
| OpenPDF | 1.3.30 | PDF report generation |
| Apache Commons CSV | 1.10.0 | CSV export |
| Lombok | (managed) | Boilerplate code reduction |
| Gradle | (wrapper) | Build tool |
| Docker | - | Containerization |

---

## Project Structure

```
writein-backend/
├── Dockerfile                          # Multi-stage Docker build
├── build.gradle                        # Gradle build configuration
├── gradle.properties                   # Gradle settings (configuration cache enabled)
├── settings.gradle                     # Project name: "writein"
├── gradlew / gradlew.bat              # Gradle wrapper scripts
├── README.md                           # Setup instructions
├── WIKI.md                             # This file
└── src/main/
    ├── java/com/example/writein/
    │   ├── WriteInApplication.java     # Spring Boot entry point
    │   ├── controllers/
    │   │   ├── AuthController.java     # Login and registration
    │   │   ├── AdminUserController.java # Admin user management
    │   │   ├── UserController.java     # Write-in user management
    │   │   ├── ElectionController.java # Election CRUD
    │   │   ├── OfficeController.java   # Office CRUD
    │   │   ├── CountingBoardController.java # Counting board CRUD
    │   │   ├── WriteInController.java  # Write-in record CRUD (per user)
    │   │   ├── RecordController.java   # Record queries, PDF/CSV export
    │   │   └── TestController.java     # Public/role-based test endpoints
    │   ├── models/
    │   │   ├── AbstractEntity.java     # Base entity (id, version)
    │   │   ├── DateAudit.java          # Adds createdAt, updatedAt
    │   │   ├── UserDateAudit.java      # Adds createdBy, updatedBy
    │   │   ├── ERole.java              # Enum: ROLE_USER, ROLE_ADMIN
    │   │   ├── EElection.java          # Enum: PrePublished, Published, Locked
    │   │   ├── EUser.java              # Enum: Active, Archived, Locked
    │   │   └── entities/
    │   │       ├── Election.java       # Election entity
    │   │       ├── Office.java         # Office entity
    │   │       ├── CountingBoard.java  # Counting board entity
    │   │       ├── User.java           # User entity
    │   │       ├── Role.java           # Role entity
    │   │       └── WriteInRecord.java  # Write-in record entity
    │   ├── repository/
    │   │   ├── ElectionRepository.java
    │   │   ├── OfficeRepository.java
    │   │   ├── CountingBoardRepository.java
    │   │   ├── UserRepository.java
    │   │   ├── RoleRepository.java
    │   │   └── WriteInRepository.java
    │   ├── security/
    │   │   ├── WebSecurityConfig.java  # Security filter chain config
    │   │   ├── jwt/
    │   │   │   ├── AuthEntryPointJwt.java  # Unauthorized handler
    │   │   │   ├── AuthTokenFilter.java    # JWT token filter
    │   │   │   └── JwtUtils.java           # JWT utility methods
    │   │   └── services/
    │   │       ├── UserDetailsImpl.java
    │   │       └── UserDetailsServiceImpl.java
    │   ├── dto/
    │   │   └── UserInput.java          # DTO for batch creation input
    │   ├── payload/
    │   │   ├── request/
    │   │   │   ├── LoginRequest.java
    │   │   │   └── RegisterRequest.java
    │   │   └── response/
    │   │       ├── JwtResponse.java
    │   │       └── MessageResponse.java
    │   ├── utils/
    │   │   ├── Constants.java          # API_ENDPOINT, SERVER_URL
    │   │   ├── DataGenerator.java      # Data generation utilities
    │   │   ├── HasLogger.java          # Logger mixin
    │   │   ├── UserPDFGenerator.java   # Generates user PDF reports
    │   │   ├── WriteInCSVExportService.java  # CSV export service
    │   │   └── WriteInReportPDFGenerator.java # Write-in PDF reports
    │   └── exceptions/
    │       └── ResourceNotFoundException.java
    └── resources/
        ├── application.properties      # Production configuration
        └── application-dev.properties  # Development configuration
```

---

## Data Model

### Entity Relationship Diagram

```
Election (1) ──── (*) CountingBoard
    |                     |
    |                     * (many-to-many)
    |                     |
    + ──── (*) Office ────+
    |
    + ──── (*) User
    |
    + ──── (*) WriteInRecord ──── (1) Office
                |                  (1) CountingBoard
                + ──────────────── (1) User
```

### Entities

#### AbstractEntity (Base)

All entities inherit from `AbstractEntity`, which provides:

| Field | Type | Description |
|:---|:---|:---|
| `id` | `Long` | Auto-generated primary key (IDENTITY strategy) |
| `version` | `int` | Optimistic locking version |

#### DateAudit (extends AbstractEntity)

Adds automatic JPA auditing fields:

| Field | Type | Description |
|:---|:---|:---|
| `createdAt` | `LocalDateTime` | Auto-set on creation |
| `updatedAt` | `LocalDateTime` | Auto-set on modification |

#### UserDateAudit (extends DateAudit)

Adds user tracking fields:

| Field | Type | Description |
|:---|:---|:---|
| `createdBy` | `Long` | User ID who created the record |
| `updatedBy` | `Long` | User ID who last modified the record |

#### Election

Table: `elections` | Extends: `UserDateAudit`

| Field | Type | Constraints | Description |
|:---|:---|:---|:---|
| `code` | `String` | NotBlank, max 128 | Election code |
| `title` | `String` | NotBlank, max 128 | Election title |
| `electionDate` | `LocalDate` | NotNull | Date of election |
| `advanceVoteDate` | `LocalDate` | - | Advance voting date |
| `nominationPeriodDate` | `LocalDate` | - | Nomination period date |
| `defaultTag` | `boolean` | NotNull | Whether this is the default election |
| `serialNumber` | `Integer` | - | Serial number |
| `status` | `EElection` | - | PrePublished, Published, or Locked |

Relationships: Has many `CountingBoard`, `Office`, `User`, and `WriteInRecord` (cascade ALL, orphan removal).

#### Office

Table: `offices` | Extends: `DateAudit`

| Field | Type | Constraints | Description |
|:---|:---|:---|:---|
| `title` | `String` | NotBlank, max 128 | Office title |
| `displayOrder` | `Integer` | NotNull | Display ordering |

Relationships: Belongs to one `Election`. Many-to-many with `CountingBoard` (via `offices_counting_boards` join table).

#### CountingBoard

Table: `counting_boards` | Extends: `DateAudit`

| Field | Type | Constraints | Description |
|:---|:---|:---|:---|
| `title` | `String` | NotBlank, max 128 | Board title |
| `displayOrder` | `Integer` | NotNull | Display ordering |

Relationships: Belongs to one `Election`. Many-to-many with `Office`.

#### User

Table: `users` | Extends: `DateAudit`

| Field | Type | Constraints | Description |
|:---|:---|:---|:---|
| `username` | `String` | NotBlank, max 64, unique | Username |
| `password` | `String` | NotBlank, max 100 | BCrypt-encoded password |
| `email` | `String` | Email, max 64, unique | Email address |
| `firstName` | `String` | max 64 | First name |
| `lastName` | `String` | max 64 | Last name |
| `userCode` | `String` | max 64 | User code |
| `status` | `EUser` | - | Active, Archived, or Locked |

Relationships: Belongs to one `Election`. Many-to-many with `Role` (via `user_roles` join table).

#### Role

Table: `roles` | Extends: `AbstractEntity`

| Field | Type | Description |
|:---|:---|:---|
| `name` | `ERole` | ROLE_USER or ROLE_ADMIN |

#### WriteInRecord

Table: `write_in_records` | Extends: `DateAudit`

| Field | Type | Constraints | Description |
|:---|:---|:---|:---|
| `firstName` | `String` | max 64 | Write-in candidate first name |
| `lastName` | `String` | max 64 | Write-in candidate last name |
| `middleName` | `String` | max 64 | Write-in candidate middle name |
| `recordCount` | `Integer` | NotNull | Number of records |
| `creatorName` | `String` | max 128 | Name of the record creator |
| `batchNumber` | `String` | NotNull, max 128 | Batch number |
| `electionTitle` | `String` | max 255 | Election title |
| `deletedTag` | `boolean` | - | Soft delete flag |
| `backEditedTag` | `boolean` | - | Back-edited flag |

Relationships: Belongs to one `Election`, one `Office`, one `CountingBoard`, and one `User`.

### Enumerations

| Enum | Values | Used By |
|:---|:---|:---|
| `ERole` | `ROLE_USER`, `ROLE_ADMIN` | `Role.name` |
| `EElection` | `PrePublished`, `Published`, `Locked` | `Election.status` |
| `EUser` | `Active`, `Archived`, `Locked` | `User.status` |

---

## API Reference

**Base URL:** `/api/v1/` (for most endpoints) or `/api/` (for auth and test endpoints)

All endpoints except `/api/auth/**` and `/api/test/**` require a valid JWT token in the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

---

### Authentication API

**Base Path:** `/api/auth`

#### POST `/api/auth/login`

Authenticate a user and receive a JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOi...",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

#### POST `/api/auth/register`

Register a new user account.

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "role": ["admin"]
}
```

**Response (200):**
```json
{
  "message": "User registered successfully!"
}
```

If the `role` field is omitted or contains a value other than `"admin"`, the user is assigned `ROLE_USER` by default.

---

### Elections API

**Base Path:** `/api/v1/elections`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/elections` | List all elections |
| GET | `/elections/{id}` | Get election by ID |
| GET | `/elections/defaultTag` | Get the default election |
| POST | `/elections` | Create a new election |
| PUT | `/elections/{id}` | Update an election |
| DELETE | `/elections/{id}` | Delete an election |
| POST | `/elections/deleteBatch` | Batch delete elections by IDs |

**Notes:**
- The first election created is automatically set as the default.
- Setting a new election as default automatically unsets the previous default.
- Batch delete ensures a default election is always present by promoting the first remaining election.

---

### Offices API

**Base Path:** `/api/v1/offices`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/offices` | List all offices |
| POST | `/offices/elections/{electionId}` | Create an office under an election |
| PUT | `/offices/{id}` | Update an office |
| POST | `/offices/deleteBatch` | Batch delete offices by IDs |
| GET | `/office/{title}` | Check if an office title is already taken |

---

### Counting Boards API

**Base Path:** `/api/v1/countingBoards`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/countingBoards` | List all counting boards |
| POST | `/countingBoards/elections/{electionId}` | Create a counting board under an election |
| PUT | `/countingBoards/{id}` | Update a counting board |
| POST | `/countingBoards/deleteBatch` | Batch delete counting boards by IDs |
| GET | `/countingBoard/{title}` | Check if a title is already taken |
| POST | `/countingBoard` | Check if a title prefix is already taken |
| POST | `/countingBoards/createBatch/{electionId}` | Batch create counting boards |

**Batch Create Request Body:**
```json
{
  "initString": "CB-",
  "startNumber": 1,
  "endNumber": 10
}
```
This creates counting boards named `CB-1` through `CB-10` under the specified election.

---

### Users API

**Base Path:** `/api/v1/users`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/users/{id}` | Get user by ID |
| POST | `/users/elections/{electionId}` | Create a write-in user under an election |
| PUT | `/users/{id}` | Update a user |
| POST | `/users/deleteBatch` | Batch delete users by IDs |
| POST | `/users/printBatch` | Generate a PDF for selected users |
| GET | `/user/{username}` | Check if a username is already taken |
| POST | `/user` | Check if a username prefix is already taken |
| POST | `/users/createBatch/{electionId}` | Batch create users |

**Batch Create Request Body:**
```json
{
  "initString": "user",
  "startNumber": 1,
  "endNumber": 50
}
```
This creates users named `user1` through `user50` with randomly generated user codes and passwords, assigned `ROLE_USER`.

---

### Admin Users API

**Base Path:** `/api/v1/adminUsers`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/adminUsers` | List all admin users |
| GET | `/adminUsers/{id}` | Get admin user by ID |
| GET | `/adminUser/{username}` | Check if a username is already taken |
| POST | `/adminUsers` | Create a new admin user |
| PUT | `/adminUsers/{id}` | Update an admin user |
| DELETE | `/adminUsers/{id}` | Delete an admin user |
| POST | `/adminUsers/deleteBatch` | Batch delete admin users by IDs |

---

### Write-In Records API

**Base Path:** `/api/v1/writeIns`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/writeIns/elections/{electionId}/{userId}` | Get records by election and user (paginated) |
| POST | `/writeIns/elections/{electionId}` | Create a write-in record under an election |
| PUT | `/writeIns/{id}` | Update a write-in record |
| POST | `/writeIns/deleteBatch` | Batch delete write-in records by IDs |

**Query Parameters for GET:**

| Parameter | Default | Description |
|:---|:---|:---|
| `filter` | (none) | Search filter string |
| `page` | 0 | Page number (zero-based) |
| `size` | 10 | Page size |
| `sort` | `createdAt,desc` | Sort field and direction |

**Paginated Response:**
```json
{
  "records": [...],
  "currentPage": 0,
  "totalItems": 100,
  "totalPages": 10
}
```

---

### Records & Reports API

**Base Path:** `/api/v1/records`

| Method | Endpoint | Description |
|:---|:---|:---|
| GET | `/records/elections/{electionId}` | Get all records by election (paginated) |
| PUT | `/records/{id}` | Update a record |
| POST | `/records/deleteBatch` | Batch delete records by IDs |
| GET | `/records/printPdf/{electionId}` | Generate PDF report for an election |
| GET | `/records/exportExcel` | Export all records as CSV |

**PDF Report:** Returns `application/pdf` with `Content-Disposition: inline; filename=writeInReport.pdf`.

**CSV Export:** Returns `text/csv` with `Content-Disposition: attachment; filename="writeins.csv"`.

---

### Test Endpoints

**Base Path:** `/api/test` (publicly accessible)

| Method | Endpoint | Access | Response |
|:---|:---|:---|:---|
| GET | `/api/test/all` | Public | `"Public Content."` |
| GET | `/api/test/user` | USER or ADMIN | `"User Content."` |
| GET | `/api/test/admin` | ADMIN only | `"Admin Board."` |

---

## Security

### Authentication Flow

1. Client sends `POST /api/auth/login` with username and password.
2. Server authenticates via `AuthenticationManager` using `BCryptPasswordEncoder`.
3. On success, server generates a JWT token using the configured secret key.
4. Client includes the token in subsequent requests via the `Authorization: Bearer <token>` header.
5. `AuthTokenFilter` intercepts each request, validates the JWT, and sets the security context.

### JWT Configuration

| Property | Value | Description |
|:---|:---|:---|
| `writein.app.jwtSecret` | Base64-encoded key | HMAC signing key |
| `writein.app.jwtExpirationMs` | `86400000` (24 hours) | Token time-to-live |

### Access Control

- **Public endpoints:** `/api/auth/**`, `/api/test/**`
- **Authenticated endpoints:** All other `/api/v1/**` endpoints
- **Role-based access:** `ROLE_USER` and `ROLE_ADMIN` (enforced via `@PreAuthorize` where applicable)
- **Session policy:** `STATELESS` (no server-side sessions)
- **CSRF:** Disabled (appropriate for stateless JWT APIs)

### Password Encoding

All passwords are hashed using `BCryptPasswordEncoder` before storage.

---

## Configuration

### application.properties (Production)

```properties
spring.datasource.url=jdbc:mariadb://<host>:3306/writeindb?useSSL=false
spring.datasource.username=crm
spring.datasource.password=crm
spring.jpa.hibernate.ddl-auto=update

writein.app.jwtSecret=<Base64-encoded-secret-key>
writein.app.jwtExpirationMs=86400000
```

### application-dev.properties (Development)

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/writeindb?useSSL=false
spring.datasource.username=crm
spring.datasource.password=crm
spring.jpa.hibernate.ddl-auto=update

writein.app.jwtSecret=<Base64-encoded-secret-key>
```

### Key Configuration Notes

- **`ddl-auto=update`**: Hibernate automatically synchronizes the database schema with Java entities on startup.
- **Gradle Configuration Cache** is enabled in `gradle.properties` for faster builds.
- The standard `jar` task is disabled in favor of Spring Boot's `bootJar` task.

---

## Docker Deployment

The application uses a multi-stage Docker build:

### Stage 1: Build
- Base image: `gradle:jdk17-alpine`
- Compiles the application using `gradle clean build -x test`

### Stage 2: Runtime
- Base image: `openjdk:17`
- Copies the built JAR as `app.jar`
- Exposes port `8080`

### Build and Run

```bash
# Build the Docker image
docker build -t writein-backend .

# Run the container
docker run -p 8080:8080 writein-backend
```

Ensure the MariaDB database is accessible from the container. You may need to adjust `spring.datasource.url` or use Docker networking.

---

## Local Development

### Prerequisites

- Java 17 (JDK)
- MariaDB with a database named `writeindb`
- Database user `crm` with password `crm` (or modify `application-dev.properties`)

### Database Setup

```sql
CREATE DATABASE writeindb;
CREATE USER 'crm'@'localhost' IDENTIFIED BY 'crm';
GRANT ALL PRIVILEGES ON writeindb.* TO 'crm'@'localhost';
FLUSH PRIVILEGES;
```

The `roles` table must be seeded with the following values:

```sql
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application with the dev profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The server starts on `http://localhost:8080`.

### Running Tests

```bash
./gradlew test
```
