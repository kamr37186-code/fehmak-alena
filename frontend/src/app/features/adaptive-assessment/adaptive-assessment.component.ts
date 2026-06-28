import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AssessmentService } from '../../core/services/assessment.service';
import { Quiz, Question, AssessmentSubmissionResponse, CheatEvent } from '../../core/models/assessment.model';
import { GamificationService } from '../../core/services/gamification.service';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-adaptive-assessment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './adaptive-assessment.component.html',
  styleUrl: './adaptive-assessment.component.css'
})
export class AdaptiveAssessmentComponent implements OnInit, OnDestroy {
  quizzes: Quiz[] = [];
  activeQuiz: Quiz | null = null;
  currentQuestion: Question | null = null;
  parsedOptions: string[] = [];
  
  selectedAnswer: string = '';
  aiFeedback: string = '';
  isCorrect: boolean | null = null;
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  timer: string = '19:59';
  
  currentUserId: number | null = null;
  private authSub?: Subscription;

  sessionId: number | null = null;
  private eventBuffer: CheatEvent[] = [];
  private blurTimestamp: number | null = null;
  private questionStartTime: number = Date.now();
  
  constructor(
    private assessmentService: AssessmentService,
    private gamificationService: GamificationService,
    private authService: AuthService
  ) {}

  @HostListener('window:blur')
  onWindowBlur() {
    this.blurTimestamp = Date.now();
    this.eventBuffer.push({
      eventType: 'WINDOW_BLUR',
      timestamp: new Date().toISOString(),
      metadata: '{}'
    });
  }

  @HostListener('window:focus')
  onWindowFocus() {
    let durationMs = 0;
    if (this.blurTimestamp) {
      durationMs = Date.now() - this.blurTimestamp;
      this.blurTimestamp = null;
    }
    this.eventBuffer.push({
      eventType: 'WINDOW_FOCUS',
      timestamp: new Date().toISOString(),
      metadata: JSON.stringify({ durationMs })
    });
    this.flushEvents();
  }

  @HostListener('document:copy', ['$event'])
  onCopy(event: ClipboardEvent) {
    this.eventBuffer.push({
      eventType: 'COPY_DETECTED',
      timestamp: new Date().toISOString(),
      metadata: '{}'
    });
  }

  @HostListener('document:paste', ['$event'])
  onPaste(event: ClipboardEvent) {
    this.eventBuffer.push({
      eventType: 'PASTE_DETECTED',
      timestamp: new Date().toISOString(),
      metadata: '{}'
    });
  }

  private flushEvents() {
    if (this.eventBuffer.length > 0 && this.sessionId) {
      this.assessmentService.reportEvents(this.sessionId, this.eventBuffer).subscribe();
      this.eventBuffer = [];
    }
  }

  ngOnInit() {
    this.loadQuizzes();
    this.authSub = this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        this.currentUserId = user.userId;
      }
    });
  }

  loadQuizzes() {
    this.isLoading = true;
    this.assessmentService.getAllQuizzes().subscribe({
      next: (quizzes: Quiz[]) => {
        this.quizzes = quizzes;
        if (this.quizzes.length > 0) {
          this.activeQuiz = this.quizzes[0];
          if (this.activeQuiz.questions && this.activeQuiz.questions.length > 0) {
            this.setQuestion(this.activeQuiz.questions[0]);
            this.assessmentService.startSession(this.activeQuiz.id).subscribe({
              next: (res) => this.sessionId = res.sessionId
            });
          }
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load quizzes', err);
        this.isLoading = false;
      }
    });
  }

  setQuestion(question: Question) {
    this.currentQuestion = question;
    this.selectedAnswer = '';
    this.aiFeedback = '';
    this.isCorrect = null;
    this.questionStartTime = Date.now();
    
    // Parse options from JSON string
    try {
      this.parsedOptions = JSON.parse(question.options);
    } catch (e) {
      console.error('Failed to parse options', e);
      this.parsedOptions = [];
    }
  }

  selectAnswer(answer: string) {
    if (this.isSubmitting || this.isCorrect === true) return;
    this.selectedAnswer = answer;
  }

  submitAnswer() {
    if (!this.selectedAnswer || this.isSubmitting || !this.currentQuestion) return;
    
    this.isSubmitting = true;
    this.aiFeedback = 'Analyzing your logic and generating feedback...';
    this.isCorrect = null;
    
    const timeTaken = Date.now() - this.questionStartTime;
    if (timeTaken < 3000) {
      this.eventBuffer.push({
        eventType: 'RAPID_ANSWER',
        timestamp: new Date().toISOString(),
        metadata: JSON.stringify({ timeTakenMs: timeTaken })
      });
      this.flushEvents();
    }
    
    this.assessmentService.submitAnswer(this.currentQuestion.id, this.selectedAnswer).subscribe({
      next: (res: AssessmentSubmissionResponse) => {
        this.isCorrect = res.isCorrect;
        this.aiFeedback = res.aiFeedback;
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Error submitting answer', err);
        this.aiFeedback = 'Sorry, our AI tutor is currently unavailable. Please try again later.';
        this.isSubmitting = false;
      }
    });
  }

  nextQuestion() {
    if (!this.activeQuiz || !this.currentQuestion) return;
    const currentIndex = this.activeQuiz.questions.findIndex(q => q.id === this.currentQuestion!.id);
    if (currentIndex >= 0 && currentIndex < this.activeQuiz.questions.length - 1) {
      this.setQuestion(this.activeQuiz.questions[currentIndex + 1]);
    } else {
      // Quiz finished logic
      if (this.sessionId) {
        this.assessmentService.submitSession(this.sessionId).subscribe();
      }
      this.aiFeedback = "You've completed the quiz! Great job.";
      this.isCorrect = null;
      
      // Award XP
      if (this.currentUserId) {
        this.gamificationService.awardXp(this.currentUserId, 'QUIZ_COMPLETION', 100).subscribe({
          next: () => this.aiFeedback += ' +100 XP awarded!',
          error: (err) => console.error('Failed to award XP', err)
        });
      }
    }
  }

  ngOnDestroy(): void {
    if (this.authSub) {
      this.authSub.unsubscribe();
    }
  }
}
