import api from './api';

// GET /api/users -> UserResponse[]. Restricted to ADMIN on the backend.
export async function getAllUsers() {
  const response = await api.get('/api/users');
  return response.data;
}

// Agents are derived client-side since the backend only exposes one users endpoint.
export async function getAgents() {
  const users = await getAllUsers();
  return users.filter((user) => user.role === 'AGENT');
}
