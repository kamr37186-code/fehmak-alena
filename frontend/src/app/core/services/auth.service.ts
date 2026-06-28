import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/auth.model';
import { AuthStore } from '../store/auth.store';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private authStore = inject(AuthStore);
  private platformId = inject(PLATFORM_ID);
  public currentUser$ = toObservable(this.authStore.user);

  getCurrentUser() {
    return this.authStore.user();
  }

  constructor(private http: HttpClient) {
    if (isPlatformBrowser(this.platformId)) {
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        this.authStore.setUser(JSON.parse(savedUser));
      }
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true }).pipe(
      tap((response: AuthResponse) => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(response));
          }
          this.authStore.setUser(response);
        }
      })
    );
  }

  loginWithGoogle(token: string, role?: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/oauth2/google`, { token, provider: 'GOOGLE', role }, { withCredentials: true }).pipe(
      tap((response: AuthResponse) => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(response));
          }
          this.authStore.setUser(response);
        }
      })
    );
  }

  loginWithFacebook(token: string, role?: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/oauth2/facebook`, { token, provider: 'FACEBOOK', role }, { withCredentials: true }).pipe(
      tap((response: AuthResponse) => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(response));
          }
          this.authStore.setUser(response);
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData, { withCredentials: true }).pipe(
      tap((response: AuthResponse) => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(response));
          }
          this.authStore.setUser(response);
        }
      })
    );
  }

  logout() {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.clearLocalSession(),
      error: () => this.clearLocalSession()
    });
  }

  clearLocalSession() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('user');
    }
    this.authStore.logout();
  }

  isAuthenticated(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('user');
    }
    return false;
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}, { withCredentials: true }).pipe(
      tap((response: AuthResponse) => {
        if (response) {
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(response));
          }
          this.authStore.setUser(response);
        }
      })
    );
  }
}
