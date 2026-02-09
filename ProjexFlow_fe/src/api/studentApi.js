import axios from 'axios';

// Create axios instance for UMS service
const umsApi = axios.create({
    baseURL: '/ums',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
umsApi.interceptors.request.use(
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

// API functions
export const fetchAllStudents = async () => {
    const response = await umsApi.get('/api/v1/students');
    return response.data;
};

export const updateStudentActiveStatus = async (studentId, active) => {
    const response = await umsApi.patch(`/api/v1/admins/students/${studentId}/active`, {
        active,
    });
    return response.data;
};

export const createStudent = async (studentData) => {
    const response = await umsApi.post('/api/v1/students', studentData);
    return response.data;
};
