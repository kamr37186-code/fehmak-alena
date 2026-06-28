export interface UserProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
}
