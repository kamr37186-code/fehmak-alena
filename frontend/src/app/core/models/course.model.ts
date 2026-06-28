export interface Course {
  id: number;
  title: string;
  description: string;
  instructorName: string;
  category: string;
  difficultyLevel: string;
  durationHours: number;
  price: number;
  creationDate: string;
}

export interface CourseRequest {
  title: string;
  description: string;
  category: string;
  difficultyLevel: string;
  price: number;
}

