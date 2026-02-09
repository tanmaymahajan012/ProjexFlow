import axios from 'axios';

// Create axios instance for PMS (Project Management Service)
const pmsApi = axios.create({
    baseURL: '/pms',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
pmsApi.interceptors.request.use(
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

// Response interceptor to handle global errors like 401
pmsApi.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            console.warn("Session expired or unauthorized");
        }
        return Promise.reject(error);
    }
);

// --- Student Project Endpoints ---

// Get My Project
export const getMyProject = async () => {
    const response = await pmsApi.get('/projects/my');
    return response.data;
};

export default pmsApi;
