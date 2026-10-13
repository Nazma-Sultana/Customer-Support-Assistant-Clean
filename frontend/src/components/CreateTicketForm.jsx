import { useState } from 'react';

export default function CreateTicketForm({ onCreate }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!title.trim() || !description.trim()) {
      setError('Title and description are required.');
      return;
    }
    setError('');
    setSubmitting(true);
    try {
      await onCreate({ title: title.trim(), description: description.trim() });
      setTitle('');
      setDescription('');
    } catch (err) {
      setError(err.response?.data?.error || 'Could not create the ticket. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <h2>Create a new ticket</h2>
      {error && <p className="form-error">{error}</p>}
      <label>
        Title
        <input
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          placeholder="Short summary of the issue"
        />
      </label>
      <label>
        Description
        <textarea
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          rows={4}
          placeholder="Describe the issue in detail"
        />
      </label>
      <button className="btn btn-primary" type="submit" disabled={submitting}>
        {submitting ? 'Submitting...' : 'Submit ticket'}
      </button>
    </form>
  );
}
