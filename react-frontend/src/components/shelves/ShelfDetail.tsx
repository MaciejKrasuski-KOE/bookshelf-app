import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getShelf, addBook, removeBook } from '../../api/shelves';

export default function ShelfDetail() {
  const { id } = useParams<{ id: string }>();
  const qc = useQueryClient();
  const [bookId, setBookId] = useState('');

  const { data: shelf, isLoading } = useQuery({
    queryKey: ['shelf', id],
    queryFn: () => getShelf(id!),
    enabled: !!id,
  });

  const addMutation = useMutation({
    mutationFn: (bid: string) => addBook(id!, bid),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['shelf', id] }); setBookId(''); },
  });

  const removeMutation = useMutation({
    mutationFn: (bid: string) => removeBook(id!, bid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['shelf', id] }),
  });

  if (isLoading) return <p>Loading...</p>;
  if (!shelf) return <p>Shelf not found.</p>;

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', padding: 24 }}>
      <Link to="/shelves">← Back</Link>
      <h2>{shelf.name}</h2>
      <p>{shelf.bookIds.length} book(s)</p>

      <div style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
        <input
          placeholder="Book ID / ISBN"
          value={bookId}
          onChange={e => setBookId(e.target.value)}
        />
        <button
          onClick={() => bookId.trim() && addMutation.mutate(bookId.trim())}
          disabled={addMutation.isPending}
        >
          Add Book
        </button>
      </div>

      <ul style={{ listStyle: 'none', padding: 0 }}>
        {shelf.bookIds.map(bid => (
          <li key={bid} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
            <Link to={`/reviews/${bid}`}>{bid}</Link>
            <button onClick={() => removeMutation.mutate(bid)}>Remove</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
