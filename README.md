<br>
<p align="center">
  <img src="assets/logo.png" alt="BookTracker Logo" width="250"/>
</p>
<br>

<p align="center">
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/license-MIT-yellow.svg" alt="MIT"></a>
  <a href="https://github.com/Nirtas/booktracker-backend/releases/latest"><img src="https://img.shields.io/github/v/release/Nirtas/booktracker-backend" alt="GitHub Release"></a>
  <a href="https://hub.docker.com/r/jerael/booktracker-backend"><img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff" alt="Docker"></a>
</p>

[Русская версия](README.ru.md)

# BookTracker

Backend for tracking your reading progress, built with **Clean Architecture** principles. This server manages book data,
genres, users and secure cover storage.

## Tech Stack

- **Framework:** Spring Boot 4 (Java 17)
- **Security:** Spring Security, Argon2id hashing, JWT (Nimbus JOSE + JWT)
- **Database:** PostgreSQL
- **Migrations:** Liquibase (YAML)
- **File Storage:** MinIO (S3 compatible)
- **Documentation:** OpenAPI / Swagger UI
- **Deployment:** Docker & Docker Compose
- **SMTP** for email delivery

## Architecture

The project follows **Clean Architecture** to ensure maintainability and testability:

- **Web:** REST Controllers, DTOs, Security Filters and Web Mappers.
- **Application:** Orchestration of business logic through use cases and domain services.
- **Domain:** Pure business logic, Entities, Repository interfaces and Validation rules.
- **Data:** Infrastructure implementations (JPA Repositories, S3 Storage, Image Processor, Password Hasher, Identity
  Token Provider).
 
## Diagrams

<details>
<summary><b>ER diagram</b></summary>

```mermaid
erDiagram
    users ||--o{ books : owns
    users ||--o{ refresh_tokens : has
    users ||--o{ email_verifications : initiates
    books ||--o{ book_genres : has
    genres ||--o{ book_genres : "associated with"

    users {
        uuid user_id PK
        varchar email
        varchar password_hash
        boolean is_verified
        timestamptz created_at
    }

    books {
        uuid book_id PK
        uuid user_id FK
        varchar title
        varchar author
        text cover_file_name
        text status
        timestamptz created_at
    }

    genres {
        int genre_id PK
        varchar genre_name
    }

    book_genres {
        uuid book_id PK, FK
        int genre_id PK, FK
    }

    refresh_tokens {
        uuid id PK
        uuid user_id FK
        varchar token_hash
        timestamptz expires_at
    }

    email_verifications {
        uuid id PK
        uuid user_id FK
        varchar email
        varchar verification_type
        varchar token
        timestamptz expires_at
        timestamptz created_at
    }
```

</details>

<details>
<summary><b>Workflow 1: User registration (without verification)</b></summary>

```mermaid
sequenceDiagram
    autonumber
    actor C as Client
    participant AC as AuthController
    participant CUUC as CreateUserUseCase
    participant UV as UserValidator
    participant PH as PasswordHasher
    participant UR as UserRepository
    participant EVS as EmailVerificationService
    participant EVR as EmailVerificationRepository
    participant SS as SmtpService
    participant VTG as VerificationTokenGenerator

    C->>AC: POST /auth/register (UserCreationRequest)
    Note over AC: Mapping into UserCreation
    AC->>CUUC: execute(UserCreation)
    
    CUUC->>UV: validateCreation(UserCreation)
    
    alt Validation failed
        UV-->>CUUC: throw ValidationException
        CUUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end
    
    CUUC->>UR: findByEmail(email)
    
    alt User found
        UR-->>CUUC: User
        CUUC-->>AC: throw AlreadyExistsException
        AC-->>C: 409 Conflict
    end

    CUUC->>PH: hash(password)
    PH-->>CUUC: passwordHash
    
    Note over CUUC: Create model<br>User (isVerified=false)
    
    CUUC->>UR: save(newUser)
    Note over UR: Save in database
    UR-->>CUUC: createdUser (with new id)

    Note over CUUC: Create model<br>EmailVerificationInitiation<br>with type: REGISTRATION

    CUUC->>EVS: initiate(EmailVerificationInitiation)
    EVS->>EVR: findByUserIdAndType(userId, VerificationType)

    alt Verification found
        EVR-->>EVS: EmailVerification

        alt Less than a minute has passed since creation
            EVS-->>CUUC: throw TooManyRequests
            CUUC-->>AC: throw TooManyRequests
            AC-->>C: 429 Too Many Requests
        else Code is still valid
            Note over EVS: Resending this code
            EVS->>SS: sendEmail(email, VerificationMailMessage)
            EVS-->>CUUC: expiresAt
            CUUC-->>AC: UserCreationResult
            Note over AC: Mapping into UserCreationResponse
            AC-->>C: UserCreationResponse
        end

        EVS->>EVR: deleteByUserIdAndType(userId, VerificationType)
    end

    EVS->>VTG: generate
    VTG-->>EVS: VerificationToken
    Note over EVS: Create model<br>EmailVerification
    EVS->>EVR: save(emailVerification)
    EVS->>SS: sendEmail(email, VerificationMailMessage)
    EVS-->>CUUC: expiresAt

    CUUC-->>AC: UserCreationResult
    Note over AC: Mapping into UserCreationResponse
    AC-->>C: UserCreationResponse
```

