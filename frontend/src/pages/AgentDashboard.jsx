import { useCallback, useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { getTickets, updateTicketStatus } from '../services/ticketService';

const PAGE_SIZE = 5;
const STATUS_OPTIONS = ['OPEN', 'IN_PROGRESS', 'CLOSED'];

export default function AgentDashboard() {
  const [ticketPage, setTicketPage] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [drafts, setDrafts] = useState({}); // ticketId -> { status, resolutionNotes }
  const [feedback, setFeedback] = useState({}); // ticketId -> { type, message }
  const [savingId, setSavingId] = useState(null);

  const loadTickets = useCallback(async (targetPage) => {
    setLoading(true);
    setError('');
    try {
      const data = await getTickets(targetPage, PAGE_SIZE);
      setTicketPage(data);
      const nextDrafts = {};
      data.content.forEach((ticket) => {
        nextDrafts[ticket.id] = { status: ticket.status, resolutionNotes: ticket.resolutionNotes || '' };
      });
      setDrafts(nextDrafts);
    } catch {
      setError('Could not load assigned tickets. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTickets(page);
  }, [page, loadTickets]);

  const updateDraft = (ticketId, field, value) => {
    setDrafts((prev) => ({ ...prev, [ticketId]: { ...prev[ticketId], [field]: value } }));
  };

  const applySuggestion = (ticket) => {
    updateDraft(ticket.id, 'resolutionNotes', ticket.aiSuggestedResolution || '');
  };

  const handleSave = async (ticket) => {
    const draft = drafts[ticket.id];
    if (draft.status === 'CLOSED' && !draft.resolutionNotes.trim()) {
      setFeedback((prev) => ({
        ...prev,
        [ticket.id]: { type: 'error', message: 'Resolution notes are required to close a ticket.' },
      }));
      return;
    }
    setSavingId(ticket.id);
    setFeedback((prev) => ({ ...prev, [ticket.id]: null }));
    try {
      await updateTicketStatus(ticket.id, {
        status: draft.status,
        resolutionNotes: draft.resolutionNotes.trim(),
      });
      setFeedback((prev) => ({ ...prev, [ticket.id]: { type: 'success', message: 'Ticket updated successfully.' } }));
      await loadTickets(page);
    } catch (err) {
      setFeedback((prev) => ({
        ...prev,
        [ticket.id]: { type: 'error', message: err.response?.data?.error || 'Could not update the ticket.' },
      }));
    } finally {
      setSavingId(null);
    }
  };

  const tickets = ticketPage?.content ?? [];

  return (
    <>
      <Navbar />
      <main className="page-content">
        <h1 className="page-title">Assigned tickets</h1>
        {loading && <LoadingSpinner label="Loading assigned tickets..." />}
        {!loading && error && <p className="form-error">{error}</p>}
        {!loading && !error && tickets.length === 0 && (
          <EmptyState title="No tickets assigned" message="You have no tickets assigned to you right now." />
        )}
        {!loading && !error && tickets.length > 0 && (
          <>
            <div className="ticket-grid">
              {tickets.map((ticket) => {
                const draft = drafts[ticket.id] || { status: ticket.status, resolutionNotes: '' };
                const ticketFeedback = feedback[ticket.id];
                return (
                  <article className="card ticket-card" key={ticket.id}>
                    <header className="ticket-card-header">
                      <h3>
                        #{ticket.id} — {ticket.title}
                      </h3>
                      <div className="badges">
                        <StatusBadge status={ticket.status} />
                        <PriorityBadge priority={ticket.aiPriority} />
                      </div>
                    </header>
                    <p className="ticket-description">{ticket.description}</p>
                    <dl className="ticket-meta">
                      <div>
                        <dt>Customer</dt>
                        <dd>{ticket.createdByUsername}</dd>
                      </div>
                      <div>
                        <dt>Created</dt>
                        <dd>{new Date(ticket.createdAt).toLocaleString()}</dd>
                      </div>
                      <div>
                        <dt>Assigned agent</dt>
                        <dd>{ticket.assignedAgentUsername || 'Unassigned'}</dd>
                      </div>
                    </dl>

                    <div className="ai-panel">
                      <h4>AI triage</h4>
                      <p>
                        <strong>Summary:</strong> {ticket.aiSummary || 'Not available'}
                      </p>
                      <p>
                        <strong>Suggested resolution:</strong> {ticket.aiSuggestedResolution || 'Not available'}
                      </p>
                      {ticket.aiSuggestedResolution && (
                        <button type="button" className="btn btn-ghost" onClick={() => applySuggestion(ticket)}>
                          Use AI suggestion as a starting point
                        </button>
                      )}
                    </div>

                    <div className="resolution-form">
                      <label>
                        Status
                        <select
                          value={draft.status}
                          onChange={(event) => updateDraft(ticket.id, 'status', event.target.value)}
                        >
                          {STATUS_OPTIONS.map((status) => (
                            <option key={status} value={status}>
                              {status.replace('_', ' ')}
                            </option>
                          ))}
                        </select>
                      </label>
                      <label>
                        Final resolution notes
                        <textarea
                          rows={3}
                          value={draft.resolutionNotes}
                          onChange={(event) => updateDraft(ticket.id, 'resolutionNotes', event.target.value)}
                          placeholder="Write the resolution you are sending to the customer"
                        />
                      </label>
                      {ticketFeedback && (
                        <p className={ticketFeedback.type === 'error' ? 'form-error' : 'form-success'}>
                          {ticketFeedback.message}
                        </p>
                      )}
                      <button
                        className="btn btn-primary"
                        type="button"
                        disabled={savingId === ticket.id}
                        onClick={() => handleSave(ticket)}
                      >
                        {savingId === ticket.id ? 'Saving...' : 'Save update'}
                      </button>
                    </div>
                  </article>
                );
              })}
            </div>
            <Pagination page={ticketPage.number} totalPages={ticketPage.totalPages} onPageChange={setPage} />
          </>
        )}
      </main>
    </>
  );
}
