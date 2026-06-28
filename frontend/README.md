# Fahmak Alena - Frontend Application

This project is the main interactive user interface for the **Fahmak Alena** AI-powered educational platform.

## Architecture

The frontend follows a **Feature-First Standalone Architecture**. We do not use NgModules; instead, everything is a `Standalone Component`.
- **UI Framework:** Angular 21
- **Reactivity:** Zoneless Change Detection (`provideZonelessChangeDetection`), Signal-based component state (`signal`, `computed`, `input`, `viewChild`), and Signal Forms (no `ReactiveFormsModule`).
- **Styling:** Tailwind CSS v4 (configured with `@tailwindcss/postcss`)
- **Animations:** GSAP (GreenSock Animation Platform) is heavily used for micro-animations, transitions, and modern UI interactions.
- **Testing:** Vitest replaces Jasmine/Karma, configured via `@analogjs/vite-plugin-angular`.
- **Routing & SSR:** Configured centrally in `app.routes.ts` with lazy loading. Server-Side Rendering (SSR) is enabled with runtime hydration (`RenderMode.Server`) and event replay.
- **State & Data:** Angular Services (`core/services/`) act as the single source of truth. The modern `provideHttpClient(withFetch())` API is used for backend communication.

## Key Features
- **Student & Instructor Dashboards:** Personalized views based on user roles (JWT protected).
- **Course Management:** Instructors can fully manage their courses (`/instructor/courses`).
- **Instructor Integrity Dashboard:** Review flagged student sessions and cheat events detected by the AI (`/instructor/integrity`).
- **Profile Settings:** Update personal user info and preferences (`/profile`).
- **Immersive Hub & Virtual Classroom:** WebRTC/ZegoCloud powered immersive classrooms encapsulated in `@defer` blocks for optimal SSR compatibility.
- **AI Chat & Adaptive Assessments:** Direct integration with Groq/Vertex AI via the backend.

## Development server

Run `npm start` (or `ng serve`) for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component (it is standalone by default in v21). You can also use `ng generate directive|pipe|service|class|guard|interface|enum`.

## Build

Run `npm run build` (or `ng build`) to build the project. The build artifacts will be stored in the `dist/` directory.

## Testing

Run `npm run test` (or `vitest run`) to execute the unit tests via Vitest.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