</details>

<details>
<summary><b>Workflow 2: Email verification</b></summary>

```mermaid
sequenceDiagram
    autonumber
    actor C as Client
    participant AC as AuthController
    participant CRUC as ConfirmRegistrationUseCase
    participant AV as AuthValidator
    participant UR as UserRepository
    participant EVS as EmailVerificationService
    participant EVR as EmailVerificationRepository
    participant VTV as VerificationTokenValidator
    participant ATS as AuthTokenService
    participant ITP as IdentityTokenProvider
    participant PH as PasswordHasher
    participant RTR as RefreshTokenRepository

    C->>AC: POST /auth/confirm-registration (ConfirmRegistrationRequest)
    Note over AC: Mapping into ConfirmRegistration
    AC->>CRUC: execute(ConfirmRegistration)

    CRUC->>AV: validateRegistrationConfirmation(ConfirmRegistration)

    alt Validation failed
        AV-->>CRUC: throw ValidationException
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    CRUC->>UR: findById(userId)

    alt User not found
        UR-->>CRUC: User not found
        CRUC-->>AC: throw NotFoundException
        AC-->>C: 404 Not Found
    end

    alt User already verified
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    Note over CRUC: Create model<br>EmailVerificationConfirmation<br>with type: REGISTRATION

    CRUC->>EVS: confirm(EmailVerificationConfirmation)
    EVS->>EVR: findByUserIdAndType(userId, VerificationType)

    alt Verification not found
        EVR-->>EVS: Verification not found
        EVS-->>CRUC: throw NotFoundException
        CRUC-->>AC: throw NotFoundException
        AC-->>C: 404 Not Found
    end

    EVS->>VTV: validate(EmailVerification, code)

    alt Validation failed
        VTV-->>EVS: throw ValidationException
        EVS-->>CRUC: throw ValidationException
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    EVS->>EVR: deleteByUserIdAndType(userId, VerificationType)
    EVS-->>CRUC: EmailVerification

    Note over CRUC: Create model<br>User (isVerified = true)

    CRUC->>UR: save(verifiedUser)
    CRUC->>ATS: issueTokens(userId)

    Note over ATS: Create model<br>IdentityTokenClaims<br>with content for the access token
    ATS->>ITP: encode(IdentityTokenClaims)
    ITP-->>ATS: accessToken (String)
    
    Note over ATS: Create model<br>IdentityTokenClaims<br>with content for the refresh token
    ATS->>ITP: encode(IdentityTokenClaims)
    ITP-->>ATS: refreshToken (String)
    
    ATS->>PH: hash(refreshToken)
    PH-->>ATS: tokenHash
    Note over ATS: Create model<br>RefreshToken with tokenHash
    ATS->>RTR: save(RefreshToken)
    ATS-->>CRUC: TokenPair(accessToken, refreshToken)

    CRUC-->>AC: TokenPair(accessToken, refreshToken)
    Note over AC: Mapping into AuthResponse
    AC-->>C: AuthResponse(accessToken, refreshToken)
```

</details>

## Setup & Development

### Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)

### Configuration

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
2. Fill in your credentials and properties in the `.env` file (DB, MinIO, Argon2, SMTP, JWT).

### Launch Options

#### 1. Development (Infrastructure only)

Start PostgreSQL and MinIO to run the application from your IDE:

```bash
docker compose -f docker-compose.dev.yml up --build -d
```

#### 2. Production (Full Stack from source)

Build and start all services (App + DB + Storage) in containers:

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

## API Documentation

Once the server is running and Swagger enabled in `.env` file (`ENABLE_SWAGGER_UI=true`), explore the API and test
endpoints via Swagger UI:
`http://localhost:8080/swagger-ui/index.html`

### Quick API Reference

All endpoints are prefixed with `/api/v1`.

| Method     | Endpoint                     | Auth Required | Description              |
|------------|------------------------------|---------------|--------------------------|
| **Auth**   |
| `POST`     | `/auth/register`             | No            | Register new user        |
| `POST`     | `/auth/confirm-registration` | No            | Confirm registration     |
| `POST`     | `/auth/login`                | No            | Login                    |
| `POST`     | `/auth/refresh`              | No            | Refresh tokens           |
| `POST`     | `/auth/logout`               | No            | Logout                   |
| `POST`     | `/auth/resend-code`          | No            | Resend verification code |
| **Users**  |
| `GET`      | `/users/me`                  | **Yes**       | Get current user details |
| **Books**  |
| `GET`      | `/books`                     | **Yes**       | Get user's books         |
| `GET`      | `/books/{id}`                | **Yes**       | Get book by id           |
| `DELETE`   | `/books/{id}`                | **Yes**       | Delete book by id        |
| `PATCH`    | `/books/{id}`                | **Yes**       | Update book details      |
| `POST`     | `/books`                     | **Yes**       | Create book              |
| `POST`     | `/books/{id}/cover`          | **Yes**       | Upload book cover        |
| `DELETE`   | `/books/{id}/cover`          | **Yes**       | Delete book cover        |
| `GET`      | `/books/{id}/cover`          | **Yes**       | Get book cover           |
| **Genres** |
| `GET`      | `/genres`                    | **Yes**       | Get all genres           |
| `GET`      | `/genres/{id}`               | **Yes**       | Get genre by id          |
