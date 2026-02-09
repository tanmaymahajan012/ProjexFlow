import axios from 'axios';

// Create axios instance for TMS (Task Management Service) via API Gateway
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

// Get assignments list for mentor
export const getAssignments = async (batchId, state = null) => {
    let url = `/mentor/assignments?batchId=${batchId}`;
    if (state && state !== 'All') {
        url += `&state=${state}`;
    }
    const response = await tmsApi.get(url);
    return response.data;
};

// Get submissions for a specific assignment
export const getSubmissions = async (assignmentId) => {
    const response = await tmsApi.get(`/mentor/assignments/${assignmentId}/submissions`);
    return response.data;
};

// Submit review for an assignment
export const submitReview = async (assignmentId, reviewData) => {
    const response = await tmsApi.post(`/mentor/assignments/${assignmentId}/review`, reviewData);
    return response.data;
};
