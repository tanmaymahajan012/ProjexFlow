import axios from 'axios';

// Create axios instance for TMS (Task Management Service)
const tmsApi = axios.create({
    baseURL: '/tms',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Create axios instance for MAMS (Mentor Assignment Management Service)
const mamsApi = axios.create({
    baseURL: '/mams',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
const addAuthInterceptor = (instance) => {
    instance.interceptors.request.use(
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
};

// Apply interceptors
addAuthInterceptor(tmsApi);
addAuthInterceptor(mamsApi);

// Create a new task
export const createTask = async (taskData) => {
    const response = await tmsApi.post('/mentor/tasks', taskData);
    return response.data;
};

// Get tasks by batch ID
export const getTasksByBatch = async (batchId) => {
    const response = await tmsApi.get(`/mentor/tasks?batchId=${batchId}`);
    return response.data;
};

// Assign task to all groups
export const assignTaskToAll = async (taskId, batchId) => {
    const response = await tmsApi.post(`/mentor/tasks/${taskId}/assign-all?batchId=${batchId}`);
    return response.data;
};

// Get group IDs for mentor by batch
export const getMentorGroupIds = async (mentorId, batchId) => {
    const response = await mamsApi.get(`/mentors/${mentorId}/batches/${batchId}/groups`);
    return response.data;
};

// Assign task to specific groups
export const assignTaskToGroups = async (taskId, assignmentData) => {
    const response = await tmsApi.post(`/mentor/tasks/${taskId}/assign`, assignmentData);
    return response.data;
};
