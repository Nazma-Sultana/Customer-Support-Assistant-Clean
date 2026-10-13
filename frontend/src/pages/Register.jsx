import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../services/authService';

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (field) => (event) =>
    setForm((prev) => ({ ...prev, [field]: event.target.value }));

  const validate = () => {
    if (!form.username.trim() || !form.email.trim() || !form.password || !form.confirmPassword) {
      return 'All fields are required.';
    }
    if (form.password.length < 6) {
      return 'Password must be at least 6 characters.';
    }
    if (form.password !== form.confirmPassword) {
      return 'Passwords do not match.';
    }
    return '';
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }
    setError('');
    setSubmitting(true);
    try {
      // Role is never sent — the backend always assigns CUSTOMER for this endpoint.
      await register({ username: form.username.trim(), email: form.email.trim(), password: form.password });
      setSuccess(true);
      setTimeout(() => navigate('/login', { replace: true }), 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Could not create account. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="card auth-card" onSubmit={handleSubmit}>
        <h1>Create your account</h1>
        <p className="auth-subtitle">Register as a customer to submit and track support tickets.</p>
        {error && <p className="form-error">{error}</p>}
        {success && <p className="form-success">Account created! Redirecting to sign in...</p>}
        <label>
          Username
          <input value={form.username} onChange={handleChange('username')} autoFocus />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={handleChange('email')} />
        </label>
        <label>
          Password
          <input type="password" value={form.password} onChange={handleChange('password')} />
        </label>
        <label>
          Confirm password
          <input type="password" value={form.confirmPassword} onChange={handleChange('confirmPassword')} />
        </label>
        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? 'Creating account...' : 'Register'}
        </button>
        <p className="auth-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </form>
    </div>
  );
}
