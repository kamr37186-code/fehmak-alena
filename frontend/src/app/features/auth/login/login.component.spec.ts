import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { OAuthHelper } from '../../../core/services/oauth.helper';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: any;
  let oauthHelperSpy: any;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = {
      login: vi.fn(),
      loginWithGoogle: vi.fn(),
      logout: vi.fn(),
      loginWithFacebook: vi.fn()
    };
    oauthHelperSpy = {
      initializeGoogleSignIn: vi.fn(),
      renderGoogleButton: vi.fn(),
      initializeFacebookSignIn: vi.fn(),
      loginWithFacebook: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: OAuthHelper, useValue: oauthHelperSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } }
      ]
    }).compileComponents();
    
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty values', () => {
    expect(component.email()).toBe('');
    expect(component.password()).toBe('');
    expect(component.isFormValid()).toBe(false);
  });

  it('should invalidate form if email is missing or malformed', () => {
    component.email.set('invalid-email');
    component.password.set('password123');
    expect(component.isFormValid()).toBe(false);

    component.email.set('test@test.com');
    expect(component.isFormValid()).toBe(true);
  });

  it('should validate form if fields are correct', () => {
    component.email.set('test@test.com');
    component.password.set('password123');
    expect(component.isFormValid()).toBe(true);
  });

  it('should not submit if form is invalid', () => {
    component.onSubmit();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should show loading and call login on valid submit', () => {
    component.email.set('test@test.com');
    component.password.set('password123');
    authServiceSpy.login.mockReturnValue(of({ accessToken: '123', role: 'STUDENT', userId: 1, email: 'test@test.com', firstName: 'Test', lastName: 'User', message: 'Success' }));

    component.onSubmit();

    expect(component.isLoading()).toBe(false);
    expect(authServiceSpy.login).toHaveBeenCalledWith({ email: 'test@test.com', password: 'password123' });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should handle backend connection error (status 0)', () => {
    component.email.set('test@test.com');
    component.password.set('password123');
    authServiceSpy.login.mockReturnValue(throwError(() => ({ status: 0 })));

    component.onSubmit();

    expect(component.error()).toEqual('Unable to connect to the server. Is the backend running?');
    expect(component.isLoading()).toBe(false);
  });

  it('should handle invalid credentials error', () => {
    component.email.set('test@test.com');
    component.password.set('password123');
    authServiceSpy.login.mockReturnValue(throwError(() => ({ error: { message: 'Invalid credentials' } })));

    component.onSubmit();

    expect(component.error()).toEqual('Invalid credentials');
    expect(component.isLoading()).toBe(false);
  });

  it('should initialize google sign in after view init', () => {
    component.ngAfterViewInit();
    expect(oauthHelperSpy.initializeGoogleSignIn).toHaveBeenCalled();
    expect(oauthHelperSpy.renderGoogleButton).toHaveBeenCalledWith('google-btn-login');
  });

  it('should handle google login successfully', () => {
    component.activeModule.set('INSTRUCTOR');
    authServiceSpy.loginWithGoogle.mockReturnValue(of({ accessToken: '123', role: 'INSTRUCTOR', userId: 1, email: 'test@test.com', firstName: 'Test', lastName: 'User', message: 'Success' }));
    
    component.handleGoogleLogin('google-token');

    expect(authServiceSpy.loginWithGoogle).toHaveBeenCalledWith('google-token', 'INSTRUCTOR');
    expect(router.navigate).toHaveBeenCalledWith(['/instructor']);
  });

  it('should handle admin routing', () => {
    component.email.set('test@test.com');
    component.password.set('password123');
    authServiceSpy.login.mockReturnValue(of({ accessToken: '123', role: 'ADMIN', userId: 1, email: 'test@test.com', firstName: 'Test', lastName: 'User', message: 'Success' }));

    component.onSubmit();
    expect(router.navigate).toHaveBeenCalledWith(['/admin']);
  });
});
