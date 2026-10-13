import { useCallback, useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { getTickets, assignTicket } from '../services/ticketService';
import { getAgents } from '../services/userService';

const PAGE_SIZE = 5;

export default function AdminDashboard() {
  const [ticketPage, setTicketPage] = useState(null);
  const [page, setPage] = useState(0);
  const [agents, setAgents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selections, setSelections] = useState({}); // ticketId -> selected agent username
  const [feedback, setFeedback] = useState({});
  const [assigningId, setAssigningId] = useState(null);

  const loadData = useCallback(async (targetPage) => {
    setLoading(true);
    setError('');
    try {
      const [ticketsData, agentList] = await Promise.all([
        getTickets(targetPage, PAGE_SIZE),
        getAgents(),
      ]);
      setTicketPage(ticketsData);
      setAgents(agentList);
    } catch {
      setError('Could not load tickets. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData(page);
  }, [page, loadData]);

  const handleAssign = async (ticket) => {
    const username = selections[ticket.id];
    if (!username) {
      setFeedback((prev) => ({ ...prev, [ticket.id]: { type: 'error', message: 'Select an agent first.' } }));
      return;
    }
    setAssigningId(ticket.id);
    try {
      await assignTicket(ticket.id, username);
      setFeedback((prev) => ({ ...prev, [ticket.id]: { type: 'success', message: `Assigned to ${username}.` } }));
      await loadData(page);
    } catch (err) {
      setFeedback((prev) => ({
        ...prev,
        [ticket.id]: { type: 'error', message: err.response?.data?.error || 'Could not assign the ticket.' },
      }));
    } finally {
      setAssigningId(null);
    }
  };

  const tickets = ticketPage?.content ?? [];

  return (
    <>
      <Navbar />
      <main className="page-content">
        <h1 className="page-title">All tickets</h1>
        {loading && <LoadingSpinner label="Loading tickets..." />}
        {!loading && error && <p className="form-error">{error}</p>}
        {!loading && !error && tickets.length === 0 && (
          <EmptyState title="No tickets" message="No tickets have been submitted yet." />
        )}
        {!loading && !error && tickets.length > 0 && (
          <>
            {agents.length === 0 && (
              <p className="helper-text">
                No agent accounts were found, so tickets cannot be assigned yet. Create an AGENT user
                directly in the database to enable assignment.
              </p>
            )}
            <div className="ticket-grid">
              {tickets.map((ticket) => {
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
                    </div>

                    {ticket.resolutionNotes && (
                      <p>
                        <strong>Resolution notes:</strong> {ticket.resolutionNotes}
                      </p>
                    )}

                    <div className="assign-form">
                      <label>
                        Assign to agent
                        <select
                          value={selections[ticket.id] || ''}
                          onChange={(event) =>
                            setSelections((prev) => ({ ...prev, [ticket.id]: event.target.value }))
                          }
                          disabled={agents.length === 0}
                        >
                          <option value="">Select an agent</option>
                          {agents.map((agent) => (
                            <option key={agent.id} value={agent.username}>
                              {agent.username}
                            </option>
                          ))}
                        </select>
                      </label>
                      {ticketFeedback && (
                        <p className={ticketFeedback.type === 'error' ? 'form-error' : 'form-success'}>
                          {ticketFeedback.message}
                        </p>
                      )}
                      <button
                        className="btn btn-primary"
                        type="button"
                        disabled={assigningId === ticket.id || agents.length === 0}
                        onClick={() => handleAssign(ticket)}
                      >
                        {assigningId === ticket.id
                          ? 'Assigning...'
                          : ticket.assignedAgentUsername
                            ? 'Reassign'
                            : 'Assign'}
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
