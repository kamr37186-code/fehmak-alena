import { Component, OnInit, ElementRef, viewChild, AfterViewInit, signal, computed, Inject, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { AuthStore } from '../../core/store/auth.store';
import { UserProfile } from '../../core/models/user.model';
import { gsap } from 'gsap';

@Component({
  selector: 'app-profile-settings',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile-settings.component.html',
  styleUrls: ['./profile-settings.component.css']
})
export class ProfileSettingsComponent implements OnInit, AfterViewInit {
  profileContainer = viewChild.required<ElementRef>('profileContainer');
  successMessage = viewChild<ElementRef>('successMessage');

  firstName = signal('');
  lastName = signal('');
  email = signal('');

  isFormValid = computed(() => {
    return this.firstName().length >= 2 && this.lastName().length >= 2;
  });

  userProfile: UserProfile | null = null;
  isLoading = signal(true);
  isSaving = signal(false);
  successText = signal('');

  private authService = inject(AuthService);
  private authStore = inject(AuthStore);

  constructor(
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      gsap.from(this.profileContainer().nativeElement.children, {
        y: 30,
        opacity: 0,
        duration: 0.8,
        stagger: 0.1,
        ease: 'power3.out'
      });
    }
  }

  loadProfile(): void {
    this.userService.getCurrentProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.firstName.set(profile.firstName);
        this.lastName.set(profile.lastName);
        this.email.set(profile.email);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load profile', err);
        this.isLoading.set(false);
      }
    });
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.isSaving.set(true);
    const updateReq = {
      firstName: this.firstName(),
      lastName: this.lastName()
    };

    this.userService.updateProfile(updateReq).subscribe({
      next: (res) => {
        this.isSaving.set(false);
        this.showSuccess(res.message);

        // Update AuthStore so the header reacts instantly
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
          const updatedUser = {
            ...currentUser,
            firstName: this.firstName(),
            lastName: this.lastName()
          };
          this.authStore.setUser(updatedUser);
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('user', JSON.stringify(updatedUser));
          }
        }
      },
      error: (err) => {
        console.error('Failed to update profile', err);
        this.isSaving.set(false);
      }
    });
  }

  private showSuccess(message: string): void {
    this.successText.set(message);
    if (isPlatformBrowser(this.platformId)) {
      setTimeout(() => {
        if (this.successMessage()) {
          gsap.fromTo(this.successMessage()!.nativeElement, 
            { opacity: 0, y: -20 },
            { opacity: 1, y: 0, duration: 0.5, ease: 'back.out(1.7)' }
          );
          
          setTimeout(() => {
            gsap.to(this.successMessage()!.nativeElement, {
              opacity: 0,
              y: -20,
              duration: 0.5,
              onComplete: () => {
                this.successText.set('');
              }
            });
          }, 3000);
        }
      }, 0);
    } else {
      setTimeout(() => {
        this.successText.set('');
      }, 3000);
    }
  }
}
