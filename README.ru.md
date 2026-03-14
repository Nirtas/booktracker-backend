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

[English version](README.md)

# BookTracker

Бэкенд для отслеживания прогресса чтения книг, построенный на принципах **Clean Architecture**. Сервер управляет данными
книг, жанрами, пользователями и обеспечивает безопасное хранение обложек.

## Стек технологий

- **Фреймворк:** Spring Boot 4 (Java 17)
- **Безопасность:** Spring Security, Argon2id hashing, JWT (Nimbus JOSE + JWT)
- **База данных:** PostgreSQL
- **Миграции:** Liquibase (YAML)
- **Хранилище файлов:** MinIO (S3 совместимое)
- **Документация:** OpenAPI / Swagger UI
- **Развертывание:** Docker & Docker Compose
- **SMTP** для доставки электронных писем

## Архитектура

Проект следует принципам **Clean Architecture** для обеспечения расширяемости и тестируемости:

- **Web:** REST контроллеры, DTO, фильтры безопасности и мапперы.
- **Application:** Оркестрация бизнес-логики через юзкейсы и доменные сервисы.
- **Domain:** Чистая бизнес-логика, сущности, интерфейсы репозиториев и правила валидации.
- **Data:** Реализация инфраструктуры (JPA репозитории, S3 хранилище, обработчик изображений, хэшер паролей, поставщик
  токенов идентификации).

## Диаграммы

<details>
<summary><b>ER-диаграмма</b></summary>

```mermaid
erDiagram
    users ||--o{ books : владеет
    users ||--o{ refresh_tokens : имеет
    users ||--o{ email_verifications : инициирует
    books ||--o{ book_genres : имеет
    genres ||--o{ book_genres : "ассоциируется с"

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
<summary><b>Сценарий 1: Регистрация пользователя (без верификации)</b></summary>

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
    Note over AC: Маппинг в UserCreation
    AC->>CUUC: execute(UserCreation)
    
    CUUC->>UV: validateCreation(UserCreation)
    
    alt Валидация провалена
        UV-->>CUUC: throw ValidationException
        CUUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end
    
    CUUC->>UR: findByEmail(email)
    
    alt Пользователь найден
        UR-->>CUUC: User
        CUUC-->>AC: throw AlreadyExistsException
        AC-->>C: 409 Conflict
    end

    CUUC->>PH: hash(password)
    PH-->>CUUC: passwordHash
    
    Note over CUUC: Создание модели<br>User (isVerified=false)
    
    CUUC->>UR: save(newUser)
    Note over UR: Сохранение в базе данных
    UR-->>CUUC: createdUser (с новым id)

    Note over CUUC: Создание модели<br>EmailVerificationInitiation<br>с типом REGISTRATION

    CUUC->>EVS: initiate(EmailVerificationInitiation)
    EVS->>EVR: findByUserIdAndType(userId, VerificationType)

    alt Верификация найдена
        EVR-->>EVS: EmailVerification

        alt Прошло меньше минуты с момента создания
            EVS-->>CUUC: throw TooManyRequests
            CUUC-->>AC: throw TooManyRequests
            AC-->>C: 429 Too Many Requests
        else Код еще действителен
            Note over EVS: Повторная отправка этого кода
            EVS->>SS: sendEmail(email, VerificationMailMessage)
            EVS-->>CUUC: expiresAt
            CUUC-->>AC: UserCreationResult
            Note over AC: Маппинг в UserCreationResponse
            AC-->>C: UserCreationResponse
        end

        EVS->>EVR: deleteByUserIdAndType(userId, VerificationType)
    end

    EVS->>VTG: generate
    VTG-->>EVS: VerificationToken
    Note over EVS: Создание модели<br>EmailVerification
    EVS->>EVR: save(emailVerification)
    EVS->>SS: sendEmail(email, VerificationMailMessage)
    EVS-->>CUUC: expiresAt

    CUUC-->>AC: UserCreationResult
    Note over AC: Маппинг в UserCreationResponse
    AC-->>C: UserCreationResponse
```

</details>

