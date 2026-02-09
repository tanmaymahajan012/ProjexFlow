import React from 'react';
import { useAuth } from '../context/AuthContext';
import DashboardLayout from '../layout/DashboardLayout';
import StatCard from '../components/StatCard';

const Dashboard = () => {
    const { user, role } = useAuth();

    const stats = [
        { label: 'Total Projects', value: '12', icon: '📁', trend: 'up', trendValue: '15%' },
        { label: 'Active Tasks', value: '24', icon: '📝', trend: 'down', trendValue: '3%' },
        { label: 'Team Members', value: '8', icon: '👥' },
        { label: 'Completed', value: '85%', icon: '✅', trend: 'up', trendValue: '5%' },
    ];

    const recentActivities = [
        { id: 1, type: 'comment', user: 'Jane Doe', project: 'Design System', time: '2h ago', content: 'Added new icons to the library' },
        { id: 2, type: 'upload', user: 'John Smith', project: 'API Integration', time: '4h ago', content: 'Uploaded documentation' },
        { id: 3, type: 'status', user: 'Alex Lee', project: 'Mobile App', time: '1d ago', content: 'Moved task to "Done"' },
    ];

    return (
        <DashboardLayout>
            <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>Welcome back, {user?.name || user?.email?.split('@')[0]}!</h1>
                <p style={{ color: 'var(--text-muted)' }}>Here's what's happening with your projects today.</p>
            </div>

            <div className="stats-grid">
                {stats.map((stat, index) => (
                    <StatCard key={index} {...stat} />
                ))}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginTop: '2rem' }}>
                <div style={{ background: 'var(--card-bg)', padding: '1.5rem', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                        <h2 style={{ fontSize: '1.25rem', fontWeight: '600' }}>Recent Activity</h2>
                        <button style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontSize: '0.875rem' }}>View All</button>
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                        {recentActivities.map((activity) => (
                            <div key={activity.id} style={{ display: 'flex', gap: '1rem' }}>
                                <div style={{ width: '32px', height: '32px', borderRadius: '50%', background: '#374151', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1rem' }}>
                                    {activity.type === 'comment' ? '💬' : activity.type === 'upload' ? '📁' : '✅'}
                                </div>
                                <div style={{ flex: 1 }}>
                                    <div style={{ fontSize: '0.875rem' }}>
                                        <span style={{ fontWeight: '600' }}>{activity.user}</span> in <span style={{ fontWeight: '600', color: 'var(--primary)' }}>{activity.project}</span>
                                    </div>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.25rem' }}>{activity.content}</div>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem', marginTop: '0.25rem' }}>{activity.time}</div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div style={{ background: 'var(--card-bg)', padding: '1.5rem', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                        <h2 style={{ fontSize: '1.25rem', fontWeight: '600' }}>My Projects</h2>
                        <button className="btn" style={{ width: 'auto', padding: '0.5rem 1rem', fontSize: '0.875rem' }}>+ New Project</button>
                    </div>
                    <div style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem 0' }}>
                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>🏗️</div>
                        <p>No project data available yet.</p>
                        <p style={{ fontSize: '0.875rem' }}>Complete your profile to get started.</p>
                    </div>
                </div>
            </div>

            <style>{`
                @media (max-width: 1024px) {
                    div[style*="grid-template-columns: 1fr 1fr"] {
                        grid-template-columns: 1fr !important;
                    }
                }
            `}</style>
        </DashboardLayout>
    );
};

export default Dashboard;
