import client from './client';
import type { AuthResponse, User } from '../types';

export const register = (username: string, email: string, password: string) =>
  client.post<AuthResponse>('/api/auth/register', { username, email, password }).then(r => r.data);

export const login = (username: string, password: string) =>
  client.post<AuthResponse>('/api/auth/login', { username, password }).then(r => r.data);

export const me = () =>
  client.get<User>('/api/auth/me').then(r => r.data);
