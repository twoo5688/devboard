# DevBoard

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot)
![Angular](https://img.shields.io/badge/Angular-21-DD0031?logo=angular)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

DevBoard is a **portfolio-grade, full-stack Kanban project management app**: JWT-authenticated users manage **projects** and **tasks** with drag-and-drop columns (To do / In progress / Done), backed by **Spring Boot** and **PostgreSQL**, with an **Angular** SPA.

> **Note:** This repository is a **portfolio project** demonstrating clean architecture, TDD-friendly tests, and production-minded Docker setup—not a commercial product.

## Features

- User **registration** and **login** with **JWT** (Bearer token stored client-side)
- **Projects** CRUD for the authenticated owner
- **Tasks** per project with **Kanban** board and **Angular CDK** drag-and-drop
- **Dark-themed** responsive UI (SCSS + Google Fonts)
- **OpenAPI / Swagger UI** on the backend
- **CORS** enabled for local Angular dev (`http://localhost:4200`)
- **Docker Compose** for Postgres, API, and static frontend (Nginx reverse proxy to `/api`)

## Architecture (ASCII)

```
┌─────────────────────────────────────────────────────────────────┐
│  Browser (Angular SPA) :4200  │  docker: Nginx → static + /api │
└───────────────────┬─────────────────────────────────────────────┘
                    │  HTTP  /api/*
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│  Spring Boot API :8080                                           │
│  Security: JWT filter → controllers → services → JPA repos       │
└───────────────────┬─────────────────────────────────────────────┘
                    │  JDBC
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│  PostgreSQL :5432 (Docker)                                       │
└─────────────────────────────────────────────────────────────────┘
```

## Prerequisites

- **Docker** and **Docker Compose**
- For **local** frontend dev (optional): **Node.js** `^20.19.0` or `^22.12.0` (see `frontend/package.json` `engines`). NPM scripts use a temporary **Node 22** via `npx` if your global Node is older.
- **Java 21** and **Maven** (for backend tests / non-Docker runs)

## How to run locally (Docker)

From the repository root:

```bash
cp .env.example .env
docker compose up --build
```

The example values are intended for local development. Change `DB_PASSWORD` and `JWT_SECRET` before deploying outside your machine.

Then:

| Service    | URL |
|-----------|-----|
| Frontend  | http://localhost:4200 |
| Backend   | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Postgres  | `localhost:5432` (credentials configured in `.env`) |

**Smoke flow:** register → create a project → open the board → add tasks → drag tasks between columns. Use the browser **Network** tab to confirm `/api/...` calls return **200/201/204**.

### Local frontend against Docker backend

```bash
cd frontend
npm install
npm start
```

`ng serve` uses `proxy.conf.json` to forward `/api` to `http://localhost:8080`.

## API endpoints

All authenticated routes expect header: `Authorization: Bearer <jwt>`.

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Register; returns token |
| `POST` | `/api/auth/login` | Login; returns token |
| `GET` | `/api/projects` | List current user’s projects |
| `GET` | `/api/projects/{id}` | Get an owned project |
| `POST` | `/api/projects` | Create project |
| `PUT` | `/api/projects/{id}` | Update an owned project |
| `DELETE` | `/api/projects/{id}` | Delete project |
| `GET` | `/api/projects/{projectId}/tasks` | List tasks |
| `POST` | `/api/projects/{projectId}/tasks` | Create task |
| `PUT` | `/api/projects/{projectId}/tasks/{taskId}` | Update task |
| `DELETE` | `/api/projects/{projectId}/tasks/{taskId}` | Delete task |
| `GET` | `/api/health` | Health check |

OpenAPI JSON: `http://localhost:8080/v3/api-docs`.

## Folder structure (overview)

```
devboard/
├── backend/                 # Spring Boot API
│   └── src/main/java/...    # controllers, services, entities, security
├── frontend/                # Angular SPA
│   └── src/app/
│       ├── auth/            # login, register
│       ├── projects/        # list, kanban, task modal
│       ├── core/            # guard, interceptor, validators
│       ├── models/          # TypeScript interfaces
│       └── services/        # HTTP services
├── docker-compose.yml
└── README.md
```

## Tests

**Backend**

```bash
cd backend && mvn clean test
```

Uses an **H2 in-memory** database (`application-test.properties`) so tests do not require Postgres.

**Frontend**

```bash
cd frontend && npm test
```

## OpenAPI dependency note

The app uses **Spring Boot 4.x**. Swagger is provided by **springdoc-openapi** version **3.0.3**, which tracks the Spring Boot 4 line (the older `2.3.0` coordinates are intended for Spring Boot 3.x).

## License

See [LICENSE](LICENSE).
