import React from 'react';
import { useAuth } from '../context/AuthContext';
import { 
  LayoutDashboard, 
  Building2, 
  Calendar, 
  BookOpen, 
  Users, 
  UserCheck,
  Award, 
  AlertTriangle, 
  Sparkles,
  CheckCircle2,
  TrendingUp,
  FileCheck
} from 'lucide-react';

export const Sidebar = ({ activeTab, setActiveTab }) => {
  const { primaryRole } = useAuth();

  const getNavItems = () => {
    switch (primaryRole) {
      case 'ADMIN':
        return [
          { id: 'dashboard', label: 'IT Dept Overview', icon: LayoutDashboard },
          { id: 'academic-years', label: 'Academic Years', icon: Calendar },
          { id: 'classes-subjects', label: 'Classes & Subjects', icon: BookOpen },
          { id: 'allocations', label: 'Teacher Allocations', icon: Users },
          { id: 'tutors-advisors', label: 'Tutors & Advisors', icon: UserCheck },
          { id: 'risk-alerts', label: 'Student Risk Monitor', icon: AlertTriangle },
          { id: 'certificates-admin', label: 'Certificate Monitoring', icon: FileCheck },
          { id: 'ai-advisor', label: 'AI Intelligence', icon: Sparkles }
        ];
      case 'FACULTY':
        return [
          { id: 'dashboard', label: 'Assigned Subjects', icon: LayoutDashboard },
          { id: 'marks-entry', label: 'Marks Management', icon: BookOpen },
          { id: 'verify-certificates', label: 'Verify Certificates', icon: CheckCircle2 },
          { id: 'risk-alerts', label: 'Student Risk Monitor', icon: AlertTriangle },
          { id: 'ai-advisor', label: 'AI Faculty Assistant', icon: Sparkles }
        ];
      case 'STUDENT':
        return [
          { id: 'dashboard', label: 'Academic Overview', icon: LayoutDashboard },
          { id: 'my-marks', label: 'Internal & Sem Marks', icon: BookOpen },
          { id: 'sem-compare', label: 'Semester Comparison', icon: TrendingUp },
          { id: 'my-certificates', label: 'Certificates & Credit', icon: Award },
          { id: 'ai-advisor', label: 'AI Academic Advisor', icon: Sparkles }
        ];
      default:
        return [];
    }
  };

  const navItems = getNavItems();

  return (
    <aside style={{
      width: '260px',
      background: '#FFFFFF',
      borderRight: '1px solid #E2E8F0',
      padding: '1.5rem 1rem',
      display: 'flex',
      flexDirection: 'column',
      gap: '0.35rem'
    }}>
      <div style={{ padding: '0 0.75rem 0.75rem', fontSize: '0.75rem', fontWeight: 700, color: '#94A3B8', textTransform: 'uppercase', letterSpacing: '0.8px' }}>
        Main Navigation
      </div>
      {navItems.map(item => {
        const Icon = item.icon;
        const isActive = activeTab === item.id;
        return (
          <button
            key={item.id}
            onClick={() => setActiveTab(item.id)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.85rem',
              padding: '0.75rem 1rem',
              borderRadius: 'var(--radius-md)',
              border: isActive ? '1px solid #BFDBFE' : '1px solid transparent',
              background: isActive ? '#EFF6FF' : 'transparent',
              color: isActive ? '#2563EB' : '#64748B',
              fontWeight: isActive ? 700 : 500,
              fontSize: '0.9rem',
              cursor: 'pointer',
              textAlign: 'left',
              transition: 'all 0.2s ease'
            }}
          >
            <Icon size={18} color={isActive ? '#2563EB' : '#64748B'} />
            <span>{item.label}</span>
          </button>
        );
      })}
    </aside>
  );
};
