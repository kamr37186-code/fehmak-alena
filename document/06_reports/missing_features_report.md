# Gap Analysis & Missing Features Report

*Date: June 12, 2026*

This report outlines the remaining features and structural requirements that need to be addressed before the **Fahmak Alena** platform reaches full production readiness. 

The previous major gaps (Angular 21 migration, Zoneless architecture, Signal Forms, Stripe integration, Vitest testing, and WebRTC integration) have been successfully resolved.

## 1. Cloud Deployments & CI/CD
- **Current State:** The application runs locally via Docker Compose or manual `npm start` and `mvnw spring-boot:run` commands.
- **Missing Feature:** We need a robust Continuous Integration / Continuous Deployment (CI/CD) pipeline using GitHub Actions.
- **Action Items:**
  - Create Dockerfiles optimized for production builds (multi-stage) for both frontend and backend.
  - Configure Kubernetes deployment and service descriptors.
  - Setup automated daily database backups (`pg_dump`) to secure cloud storage (AWS S3 or GCP Cloud Storage).

## 2. Advanced Analytics & Reporting
- **Current State:** Basic data aggregation exists.
- **Missing Feature:** The Admin Revenue Dashboard and Instructor Analytics lack detailed, interactive data visualizations.
- **Action Items:**
  - Integrate a charting library compatible with Angular 21 Signals (e.g., modern Chart.js or specialized D3 signal wrappers).
  - Expose complex SQL aggregation endpoints via the backend `/api/dashboard`.

## 3. Immersive Hub 3D Enhancements
- **Current State:** The Immersive Hub supports WebRTC collaborative sessions and basic 3D environments via `@splinetool/viewer`.
- **Missing Feature:** The 3D environments need deeper interactivity and event-driven communication with the Angular state.
- **Action Items:**
  - Map Spline 3D objects to interactive Angular events using `spline-viewer` event listeners.
  - Synchronize avatar positions or interactive states across clients via WebSockets.

## 4. Social Login Completion
- **Current State:** JWT Email/Password authentication is fully functional.
- **Missing Feature:** Google/Facebook OAuth2 Client integration on the backend.
- **Action Items:**
  - Configure Spring Security OAuth2 resource server/client.
  - Update `User` entity to support multiple authentication providers.
  - Add Google/Facebook sign-in buttons to the frontend auth components.
