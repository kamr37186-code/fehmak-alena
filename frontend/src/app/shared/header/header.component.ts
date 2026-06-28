import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GamificationService } from '../../core/services/gamification.service';
import { GamificationProfile } from '../../core/models/gamification.model';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../core/models/notification.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  userName: string = 'User';
  userRole: string = 'Student';
  gamificationProfile: GamificationProfile | null = null;
  xpPercentage: number = 0;
  
  // Notifications state
  notifications: Notification[] = [];
  unreadCount: number = 0;
  isNotificationsOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private gamificationService: GamificationService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });

    this.gamificationService.profile$.subscribe(profile => {
      if (profile) {
        this.gamificationProfile = profile;
        this.xpPercentage = Math.min((profile.totalXp / 10000) * 100, 100);
      }
    });

    this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        const u: any = user.user || user;
        this.userName = `${u.firstName} ${u.lastName}`;
        this.userRole = u.role || 'STUDENT';
        const userId = u.id || user.userId;
        
        if (userId) {
          // Fetch initial notifications
          this.fetchNotifications();
          
          if (this.userRole === 'STUDENT') {
            this.gamificationService.getUserProfile(userId).subscribe({
              error: (err) => console.error('Error fetching gamification profile', err)
            });
          }
        }
      }
    });
  }

  fetchNotifications() {
    this.notificationService.getUserNotifications().subscribe({
      next: (notifs) => {
        this.notifications = notifs;
        this.unreadCount = notifs.filter(n => !n.isRead).length;
      },
      error: (err) => console.error('Error fetching notifications', err)
    });
  }

  toggleNotifications() {
    this.isNotificationsOpen = !this.isNotificationsOpen;
    if (this.isNotificationsOpen) {
      this.fetchNotifications(); // Refresh on open
    }
  }

  markAsRead(id: number) {
    this.notificationService.markAsRead(id).subscribe(() => {
      this.fetchNotifications();
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.fetchNotifications();
    });
  }

  getAvatarUrl(): string {
    return `https://ui-avatars.com/api/?name=${this.userName.replace(' ', '+')}&background=random`;
  }
}
