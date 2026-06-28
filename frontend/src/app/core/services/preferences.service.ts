import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface UserPreferences {
  highContrastMode: boolean;
  colorBlindnessFilters: boolean;
  textScaling: number;
  reducedMotion: boolean;
  aiPersona: string;
}

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {
  private readonly API_URL = 'http://localhost:8080/api/preferences';

  private preferencesSubject = new BehaviorSubject<UserPreferences | null>(null);
  public preferences$ = this.preferencesSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  public loadPreferences(): void {
    this.http.get<UserPreferences>(this.API_URL).subscribe({
      next: (prefs: UserPreferences) => {
        this.preferencesSubject.next(prefs);
        this.applyVisualPreferences(prefs);
      },
      error: (err: any) => {
        console.error('Failed to load preferences', err);
      }
    });
  }

  public savePreferences(prefs: UserPreferences): Observable<UserPreferences> {
    return this.http.put<UserPreferences>(this.API_URL, prefs).pipe(
      tap((savedPrefs: UserPreferences) => {
        this.preferencesSubject.next(savedPrefs);
        this.applyVisualPreferences(savedPrefs);
      })
    );
  }

  public applyVisualPreferences(prefs: UserPreferences): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const docBody = document.body;
    const docEl = document.documentElement;

    if (prefs.highContrastMode) {
      docBody.classList.add('high-contrast');
    } else {
      docBody.classList.remove('high-contrast');
    }

    if (prefs.reducedMotion) {
      docBody.classList.add('reduced-motion');
    } else {
      docBody.classList.remove('reduced-motion');
    }

    if (prefs.colorBlindnessFilters) {
      docBody.classList.add('color-blind-filter');
    } else {
      docBody.classList.remove('color-blind-filter');
    }

    // Text scaling handles percentages
    docEl.style.fontSize = `${prefs.textScaling}%`;
  }

  public getCurrentPreferences(): UserPreferences | null {
    return this.preferencesSubject.getValue();
  }
}
