import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';
import { OAuthHelper } from '../../../core/services/oauth.helper';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: any;
  let oauthHelperSpy: any;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = {
      register: vi.fn(),
      loginWithGoogle: vi.fn(),
      loginWithFacebook: vi.fn()
    };
    oauthHelperSpy = {
      initializeGoogleSignIn: vi.fn(),
      renderGoogleButton: vi.fn(),
      initializeFacebookSignIn: vi.fn(),
      loginWithFacebook: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: OAuthHelper, useValue: oauthHelperSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } }
      ]
    }).compileComponents();
    
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty values', () => {
    expect(component.firstName()).toBe('');
    expect(component.lastName()).toBe('');
    expect(component.email()).toBe('');
    expect(component.password()).toBe('');
    expect(component.activeModule()).toBe('STUDENT');
    expect(component.agreeTerms()).toBe(false);
    expect(component.isFormValid()).toBe(false);
  });

  it('should invalidate form if fields are missing or invalid', () => {
    component.email.set('invalid-email');
    component.password.set('short');
    component.agreeTerms.set(false);
    expect(component.isFormValid()).toBe(false);
  });

  it('should validate form if fields are correct', () => {
    component.firstName.set('John');
    component.lastName.set('Doe');
    component.email.set('john@test.com');
    component.password.set('Password123!');
    component.activeModule.set('STUDENT');
    component.agreeTerms.set(true);
    expect(component.isFormValid()).toBe(true);
  });

  it('should not call register if form is invalid on submit', () => {
    component.onSubmit();
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should show loading and call register on valid submit with auto-login', () => {
    component.firstName.set('John');
    component.lastName.set('Doe');
    component.email.set('john@test.com');
    component.password.set('Password123!');
    component.activeModule.set('STUDENT');
    component.agreeTerms.set(true);

    authServiceSpy.register.mockReturnValue(of({ accessToken: '123', role: 'STUDENT', userId: 1, email: 'john@test.com', firstName: 'John', lastName: 'Doe', message: 'Success' }));

    component.onSubmit();

    expect(component.isLoading()).toBe(false);
    expect(authServiceSpy.register).toHaveBeenCalledWith({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@test.com',
      password: 'Password123!',
      role: 'STUDENT'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should route to login if registration succeeds but no auto-login token is provided', () => {
    component.firstName.set('John');
    component.lastName.set('Doe');
    component.email.set('john@test.com');
    component.password.set('Password123!');
    component.activeModule.set('STUDENT');
    component.agreeTerms.set(true);

    authServiceSpy.register.mockReturnValue(of({ message: 'Success' }));

    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { registered: 'true' } });
  });

  it('should handle backend connection error (status 0)', () => {
    component.firstName.set('John');
    component.lastName.set('Doe');
    component.email.set('john@test.com');
    component.password.set('Password123!');
    component.activeModule.set('STUDENT');
    component.agreeTerms.set(true);

    authServiceSpy.register.mockReturnValue(throwError(() => ({ status: 0 })));

    component.onSubmit();

    expect(component.error()).toEqual('Unable to connect to the server. Is the backend running?');
    expect(component.isLoading()).toBe(false);
  });

  it('should handle registration error', () => {
    component.firstName.set('John');
    component.lastName.set('Doe');
    component.email.set('john@test.com');
    component.password.set('Password123!');
    component.activeModule.set('STUDENT');
    component.agreeTerms.set(true);

    authServiceSpy.register.mockReturnValue(throwError(() => ({ error: { message: 'Email already exists' } })));

    component.onSubmit();

    expect(component.error()).toEqual('Email already exists');
    expect(component.isLoading()).toBe(false);
  });

  it('should initialize google sign in after view init', () => {
    component.ngAfterViewInit();
    expect(oauthHelperSpy.initializeGoogleSignIn).toHaveBeenCalled();
    expect(oauthHelperSpy.renderGoogleButton).toHaveBeenCalledWith('google-btn-register');
  });

  it('should handle google login successfully', () => {
    authServiceSpy.loginWithGoogle.mockReturnValue(of({ accessToken: '123', role: 'INSTRUCTOR', userId: 1, email: 'john@test.com', firstName: 'John', lastName: 'Doe', message: 'Success' }));
    
    component.handleGoogleLogin('google-token');

    expect(authServiceSpy.loginWithGoogle).toHaveBeenCalledWith('google-token', 'STUDENT');
    expect(router.navigate).toHaveBeenCalledWith(['/instructor']);
  });
});
