import React from 'react';

export const StatCard = ({ title, value, subtext, icon: Icon, trend, color = 'primary' }) => {
  const getIconBg = () => {
    switch (color) {
      case 'emerald': return '#ECFDF5';
      case 'rose': return '#FEF2F2';
      case 'amber': return '#FFFBEB';
      case 'cyan': return '#ECFEFF';
      default: return '#EFF6FF';
    }
  };

  const getIconColor = () => {
    switch (color) {
      case 'emerald': return '#10B981';
      case 'rose': return '#EF4444';
      case 'amber': return '#F59E0B';
      case 'cyan': return '#06B6D4';
      default: return '#2563EB';
    }
  };

  return (
    <div className="glass-card" style={{ background: '#FFFFFF', border: '1px solid #E2E8F0' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
        <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#64748B' }}>{title}</span>
        {Icon && (
          <div style={{
            padding: '0.5rem',
            borderRadius: '10px',
            background: getIconBg(),
            border: `1px solid ${getIconBg()}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Icon size={20} color={getIconColor()} />
          </div>
        )}
      </div>

      <div style={{ fontSize: '1.85rem', fontWeight: 800, color: '#0F172A', marginBottom: '0.35rem' }}>
        {value}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '0.78rem' }}>
        <span style={{ color: '#64748B' }}>{subtext}</span>
        {trend && (
          <span style={{ color: trend.startsWith('+') ? '#10B981' : '#EF4444', fontWeight: 700 }}>
            {trend}
          </span>
        )}
      </div>
    </div>
  );
};

