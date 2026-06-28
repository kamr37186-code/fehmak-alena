# Fahmak Alena - Project Context & AI Instructions

## 1. Project Overview
- **Name:** Fahmak Alena
- **Description:** An AI-Powered Educational Platform featuring personalized learning paths, adaptive assessments, gamification, and specialized dashboards for Students, Instructors, and Administrators.
- **Goal:** To provide a comprehensive, interactive, and intelligent learning environment.

## 2. Technology Stack
**Frontend:**
- **Framework:** Angular 21 (Zoneless, Signal-based Reactivity)
- **Styling:** Tailwind CSS v4
- **Animations:** GSAP (GreenSock Animation Platform) for rich, interactive, and high-performance micro-animations and transitions.
- **State Management/Core:** TypeScript, HTML, CSS, RxJS, Angular Signals
- **Architecture Features:** Feature-first modular structure with Standalone Components.

**Backend:**
- **Framework:** Spring Boot 4.1.0 (Java 17)
- **Database:** PostgreSQL (production/target), H2 (in-memory for development)
- **Caching/Data:** Redis
- **Security:** Spring Security, JWT (Role-based access)
- **AI Integration:** Google Gemini / Vertex AI API

**Architecture:**
- **Backend:** Modular Monolithic Architecture. The system is divided logically into distinct packages representing core domains rather than separate physical microservices. Implemented domains include:
  - `admin`, `ai`, `assessment`, `course`, `gamification`, `instructor`, `notification`, `payment`, `user`.
- **Frontend:** Feature-first Standalone Architecture. The frontend uses a highly modular structure divided into features:
  - `accessibility-preferences`, `achievements`, `adaptive-assessment`, `admin-dashboard`, `ai-chat`, `auth`, `course-details`, `course-management`, `immersive-hub`, `instructor-dashboard`, `instructor-integrity`, `landing`, `leaderboard`, `library`, `profile-settings`, `student-dashboard`, `virtual-classroom`.
  - Shared components like `header` and `sidebar`.
  - Core services, guards, interceptors, and models.

## 3. UI/UX Design System & Theme
When generating new frontend components or pages, strictly adhere to the following Tailwind CSS configuration and design principles:

**Typography:**
- **Primary Font:** `Inter`, sans-serif
- **General Styling:** `text-gray-900 bg-gray-50 antialiased` (Defined in global `index.html`)

**Color Palette:**
- **Brand Colors (Blues):**
  - `brand-50`: `#eff6ff`
  - `brand-100`: `#dbeafe`
  - `brand-500`: `#3b82f6` (Primary Brand Color)
  - `brand-600`: `#2563eb` (Hover states)
  - `brand-900`: `#1e3a8a` (Dark text/accents)
- **Surface Colors (Whites/Grays):**
  - `surface`: `#ffffff` (Default background for cards/containers)
  - `surface-50`: `#f8fafc`
  - `surface-100`: `#f1f5f9`
  - `surface-200`: `#e2e8f0`

**Effects & Shadows:**
- `shadow-soft`: `0 4px 20px -2px rgba(0, 0, 0, 0.05)` (Use for soft floating cards and containers to give a modern depth)
- `shadow-glow`: `0 0 20px rgba(59, 130, 246, 0.5)` (Use for glowing interactive elements, primary buttons, or active states)

**Design Principles:**
- **Modern & Clean:** Use plenty of whitespace, rounded corners (e.g., `rounded-lg`, `rounded-xl`, `rounded-2xl`), and the `shadow-soft` class to create a premium, glassmorphic or highly polished feel.
- **Component Based:** Build reusable UI components rather than duplicating layouts.
- **Responsive:** Always use Tailwind's responsive utility variants (`sm:`, `md:`, `lg:`, `xl:`) to ensure perfect layout across mobile, tablet, and desktop screens.

## 4. Coding Conventions & Best Practices
**Frontend (Angular 21):**
- Strictly use **Standalone Components** (`standalone: true`).
- Strictly use **Zoneless Change Detection**. Do not import or use `zone.js`.
- Strictly use **Signal-based reactivity** (`signal()`, `computed()`, `input()`, `viewChild()`).
- Strictly use **Signal-based Forms**. Do NOT use or import `ReactiveFormsModule`, `FormGroup`, or `FormControl`.
- Use `app.routes.ts` for routing configurations with Server-Side Rendering (`RenderMode`).
- Style strictly with Tailwind v4 utility classes in the HTML.
- For unit testing, use **Vitest** (`describe`, `it`, `expect` from `vitest/globals`). Do NOT use Jasmine or Karma.
- Encapsulate heavy third-party libraries (e.g. ZegoCloud) in `@defer` blocks to prevent SSR optimization bailouts.

**Backend (Spring Boot):**
- Use Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, etc.) to reduce boilerplate code.
- Follow standard layered architecture within each domain package: `Controllers` -> `Services` -> `Repositories`.
- Keep entities within their respective domain packages (e.g., `Assessment`, `Course`, `User`).
- Secure API endpoints using Spring Security, JWT interceptors, and role-based access.
- Ensure database interactions are optimized, utilizing Spring Data JPA.

## 5. Instructions for AI Assistants
When asked to write code, design new pages, or add features to "Fahmak Alena", you must act as if you were the original author by following these rules:
1. **Match the Style:** Always use the defined `brand` and `surface` color palettes, along with the `Inter` font and custom shadows from the Tailwind config. Do not use generic colors when custom brand colors are available.
2. **Contextual Awareness:** Remember the distinct roles (Student, Instructor, Admin) and tailor the UI, wording, and logic accordingly.
3. **Integration:** If adding new backend features, structure them as new packages within the monolithic architecture following the existing `controller/service/repository` pattern.
4. **Consistency:** Write modern, clean, and robust code matching Angular 21 (Zoneless, Signals) and Spring Boot 4.1.0 standards. Always verify that the generated code aligns with the overall "Fahmak Alena" vision of being a cutting-edge, AI-powered educational platform.
