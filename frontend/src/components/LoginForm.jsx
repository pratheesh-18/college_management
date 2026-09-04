import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { GraduationCap, LogIn, Shield, Users, UserCheck } from 'lucide-react';

export const LoginForm = () => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e?.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
    } catch (err) {
      setError(err.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  const fillQuickLogin = async (quickEmail, quickPass) => {
    setEmail(quickEmail);
    setPassword(quickPass);
    setError('');
    setLoading(true);
    try {
      await login(quickEmail, quickPass);
    } catch (err) {
      setError(err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem',
      background: '#F8FAFC'
    }}>
      <div className="glass-card" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem', background: '#FFFFFF', border: '1px solid #E2E8F0' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{
            width: '56px',
            height: '56px',
            borderRadius: '16px',
            background: 'linear-gradient(135deg, #2563EB 0%, #4F46E5 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem',
            boxShadow: '0 8px 20px rgba(37, 99, 235, 0.25)'
          }}>
            <GraduationCap size={32} color="#ffffff" />
          </div>
          <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0F172A' }}>EduSphere <span style={{ color: '#2563EB' }}>AI</span></h2>
          <p style={{ color: '#64748B', fontSize: '0.9rem', marginTop: '0.35rem' }}>
            Academic Management & Intelligence Portal
          </p>
        </div>

        {error && (
          <div style={{
            background: '#FEF2F2',
            border: '1px solid #FCA5A5',
            color: '#EF4444',
            padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            fontSize: '0.85rem',
            marginBottom: '1.25rem',
            textAlign: 'center',
            fontWeight: 600
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              className="form-input"
              placeholder="user@edusphere.edu"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', justifyContent: 'center', padding: '0.85rem' }}>
            <LogIn size={18} /> {loading ? 'Signing In...' : 'Sign In to Portal'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid #E2E8F0' }}>
          <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#64748B', textAlign: 'center', marginBottom: '1rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
            ⚡ Instant Demo Accounts (1-Click Login)
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
            <button
              onClick={() => fillQuickLogin('admin@edusphere.edu', 'admin123')}
              className="btn-secondary btn-sm"
              style={{ justifyContent: 'space-between' }}
            >
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Shield size={14} color="#2563EB" /> Admin Portal
              </span>
              <span style={{ color: '#64748B', fontSize: '0.75rem' }}>admin@edusphere.edu</span>
            </button>

            <button
              onClick={() => fillQuickLogin('faculty@edusphere.edu', 'faculty123')}
              className="btn-secondary btn-sm"
              style={{ justifyContent: 'space-between' }}
            >
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Users size={14} color="#06B6D4" /> Faculty Portal
              </span>
              <span style={{ color: '#64748B', fontSize: '0.75rem' }}>faculty@edusphere.edu</span>
            </button>

            <button
              onClick={() => fillQuickLogin('student@edusphere.edu', 'student123')}
              className="btn-secondary btn-sm"
              style={{ justifyContent: 'space-between' }}
            >
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <UserCheck size={14} color="#10B981" /> Student Portal
              </span>
              <span style={{ color: '#64748B', fontSize: '0.75rem' }}>student@edusphere.edu</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

