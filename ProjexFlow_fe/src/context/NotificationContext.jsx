import { createContext, useState, useContext, useEffect, useCallback, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getNotifications, markNotificationAsRead, deleteNotification as deleteNotificationApi } from '../api/notificationApi';
import { useAuth } from './AuthContext';

const NotificationContext = createContext();

export const NotificationProvider = ({ children }) => {
    const { user, role, token } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [stompClient, setStompClient] = useState(null);

    // Track processed notification IDs to prevent duplicates
    const processedNotificationIds = useRef(new Set());

    // Calculate unread count whenever notifications change
    useEffect(() => {
        const count = notifications.filter(n => !n.isRead).length;
        console.log(`Unread count updated: ${count} (Total notifications: ${notifications.length})`);
        setUnreadCount(count);
    }, [notifications]);

    // Fetch notifications when user logs in
    useEffect(() => {
        if (user && token) {
            fetchNotifications();
        } else {
            // Clear notifications on logout
            setNotifications([]);
            setUnreadCount(0);
            processedNotificationIds.current.clear();
        }
    }, [user, token]);

    // WebSocket connection management
    useEffect(() => {
        if (user && token && role) {
            connectWebSocket();
        } else {
            disconnectWebSocket();
        }

        // Cleanup on unmount
        return () => {
            disconnectWebSocket();
        };
    }, [user, token, role]);

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            console.log('=== FETCHING NOTIFICATIONS ===');
            console.log('User:', user);
            console.log('Role:', role);
            console.log('Token:', token ? 'Present' : 'Missing');

            const data = await getNotifications();

            console.log(`✅ Fetched ${data.length} notifications from database`);
            console.log('Notifications:', data);
            console.log('Unread notifications:', data.filter(n => !n.isRead).length);

            setNotifications(data);
            // DON'T add to processedNotificationIds - this was causing notifications to disappear on refresh
            // The duplicate detection is only for real-time WebSocket messages, not for fetched notifications
        } catch (error) {
            console.error('❌ Failed to fetch notifications:', error);
            console.error('Error details:', error.response?.data || error.message);
        } finally {
            setLoading(false);
        }
    };

    const connectWebSocket = () => {
        try {
            // Create SockJS connection directly to NMS (API Gateway doesn't support WebSocket routing)
            const socket = new SockJS('http://localhost:9131/ws');

            // Create STOMP client
            const client = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`,
                },
                debug: (str) => {
                    console.log('STOMP Debug:', str);
                },
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            });

            client.onConnect = () => {
                console.log('WebSocket Connected');

                // Subscribe to user-specific notification queue
                client.subscribe('/user/queue/notifications', (message) => {
                    try {
                        const notification = JSON.parse(message.body);
                        console.log('Received notification:', notification);

                        // Prevent duplicate notifications
                        if (processedNotificationIds.current.has(notification.id)) {
                            console.log('Duplicate notification ignored:', notification.id);
                            return;
                        }

                        // Mark as processed
                        processedNotificationIds.current.add(notification.id);

                        // Add new notification to the list
                        setNotifications(prev => [notification, ...prev]);

                        // Optional: Show browser notification
                        if ('Notification' in window && Notification.permission === 'granted') {
                            new Notification(notification.title, {
                                body: notification.message,
                                icon: '/favicon.ico',
                            });
                        }
                    } catch (error) {
                        console.error('Error processing notification:', error);
                    }
                });
            };

            client.onStompError = (frame) => {
                console.error('STOMP error:', frame);
            };

            client.onWebSocketClose = () => {
                console.log('WebSocket Disconnected');
            };

            client.activate();
            setStompClient(client);
        } catch (error) {
            console.error('Failed to connect WebSocket:', error);
        }
    };

    const disconnectWebSocket = () => {
        if (stompClient) {
            stompClient.deactivate();
            setStompClient(null);
            console.log('WebSocket disconnected');
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await markNotificationAsRead(notificationId);

            // Update local state
            setNotifications(prev =>
                prev.map(n =>
                    n.id === notificationId
                        ? { ...n, isRead: true, readAt: new Date().toISOString() }
                        : n
                )
            );
        } catch (error) {
            console.error('Failed to mark notification as read:', error);
            throw error;
        }
    };

    const deleteNotification = async (notificationId) => {
        try {
            await deleteNotificationApi(notificationId);

            // Remove from local state
            setNotifications(prev => prev.filter(n => n.id !== notificationId));

            // Remove from processed IDs
            processedNotificationIds.current.delete(notificationId);
        } catch (error) {
            console.error('Failed to delete notification:', error);
            throw error;
        }
    };

    // Request browser notification permission
    useEffect(() => {
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
    }, []);

    const value = {
        notifications,
        unreadCount,
        loading,
        markAsRead,
        deleteNotification,
        refreshNotifications: fetchNotifications,
    };

    return (
        <NotificationContext.Provider value={value}>
            {children}
        </NotificationContext.Provider>
    );
};

export const useNotification = () => {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error('useNotification must be used within NotificationProvider');
    }
    return context;
};

