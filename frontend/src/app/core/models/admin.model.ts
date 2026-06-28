export interface SystemStats {
  concurrentUsers: number;
  systemUptime: number;
  monthlyRevenue: string;
  userGrowthTrend: number;
  revenueGrowthTrend: number;
  proConversion: number;
  ltvAverage: number;
}

export interface UserManagement {
  id: string;
  name: string;
  email: string;
  role: string;
  status: string;
  lastActivity: string;
}

export interface AdminDashboardResponse {
  systemStats: SystemStats;
  users: UserManagement[];
  totalUsers: number;
}
