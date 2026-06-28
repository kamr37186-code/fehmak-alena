import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../../core/services/course.service';
import { Course } from '../../core/models/course.model';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit {
  course: Course | null = null;
  isLoading = true;
  isEnrolling = false;
  enrollmentMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const courseId = parseInt(idParam, 10);
      this.courseService.getCourseById(courseId).subscribe({
        next: (course) => {
          this.course = course;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Failed to load course details', err);
          this.isLoading = false;
        }
      });
    }
  }

  enroll(): void {
    if (!this.course) return;
    this.isEnrolling = true;
    this.enrollmentMessage = '';
    
    this.courseService.enrollInCourse(this.course.id).subscribe({
      next: (response) => {
        this.isEnrolling = false;
        this.enrollmentMessage = 'Successfully enrolled in course!';
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 2000);
      },
      error: (err) => {
        this.isEnrolling = false;
        this.enrollmentMessage = 'Failed to enroll. You might already be enrolled.';
        console.error('Enrollment error', err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
