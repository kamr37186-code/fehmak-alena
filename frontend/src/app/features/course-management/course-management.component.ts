import { Component, OnInit, ElementRef, viewChild, AfterViewInit, signal, computed, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, CurrencyPipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../core/services/course.service';
import { Course, CourseRequest } from '../../core/models/course.model';
import { gsap } from 'gsap';

@Component({
  selector: 'app-course-management',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  templateUrl: './course-management.component.html',
  styleUrls: ['./course-management.component.css']
})
export class CourseManagementComponent implements OnInit, AfterViewInit {
  coursesGrid = viewChild<ElementRef>('coursesGrid');
  modalOverlay = viewChild<ElementRef>('modalOverlay');
  modalContent = viewChild<ElementRef>('modalContent');

  courses: Course[] = [];
  isLoading = true;
  isModalOpen = false;
  isSaving = false;
  editingCourseId: number | null = null;

  title = signal('');
  description = signal('');
  category = signal('');
  difficultyLevel = signal('');
  price = signal(0);

  isFormValid = computed(() => {
    return this.title().length >= 5 &&
           this.description().length >= 20 &&
           this.category() !== '' &&
           this.difficultyLevel() !== '' &&
           this.price() >= 0;
  });

  categories = ['Programming', 'Design', 'Business', 'Marketing', 'Data Science', 'Other'];
  difficulties = ['Beginner', 'Intermediate', 'Advanced'];

  constructor(
    private courseService: CourseService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  ngAfterViewInit(): void {
    // Initial animations
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        this.courses = data;
        this.isLoading = false;
        this.animateGrid();
      },
      error: (err) => {
        console.error('Failed to load courses', err);
        this.isLoading = false;
      }
    });
  }

  animateGrid(): void {
    if (isPlatformBrowser(this.platformId)) {
      setTimeout(() => {
        if (this.coursesGrid()) {
          gsap.from(this.coursesGrid()!.nativeElement.children, {
            y: 40,
            opacity: 0,
            duration: 0.6,
            stagger: 0.1,
            ease: 'power2.out'
          });
        }
      }, 50);
    }
  }

  openCreateModal(): void {
    this.editingCourseId = null;
    this.title.set('');
    this.description.set('');
    this.category.set('');
    this.difficultyLevel.set('');
    this.price.set(0);
    this.showModal();
  }

  openEditModal(course: Course): void {
    this.editingCourseId = course.id;
    this.title.set(course.title);
    this.description.set(course.description);
    this.category.set(course.category);
    this.difficultyLevel.set(course.difficultyLevel);
    this.price.set(course.price);
    this.showModal();
  }

  showModal(): void {
    this.isModalOpen = true;
    if (isPlatformBrowser(this.platformId)) {
      setTimeout(() => {
        gsap.to(this.modalOverlay()!.nativeElement, { opacity: 1, duration: 0.3 });
        gsap.fromTo(this.modalContent()!.nativeElement,
          { y: -50, opacity: 0, scale: 0.95 },
          { y: 0, opacity: 1, scale: 1, duration: 0.4, ease: 'back.out(1.5)' }
        );
      }, 10);
    }
  }

  closeModal(): void {
    if (isPlatformBrowser(this.platformId)) {
      gsap.to(this.modalOverlay()!.nativeElement, { opacity: 0, duration: 0.3 });
      gsap.to(this.modalContent()!.nativeElement, {
        y: 30, opacity: 0, scale: 0.95, duration: 0.3, ease: 'power2.in',
        onComplete: () => {
          this.isModalOpen = false;
        }
      });
    } else {
      this.isModalOpen = false;
    }
  }

  onSubmit(): void {
    if (!this.isFormValid()) return;

    this.isSaving = true;
    const request: CourseRequest = {
      title: this.title(),
      description: this.description(),
      category: this.category(),
      difficultyLevel: this.difficultyLevel(),
      price: this.price()
    };

    if (this.editingCourseId) {
      this.courseService.updateCourse(this.editingCourseId, request).subscribe({
        next: (updated) => {
          const idx = this.courses.findIndex(c => c.id === this.editingCourseId);
          if (idx !== -1) this.courses[idx] = updated;
          this.isSaving = false;
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to update', err);
          this.isSaving = false;
        }
      });
    } else {
      this.courseService.createCourse(request).subscribe({
        next: (created) => {
          this.courses.unshift(created);
          this.isSaving = false;
          this.closeModal();
          this.animateGrid(); // re-animate
        },
        error: (err) => {
          console.error('Failed to create', err);
          this.isSaving = false;
        }
      });
    }
  }

  deleteCourse(id: number, index: number, cardElement: HTMLElement): void {
    if (confirm('Are you sure you want to delete this course? This action cannot be undone.')) {
      if (isPlatformBrowser(this.platformId)) {
        gsap.to(cardElement, {
          scale: 0.8,
          opacity: 0,
          duration: 0.3,
          onComplete: () => {
            this.executeDelete(id, index, cardElement);
          }
        });
      } else {
        this.executeDelete(id, index, cardElement);
      }
    }
  }

  private executeDelete(id: number, index: number, cardElement: HTMLElement): void {
    this.courseService.deleteCourse(id).subscribe({
      next: () => {
        this.courses.splice(index, 1);
      },
      error: (err) => {
        console.error('Failed to delete', err);
        if (isPlatformBrowser(this.platformId)) {
          gsap.to(cardElement, { scale: 1, opacity: 1, duration: 0.3 });
        }
      }
    });
  }
}
