import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { GamificationProfile } from '../models/gamification.model';

@Injectable({
  providedIn: 'root'
})
export class GamificationService {
  private apiUrl = 'http://localhost:8080/api/gamification';

  private profileSubject = new BehaviorSubject<GamificationProfile | null>(null);
  public profile$ = this.profileSubject.asObservable();

  constructor(private http: HttpClient) {}

  getUserProfile(userId: number): Observable<GamificationProfile> {
    return this.http.get<GamificationProfile>(`${this.apiUrl}/users/${userId}/profile`).pipe(
      tap(profile => this.profileSubject.next(profile))
    );
  }

  awardXp(userId: number, activityType: string, xp: number): Observable<any> {
    const payload = { activityType, xp };
    return this.http.post(`${this.apiUrl}/users/${userId}/award`, payload).pipe(
      tap(() => this.getUserProfile(userId).subscribe())
    );
  }

  getLeaderboard(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/leaderboard`);
  }
}
