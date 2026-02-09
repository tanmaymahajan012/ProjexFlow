import React from 'react';

const StatCard = ({ label, value, icon, trend, trendValue }) => {
    return (
        <div className="stat-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
                <span className="stat-label">{label}</span>
                <span style={{ fontSize: '1.25rem' }}>{icon}</span>
            </div>
            <div className="stat-value">{value}</div>
            {trend && (
                <div style={{ marginTop: '0.5rem', fontSize: '0.75rem', color: trend === 'up' ? 'var(--success)' : 'var(--error)' }}>
                    {trend === 'up' ? '↑' : '↓'} {trendValue} <span style={{ color: 'var(--text-muted)' }}>vs last month</span>
                </div>
            )}
        </div>
    );
};

export default StatCard;
