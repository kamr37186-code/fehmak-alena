import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { AdaptiveAssessmentComponent } from './adaptive-assessment.component';
import { AssessmentService } from '../../core/services/assessment.service';
import { GamificationService } from '../../core/services/gamification.service';
import { AuthService } from '../../core/services/auth.service';

describe('AdaptiveAssessmentComponent', () => {
  let component: AdaptiveAssessmentComponent;
  let fixture: ComponentFixture<AdaptiveAssessmentComponent>;
  let assessmentServiceSpy: jasmine.SpyObj<AssessmentService>;
  let gamificationServiceSpy: jasmine.SpyObj<GamificationService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let currentUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    assessmentServiceSpy = jasmine.createSpyObj('AssessmentService', [
      'getAllQuizzes', 'startSession', 'reportEvents', 'submitAnswer', 'submitSession'
    ]);
    gamificationServiceSpy = jasmine.createSpyObj('GamificationService', ['awardXp']);
    
    currentUserSubject = new BehaviorSubject<any>(null);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { currentUser$: currentUserSubject });

    await TestBed.configureTestingModule({
      imports: [AdaptiveAssessmentComponent, CommonModule],
      providers: [
        { provide: AssessmentService, useValue: assessmentServiceSpy },
        { provide: GamificationService, useValue: gamificationServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdaptiveAssessmentComponent);
    component = fixture.componentInstance;
  });

  it('should create and load quizzes', () => {
    const mockQuizzes = [
      { 
        id: 1, 
        title: 'Q1', 
        questions: [{ id: 10, content: 'Q?', options: '["A", "B"]' }] 
      }
    ] as any;
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of(mockQuizzes));
    assessmentServiceSpy.startSession.and.returnValue(of({ sessionId: 123 }));
    currentUserSubject.next({ user: { id: 1 } });

    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.quizzes).toEqual(mockQuizzes);
    expect(component.activeQuiz).toEqual(mockQuizzes[0]);
    expect(component.currentQuestion?.id).toEqual(10);
    expect(component.parsedOptions).toEqual(['A', 'B']);
    expect(component.sessionId).toEqual(123);
    expect(assessmentServiceSpy.startSession).toHaveBeenCalledWith(1);
    expect(component.isLoading).toBeFalse();
  });

  it('should handle error when loading quizzes', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(throwError(() => new Error('Error')));

    fixture.detectChanges();

    expect(component.quizzes).toEqual([]);
    expect(component.isLoading).toBeFalse();
  });

  it('should handle invalid JSON in options', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.setQuestion({ id: 1, content: 'Q', options: 'invalid-json', answer: '', difficulty: 'EASY', topic: '' } as any);

    expect(component.parsedOptions).toEqual([]);
  });

  it('should select answer', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.selectAnswer('A');
    expect(component.selectedAnswer).toEqual('A');

    // Should not allow select if already correct or submitting
    component.isCorrect = true;
    component.selectAnswer('B');
    expect(component.selectedAnswer).toEqual('A');
  });

  it('should submit answer', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.currentQuestion = { id: 1 } as any;
    component.selectedAnswer = 'A';
    component.sessionId = 123;
    
    // Make sure we simulate some time passing so it's not a RAPID_ANSWER or just bypass it
    (component as any).questionStartTime = Date.now() - 5000;

    assessmentServiceSpy.submitAnswer.and.returnValue(of({ isCorrect: true, aiFeedback: 'Good job!' } as any));

    component.submitAnswer();

    expect(component.isSubmitting).toBeFalse();
    expect(component.isCorrect).toBeTrue();
    expect(component.aiFeedback).toEqual('Good job!');
    expect(assessmentServiceSpy.submitAnswer).toHaveBeenCalledWith(1, 'A');
  });

  it('should report rapid answer event if answered too quickly', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.currentQuestion = { id: 1 } as any;
    component.selectedAnswer = 'A';
    component.sessionId = 123;
    (component as any).questionStartTime = Date.now(); // Less than 3000ms

    assessmentServiceSpy.submitAnswer.and.returnValue(of({ isCorrect: false, aiFeedback: 'Wrong' } as any));
    assessmentServiceSpy.reportEvents.and.returnValue(of(null));

    component.submitAnswer();

    expect(assessmentServiceSpy.reportEvents).toHaveBeenCalled();
  });

  it('should handle answer submission error', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.currentQuestion = { id: 1 } as any;
    component.selectedAnswer = 'A';
    
    assessmentServiceSpy.submitAnswer.and.returnValue(throwError(() => new Error('Error')));

    component.submitAnswer();

    expect(component.isSubmitting).toBeFalse();
    expect(component.aiFeedback).toContain('currently unavailable');
  });

  it('should navigate to next question', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.activeQuiz = {
      questions: [
        { id: 1, options: '[]' },
        { id: 2, options: '[]' }
      ]
    } as any;
    component.currentQuestion = component.activeQuiz!.questions[0];

    component.nextQuestion();

    expect(component.currentQuestion.id).toBe(2);
  });

  it('should finish quiz and award XP', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.currentUserId = 1;
    component.sessionId = 123;
    component.activeQuiz = {
      questions: [
        { id: 1, options: '[]' }
      ]
    } as any;
    component.currentQuestion = component.activeQuiz!.questions[0];

    assessmentServiceSpy.submitSession.and.returnValue(of(null));
    gamificationServiceSpy.awardXp.and.returnValue(of(null));

    component.nextQuestion(); // No more questions

    expect(assessmentServiceSpy.submitSession).toHaveBeenCalledWith(123);
    expect(gamificationServiceSpy.awardXp).toHaveBeenCalledWith(1, 'QUIZ_COMPLETION', 100);
    expect(component.aiFeedback).toContain('completed the quiz');
  });

  it('should track window focus and blur events', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();
    component.sessionId = 123;
    assessmentServiceSpy.reportEvents.and.returnValue(of(null));

    component.onWindowBlur();
    component.onWindowFocus();

    expect(assessmentServiceSpy.reportEvents).toHaveBeenCalled();
    const args = assessmentServiceSpy.reportEvents.calls.mostRecent().args;
    expect(args[0]).toBe(123);
    expect(args[1].length).toBe(2); // BLUR and FOCUS events
  });

  it('should track copy and paste events', () => {
    assessmentServiceSpy.getAllQuizzes.and.returnValue(of([]));
    fixture.detectChanges();

    component.onCopy({} as ClipboardEvent);
    component.onPaste({} as ClipboardEvent);

    expect((component as any).eventBuffer.length).toBe(2);
    expect((component as any).eventBuffer[0].eventType).toBe('COPY_DETECTED');
    expect((component as any).eventBuffer[1].eventType).toBe('PASTE_DETECTED');
  });
});
