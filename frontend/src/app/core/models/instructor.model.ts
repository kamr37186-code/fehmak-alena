export interface InstructorStats {
  totalActiveStudents: number;
  avgCourseScore: number;
  completionRate: number;
  pendingAssignments: number;
  newDiscussions: number;
  liveSession1Joined: number;
  liveSession2Joined: number;
}

export interface StudentProgress {
  id: string;
  studentName: string;
  studentEmail: string;
  enrolledCourse: string;
  progressPercentage: number;
  currentScore: number;
  avatarUrl: string;
}

export interface InstructorDashboardResponse {
  stats: InstructorStats;
  studentProgress: StudentProgress[];
  totalStudents: number;
  flaggedSessionCount: number;
}

export interface CheatEventDto {
  id?: number;
  eventType: string;
  timestamp: string;
  metadata: string;
}

export interface IntegrityReportResponse {
  sessionId: number;
  studentName: string;
  studentEmail: string;
  quizTitle: string;
  verdict: string;
  cheatEvents: CheatEventDto[];
  startedAt: string;
  completedAt: string;
}
