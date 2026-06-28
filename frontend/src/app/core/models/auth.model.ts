export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
}

export interface AuthResponse {
  message: string;
  userId: number;
  role: string;
  firstName: string;
  lastName: string;
  email: string;
  accessToken: string;
}
