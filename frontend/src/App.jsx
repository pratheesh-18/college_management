import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { LoginForm } from './components/LoginForm';
import { AdminDashboard } from './components/AdminDashboard';
import { FacultyDashboard } from './components/FacultyDashboard';
import { StudentDashboard } from './components/StudentDashboard';
import { AiAdvisorChat } from './components/AiAdvisorChat';

const MainLayout = () => {
  const { user, primaryRole, loading } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  if (loading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#818cf8', fontWeight: 700 }}>
        Initializing EduSphere AI...
      </div>
    );
  }

  if (!user) {
    return <LoginForm />;
  }

  const renderActivePortal = () => {
    if (activeTab === 'ai-advisor') {
      return <AiAdvisorChat />;
    }

    switch (primaryRole) {
      case 'ADMIN':
        return <AdminDashboard activeTab={activeTab} />;
      case 'FACULTY':
        return <FacultyDashboard activeTab={activeTab} />;
      case 'STUDENT':
        return <StudentDashboard activeTab={activeTab} />;
      default:
        return <div className="page-wrapper">Unknown Role Access</div>;
    }
  };

  return (
    <div className="app-container">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      <div className="main-content">
        <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />
        {renderActivePortal()}
      </div>
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <MainLayout />
    </AuthProvider>
  );
}
