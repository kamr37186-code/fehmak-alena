import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz, AssessmentSubmissionResponse, StartSessionResponse, CheatEvent } from '../models/assessment.model';

@Injectable({
  providedIn: 'root'
})
export class AssessmentService {
  private apiUrl = 'http://localhost:8080/api/assessments';

  constructor(private http: HttpClient) {}

  getAllQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/quizzes`);
  }

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/quizzes/${id}`);
  }

  submitAnswer(questionId: number, answer: string): Observable<AssessmentSubmissionResponse> {
    return this.http.post<AssessmentSubmissionResponse>(`${this.apiUrl}/questions/${questionId}/submit`, { answer });
  }

  startSession(quizId: number): Observable<StartSessionResponse> {
    return this.http.post<StartSessionResponse>(`${this.apiUrl}/sessions/start`, { quizId });
  }

  reportEvents(sessionId: number, events: CheatEvent[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/sessions/${sessionId}/events`, { events });
  }

  submitSession(sessionId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/sessions/${sessionId}/submit`, {});
  }
}
