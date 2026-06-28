import { Component, OnInit, OnDestroy, ElementRef, viewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService, ChatMessage } from '../../core/services/ai.service';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-ai-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-chat.component.html',
  styles: [`
    .markdown-body {
      white-space: pre-wrap;
      word-break: break-word;
    }
  `]
})
export class AiChatComponent implements OnInit, OnDestroy {
  private chatScrollContainer = viewChild<ElementRef>('chatScrollContainer');
  
  messages: ChatMessage[] = [];
  newMessageText: string = '';
  isLoading: boolean = false;
  isTyping: boolean = false;
  userName: string = 'Student';
  private authSub?: Subscription;
  
  constructor(private aiService: AiService, private authService: AuthService) {}

  ngOnInit(): void {
    // Get actual user name
    this.authSub = this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        this.userName = `${user.firstName} ${user.lastName}`;
      }
    });

    this.loadHistory();
  }
  
  private loadHistory(): void {
    this.isLoading = true;
    this.aiService.getHistory().subscribe({
      next: (history) => {
        if (history && history.length > 0) {
          this.messages = history;
        } else {
          // Initial welcome message if no history
          this.messages.push({
            id: this.generateId(),
            sender: 'ai',
            text: `Hello, ${this.userName}! I am your AI Learning Assistant. How can I help you with your studies today?`,
            timestamp: new Date()
          });
        }
        this.isLoading = false;
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Failed to load history', err);
        this.isLoading = false;
        this.messages.push({
          id: this.generateId(),
          sender: 'ai',
          text: `Hello! I am your AI Learning Assistant. I couldn't load past messages, but we can start a new chat!`,
          timestamp: new Date()
        });
      }
    });
  }

  private scrollToBottom(): void {
    try {
      if (this.chatScrollContainer()) {
        this.chatScrollContainer()!.nativeElement.scrollTop = this.chatScrollContainer()!.nativeElement.scrollHeight;
      }
    } catch(err) { }
  }

  sendMessage(): void {
    if (!this.newMessageText.trim() || this.isLoading || this.isTyping) return;
    
    const userMsg: ChatMessage = {
      id: this.generateId(),
      sender: 'user',
      text: this.newMessageText,
      timestamp: new Date()
    };
    
    this.messages.push(userMsg);
    this.scrollToBottom();
    
    const messageText = this.newMessageText;
    this.newMessageText = '';
    this.isLoading = true;
    
    // Context and Persona are handled dynamically by the Backend Controller
    this.aiService.chat(messageText).subscribe({
      next: (response) => {
        this.isLoading = false;
        const replyText = response.reply || response.content || 'I received your message but could not parse the response.';
        this.typeWriterEffect(replyText);
      },
      error: (err) => {
        console.error('AI Chat Error', err);
        this.isLoading = false;
        this.messages.push({
          id: this.generateId(),
          sender: 'ai',
          text: 'Sorry, I am having trouble connecting to my servers right now. Please try again later.',
          timestamp: new Date()
        });
        this.scrollToBottom();
      }
    });
  }

  private typeWriterEffect(fullText: string): void {
    this.isTyping = true;
    
    const aiMsg: ChatMessage = {
      id: this.generateId(),
      sender: 'ai',
      text: '', // Start empty
      timestamp: new Date()
    };
    this.messages.push(aiMsg);
    
    let i = 0;
    // Faster typing for longer texts, roughly 10-20ms per char
    const typingSpeed = Math.max(5, 20 - Math.floor(fullText.length / 100));
    
    const typingInterval = setInterval(() => {
      if (i < fullText.length) {
        // Find the newly pushed message by reference or ID and update text
        aiMsg.text += fullText.charAt(i);
        i++;
        
        // Auto scroll down every few characters
        if (i % 10 === 0) {
          this.scrollToBottom();
        }
      } else {
        clearInterval(typingInterval);
        this.isTyping = false;
        this.scrollToBottom();
      }
    }, typingSpeed);
  }

  clearChat(): void {
    // Note: Clearing from DB requires a new backend endpoint.
    // For now, we clear the UI. 
    if (confirm('Are you sure you want to clear this conversation from view? (It remains saved in your history on the server)')) {
      this.messages = [];
      this.messages.push({
        id: this.generateId(),
        sender: 'ai',
        text: `Conversation cleared from view. How else can I help?`,
        timestamp: new Date()
      });
    }
  }

  rewriteLastMessage(): void {
    if (this.messages.length <= 1 || this.isLoading || this.isTyping) return;

    let lastUserMsgIndex = -1;
    for (let i = this.messages.length - 1; i >= 0; i--) {
      if (this.messages[i].sender === 'user') {
        lastUserMsgIndex = i;
        break;
      }
    }

    if (lastUserMsgIndex !== -1) {
      const lastUserText = this.messages[lastUserMsgIndex].text;
      // We don't delete from backend here (needs an endpoint), just resend.
      this.newMessageText = lastUserText;
      // Just visually remove the last AI message so it doesn't look messy
      this.messages.splice(lastUserMsgIndex);
      this.sendMessage();
    }
  }
  
  private generateId(): string {
    return Math.random().toString(36).substring(2, 9);
  }
  
  getAvatarUrl(): string {
    return `https://ui-avatars.com/api/?name=${this.userName.replace(' ', '+')}&background=random`;
  }

  ngOnDestroy(): void {
    if (this.authSub) {
      this.authSub.unsubscribe();
    }
  }
}
