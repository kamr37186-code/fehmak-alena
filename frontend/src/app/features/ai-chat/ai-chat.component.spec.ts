import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { AiChatComponent } from './ai-chat.component';
import { AiService } from '../../core/services/ai.service';
import { AuthService } from '../../core/services/auth.service';

describe('AiChatComponent', () => {
  let component: AiChatComponent;
  let fixture: ComponentFixture<AiChatComponent>;
  let aiServiceSpy: jasmine.SpyObj<AiService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let currentUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    aiServiceSpy = jasmine.createSpyObj('AiService', ['chat', 'getHistory']);
    aiServiceSpy.getHistory.and.returnValue(of([]));
    currentUserSubject = new BehaviorSubject<any>(null);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], { currentUser$: currentUserSubject });

    await TestBed.configureTestingModule({
      imports: [AiChatComponent, FormsModule],
      providers: [
        { provide: AiService, useValue: aiServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    sessionStorage.clear();
    fixture = TestBed.createComponent(AiChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and initialize welcome message', () => {
    expect(component).toBeTruthy();
    expect(component.messages.length).toBe(1);
    expect(component.messages[0].sender).toBe('ai');
    expect(component.messages[0].text).toContain('Hello, Student!');
  });

  it('should update username from auth service', () => {
    currentUserSubject.next({ firstName: 'Alice', lastName: 'Smith' });
    fixture.detectChanges();
    
    expect(component.userName).toEqual('Alice Smith');
    expect(component.getAvatarUrl()).toContain('Alice+Smith');
  });

  it('should load chat history from service', () => {
    aiServiceSpy.getHistory.and.returnValue(of([
      { id: '1', sender: 'ai', text: 'Restored msg', timestamp: new Date() }
    ]));

    const fixture2 = TestBed.createComponent(AiChatComponent);
    const component2 = fixture2.componentInstance;
    fixture2.detectChanges();

    expect(component2.messages.length).toBe(1);
    expect(component2.messages[0].text).toBe('Restored msg');
  });

  it('should not send message if input is empty or loading', () => {
    component.newMessageText = '   ';
    component.sendMessage();

    expect(aiServiceSpy.chat).not.toHaveBeenCalled();
    
    component.newMessageText = 'Hello';
    component.isLoading = true;
    component.sendMessage();

    expect(aiServiceSpy.chat).not.toHaveBeenCalled();
  });

  it('should send user message and handle AI response', fakeAsync(() => {
    component.newMessageText = 'What is AI?';
    aiServiceSpy.chat.and.returnValue(of({ reply: 'AI is artificial intelligence.' }));

    component.sendMessage();
    tick(2000);

    expect(component.messages.length).toBe(3); // Welcome + User + AI
    expect(component.messages[1].text).toEqual('What is AI?');
    expect(component.messages[1].sender).toEqual('user');

    expect(component.messages[2].text).toEqual('AI is artificial intelligence.');
    expect(component.messages[2].sender).toEqual('ai');
    
    expect(component.newMessageText).toEqual('');
    expect(component.isLoading).toBeFalse();
    expect(aiServiceSpy.chat).toHaveBeenCalledWith('What is AI?');
  }));

  it('should handle API error gracefully', () => {
    component.newMessageText = 'Fail';
    aiServiceSpy.chat.and.returnValue(throwError(() => new Error('Server Error')));

    component.sendMessage();

    expect(component.messages.length).toBe(3);
    expect(component.messages[2].text).toContain('trouble connecting to my servers');
    expect(component.messages[2].sender).toEqual('ai');
    expect(component.isLoading).toBeFalse();
  });

  it('should clear chat when confirmed', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    
    component.messages.push({ id: '2', sender: 'user', text: 'Hi', timestamp: new Date() });
    expect(component.messages.length).toBe(2);

    component.clearChat();

    expect(component.messages.length).toBe(1); // Reset to just the welcome message
  });

  it('should not clear chat when cancelled', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    
    component.messages.push({ id: '2', sender: 'user', text: 'Hi', timestamp: new Date() });
    
    component.clearChat();

    expect(component.messages.length).toBe(2);
  });

  it('should rewrite last message', fakeAsync(() => {
    component.newMessageText = 'Original msg';
    aiServiceSpy.chat.and.returnValue(of({ reply: 'Response' }));
    component.sendMessage(); // msg array length becomes 3
    tick(2000);
    
    aiServiceSpy.chat.calls.reset();
    
    component.rewriteLastMessage();
    tick(2000);
    // It should splice the last user message and the subsequent AI message out, and then re-send
    // So after splice length is 1, then sendMessage adds User and AI -> length is 3 again

    expect(aiServiceSpy.chat).toHaveBeenCalledWith('Original msg');
  }));
});
