# Backend Services Implementation Plan

This document outlines the specific responsibilities and roadmap for the Spring Boot backend services of **Fahmak Alena**.

## 1. Technology Stack

*   **Core:** Spring Boot 4.1.0, Java 17
*   **Data Access:** Spring Data JPA, Hibernate
*   **Security:** Spring Security, JWT (io.jsonwebtoken)
*   **API Documentation:** Springdoc OpenAPI (Swagger)

## 2. Core Modules & Controllers

The application is structured by domain feature (`com.fahmak.alena.<domain>`).

### User Management (`/api/users`, `/api/auth`)
*   **Controllers:** `AuthController`, `UserController`
*   **Responsibilities:** Registration, Authentication (JWT generation), Password Reset, Profile Management, Role Assignment (Student, Instructor, Admin).

### Course Management (`/api/courses`)
*   **Controllers:** `CourseController`, `ModuleController`
*   **Responsibilities:** CRUD operations for courses, curriculum building, tracking student enrollment, and course progress tracking.

### Assessment & Grading (`/api/assessments`)
*   **Controllers:** `AssessmentController`
*   **Responsibilities:** Fetching quizzes, submitting answers, calculating scores. Integrates with the AI service to generate dynamic questions based on user skill level.

### Gamification Engine (`/api/gamification`)
*   **Controllers:** `LeaderboardController`, `AchievementController`
*   **Responsibilities:** Awarding points upon task completion, managing badges, and calculating leaderboard rankings (utilizing Redis for performance).

### Dashboard & Analytics (`/api/dashboard`)
*   **Controllers:** `StudentDashboardController`, `InstructorDashboardController`
*   **Responsibilities:** Aggregating data across multiple services to provide real-time metrics and charts for user dashboards.

### Payment & Subscriptions (`/api/payments`)
*   **Controllers:** `PaymentController`
*   **Responsibilities:** Stripe SDK integration, handling webhooks, tracking `PaymentTransaction` and `Subscription` entities.

### Collaborative & Signaling (`/ws/collaborative`)
*   **Controllers:** `CollaborativeWebSocketController`
*   **Responsibilities:** Real-time WebSockets/STOMP endpoint for matching students in the immersive hub and WebRTC signaling.

## 3. Development Roadmap

### Phase 1: Security & Foundation (Completed)
*   [x] Setup Spring Boot 4.1.0, PostgreSQL, and basic folder structure.
*   [x] Implement Spring Security configuration and JWT filters.
*   [x] Finalize User entity relationships and Auth controllers.

### Phase 2: Core Business Logic (Completed)
*   [x] Implement Course, Module, and Lesson entities and repositories.
*   [x] Implement Assessment logic and basic grading.

### Phase 3: AI & Gamification (Completed)
*   [x] Build internal AI Service client for Vertex AI/Gemini.
*   [x] Implement gamification triggers and Redis-backed leaderboards.

### Phase 4: Integrations & Cloud (In Progress)
*   [x] Integrate Stripe for payments.
*   [x] Integrate WebSockets for real-time collaboration.
*   [ ] Build complex data aggregation queries for admin dashboards.
*   [ ] Finalize Swagger documentation.
