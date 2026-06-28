import { Component, AfterViewInit, signal, computed, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, TitleCasePipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { OAuthHelper } from '../../../core/services/oauth.helper';
import gsap from 'gsap';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TitleCasePipe],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements AfterViewInit {
  firstName = signal('');
  lastName = signal('');
  email = signal('');
  password = signal('');
  agreeTerms = signal(false);
  
  error = signal('');
  isLoading = signal(false);
  activeModule = signal<'STUDENT' | 'INSTRUCTOR'>('STUDENT');

  // Matches backend requirements: at least one digit, one lowercase, one uppercase, one special character, min 8 chars
  passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_]).{8,}$/;

  isFormValid = computed(() => {
    return this.firstName().length > 0 &&
           this.lastName().length > 0 &&
           this.email().includes('@') &&
           this.email().length > 3 &&
           this.passwordRegex.test(this.password()) &&
           this.agreeTerms();
  });

  constructor(
    private authService: AuthService,
    private oauthHelper: OAuthHelper,
    private router: Router,
    private route: ActivatedRoute,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      // GSAP Entrance Animations
      gsap.fromTo('.auth-stagger', 
        { opacity: 0, y: 30 },
        { opacity: 1, y: 0, duration: 1, stagger: 0.15, ease: 'power3.out' }
      );

      gsap.fromTo('.graphic-3d',
        { opacity: 0, scale: 0.9, x: -50 },
        { opacity: 1, scale: 1, x: 0, duration: 1.5, ease: 'power3.out', delay: 0.3 }
      );

      // OAuth Init
      this.oauthHelper.initializeGoogleSignIn('YOUR_GOOGLE_CLIENT_ID', (response) => {
        this.handleGoogleLogin(response.credential);
      });
      this.oauthHelper.renderGoogleButton('google-btn-register');
      this.oauthHelper.initializeFacebookSignIn('YOUR_FACEBOOK_APP_ID');
    }
  }

  handleGoogleLogin(token: string) {
    this.isLoading.set(true);
    this.error.set('');
    this.authService.loginWithGoogle(token, this.activeModule()).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        const role = response.role;
        if (role === 'INSTRUCTOR') {
          this.router.navigate(['/instructor']);
        } else if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.error.set(err.error?.error || 'Google login failed');
        this.isLoading.set(false);
      }
    });
  }

  handleFacebookLogin() {
    this.oauthHelper.loginWithFacebook((response) => {
      if (response && response.authResponse) {
        this.isLoading.set(true);
        this.error.set('');
        this.authService.loginWithFacebook(response.authResponse.accessToken, this.activeModule()).subscribe({
          next: (res) => {
            this.isLoading.set(false);
            const role = res.role;
            if (role === 'INSTRUCTOR') {
              this.router.navigate(['/instructor']);
            } else if (role === 'ADMIN') {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/dashboard']);
            }
          },
          error: (err) => {
            this.error.set(err.error?.error || 'Facebook login failed');
            this.isLoading.set(false);
          }
        });
      } else {
        this.error.set('Facebook login was cancelled or failed');
      }
    });
  }

  setModule(module: 'STUDENT' | 'INSTRUCTOR') {
    this.activeModule.set(module);
  }

  onSubmit() {
    if (this.isFormValid()) {
      this.isLoading.set(true);
      this.error.set('');

      const userData = {
        firstName: this.firstName(),
        lastName: this.lastName(),
        email: this.email(),
        password: this.password(),
        role: this.activeModule()
      };

      this.authService.register(userData).subscribe({
        next: (response) => {
          this.isLoading.set(false);
          // Since token is handled via cookies, we just check if response exists
          if (response && response.accessToken) {
            const role = response.role;
            if (role === 'INSTRUCTOR') {
              this.router.navigate(['/instructor']);
            } else if (role === 'ADMIN') {
              this.router.navigate(['/admin']);
            } else {
              this.router.navigate(['/dashboard']);
            }
          } else {
            this.router.navigate(['/login'], {
              queryParams: { registered: 'true' }
            });
          }
        },
        error: (err) => {
          if (err.status === 0) {
            this.error.set('Unable to connect to the server. Is the backend running?');
          } else {
            this.error.set(err.error?.error || err.error?.message || 'Registration failed. Please try again.');
          }
          this.isLoading.set(false);
        }
      });
    }
  }
}
