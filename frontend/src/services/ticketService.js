import api from './api';

// POST /api/tickets -> TicketResponse
export async function createTicket({ title, description }) {
  const response = await api.post('/api/tickets', { title, description });
  return response.data;
}

// GET /api/tickets?page=&size= -> Spring Data Page of TicketResponse (admin/agent) or
// CustomerTicketResponse (customer) depending on the caller's role.
export async function getTickets(page = 0, size = 5) {
  const response = await api.get('/api/tickets', { params: { page, size } });
  return response.data;
}

// PUT /api/tickets/{id}/assign?username= -> TicketResponse. ADMIN only.
export async function assignTicket(ticketId, username) {
  const response = await api.put(`/api/tickets/${ticketId}/assign`, null, {
    params: { username },
  });
  return response.data;
}

// PUT /api/tickets/{id}/status -> TicketResponse. ADMIN or the assigned AGENT.
export async function updateTicketStatus(ticketId, { status, resolutionNotes }) {
  const response = await api.put(`/api/tickets/${ticketId}/status`, { status, resolutionNotes });
  return response.data;
}
