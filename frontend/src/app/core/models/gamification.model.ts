export interface GamificationBadge {
  id: number;
  name: string;
  description: string;
  iconUrl: string;
}

export interface GamificationActivity {
  id: number;
  activityType: string;
  xpGained: number;
  activityDate: string;
  badgeAwarded?: GamificationBadge;
}

export interface GamificationProfile {
  userId: number;
  totalXp: number;
  level: number;
  badges: GamificationBadge[];
  recentActivities: GamificationActivity[];
}
