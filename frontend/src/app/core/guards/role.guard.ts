import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take, of } from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  
  if (!isPlatformBrowser(platformId)) {
    return of(true);
  }

  const expectedRoles: string[] = route.data['roles'];

  const userObj = authService.getCurrentUser();
  
  if (!userObj) {
    return router.parseUrl('/login');
  }

  const role: string | undefined = userObj.role;
  
  if (!expectedRoles || (role && expectedRoles.includes(role))) {
    return true;
  }
  
  // If role doesn't match, redirect to their proper dashboard
  if (role === 'INSTRUCTOR') return router.parseUrl('/instructor');
  if (role === 'ADMIN') return router.parseUrl('/admin');
  
  return router.parseUrl('/dashboard');
};
