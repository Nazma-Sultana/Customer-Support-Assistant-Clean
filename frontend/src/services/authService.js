import api from './api';

// POST /api/auth/login -> { token }
export async function login(username, password) {
  const response = await api.post('/api/auth/login', { username, password });
  return response.data;
}

// POST /api/users -> UserResponse (role is always CUSTOMER server-side for public registration)
export async function register({ username, email, password }) {
  const response = await api.post('/api/users', { username, email, password });
  return response.data;
}

// GET /api/users/me -> UserResponse { id, username, email, role }
export async function getCurrentUser() {
  const response = await api.get('/api/users/me');
  return response.data;
}
