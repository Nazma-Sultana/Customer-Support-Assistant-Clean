import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <header className="navbar">
      <div className="navbar-brand">Customer Support Assistant</div>
      {user && (
        <div className="navbar-user">
          <span className="navbar-username">{user.username}</span>
          <span className={`badge role-badge role-${user.role.toLowerCase()}`}>{user.role}</span>
          <button type="button" className="btn btn-ghost" onClick={handleLogout}>
            Logout
          </button>
        </div>
      )}
    </header>
  );
}
