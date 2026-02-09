import axios from 'axios';

// Create axios instance for TMS (Task Management Service)
const tmsApi = axios.create({
    baseURL: '/tms',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token
tmsApi.interceptors.request.use(
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
tmsApi.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            // Optional: Dispatch a logout event or redirect if implemented globally
            console.warn("Session expired or unauthorized");
        }
        return Promise.reject(error);
    }
);

// --- Student Task Endpoints ---

// A) List assignments
export const listStudentAssignments = async () => {
    const response = await tmsApi.get('/student/assignments');
    return response.data;
};

// B) Assignment detail
export const getAssignmentDetail = async (assignmentId) => {
    const response = await tmsApi.get(`/student/assignments/${assignmentId}`);
    return response.data;
};

// C) Submit work
export const submitAssignment = async (assignmentId, { repoUrl, prUrl }) => {
    const response = await tmsApi.post(`/student/assignments/${assignmentId}/submit`, {
        repoUrl,
        prUrl
    });
    return response.data;
};

// D) Submission history
export const listMySubmissions = async (assignmentId) => {
    const response = await tmsApi.get(`/student/assignments/${assignmentId}/submissions`);
    return response.data;
};

export default tmsApi;
