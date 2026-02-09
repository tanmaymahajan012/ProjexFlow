import React from 'react';
import { useAuth } from '../context/AuthContext';
import DashboardLayout from '../layout/DashboardLayout';

const Profile = () => {
    const { user, role } = useAuth();

    // Filter out JWT token properties and fields not to display
    const excludedKeys = ['id', 'userId', 'token', 'email', 'role', 'iat', 'exp', 'profilePhotoUrl', 'createdAt', 'updatedAt', 'active'];

    // Get user info fields
    const getUserInfo = () => {
        if (!user) return {};

        const filteredUser = {};
        Object.keys(user).forEach(key => {
            if (!excludedKeys.includes(key)) {
                filteredUser[key] = user[key];
            }
        });
        return filteredUser;
    };

    const userInfo = getUserInfo();

    // Format field names for display
    const formatFieldName = (key) => {
        return key
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    };

    // Get icon for field
    const getFieldIcon = (key) => {
        const iconMap = {
            fullName: '👤',
            name: '👤',
            email: '📧',
            phone: '📱',
            department: '🏢',
            course: '📚',
            empId: '🆔',
            rollNo: '🎓',
            prn: '📋',
            batchId: '📅',
            githubUrl: '💻',
            profilePhotoUrl: '🖼️',
            active: '✓'
        };
        return iconMap[key] || '📌';
    };

    // Format value for display
    const formatValue = (key, value) => {
        if (typeof value === 'boolean') {
            return value ? 'Yes' : 'No';
        }
        if (key === 'githubUrl' || key === 'profilePhotoUrl') {
            return (
                <a
                    href={value}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                        color: 'var(--primary)',
                        textDecoration: 'none',
                        wordBreak: 'break-all'
                    }}
                    onMouseEnter={(e) => e.target.style.textDecoration = 'underline'}
                    onMouseLeave={(e) => e.target.style.textDecoration = 'none'}
                >
                    {value}
                </a>
            );
        }
        return value;
    };

    return (
        <DashboardLayout>
            <div style={{ maxWidth: '900px', margin: '0 auto' }}>
                {/* Page Header */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        My Profile
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        View your personal information and account details
                    </p>
                </div>

                {/* Profile Card */}
                <div style={{
                    background: 'var(--card-bg)',
                    borderRadius: 'var(--radius)',
                    border: '1px solid var(--border-color)',
                    overflow: 'hidden'
                }}>
                    {/* Profile Header with Gradient */}
                    <div style={{
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        padding: '3rem 2rem',
                        position: 'relative',
                        overflow: 'hidden'
                    }}>
                        {/* Decorative circles */}
                        <div style={{
                            position: 'absolute',
                            top: '-50px',
                            right: '-50px',
                            width: '200px',
                            height: '200px',
                            borderRadius: '50%',
                            background: 'rgba(255, 255, 255, 0.1)',
                            filter: 'blur(40px)'
                        }} />
                        <div style={{
                            position: 'absolute',
                            bottom: '-30px',
                            left: '-30px',
                            width: '150px',
                            height: '150px',
                            borderRadius: '50%',
                            background: 'rgba(255, 255, 255, 0.1)',
                            filter: 'blur(30px)'
                        }} />

                        {/* Profile Content */}
                        <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '2rem',
                            position: 'relative',
                            zIndex: 1
                        }}>
                            {/* Avatar */}
                            <div style={{
                                position: 'relative',
                                width: '120px',
                                height: '120px'
                            }}>
                                <div style={{
                                    width: '120px',
                                    height: '120px',
                                    borderRadius: '50%',
                                    background: user?.profilePhotoUrl ? `url(${user.profilePhotoUrl})` : 'white',
                                    backgroundSize: 'cover',
                                    backgroundPosition: 'center',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontSize: '3rem',
                                    fontWeight: '700',
                                    color: '#667eea',
                                    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.3)',
                                    border: '4px solid rgba(255, 255, 255, 0.3)'
                                }}>
                                    {!user?.profilePhotoUrl && (
                                        user?.fullName?.charAt(0)?.toUpperCase() ||
                                        user?.name?.charAt(0)?.toUpperCase() ||
                                        user?.email?.charAt(0)?.toUpperCase() ||
                                        'U'
                                    )}
                                </div>
                                {/* Status Indicator Dot */}
                                <div style={{
                                    position: 'absolute',
                                    bottom: '5px',
                                    right: '5px',
                                    width: '20px',
                                    height: '20px',
                                    borderRadius: '50%',
                                    background: user?.active ? '#10b981' : '#ef4444',
                                    border: '3px solid white',
                                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.2)'
                                }} />
                            </div>

                            {/* Name and Role */}
                            <div>
                                <h2 style={{
                                    fontSize: '2rem',
                                    fontWeight: '700',
                                    marginBottom: '0.5rem',
                                    color: 'white',
                                    textShadow: '0 2px 4px rgba(0, 0, 0, 0.2)'
                                }}>
                                    {user?.fullName || user?.name || user?.email?.split('@')[0] || 'User'}
                                </h2>
                                <div style={{
                                    display: 'inline-block',
                                    padding: '0.5rem 1.25rem',
                                    background: 'rgba(255, 255, 255, 0.2)',
                                    backdropFilter: 'blur(10px)',
                                    borderRadius: '20px',
                                    color: 'white',
                                    fontSize: '0.875rem',
                                    fontWeight: '600',
                                    border: '1px solid rgba(255, 255, 255, 0.3)'
                                }}>
                                    {role || 'User'}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Profile Information */}
                    <div style={{ padding: '2rem' }}>
                        {Object.keys(userInfo).length > 0 ? (
                            <>
                                <h3 style={{
                                    fontSize: '1.25rem',
                                    fontWeight: '700',
                                    marginBottom: '1.5rem',
                                    color: 'var(--text-color)'
                                }}>
                                    Personal Information
                                </h3>

                                <div style={{
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
                                    gap: '1.5rem'
                                }}>
                                    {Object.entries(userInfo).map(([key, value]) => (
                                        <div
                                            key={key}
                                            style={{
                                                background: 'var(--bg)',
                                                padding: '1.25rem',
                                                borderRadius: 'var(--radius)',
                                                border: '1px solid var(--border-color)',
                                                transition: 'all 0.2s ease',
                                                cursor: 'default'
                                            }}
                                            onMouseEnter={(e) => {
                                                e.currentTarget.style.borderColor = 'var(--primary)';
                                                e.currentTarget.style.transform = 'translateY(-2px)';
                                                e.currentTarget.style.boxShadow = '0 4px 12px rgba(102, 126, 234, 0.15)';
                                            }}
                                            onMouseLeave={(e) => {
                                                e.currentTarget.style.borderColor = 'var(--border-color)';
                                                e.currentTarget.style.transform = 'translateY(0)';
                                                e.currentTarget.style.boxShadow = 'none';
                                            }}
                                        >
                                            <div style={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                gap: '0.75rem',
                                                marginBottom: '0.75rem'
                                            }}>
                                                <span style={{ fontSize: '1.5rem' }}>
                                                    {getFieldIcon(key)}
                                                </span>
                                                <label style={{
                                                    fontSize: '0.875rem',
                                                    fontWeight: '600',
                                                    color: 'var(--text-muted)',
                                                    textTransform: 'uppercase',
                                                    letterSpacing: '0.05em'
                                                }}>
                                                    {formatFieldName(key)}
                                                </label>
                                            </div>
                                            <div style={{
                                                fontSize: '1rem',
                                                fontWeight: '500',
                                                color: 'var(--text-color)',
                                                wordBreak: 'break-word'
                                            }}>
                                                {formatValue(key, value) || 'Not provided'}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </>
                        ) : (
                            <div style={{
                                textAlign: 'center',
                                padding: '3rem',
                                color: 'var(--text-muted)'
                            }}>
                                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📋</div>
                                <p style={{ fontSize: '1.125rem', fontWeight: '500' }}>
                                    No additional information available
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <style>{`
                @media (max-width: 768px) {
                    div[style*="display: flex"][style*="gap: 2rem"] {
                        flex-direction: column !important;
                        text-align: center;
                    }
                }
            `}</style>
        </DashboardLayout>
    );
};

export default Profile;
