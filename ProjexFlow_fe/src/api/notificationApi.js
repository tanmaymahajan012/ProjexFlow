import axios from 'axios';

const nmsApi = axios.create({
    baseURL: '/nms',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
nmsApi.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * Fetch all notifications for the current user
 */
export const getNotifications = async () => {
    const response = await nmsApi.get('/api/v1/notifications');
    return response.data;
};

/**
 * Mark a notification as read
 */
export const markNotificationAsRead = async (notificationId) => {
    const response = await nmsApi.patch(`/api/v1/notifications/${notificationId}/read`);
    return response.data;
};

/**
 * Delete a notification permanently
 */
export const deleteNotification = async (notificationId) => {
    const response = await nmsApi.delete(`/api/v1/notifications/${notificationId}`);
    return response.data;
};

export default nmsApi;

