import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useNotification } from '../context/NotificationContext';
import { useAuth } from '../context/AuthContext';

const NotificationDropdown = ({ isOpen, onClose }) => {
    const { notifications, markAsRead, deleteNotification } = useNotification();
    const { role } = useAuth();
    const navigate = useNavigate();
    const dropdownRef = useRef(null);

    // Close dropdown when clicking outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                onClose();
            }
        };

        if (isOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isOpen, onClose]);

    // Get navigation path based on notification type and user role
    const getNavigationPath = (notification) => {
        const type = notification.type;
        const userRole = role?.toUpperCase();

        switch (type) {
            case 'GROUP_INVITE':
            case 'GROUP_INVITE_ACCEPTED':
            case 'GROUP_INVITE_REJECTED':
                return userRole === 'STUDENT' ? '/student/team' : null;

            case 'TASK_ASSIGNED':
            case 'TASK_REVIEWED':
                return userRole === 'STUDENT' ? '/student/tasks' : null;

            case 'TASK_SUBMITTED':
                return userRole === 'MENTOR' ? '/mentor/submissions' : null;

            default:
                return null;
        }
    };

    const handleNotificationClick = async (notification) => {
        try {
            // Debug: Log notification details
            console.log('Notification clicked:', {
                type: notification.type,
                role: role,
                notification: notification
            });

            // 1. Try to mark as read (but don't block if it fails)
            if (!notification.isRead) {
                try {
                    await markAsRead(notification.id);
                    console.log('✅ Marked as read');
                } catch (error) {
                    console.warn('⚠️ Failed to mark as read (continuing anyway):', error.message);
                    // Continue anyway - navigation is more important
                }
            }

            // 2. Close dropdown immediately for better UX
            onClose();

            // 3. Navigate to relevant page
            const path = getNavigationPath(notification);
            console.log('Navigation path:', path);

            if (path) {
                navigate(path);
            } else {
                console.warn('No navigation path found for notification type:', notification.type);
            }

            // 4. Delete notification after viewing (TEMPORARILY DISABLED - restart NMS to enable)
            // Uncomment after restarting NMS service
            /*
            setTimeout(async () => {
                try {
                    await deleteNotification(notification.id);
                    console.log('✅ Notification deleted');
                } catch (err) {
                    console.error('❌ Failed to delete notification:', err);
                }
            }, 500);
            */
            console.log('ℹ️ Delete disabled - restart NMS to enable notification deletion');
        } catch (error) {
            console.error('Failed to handle notification click:', error);
        }
    };

    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins}m ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        if (diffDays < 7) return `${diffDays}d ago`;
        return date.toLocaleDateString();
    };

    const getNotificationIcon = (type) => {
        switch (type) {
            case 'GROUP_INVITE':
            case 'GROUP_INVITE_ACCEPTED':
            case 'GROUP_INVITE_REJECTED':
                return '👥';
            case 'TASK_ASSIGNED':
            case 'TASK_SUBMITTED':
            case 'TASK_REVIEWED':
                return '📋';
            default:
                return '🔔';
        }
    };

    if (!isOpen) return null;

    return (
        <div
            ref={dropdownRef}
            style={{
                position: 'absolute',
                top: '100%',
                right: 0,
                marginTop: '0.5rem',
                width: '400px',
                maxHeight: '500px',
                background: 'var(--card-bg)',
                border: '1px solid var(--border-color)',
                borderRadius: 'var(--radius)',
                boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
                zIndex: 1000,
                overflow: 'hidden',
                display: 'flex',
                flexDirection: 'column',
            }}
        >
            {/* Header */}
            <div
                style={{
                    padding: '1rem',
                    borderBottom: '1px solid var(--border-color)',
                    fontWeight: '600',
                    fontSize: '1rem',
                }}
            >
                Notifications
            </div>

            {/* Notification List */}
            <div
                style={{
                    overflowY: 'auto',
                    maxHeight: '450px',
                }}
            >
                {notifications.length === 0 ? (
                    <div
                        style={{
                            padding: '3rem 1.5rem',
                            textAlign: 'center',
                            color: 'var(--text-muted)',
                        }}
                    >
                        <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>🔔</div>
                        <p>No notifications</p>
                    </div>
                ) : (
                    notifications.map((notification) => (
                        <div
                            key={notification.id}
                            onClick={() => handleNotificationClick(notification)}
                            style={{
                                padding: '1rem',
                                borderBottom: '1px solid var(--border-color)',
                                cursor: 'pointer',
                                background: notification.isRead ? 'transparent' : 'rgba(59, 130, 246, 0.05)',
                                transition: 'background 0.2s',
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.background = 'var(--hover-bg)';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.background = notification.isRead
                                    ? 'transparent'
                                    : 'rgba(59, 130, 246, 0.05)';
                            }}
                        >
                            <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'start' }}>
                                {/* Icon */}
                                <div
                                    style={{
                                        fontSize: '1.5rem',
                                        flexShrink: 0,
                                    }}
                                >
                                    {getNotificationIcon(notification.type)}
                                </div>

                                {/* Content */}
                                <div style={{ flex: 1, minWidth: 0 }}>
                                    <div
                                        style={{
                                            fontWeight: notification.isRead ? '400' : '600',
                                            marginBottom: '0.25rem',
                                            fontSize: '0.9375rem',
                                        }}
                                    >
                                        {notification.title}
                                    </div>
                                    <div
                                        style={{
                                            fontSize: '0.875rem',
                                            color: 'var(--text-muted)',
                                            marginBottom: '0.5rem',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            display: '-webkit-box',
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: 'vertical',
                                        }}
                                    >
                                        {notification.message}
                                    </div>
                                    <div
                                        style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)',
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.5rem',
                                        }}
                                    >
                                        <span>{formatTime(notification.createdAt)}</span>
                                        {!notification.isRead && (
                                            <span
                                                style={{
                                                    width: '6px',
                                                    height: '6px',
                                                    borderRadius: '50%',
                                                    background: '#3b82f6',
                                                }}
                                            />
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default NotificationDropdown;
