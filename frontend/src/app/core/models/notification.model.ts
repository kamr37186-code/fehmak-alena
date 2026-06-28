export interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'INFO' | 'ACHIEVEMENT' | 'COURSE' | 'SYSTEM' | 'ALERT';
  isRead: boolean;
  createdAt: string;
}
