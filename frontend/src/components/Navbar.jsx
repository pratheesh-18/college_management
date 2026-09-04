import React from 'react';
import { useAuth } from '../context/AuthContext';
import { GraduationCap, LogOut, UserCheck, Sparkles, Shield, User, BookOpen } from 'lucide-react';

export const Navbar = ({ activeTab, setActiveTab }) => {
  const { user, primaryRole, logout } = useAuth();

  return (
    <header style={{
      background: '#FFFFFF',
      borderBottom: '1px solid #E2E8F0',
      padding: '0.85rem 2rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      position: 'sticky',
      top: 0,
      zIndex: 100,
      boxShadow: 'var(--shadow-sm)'
    }}>
      {/* Brand Logo */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
        <div style={{
          width: '42px',
          height: '42px',
          borderRadius: '12px',
          background: 'linear-gradient(135deg, #2563EB 0%, #4F46E5 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 4px 10px rgba(37, 99, 235, 0.25)'
        }}>
          <GraduationCap size={24} color="#ffffff" />
        </div>
        <div>
          <h1 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0F172A', letterSpacing: '-0.5px' }}>
            EduSphere <span style={{ color: '#2563EB' }}>AI</span>
          </h1>
          <span style={{ fontSize: '0.72rem', color: '#64748B', fontWeight: 500 }}>Academic Intelligence Platform</span>
        </div>
      </div>

      {/* Center Nav Pill */}
      {user && (
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: '#F1F5F9',
          padding: '0.35rem 0.5rem',
          borderRadius: '24px',
          border: '1px solid #E2E8F0'
        }}>
          <button
            onClick={() => setActiveTab('dashboard')}
            className={`btn-sm ${activeTab === 'dashboard' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ borderRadius: '18px' }}
          >
            {primaryRole === 'ADMIN' ? <Shield size={15}/> : primaryRole === 'FACULTY' ? <BookOpen size={15}/> : <User size={15}/>}
            {primaryRole} Portal
          </button>
          <button
            onClick={() => setActiveTab('ai-advisor')}
            className={`btn-sm ${activeTab === 'ai-advisor' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ borderRadius: '18px', background: activeTab === 'ai-advisor' ? '#06B6D4' : '', color: activeTab === 'ai-advisor' ? '#FFFFFF' : '' }}
          >
            <Sparkles size={15} color={activeTab === 'ai-advisor' ? '#FFFFFF' : '#06B6D4'} />
            AI Advisor
          </button>
        </div>
      )}

      {/* User Info & Actions */}
      {user ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 700, fontSize: '0.9rem', color: '#0F172A' }}>{user.fullName}</div>
            <div style={{ fontSize: '0.75rem', color: '#64748B' }}>
              <span className={`badge ${primaryRole === 'ADMIN' ? 'badge-primary' : primaryRole === 'FACULTY' ? 'badge-info' : 'badge-success'}`}>
                {primaryRole}
              </span>
            </div>
          </div>
          <button onClick={logout} className="btn-secondary btn-sm" title="Sign Out">
            <LogOut size={16} />
            Logout
          </button>
        </div>
      ) : (
        <div className="badge badge-info">Guest Access</div>
      )}
    </header>
  );
};

