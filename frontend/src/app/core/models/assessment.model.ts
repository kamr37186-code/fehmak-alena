export interface Question {
  id: number;
  questionText: string;
  options: string; // JSON string from DB, we will parse it in the component
  type: string;
}

export interface Quiz {
  id: number;
  title: string;
  description: string;
  difficultyLevel: string;
  questions: Question[];
}

export interface AssessmentSubmissionResponse {
  isCorrect: boolean;
  aiFeedback: string;
  explanation: string;
}

export interface StartSessionResponse {
  sessionId: number;
}

export interface CheatEvent {
  eventType: string;
  timestamp: string;
  metadata: string;
}

export interface IntegrityReportResponse {
  sessionId: number;
  studentName: string;
  quizTitle: string;
  riskScore: number;
  verdict: string;
  focusLossCount: number;
  totalFocusLossDurationMs: number;
  rapidAnswerCount: number;
  copyPasteCount: number;
  events: CheatEvent[];
}
