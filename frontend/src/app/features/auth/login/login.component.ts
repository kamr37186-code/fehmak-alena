import { Component, AfterViewInit, signal, computed, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, TitleCasePipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { OAuthHelper } from '../../../core/services/oauth.helper';
import gsap from 'gsap';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TitleCasePipe],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements AfterViewInit {
  email = signal('');
  password = signal('');
  error = signal('');
  isLoading = signal(false);
  activeModule = signal<'STUDENT' | 'INSTRUCTOR'>('STUDENT');

  isFormValid = computed(() => {
    return this.email().includes('@') && this.email().length > 3 && this.password().length > 0;
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
        { opacity: 0, scale: 0.9, x: 50 },
        { opacity: 1, scale: 1, x: 0, duration: 1.5, ease: 'power3.out', delay: 0.3 }
      );

      // OAuth Init
      this.oauthHelper.initializeGoogleSignIn('YOUR_GOOGLE_CLIENT_ID', (response) => {
        this.handleGoogleLogin(response.credential);
      });
      this.oauthHelper.renderGoogleButton('google-btn-login');
      this.oauthHelper.initializeFacebookSignIn('YOUR_FACEBOOK_APP_ID');
    }
  }

  handleGoogleLogin(token: string) {
    this.isLoading.set(true);
    this.error.set('');
    this.authService.loginWithGoogle(token, this.activeModule()).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.redirectBasedOnRole(response.role);
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
            this.redirectBasedOnRole(res.role);
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
      
      this.authService.login({ email: this.email(), password: this.password() }).subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.redirectBasedOnRole(response.role);
        },
        error: (err) => {
          if (err.status === 0) {
            this.error.set('Unable to connect to the server. Is the backend running?');
          } else {
            this.error.set(typeof err.error === 'string' ? err.error : (err.error?.message || 'Invalid credentials'));
          }
          this.isLoading.set(false);
        }
      });
    }
  }

  private redirectBasedOnRole(role: string) {
    if (role === 'ADMIN') {
      this.router.navigate(['/admin']);
      return;
    }

    if (role !== this.activeModule()) {
      this.error.set(`This account belongs to a ${role.toLowerCase()}. Please select the correct portal to login.`);
      this.authService.logout(); // log them out since they are in the wrong portal
      return;
    }
    
    if (role === 'INSTRUCTOR') {
      this.router.navigate(['/instructor']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }
}

