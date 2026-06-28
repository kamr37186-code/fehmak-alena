# Architecture Overview: Fahmak Alena

This document outlines the high-level system architecture for the **Fahmak Alena** AI-Powered Educational Platform.

## 1. System Components

The platform follows a conceptual Microservices architecture pattern designed for scalability and clear separation of concerns.

### Frontend Application
*   **Framework:** Angular 21 (Zoneless)
*   **State Management:** Angular Signals
*   **Responsibility:** Provides the UI for Students, Instructors, and Administrators. Communicates with backend via REST APIs.

### Backend Application
*   **Framework:** Spring Boot 4.1.0
*   **Language:** Java 17
*   **Responsibility:** Handles business logic, data persistence, security, and external AI integrations.

## 2. Microservices Architecture (Conceptual)

Although currently residing within a monolithic Spring Boot structure (for development simplicity), the backend is logically separated into the following domains, allowing future decoupling:

*   **User Service (Auth & Profile):** Manages authentication (Spring Security, JWT), user roles (Student, Instructor, Admin), and profile data.
*   **Course Service:** Manages curriculum, modules, lessons, and content delivery.
*   **Assessment Service:** Handles quizzes, exams, adaptive testing logic, and grading.
*   **Gamification Service:** Manages points, badges, leaderboards, and achievements.
*   **AI Learning Service:** Integrates with Google Gemini / Vertex AI to provide personalized paths and content summarization.
*   **Dashboard Service:** Aggregates data to provide real-time analytics for students and instructors.

## 3. Data Storage

*   **Primary Database:** PostgreSQL (Production) / H2 (Development). Relational data (users, courses, grades).
*   **Caching Layer:** Redis. Used for session management, fast retrieval of frequently accessed data (like leaderboards), and rate-limiting.

## 4. Security Architecture

*   **Authentication:** Stateless JWT (JSON Web Tokens) passed in the `Authorization: Bearer <token>` header.
*   **Authorization:** Role-Based Access Control (RBAC) enforced at both the API level (`@PreAuthorize`) and the UI router level.
*   **Data Protection:** Passwords hashed via BCrypt.

## 5. Deployment Topology

*   **Containerization:** Docker & Docker Compose are used for local development, packaging the backend, database, and caching layers.
*   **Future Scaling:** The microservice-ready structure allows deployment to Kubernetes clusters or cloud-native container services (e.g., AWS ECS, Google Cloud Run).
