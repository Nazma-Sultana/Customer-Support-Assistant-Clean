import { useCallback, useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import CreateTicketForm from '../components/CreateTicketForm';
import StatusBadge from '../components/StatusBadge';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { createTicket, getTickets } from '../services/ticketService';

const PAGE_SIZE = 5;

export default function CustomerDashboard() {
  const [ticketPage, setTicketPage] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadTickets = useCallback(async (targetPage) => {
    setLoading(true);
    setError('');
    try {
      const data = await getTickets(targetPage, PAGE_SIZE);
      setTicketPage(data);
    } catch {
      setError('Could not load your tickets. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTickets(page);
  }, [page, loadTickets]);

  const handleCreate = async (payload) => {
    await createTicket(payload);
    setPage(0);
    await loadTickets(0);
  };

  const tickets = ticketPage?.content ?? [];

  return (
    <>
      <Navbar />
      <main className="page-content">
        <h1 className="page-title">My tickets</h1>

        <CreateTicketForm onCreate={handleCreate} />

        <section className="card">
          <h2>Ticket history</h2>
          {loading && <LoadingSpinner label="Loading your tickets..." />}
          {!loading && error && <p className="form-error">{error}</p>}
          {!loading && !error && tickets.length === 0 && (
            <EmptyState
              title="No tickets yet"
              message="Submit your first ticket using the form above."
            />
          )}
          {!loading && !error && tickets.length > 0 && (
            <>
              <div className="table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Title</th>
                      <th>Status</th>
                      <th>Created</th>
                      <th>Assigned agent</th>
                      <th>Resolution notes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tickets.map((ticket) => (
                      <tr key={ticket.id}>
                        <td>#{ticket.id}</td>
                        <td>
                          <div className="ticket-title">{ticket.title}</div>
                          <div className="ticket-description">{ticket.description}</div>
                        </td>
                        <td>
                          <StatusBadge status={ticket.status} />
                        </td>
                        <td>{new Date(ticket.createdAt).toLocaleString()}</td>
                        <td>{ticket.assignedAgentUsername || 'Unassigned'}</td>
                        <td>{ticket.resolutionNotes || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <Pagination
                page={ticketPage.number}
                totalPages={ticketPage.totalPages}
                onPageChange={setPage}
              />
            </>
          )}
        </section>
      </main>
    </>
  );
}
