import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChildren, QueryList, inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import { Router } from '@angular/router';
import { InstructorService } from '../../core/services/instructor.service';
import { InstructorDashboardResponse, StudentProgress } from '../../core/models/instructor.model';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';
import { StudentProfileModalComponent } from './components/student-profile-modal/student-profile-modal.component';
import gsap from 'gsap';

@Component({
  selector: 'app-instructor-dashboard',
  standalone: true,
  imports: [StudentProfileModalComponent],
  templateUrl: './instructor-dashboard.component.html',
  styleUrl: './instructor-dashboard.component.css'
})
export class InstructorDashboardComponent implements OnInit, OnDestroy, AfterViewInit {
  dashboardData: InstructorDashboardResponse | null = null;
  isLoading = true;
  userName = '';
  suspiciousSessions = 0;
  
  selectedStudent: StudentProgress | null = null;

  @ViewChildren('gsapCard') gsapCards!: QueryList<ElementRef>;
  @ViewChildren('gsapRow') gsapRows!: QueryList<ElementRef>;

  private authSub?: Subscription;

  private platformId = inject(PLATFORM_ID);

  constructor(
    private instructorService: InstructorService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authSub = this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        this.userName = `${user.firstName} ${user.lastName}`;
      }
    });

    this.instructorService.getDashboardData().subscribe({
      next: (data) => {
        this.dashboardData = data;
        if (data.flaggedSessionCount !== undefined) {
          this.suspiciousSessions = data.flaggedSessionCount;
        }
        this.isLoading = false;
        
        // Wait a tick for the DOM to render the fetched data rows, then animate them
        setTimeout(() => this.animateRows(), 50);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error fetching instructor dashboard data', err);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      // Initial animations for static elements
      gsap.from('.header-content', { 
        opacity: 0, 
        y: -20, 
        duration: 0.6, 
        ease: 'power3.out' 
      });

      gsap.from('.alert-banner', { 
        opacity: 0, 
        x: -30, 
        duration: 0.5, 
        delay: 0.2, 
        ease: 'back.out(1.5)' 
      });

      // Staggered cards animation
      if (this.gsapCards && this.gsapCards.length > 0) {
        gsap.from(this.gsapCards.map(c => c.nativeElement), {
          opacity: 0,
          y: 30,
          duration: 0.6,
          stagger: 0.1,
          ease: 'power2.out',
          delay: 0.3
        });
      }
    }
  }

  animateRows(): void {
    if (isPlatformBrowser(this.platformId) && this.gsapRows && this.gsapRows.length > 0) {
      gsap.from(this.gsapRows.map(r => r.nativeElement), {
        opacity: 0,
        x: -20,
        duration: 0.4,
        stagger: 0.05,
        ease: 'power2.out'
      });
    }
  }

  viewStudentDetails(student: StudentProgress): void {
    this.selectedStudent = student;
  }

  closeStudentDetails(): void {
    this.selectedStudent = null;
  }

  navigateToCourses(): void {
    this.router.navigate(['/instructor/courses']);
  }

  navigateToIntegrity(): void {
    this.router.navigate(['/instructor/integrity']);
  }

  ngOnDestroy(): void {
    if (this.authSub) {
      this.authSub.unsubscribe();
    }
  }
}
