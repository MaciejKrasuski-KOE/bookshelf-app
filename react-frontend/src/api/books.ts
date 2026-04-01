import client from './client';
import type { Author, CreateAuthorRequest } from '../types';

export const getAuthors = () =>
  client.get<Author[]>('/api/authors').then(r => r.data);

export const createAuthor = (data: CreateAuthorRequest) =>
  client.post<Author>('/api/authors', data).then(r => r.data);

export const deleteAuthor = (id: string) =>
  client.delete(`/api/authors/${id}`);
