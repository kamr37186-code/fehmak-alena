# Fahmak Alena - Backend Application

This is the Spring Boot backend for the **Fahmak Alena** AI-powered educational platform.

## Architecture

The backend is built using a **Modular Monolithic Architecture**. 
It utilizes **Spring Boot 4.1.0** (Java 17) and is logically divided into distinct packages (domains) rather than physically separate microservices. 

Implemented domains:
- `admin`
- `ai`
- `assessment`
- `course`
- `gamification`
- `instructor`
- `notification`
- `payment`
- `user`

## Technology Stack
- **Framework:** Spring Boot 4.1.0
- **Language:** Java 17
- **Database:** PostgreSQL (production target), H2 (in-memory for development)
- **Security:** Spring Security & JWT (v0.12.6, Stateless, Role-based access)
- **Caching:** Redis (`spring-boot-starter-cache`)
- **Integrations:** 
  - **Stripe** (v24.1.0) for payment processing
  - **Google API Client** (v2.7.2) / Gemini for AI integrations
  - **WebSockets** for real-time notifications and chat
- **Testing:** TestContainers and JUnit Jupiter
- **Build Tool:** Maven

## Running the Application

To run the application locally with the in-memory H2 database:

```bash
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

## Testing

To run the test suite (which has been fully migrated to use `@MockitoBean` instead of `@MockBean` for Spring Boot 4.1.0 compatibility):

```bash
./mvnw clean test
```

## Security & Authentication

The application uses **JWT (JSON Web Tokens)** for stateless authentication.
Endpoints are protected based on roles:
- `ROLE_STUDENT`
- `ROLE_INSTRUCTOR`
- `ROLE_ADMIN`

Test accounts are seeded into the database automatically on startup (check the root project README for credentials).
