import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { VirtualClassroomComponent } from './virtual-classroom.component';
import { AiService } from '../../core/services/ai.service';
import { AuthService } from '../../core/services/auth.service';

describe('VirtualClassroomComponent', () => {
  let component: VirtualClassroomComponent;
  let fixture: ComponentFixture<VirtualClassroomComponent>;
  let aiServiceSpy: jasmine.SpyObj<AiService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let currentUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    aiServiceSpy = jasmine.createSpyObj('AiService', ['chat']);
    currentUserSubject = new BehaviorSubject<any>(null);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { currentUser$: currentUserSubject });

    await TestBed.configureTestingModule({
      imports: [VirtualClassroomComponent, FormsModule],
      providers: [
        { provide: AiService, useValue: aiServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VirtualClassroomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


});
