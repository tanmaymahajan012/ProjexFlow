import axios from 'axios';

// Create axios instance for ALS (Activity Log Service)
const alsApi = axios.create({
    baseURL: '/als',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
alsApi.interceptors.request.use(
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

// Response interceptor
alsApi.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// --- Activity Log Endpoints ---

// Placeholder or generic logs if endpoint known. 
// User mentioned: "Activity Logs request currently hits a wrong path and sometimes 404"
// User suggestion: "Placeholder until endpoint confirmed"
// We will export a function that tries to hit a likely endpoint but frontend will handle 404.

export const getMyActivityLogs = async () => {
    // Assuming a standard path, but frontend will mask 404
    const response = await alsApi.get('/logs/my');
    return response.data;
};

export default alsApi;
