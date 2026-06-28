# Frontend Roadmap & Architecture

This document details the frontend architecture and development roadmap for the **Fahmak Alena** Angular application.

## 1. Architectural Principles

*   **Framework:** Angular 21
*   **Pattern:** Feature-First Standalone Components. We strictly use standalone components to eliminate `NgModule` boilerplate and maximize tree-shaking efficiency.
*   **Reactivity:** Zoneless change detection combined with Angular Signals (`signal`, `computed`, `input`, `viewChild`). We also use Signal-based Forms.
*   **Styling:** Tailwind CSS v4. We strictly use utility classes in HTML and adhere to the project's custom `brand` color palette and defined shadows.

## 2. Directory Structure

```
src/
└── app/
    ├── core/          # Singletons, interceptors, auth guards, services
    ├── shared/        # Reusable UI components (buttons, cards, inputs), pipes, directives
    └── features/      # Feature modules/components (lazy loaded)
        ├── auth/      # Login, Register
        ├── landing/   # Public facing landing page
        ├── student-dashboard/
        ├── instructor-dashboard/
        ├── admin-dashboard/
        ├── virtual-classroom/
        └── adaptive-assessment/
```

## 3. State Management (Angular Signals)

We utilize **Angular Signals** embedded within Injectable services to manage complex state across the application, adhering to modern reactive patterns instead of heavyweight NgRx.

### Key State Services:
1.  **Auth State:** Manages the current user, JWT token, and role using `signal()`.
2.  **Course State:** Manages the list of enrolled courses, current progress, and active modules.
3.  **Assessment State:** Tracks live quiz progress, timers, and AI-generated dynamic questions.
4.  **Gamification State:** Live points, unlocked badges, and leaderboard rankings.

## 4. Development Roadmap

### Phase 1: Core Foundation & Migrations (Completed)
*   [x] Setup Angular workspace and Tailwind CSS v4 configuration.
*   [x] Build global UI components (Navbar, Footer, Base Cards) with GSAP.
*   [x] Implement high-converting, modern Landing Page.
*   [x] Implement Auth UI (Login/Register) and basic routing guards.
*   [x] Migrate to Angular 21 (Zoneless, Signals, Signal Forms).
*   [x] Migrate from Jasmine/Karma to Vitest.
*   [x] Setup SSR with Event Replay and Hydration.

### Phase 2: Student & Instructor Experience (Completed)
*   [x] Build Student Dashboard (Course overview, recent activity).
*   [x] Build Instructor Dashboard (Analytics, course management).
*   [x] Implement Adaptive Assessment UI and Instructor Integrity Dashboard.
*   [x] Integrate ZegoCloud WebRTC for Immersive Hub / Virtual Classroom.

### Phase 3: Enhancements & Cloud Deployments (In Progress)
*   [ ] Enhance 3D Spline environments inside the Immersive Hub.
*   [ ] Implement advanced analytics charts for Admin Revenue.
*   [ ] Optimize lazy loading boundaries and bundle sizes (`@defer` usage).
*   [ ] Containerize with Docker and deploy to Kubernetes.
