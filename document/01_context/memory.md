# Fahmak Alena - Project Memory & Deep Analysis

This document serves as the internal memory and comprehensive analysis of the **Fahmak Alena** project. It is structured to provide an immediate and deep understanding of the project's architecture, technologies, components, and best practices.

## 1. Project Overview
- **Name:** Fahmak 3alena (Fahmak Alena)
- **Description:** A modern, AI-powered learning platform offering personalized learning paths, adaptive assessments, gamification, immersive video classrooms, and role-based dashboards (Student, Instructor, Admin).
- **Core Philosophy:** Clean, modern UI (glassmorphism/premium feel) backed by a robust and highly modular backend.
- **Key Accounts:**
  - Student: `omar.student@fahmak.com` / `password123`
  - Instructor: `ahmed.instructor@fahmak.com` / `password123`
  - Admin: `admin@fahmak.com` / `password123`

## 2. Infrastructure & Environment
- **Backend:** Runs on `http://localhost:8080`. Requires Java 17+. Run via `./mvnw spring-boot:run`.
- **Frontend:** Runs on `http://localhost:4200`. Requires Node.js (v18+). Run via `npm start`.
- **Database:** 
  - Dev: In-memory H2 Database.
  - Prod: PostgreSQL 15 (configured via Docker Compose).
- **Caching:** Redis 7 (configured via Docker Compose).
- **AI Integration:** OpenAI / Groq API used for AI features. Requires `OPENAI_API_KEY`.
- **DevOps:** Includes `docker-compose.yml` for Postgres, Redis, and a DB-backup cron job.

## 3. Backend Architecture (Spring Boot 4.1.0, Java 17)
The backend follows a **Modular Monolithic** architecture. It is divided into isolated domain packages rather than separate microservices.

### Domain Modules (`com.fahmak.alena.*`)
Each module follows the standard layered architecture (`Controller` -> `Service` -> `Repository` / `Entity` / `DTO`).
- **`admin`**: Administrative controls and dashboards.
- **`ai`**: Integrations with LLMs/Vertex AI for content generation and assistance.
- **`assessment`**: Handles Quizzes, Questions, Quiz Sessions, and Cheat Events (Proctoring/Integrity).
- **`course`**: Course management, modules, content delivery.
- **`gamification`**: Badges, points, and motivational systems.
- **`immersive`**: Features related to the immersive hub or virtual environments.
- **`instructor`**: Instructor-specific workflows and analytics.
- **`notification`**: System and user notifications.
- **`payment`**: Stripe integration and Webhooks.
- **`user`**: Core user management, security, and DTOs.

### Core Configuration (`config` package)
- **Security:** Managed by `SecurityConfig.java`. Stateless JWT authentication. Routes like `/api/auth/**` and `/api/public/**` are permitted. Other endpoints are secured by roles (`@EnableMethodSecurity`).
- **Data Initialization:** `DataInitializer.java` handles seeding the DB with mock data/users on startup.
- **Exception Handling:** `GlobalExceptionHandler.java` catches exceptions and formats them into a standardized `ErrorResponse`.
- **CORS:** Global CORS configured in `CorsConfig.java` (defaults to allow `http://localhost:4200`).

## 4. Frontend Architecture (Angular 21)
The frontend embraces a **Feature-First Standalone** component architecture, dropping NgModules entirely in favor of `standalone: true`. It utilizes **Zoneless Change Detection**, **Signal-based reactivity**, and **Signal-based Forms**. Testing is powered by **Vitest**.

### Core Layers (`src/app`)
- **`core/`**: The backbone of the app.
  - **`services/`**: Feature-specific API clients and state managers (e.g., `ai.service.ts`, `auth.service.ts`, `course.service.ts`, `preferences.service.ts`).
  - **`models/`**: TypeScript interfaces (e.g., `assessment.model.ts`, `auth.model.ts`).
  - **`guards/`**: Routing protection (`auth.guard.ts`, `role.guard.ts`).
  - **`interceptors/`**: HTTP interceptors (likely attaching JWTs).
- **`shared/`**: Reusable UI components.
  - `header/`, `sidebar/`

### Features Layer (`src/app/features`)
Contains standalone components loaded lazily via `app.routes.ts`:
- **Auth:** `login`, `register`.
- **Dashboards:** `student-dashboard`, `instructor-dashboard`, `admin-dashboard`.
- **Core Learning:** `course-details`, `adaptive-assessment`, `library`.
- **Interactive:** `virtual-classroom`, `tutoring-room`, `immersive-hub`, `ai-chat`.
- **Engagement:** `achievements`, `leaderboard`.
- **Other:** `landing`, `pricing`, `accessibility-preferences`.

## 5. UI/UX & Design System (Tailwind CSS v4)
The project strictly enforces a premium, modern aesthetic using Tailwind CSS v4 and GSAP animations.
- **Typography:** `Inter`, sans-serif. (`text-gray-900 bg-gray-50 antialiased` globally).
- **Brand Palette (Blues):** `brand-50` to `brand-900`, with `brand-500` (#3b82f6) as the primary brand color.
- **Surface Palette:** `surface` (#ffffff) to `surface-200`.
- **Shadows & Effects:** 
  - `shadow-soft`: For floating cards and clean depth.
  - `shadow-glow`: For active states or primary interactive buttons.
- **Guidelines:** Abundant whitespace, rounded corners (`rounded-lg`, `rounded-xl`), component-based reusability, and mobile-first responsiveness (`sm:`, `md:`, `lg:`).

## 6. Best Practices & Agent Guidelines
When modifying or extending this project, strictly adhere to:
1. **Frontend:** Use Standalone Components in Angular 21. Strictly use Zoneless Change Detection and Signal APIs. Style exclusively with Tailwind v4. For unit testing, use Vitest.
2. **Backend:** Use Lombok annotations. Group by domain (Controller/Service/Repo). Return DTOs, not raw Entities. Protect endpoints via Spring Security.
3. **Consistency:** Ensure new features match the established visual hierarchy and role-based logic. Maintain the "modern & clean" architectural vision.
