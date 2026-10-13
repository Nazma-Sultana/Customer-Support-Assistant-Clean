import axios from 'axios';

// JWT is kept in localStorage for this local learning project. This is NOT fully secure:
// any XSS vulnerability in the app (or a malicious dependency) could read this token and
// impersonate the user. A production system should prefer an httpOnly, SameSite cookie
// issued by the backend so client-side JavaScript can never read the token directly.
export const TOKEN_KEY = 'csa_token';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({ baseURL });

export function setAuthToken(token) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common.Authorization;
  }
}

// Restore the Authorization header after a page refresh.
const existingToken = localStorage.getItem(TOKEN_KEY);
if (existingToken) {
  setAuthToken(existingToken);
}

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response) {
      error.message = 'Cannot reach the server. Please check your connection and try again.';
    } else if (error.response.status === 401 || error.response.status === 403) {
      // Token missing, invalid, expired, or the backend rejected the request as unauthorized.
      localStorage.removeItem(TOKEN_KEY);
      setAuthToken(null);
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);

export default api;
