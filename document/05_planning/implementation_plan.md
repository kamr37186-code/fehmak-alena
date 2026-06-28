# Master Implementation Plan

*Updated: June 12, 2026*

This document serves as the master checklist for implementing the remaining features identified in the Gap Analysis, following the successful Angular 21 / Spring Boot 4.1.0 modernization phase.

## Module 1: CI/CD Pipeline (High Priority)
1. **GitHub Actions Workflow**
   - Create `.github/workflows/main.yml`.
   - Add jobs for: 
     - `backend-test`: Run `mvnw test`.
     - `frontend-test`: Run `vitest run`.
     - `build-images`: Build and push Docker images.
2. **Kubernetes Descriptors**
   - Create `devops/k8s/postgres-deployment.yaml`.
   - Create `devops/k8s/redis-deployment.yaml`.
   - Create `devops/k8s/backend-deployment.yaml`.
   - Create `devops/k8s/frontend-deployment.yaml`.

## Module 2: Analytics Dashboard (Medium Priority)
1. **Backend Aggregation**
   - Implement `DashboardService.java` to fetch revenue from Stripe and `PaymentTransaction` tables.
   - Expose `/api/dashboard/admin/revenue`.
2. **Frontend Charts**
   - Install a modern charting library.
   - Create `AdminRevenueChartComponent` (Standalone).
   - Use `computed()` signals to format data for the charts.

## Module 3: Social Login (Medium Priority)
1. **Spring Security OAuth2**
   - Add `spring-boot-starter-oauth2-client`.
   - Update `SecurityConfig.java` to handle OAuth2 callbacks.
   - Create `CustomOAuth2UserService` to map Google profiles to `User` entities.
2. **Frontend UI**
   - Add Google sign-in button to `LoginComponent`.
   - Implement OAuth redirect logic in `AuthService`.
