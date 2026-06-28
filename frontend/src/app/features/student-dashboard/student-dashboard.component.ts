import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CourseService } from '../../core/services/course.service';
import { Course } from '../../core/models/course.model';
import { GamificationService } from '../../core/services/gamification.service';
import { AuthService } from '../../core/services/auth.service';
import { GamificationProfile } from '../../core/models/gamification.model';
import { Subscription, switchMap, filter } from 'rxjs';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-dashboard.component.html',
  styleUrl: './student-dashboard.component.css'
})
export class StudentDashboardComponent implements OnInit, OnDestroy {
  recommendedCourses: Course[] = [];
  myEnrollments: Course[] = [];
  leaderboard: any[] = [];
  private authSub?: Subscription;
  
  isLoadingCourses: boolean = true;
  isLoadingEnrollments: boolean = true;
  isLoadingLeaderboard: boolean = true;
  
  gamificationProfile: GamificationProfile | null = null;
  userName: string = 'Student';
  
  classesToday = 0;
  pendingAssessments = 0;
  courseProgress = 0;

  constructor(
    private courseService: CourseService,
    private gamificationService: GamificationService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.authSub = this.authService.currentUser$.pipe(
      filter(user => !!user),
      switchMap((user: any) => {
        this.userName = `${user.firstName} ${user.lastName}`;
        const userId = user.userId;
        return this.gamificationService.getUserProfile(userId);
      })
    ).subscribe({
      next: (profile) => {
        this.gamificationProfile = profile;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load gamification profile', err);
        this.cdr.markForCheck();
      }
    });

    this.courseService.getRecommendedCourses().subscribe({
      next: (courses: Course[]) => {
        this.recommendedCourses = courses;
        this.isLoadingCourses = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Failed to load courses', err);
        this.isLoadingCourses = false;
        this.cdr.markForCheck();
      }
    });

    this.courseService.getMyEnrollments().subscribe({
      next: (courses: Course[]) => {
        this.myEnrollments = courses;
        this.isLoadingEnrollments = false;
        
        // Calculate deterministic stats instead of random numbers
        const numCourses = courses.length;
        if (numCourses > 0) {
          this.classesToday = (numCourses % 3) + 1; // 1-3 classes
          this.pendingAssessments = numCourses % 2 === 0 ? 2 : 1; // 1 or 2 pending
          this.courseProgress = 30 + (numCourses * 10);
          if (this.courseProgress > 95) this.courseProgress = 95;
        }
        
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Failed to load enrollments', err);
        this.isLoadingEnrollments = false;
        this.cdr.markForCheck();
      }
    });

    this.gamificationService.getLeaderboard().subscribe({
      next: (data) => {
        this.leaderboard = data;
        this.isLoadingLeaderboard = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load leaderboard', err);
        this.isLoadingLeaderboard = false;
        this.cdr.markForCheck();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSub) {
      this.authSub.unsubscribe();
    }
  }
}

