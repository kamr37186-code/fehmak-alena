import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const isApiUrl = req.url.startsWith('http://localhost:8080/api');

  if (isApiUrl) {
    const user = authService.getCurrentUser();
    const token = user?.accessToken;
    
    let headers = req.headers;
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    const cloned = req.clone({
      headers,
      withCredentials: true
    });
    return next(cloned);
  }

  return next(req);
};
