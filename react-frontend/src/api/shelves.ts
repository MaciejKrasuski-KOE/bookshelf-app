import client from './client';
import type { Shelf } from '../types';

export const getShelves = () =>
  client.get<Shelf[]>('/api/shelves').then(r => r.data);

export const getShelf = (id: string) =>
  client.get<Shelf>(`/api/shelves/${id}`).then(r => r.data);

export const createShelf = (name: string) =>
  client.post<Shelf>('/api/shelves', { name }).then(r => r.data);

export const deleteShelf = (id: string) =>
  client.delete(`/api/shelves/${id}`);

export const addBook = (shelfId: string, bookId: string) =>
  client.post<Shelf>(`/api/shelves/${shelfId}/books`, { bookId }).then(r => r.data);

export const removeBook = (shelfId: string, bookId: string) =>
  client.delete(`/api/shelves/${shelfId}/books/${bookId}`);
