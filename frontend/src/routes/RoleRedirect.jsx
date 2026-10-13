import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { roleHome } from '../utils/roleHome';
import LoadingSpinner from '../components/LoadingSpinner';

// Sends an authenticated user to their dashboard, or an anonymous visitor to /login.
export default function RoleRedirect() {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner label="Loading..." />;
  }

  return <Navigate to={user ? roleHome(user.role) : '/login'} replace />;
}
