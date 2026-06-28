import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError, EMPTY } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 0) {
        console.error('Network Error: Could not connect to the backend.');
      } else if (error.status === 401) {
        // If the request was to refresh token, don't try again
        if (req.url.includes('/api/auth/refresh')) {
          authService.clearLocalSession();
          router.navigate(['/login']);
          return throwError(() => error);
        }

        // Try to refresh token
        return authService.refreshToken().pipe(
          switchMap(() => {
            // Retry the original request
            const clonedReq = req.clone({ withCredentials: true });
            return next(clonedReq);
          }),
          catchError((refreshError) => {
            authService.clearLocalSession();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );

      } else if (error.status === 403) {
        router.navigate(['/']); // generic fallback since we don't have token payload available easily
      }

      return throwError(() => error);
    })
  );
};