<details>
<summary><b>Сценарий 2: Подтверждение почты</b></summary>

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
    Note over AC: Маппинг в ConfirmRegistration
    AC->>CRUC: execute(ConfirmRegistration)

    CRUC->>AV: validateRegistrationConfirmation(ConfirmRegistration)

    alt Валидация провалена
        AV-->>CRUC: throw ValidationException
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    CRUC->>UR: findById(userId)

    alt Пользователь не найден
        UR-->>CRUC: Пользователь не найден
        CRUC-->>AC: throw NotFoundException
        AC-->>C: 404 Not Found
    end

    alt Пользователь уже верифицирован
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    Note over CRUC: Создание модели<br>EmailVerificationConfirmation<br>с типом REGISTRATION

    CRUC->>EVS: confirm(EmailVerificationConfirmation)
    EVS->>EVR: findByUserIdAndType(userId, VerificationType)

    alt Верификация не найдена
        EVR-->>EVS: Верификация не найдена
        EVS-->>CRUC: throw NotFoundException
        CRUC-->>AC: throw NotFoundException
        AC-->>C: 404 Not Found
    end

    EVS->>VTV: validate(EmailVerification, code)

    alt Валидация провалена
        VTV-->>EVS: throw ValidationException
        EVS-->>CRUC: throw ValidationException
        CRUC-->>AC: throw ValidationException
        AC-->>C: 400 Bad Request
    end

    EVS->>EVR: deleteByUserIdAndType(userId, VerificationType)
    EVS-->>CRUC: EmailVerification

    Note over CRUC: Создание модели User<br>с isVerified = true

    CRUC->>UR: save(verifiedUser)
    CRUC->>ATS: issueTokens(userId)

    Note over ATS: Создание модели<br>IdentityTokenClaims<br>с содержимым для access токена
    ATS->>ITP: encode(IdentityTokenClaims)
    ITP-->>ATS: accessToken (String)
    
    Note over ATS: Создание модели<br>IdentityTokenClaims<br>с содержимым для refresh токена
    ATS->>ITP: encode(IdentityTokenClaims)
    ITP-->>ATS: refreshToken (String)
    
    ATS->>PH: hash(refreshToken)
    PH-->>ATS: tokenHash
    Note over ATS: Создание модели RefreshToken<br>с tokenHash
    ATS->>RTR: save(RefreshToken)
    ATS-->>CRUC: TokenPair(accessToken, refreshToken)

    CRUC-->>AC: TokenPair(accessToken, refreshToken)
    Note over AC: Маппинг в AuthResponse
    AC-->>C: AuthResponse(accessToken, refreshToken)
```

</details>

## Настройка и разработка

### Требования

- Docker & Docker Compose
- Java 17+ (для локальной разработки)

### Конфигурация

1. Скопируйте файл с примером переменных окружения:
   ```bash
   cp .env.example .env
   ```
2. Заполните учетные данные и настройки в файле `.env` (БД, MinIO, Argon2, SMTP, JWT).

### Варианты запуска

#### 1. Разработка (только инфраструктура)

Запуск PostgreSQL и MinIO для работы с приложением напрямую из IDE:

```bash
docker compose -f docker-compose.dev.yml up --build -d
```

#### 2. Продакшн (полный запуск из исходников)

Сборка и запуск всех сервисов (Приложение + БД + Хранилище) в контейнерах:

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

## API Документация

После запуска сервера и активации Swagger в `.env` (`ENABLE_SWAGGER_UI=true`), вы можете изучить API и протестировать
эндпоинты через Swagger UI:
`http://localhost:8080/swagger-ui/index.html`

### Краткий справочник по API

Все эндпоинты имеют префикс `/api/v1`.

| Метод              | Эндпоинт                     | Нужна аутентификация | Описание                             |
|--------------------|------------------------------|----------------------|--------------------------------------|
| **Аутентификация** |
| `POST`             | `/auth/register`             | Нет                  | Зарегистрировать нового пользователя |
| `POST`             | `/auth/confirm-registration` | Нет                  | Подтвердить регистрацию              |
| `POST`             | `/auth/login`                | Нет                  | Авторизоваться                       |
| `POST`             | `/auth/refresh`              | Нет                  | Обновить токены доступа              |
| `POST`             | `/auth/logout`               | Нет                  | Выйти                                |
| `POST`             | `/auth/resend-code`          | Нет                  | Повторно отправить код подтверждения |
| **Пользователи**   |
| `GET`              | `/users/me`                  | **Да**               | Получить текущие данные пользователя |
| **Книги**          |
| `GET`              | `/books`                     | **Да**               | Получить все книги пользователя      |
| `GET`              | `/books/{id}`                | **Да**               | Получить книгу по id                 |
| `DELETE`           | `/books/{id}`                | **Да**               | Удалить книгу по id                  |
| `PATCH`            | `/books/{id}`                | **Да**               | Обновить сведения о книге            |
| `POST`             | `/books`                     | **Да**               | Создать книгу                        |
| `POST`             | `/books/{id}/cover`          | **Да**               | Загрузить обложку книги              |
| `DELETE`           | `/books/{id}/cover`          | **Да**               | Удалить обложку книги                |
| `GET`              | `/books/{id}/cover`          | **Да**               | Получить обложку книги               |
| **Жанры**          |
| `GET`              | `/genres`                    | **Да**               | Получить все жанры книг              |
| `GET`              | `/genres/{id}`               | **Да**               | Получить жанр книги по id            |

