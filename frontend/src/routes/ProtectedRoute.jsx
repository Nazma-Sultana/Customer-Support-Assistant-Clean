import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { roleHome } from '../utils/roleHome';
import LoadingSpinner from '../components/LoadingSpinner';

export default function ProtectedRoute({ allowedRoles, children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner label="Checking your session..." />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={roleHome(user.role)} replace />;
  }

  return children;
}
