<br>
<p align="center">
  <img src="assets/logo.png" alt="BookTracker Logo" width="250"/>
</p>
<br>

<p align="center">
  <a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/license-MIT-yellow.svg" alt="MIT">
  </a>
  <a href="https://github.com/Nirtas/booktracker-backend/releases/latest">
    <img src="https://img.shields.io/github/v/release/Nirtas/booktracker-backend" alt="GitHub Release">
  </a>
  <a href="https://hub.docker.com/r/jerael/booktracker-backend">
    <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff" alt="Docker">
  </a>
</p>

[Русская версия](README.ru.md)

# BookTracker

Backend for tracking your reading progress, built with **Clean Architecture** principles. This server manages book data,
genres, and secure cover storage.

## Tech Stack

- **Framework:** Spring Boot 4 (Java 17)
- **Database:** PostgreSQL
- **Migrations:** Liquibase (YAML)
- **File Storage:** MinIO (S3 compatible)
- **Documentation:** OpenAPI / Swagger UI
- **Deployment:** Docker & Docker Compose

## Architecture

The project follows **Clean Architecture** to ensure maintainability and testability:

- **Web:** REST Controllers, DTOs, and Web Mappers.
- **Application:** Orchestration of business logic through use cases.
- **Domain:** Pure business logic, Entities and Repository interfaces.
- **Data:** Infrastructure implementations (JPA Repositories, S3 Storage, Image Processor).

## Setup & Development

### Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)

### Configuration

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
2. Fill in your credentials and properties in the `.env` file (DB, MinIO credentials).

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
`http://localhost:8080/swagger-ui.html`

### Quick API Reference

All endpoints are prefixed with `/api/v1`.

| Method     | Endpoint            | Description         |
|------------|---------------------|---------------------|
| **Books**  |
| `GET`      | `/books`            | Get all books       |
| `GET`      | `/books/{id}`       | Get book by id      |
| `DELETE`   | `/books/{id}`       | Delete book by id   |
| `PATCH`    | `/books/{id}`       | Update book details |
| `POST`     | `/books`            | Create book         |
| `POST`     | `/books/{id}/cover` | Upload book cover   |
| `DELETE`   | `/books/{id}/cover` | Delete book cover   |
| `GET`      | `/books/{id}/cover` | Get book cover      |
| **Genres** |
| `GET`      | `/genres`           | Get all genres      |
| `GET`      | `/genres/{id}`      | Get genre by id     |
