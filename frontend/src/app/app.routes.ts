import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/landing/landing.component')
      .then(m => m.LandingComponent),
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/student-dashboard/student-dashboard.component')
      .then(m => m.StudentDashboardComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'courses/:id',
    loadComponent: () => import('./features/course-details/course-details.component')
      .then(m => m.CourseDetailsComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'virtual-classroom',
    loadComponent: () => import('./features/virtual-classroom/virtual-classroom.component')
      .then(m => m.VirtualClassroomComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'assessment',
    loadComponent: () => import('./features/adaptive-assessment/adaptive-assessment.component')
      .then(m => m.AdaptiveAssessmentComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'pricing',
    loadComponent: () => import('./features/pricing/pricing.component')
      .then(m => m.PricingComponent)
  },
  {
    path: 'pricing/success',
    loadComponent: () => import('./features/pricing/payment-success.component')
      .then(m => m.PaymentSuccessComponent)
  },
  {
    path: 'pricing/cancel',
    loadComponent: () => import('./features/pricing/payment-cancel.component')
      .then(m => m.PaymentCancelComponent)
  },
  {
    path: 'tutoring/:id',
    loadComponent: () => import('./features/tutoring-room/tutoring-room.component')
      .then(m => m.TutoringRoomComponent),
    canActivate: [authGuard]
  },
  {
    path: 'immersive-hub',
    loadComponent: () => import('./features/immersive-hub/immersive-hub.component')
      .then(m => m.ImmersiveHubComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] }
  },
  {
    path: 'instructor',
    loadComponent: () => import('./features/instructor-dashboard/instructor-dashboard.component')
      .then(m => m.InstructorDashboardComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['INSTRUCTOR'] }
  },
  {
    path: 'instructor/integrity',
    loadComponent: () => import('./features/instructor-integrity/instructor-integrity.component')
      .then(m => m.InstructorIntegrityComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['INSTRUCTOR'] }
  },
  {
    path: 'instructor/courses',
    loadComponent: () => import('./features/course-management/course-management.component')
      .then(m => m.CourseManagementComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['INSTRUCTOR'] }
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile-settings/profile-settings.component')
      .then(m => m.ProfileSettingsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'preferences',
    loadComponent: () => import('./features/accessibility-preferences/accessibility-preferences.component')
      .then(m => m.AccessibilityPreferencesComponent),
    canActivate: [authGuard]
  },
  {
    path: 'ai-chat',
    loadComponent: () => import('./features/ai-chat/ai-chat.component')
      .then(m => m.AiChatComponent),
    canActivate: [authGuard]
  },
  {
    path: 'library',
    loadComponent: () => import('./features/library/library.component')
      .then(m => m.LibraryComponent),
    canActivate: [authGuard]
  },
  {
    path: 'leaderboard',
    loadComponent: () => import('./features/leaderboard/leaderboard.component')
      .then(m => m.LeaderboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'achievements',
    loadComponent: () => import('./features/achievements/achievements.component')
      .then(m => m.AchievementsComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
