import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getReviewsByBook } from '../../api/reviews';
import ReviewForm from './ReviewForm';

export default function ReviewList() {
  const { bookId } = useParams<{ bookId: string }>();

  const { data: reviews = [], isLoading, refetch } = useQuery({
    queryKey: ['reviews', bookId],
    queryFn: () => getReviewsByBook(bookId!),
    enabled: !!bookId,
  });

  if (isLoading) return <p>Loading reviews...</p>;

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', padding: 24 }}>
      <h2>Reviews for <code>{bookId}</code></h2>

      {reviews.length === 0 && <p>No reviews yet. Be the first!</p>}
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {reviews.map(r => (
          <li key={r.id} style={{ borderBottom: '1px solid #ccc', marginBottom: 16, paddingBottom: 16 }}>
            <strong>{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</strong>
            {r.verifiedReader && <span style={{ marginLeft: 8, color: 'green' }}>✔ Verified reader</span>}
            <p>{r.content}</p>
            <small>{new Date(r.createdAt).toLocaleDateString()}</small>
          </li>
        ))}
      </ul>

      {localStorage.getItem('token') && (
        <ReviewForm bookId={bookId!} onSuccess={() => refetch()} />
      )}
    </div>
  );
}
