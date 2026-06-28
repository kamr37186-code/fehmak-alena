import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InstructorDashboardResponse, IntegrityReportResponse } from '../models/instructor.model';

@Injectable({
  providedIn: 'root'
})
export class InstructorService {
  private apiUrl = 'http://localhost:8080/api/instructor';

  constructor(private http: HttpClient) {}

  getDashboardData(): Observable<InstructorDashboardResponse> {
    return this.http.get<InstructorDashboardResponse>(`${this.apiUrl}/dashboard`);
  }

  getIntegrityReports(quizId: number): Observable<IntegrityReportResponse[]> {
    return this.http.get<IntegrityReportResponse[]>(`${this.apiUrl}/integrity/${quizId}`);
  }

  getFlaggedSessions(): Observable<IntegrityReportResponse[]> {
    return this.http.get<IntegrityReportResponse[]>(`${this.apiUrl}/integrity/flags`);
  }
}
