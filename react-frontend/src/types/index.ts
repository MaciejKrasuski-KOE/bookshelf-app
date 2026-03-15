export interface AuthResponse {
  token: string;
  username: string;
  userId: string;
}

export interface User {
  id: string;
  username: string;
  email: string;
}

export interface Shelf {
  id: string;
  userId: string;
  name: string;
  bookIds: string[];
  createdAt: string;
}

export interface Review {
  id: string;
  userId: string;
  bookId: string;
  rating: number;
  content: string;
  verifiedReader: boolean;
  createdAt: string;
}

export interface CreateReviewRequest {
  bookId: string;
  rating: number;
  content: string;
}
