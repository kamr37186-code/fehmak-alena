# Master Prompt for Claude: Fahmak Alena Implementation Plan

*Copy the text below and paste it directly into your prompt for Claude.*

---

**Role:** You are an Expert Full-Stack Software Architect and Lead Developer, specializing in modern Spring Boot 4.1.0 (Java 17) backend development and Angular 21 (Zoneless, Signal-based) frontend development. 

**Objective:** I need you to review the comprehensive documentation, technical analysis, and gap analysis for my project, "Fahmak Alena". Based on this information, you must generate a highly detailed, step-by-step execution plan to implement the remaining roadmap features. My message limit is running low, so I need this response to be as exhaustive, precise, and actionable as possible in a single shot. 

Please carefully read the following three context documents about the project:

### 1. Project Context & Guidelines
- **Description:** An AI-Powered Educational Platform featuring personalized learning paths, adaptive assessments, gamification, and specialized dashboards for Students, Instructors, and Administrators.
- **Frontend Stack:** Angular 21 (Standalone Components API, Zoneless Change Detection, Signal-based reactivity, Vitest), Tailwind CSS v4, TypeScript, RxJS.
- **Backend Stack:** Spring Boot 4.1.0 (Java 17), PostgreSQL, Redis, Spring Security, JWT (Role-based access).
- **Backend Architecture:** Modular Monolithic Architecture. Divided into logical domain packages: `admin`, `ai`, `assessment`, `course`, `gamification`, `instructor`, `notification`, `payment`, `user`. Layered pattern (`Controllers` -> `Services` -> `Repositories`). Heavy use of DTOs and Lombok.
- **Frontend Architecture:** Feature-first Standalone Architecture. Divided into features: `accessibility-preferences`, `achievements`, `adaptive-assessment`, `admin-dashboard`, `ai-chat`, `auth`, `course-details`, `immersive-hub`, `instructor-dashboard`, `instructor-integrity`, `landing`, `leaderboard`, `library`, `profile-settings`, `student-dashboard`, `virtual-classroom`. Shared components: `header`, `sidebar`. Core services, guards, interceptors, and models are in the `core` folder.
- **UI/UX Rules:** Use Tailwind colors: `brand-50` to `brand-900` (blues, primary is `brand-500` #3b82f6), `surface` whites/grays. Font: `Inter`. Shadows: `shadow-soft` (0 4px 20px -2px rgba(0, 0, 0, 0.05)), `shadow-glow` (0 0 20px rgba(59, 130, 246, 0.5)). Use rounded corners (`rounded-lg` to `rounded-2xl`).

### 2. Deep Technical Analysis (Current State)
- The backend is a cohesive Spring Boot application (`AlenaBackendApplication.java`). Security uses `JwtAuthenticationFilter` and `JwtService` localized within the `user.security` package. Integrates Stripe for payments and WebSockets for real-time signaling.
- The frontend strictly uses Standalone components with Zoneless change detection. Routing is in `app.routes.ts` with lazy loading and SSR. API communication layers are in `core/services/` (e.g., `ai.service.ts`, `course.service.ts`). WebRTC is powered by ZegoCloud UIKits in `@defer` blocks.
- Infrastructure uses `docker-compose.yml` for PostgreSQL and Redis.

### 3. Gap Analysis (Remaining Features to Implement)
We must implement the following features to reach our final production-ready state:
1.  **Spline 3D Integration Enhancements:** Expand on the 3D Spline implementations in the immersive hub. (Priority: Medium)
2.  **Advanced Cloud Deployments:** Need to configure Kubernetes deployment descriptors and GitHub Actions CI/CD pipelines. (Priority: High)
3.  **Analytics & Reporting:** Complete the Admin revenue dashboard with detailed charts. (Priority: Medium)
4.  **Automated Data Backups:** Enhance the `docker-compose` setup with daily automated data backups (`pg_dump`) to secure cloud storage. (Priority: High)

---

### YOUR TASK & REQUIRED DELIVERABLES:

Based on the rigorous constraints of the existing architecture (Modular Monolith, Angular Standalone, specific UI themes), provide a Master Execution Plan. Format your response clearly with Markdown. Do not give generic advice; give concrete technical specifications.

Please provide:

**Part 1: Database Schema & Domain Modeling**
- Define the exact new JPA Entities required for any missing features (e.g., `AnalyticsData`).
- Detail the exact fields, relationships (e.g., `@OneToMany`, `@ManyToOne`), and constraints. 

**Part 2: API Contract Design (REST)**
- List the new REST endpoints (URLs, HTTP Methods) required for Analytics.

**Part 3: Backend Implementation Plan (Spring Boot 4.1.0)**
- Provide a step-by-step roadmap for implementing any remaining backend features.
- Detail which specific classes, controllers, and services need to be created or modified.

**Part 4: Frontend Implementation Plan (Angular 21)**
- Provide a step-by-step roadmap for implementing:
  1. The new standalone components needed (e.g., `admin-revenue-charts`).
  2. Updates to existing features.
  3. New services required in `core/services`.
- Remind yourself of the Tailwind UI/UX rules and explain how the new components will adhere to the `brand` colors and `shadow-soft`/`shadow-glow` classes.
- Ensure all implementations use Signal-based APIs and strictly avoid `ReactiveFormsModule` or `zone.js`.

**Part 5: DevOps & Backup Strategy**
- Provide a concrete script or Docker-based strategy to fulfill the "Automated Data Backups" requirement (e.g., a sample bash script using `pg_dump` and AWS CLI/gsutil running on a cronjob).

Please ensure your response is extremely comprehensive, as this will serve as the architectural blueprint for the rest of the project's development. Take a deep breath and think step-by-step.
