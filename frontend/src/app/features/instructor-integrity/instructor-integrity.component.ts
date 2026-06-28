import { Component, OnInit, ElementRef, viewChild, AfterViewInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { InstructorService } from '../../core/services/instructor.service';
import { IntegrityReportResponse } from '../../core/models/instructor.model';
import { gsap } from 'gsap';

@Component({
  selector: 'app-instructor-integrity',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './instructor-integrity.component.html',
  styleUrls: ['./instructor-integrity.component.css']
})
export class InstructorIntegrityComponent implements OnInit, AfterViewInit {
  reportsContainer = viewChild<ElementRef>('reportsContainer');
  pageHeader = viewChild<ElementRef>('pageHeader');

  flaggedSessions: IntegrityReportResponse[] = [];
  isLoading = true;

  constructor(
    private instructorService: InstructorService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.loadFlaggedSessions();
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId) && this.pageHeader()) {
      gsap.from(this.pageHeader()!.nativeElement, {
        y: -30,
        opacity: 0,
        duration: 0.8,
        ease: 'power3.out'
      });
    }
  }

  loadFlaggedSessions(): void {
    this.instructorService.getFlaggedSessions().subscribe({
      next: (sessions) => {
        this.flaggedSessions = sessions;
        this.isLoading = false;
        this.animateReports();
      },
      error: (err) => {
        console.error('Failed to load flagged sessions', err);
        this.isLoading = false;
      }
    });
  }

  animateReports(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    setTimeout(() => {
      if (this.reportsContainer()) {
        gsap.from(this.reportsContainer()!.nativeElement.children, {
          y: 30,
          opacity: 0,
          duration: 0.6,
          stagger: 0.1,
          ease: 'power2.out'
        });
      }
    }, 50);
  }

  getVerdictBadgeClass(verdict: string): string {
    switch(verdict) {
      case 'SAFE': return 'badge-safe';
      case 'SUSPICIOUS': return 'badge-suspicious';
      case 'CHEATED': return 'badge-cheated';
      default: return 'badge-secondary';
    }
  }

  getEventIcon(eventType: string): string {
    switch(eventType) {
      case 'TAB_SWITCH': return 'fa-window-restore';
      case 'COPY_PASTE': return 'fa-clipboard';
      case 'MULTIPLE_FACES': return 'fa-users';
      case 'NO_FACE': return 'fa-user-slash';
      default: return 'fa-exclamation-triangle';
    }
  }
}
