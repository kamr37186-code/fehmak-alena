import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { StudentDashboardComponent } from './student-dashboard.component';
import { CourseService } from '../../core/services/course.service';
import { GamificationService } from '../../core/services/gamification.service';
import { AuthService } from '../../core/services/auth.service';

describe('StudentDashboardComponent', () => {
  let component: StudentDashboardComponent;
  let fixture: ComponentFixture<StudentDashboardComponent>;
  let courseServiceSpy: jasmine.SpyObj<CourseService>;
  let gamificationServiceSpy: jasmine.SpyObj<GamificationService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let currentUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    courseServiceSpy = jasmine.createSpyObj('CourseService', ['getRecommendedCourses', 'getMyEnrollments']);
    gamificationServiceSpy = jasmine.createSpyObj('GamificationService', ['getUserProfile', 'getLeaderboard']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { currentUser$: new BehaviorSubject(null) });

    await TestBed.configureTestingModule({
      imports: [StudentDashboardComponent, RouterTestingModule],
      providers: [
        { provide: CourseService, useValue: courseServiceSpy },
        { provide: GamificationService, useValue: gamificationServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    currentUserSubject = new BehaviorSubject<any>(null);
    Object.defineProperty(authServiceSpy, 'currentUser$', { get: () => currentUserSubject });

    courseServiceSpy.getRecommendedCourses.and.returnValue(of([]));
    courseServiceSpy.getMyEnrollments.and.returnValue(of([]));
    gamificationServiceSpy.getLeaderboard.and.returnValue(of([]));
    gamificationServiceSpy.getUserProfile.and.returnValue(of(null as any));

    fixture = TestBed.createComponent(StudentDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should handle user details correctly', () => {
    currentUserSubject.next({ userId: 1, firstName: 'Jane', lastName: 'Doe' });
    
    fixture.detectChanges();
    
    expect(component.userName).toEqual('Jane Doe');
    expect(gamificationServiceSpy.getUserProfile).toHaveBeenCalledWith(1);
  });

  it('should fetch courses successfully', () => {
    const mockCourses: any[] = [{ id: 10, title: 'Course A' }];
    courseServiceSpy.getRecommendedCourses.and.returnValue(of(mockCourses));

    fixture.detectChanges();

    expect(component.recommendedCourses).toEqual(mockCourses);
    expect(component.isLoadingCourses).toBeFalse();
  });

  it('should handle error fetching recommended courses', () => {
    courseServiceSpy.getRecommendedCourses.and.returnValue(throwError(() => new Error('Error')));

    fixture.detectChanges();

    expect(component.recommendedCourses).toEqual([]);
    expect(component.isLoadingCourses).toBeFalse();
  });

  it('should calculate stats when enrollments exist', () => {
    const mockEnrollments: any[] = [{}, {}, {}]; // 3 enrollments
    courseServiceSpy.getMyEnrollments.and.returnValue(of(mockEnrollments));

    fixture.detectChanges();

    expect(component.myEnrollments.length).toBe(3);
    expect(component.isLoadingEnrollments).toBeFalse();
    // deterministic logic from component:
    expect(component.classesToday).toBe(1); // (3 % 3) + 1
    expect(component.pendingAssessments).toBe(1); // 3 % 2 === 1
    expect(component.courseProgress).toBe(60); // 30 + (3 * 10)
  });

  it('should cap course progress at 95', () => {
    const mockEnrollments: any[] = new Array(10).fill({}); // 10 enrollments => progress 130
    courseServiceSpy.getMyEnrollments.and.returnValue(of(mockEnrollments));

    fixture.detectChanges();
    
    expect(component.courseProgress).toBe(95);
  });

  it('should handle error fetching enrollments', () => {
    courseServiceSpy.getMyEnrollments.and.returnValue(throwError(() => new Error('Error')));

    fixture.detectChanges();

    expect(component.myEnrollments).toEqual([]);
    expect(component.isLoadingEnrollments).toBeFalse();
  });

  it('should fetch leaderboard successfully', () => {
    const mockLeaderboard: any[] = [{ rank: 1, name: 'Winner' }];
    gamificationServiceSpy.getLeaderboard.and.returnValue(of(mockLeaderboard));

    fixture.detectChanges();

    expect(component.leaderboard).toEqual(mockLeaderboard);
    expect(component.isLoadingLeaderboard).toBeFalse();
  });

  it('should handle error fetching leaderboard', () => {
    gamificationServiceSpy.getLeaderboard.and.returnValue(throwError(() => new Error('Error')));

    fixture.detectChanges();

    expect(component.leaderboard).toEqual([]);
    expect(component.isLoadingLeaderboard).toBeFalse();
  });
});
