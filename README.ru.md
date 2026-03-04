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
книг, жанрами и обеспечивает безопасное хранение обложек.

## Стек технологий

- **Фреймворк:** Spring Boot 4 (Java 17)
- **База данных:** PostgreSQL
- **Миграции:** Liquibase (YAML)
- **Хранилище файлов:** MinIO (S3 совместимое)
- **Документация:** OpenAPI / Swagger UI
- **Развертывание:** Docker & Docker Compose

## Архитектура

Проект следует принципам **Clean Architecture** для обеспечения расширяемости и тестируемости:

- **Web:** REST контроллеры, DTO и мапперы.
- **Application:** Оркестрация бизнес-логики через юзкейсы.
- **Domain:** Чистая бизнес-логика, сущности и интерфейсы репозиториев.
- **Data:** Реализация инфраструктуры (JPA репозитории, S3 хранилище, обработчик изображений).

## Настройка и разработка

### Требования

- Docker & Docker Compose
- Java 17+ (для локальной разработки)

### Конфигурация

1. Скопируйте файл с примером переменных окружения:
   ```bash
   cp .env.example .env
   ```
2. Заполните учетные данные и настройки в файле `.env` (БД, MinIO).

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
`http://localhost:8080/swagger-ui.html`

### Краткий справочник по API

Все эндпоинты имеют префикс `/api/v1`.

| Метод     | Эндпоинт            | Описание                  |
|-----------|---------------------|---------------------------|
| **Книги** |
| `GET`     | `/books`            | Получить все книги        |
| `GET`     | `/books/{id}`       | Получить книгу по id      |
| `DELETE`  | `/books/{id}`       | Удалить книгу по id       |
| `PATCH`   | `/books/{id}`       | Обновить сведения о книге |
| `POST`    | `/books`            | Создать книгу             |
| `POST`    | `/books/{id}/cover` | Загрузить обложку книги   |
| `DELETE`  | `/books/{id}/cover` | Удалить обложку книги     |
| `GET`     | `/books/{id}/cover` | Получить обложку книги    |
| **Жанры** |
| `GET`     | `/genres`           | Получить все жанры книг   |
| `GET`     | `/genres/{id}`      | Получить жанр книги по id |
