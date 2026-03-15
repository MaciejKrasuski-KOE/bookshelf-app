import client from './client';
import type { Review, CreateReviewRequest } from '../types';

export const getReviewsByBook = (bookId: string) =>
  client.get<Review[]>(`/api/reviews/book/${bookId}`).then(r => r.data);

export const getReviewsByUser = (userId: string) =>
  client.get<Review[]>(`/api/reviews/user/${userId}`).then(r => r.data);

export const createReview = (data: CreateReviewRequest) =>
  client.post<Review>('/api/reviews', data).then(r => r.data);

export const deleteReview = (id: string) =>
  client.delete(`/api/reviews/${id}`);
