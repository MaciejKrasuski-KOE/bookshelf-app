import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAuthors, createAuthor, deleteAuthor } from '../../api/books';

export default function AuthorList() {
  const qc = useQueryClient();
  const [name, setName] = useState('');

  const { data: authors, isLoading } = useQuery({
    queryKey: ['authors'],
    queryFn: getAuthors,
  });

  const addMutation = useMutation({
    mutationFn: createAuthor,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['authors'] });
      setName('');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteAuthor,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['authors'] }),
  });

  function handleAdd() {
    const trimmed = name.trim();
    if (trimmed) {
      addMutation.mutate({ name: trimmed });
    }
  }

  if (isLoading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h2>Authors</h2>

      <div>
        <input
          placeholder="Author name"
          value={name}
          onChange={e => setName(e.target.value)}
          onKeyDown={e => { if (e.key === 'Enter') { handleAdd(); } }}
        />
        <button onClick={handleAdd} disabled={addMutation.isPending}>
          Add
        </button>
        {addMutation.isError && <span>Failed to add author.</span>}
      </div>

      {authors && authors.length === 0 && <p>No authors yet.</p>}

      <ul>
        {authors?.map(author => (
          <li key={author.id}>
            {author.name}
            <button
              onClick={() => deleteMutation.mutate(author.id)}
              disabled={deleteMutation.isPending}
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
