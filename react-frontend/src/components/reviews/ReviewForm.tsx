import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { createReview } from '../../api/reviews';

interface Props {
  bookId: string;
  onSuccess: () => void;
}

export default function ReviewForm({ bookId, onSuccess }: Props) {
  const [rating, setRating] = useState(5);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const mutation = useMutation({
    mutationFn: createReview,
    onSuccess: () => { setContent(''); onSuccess(); },
    onError: (err: unknown) => {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg ?? 'Failed to submit review');
    },
  });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    mutation.mutate({ bookId, rating, content });
  }

  return (
    <div style={{ marginTop: 32 }}>
      <h3>Write a Review</h3>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Rating: </label>
          <select value={rating} onChange={e => setRating(Number(e.target.value))}>
            {[5, 4, 3, 2, 1].map(n => (
              <option key={n} value={n}>{n} ★</option>
            ))}
          </select>
        </div>
        <div style={{ marginTop: 12 }}>
          <label>Review</label><br />
          <textarea
            rows={4}
            style={{ width: '100%' }}
            value={content}
            onChange={e => setContent(e.target.value)}
          />
        </div>
        <button type="submit" style={{ marginTop: 8 }} disabled={mutation.isPending}>
          Submit
        </button>
      </form>
    </div>
  );
}
