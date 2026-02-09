import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import { useNavigate } from 'react-router-dom';
import NotificationDropdown from './NotificationDropdown';

const Header = ({ toggleSidebar }) => {
    const { user, logout } = useAuth();
    const { unreadCount } = useNotification();
    const navigate = useNavigate();
    const [showNotifications, setShowNotifications] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const toggleNotifications = () => {
        setShowNotifications(!showNotifications);
    };

    return (
        <header className="header">
            <div style={{ display: 'flex', alignItems: 'center' }}>
                <button
                    onClick={toggleSidebar}
                    className="mobile-toggle"
                    style={{
                        display: 'none',
                        background: 'none',
                        border: 'none',
                        color: 'white',
                        fontSize: '1.5rem',
                        marginRight: '1rem',
                        cursor: 'pointer'
                    }}
                >
                    ☰
                </button>

            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                <div
                    className="notifications"
                    onClick={toggleNotifications}
                    style={{
                        cursor: 'pointer',
                        position: 'relative',
                        fontSize: '1.25rem',
                        padding: '0.5rem',
                        borderRadius: '8px',
                        transition: 'background 0.2s',
                    }}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.background = 'rgba(255, 255, 255, 0.1)';
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.background = 'transparent';
                    }}
                >
                    🔔
                    {unreadCount > 0 && (
                        <span style={{
                            position: 'absolute',
                            top: '0.25rem',
                            right: '0.25rem',
                            background: 'var(--error)',
                            borderRadius: '50%',
                            width: '8px',
                            height: '8px'
                        }}></span>
                    )}

                    <NotificationDropdown
                        isOpen={showNotifications}
                        onClose={() => setShowNotifications(false)}
                    />
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', borderLeft: '1px solid var(--border-color)', paddingLeft: '1.5rem' }}>
                    <div style={{ textAlign: 'right', display: 'none', md: 'block' }}>
                        <div style={{ fontSize: '0.875rem', fontWeight: '600' }}>{user?.name || user?.email?.split('@')[0]}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user?.role || 'User'}</div>
                    </div>
                    <div className='user-avatar'
                        onClick={() => navigate('/profile')}
                        style={{
                            width: '32px',
                            height: '32px',
                            borderRadius: '50%',
                            background: 'var(--primary)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 'bold',
                            cursor: 'pointer'
                        }}
                    >
                        {(user?.name?.[0] || user?.email?.[0] || 'U').toUpperCase()}
                    </div>
                    <button
                        onClick={handleLogout}
                        style={{
                            background: 'none',
                            border: 'none',
                            color: 'var(--text-muted)',
                            cursor: 'pointer',
                            fontSize: '0.875rem'
                        }}
                    >
                        Logout
                    </button>
                </div>
            </div>

            <style>{`
                @media (max-width: 768px) {
                    .mobile-toggle { display: block !important; }
                    .search-bar { display: none; }
                }
            `}</style>
        </header>
    );
};

export default Header;

