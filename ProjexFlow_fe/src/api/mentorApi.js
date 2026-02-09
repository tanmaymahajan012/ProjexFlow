import axios from 'axios';

// Create axios instance for port 8081 (mentor service)
const mentorApi = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Create axios instance for port 8080 (UMS service)
const umsApi = axios.create({
    baseURL: '/ums',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add Authorization token for both instances
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

addAuthInterceptor(mentorApi);
addAuthInterceptor(umsApi);

// API functions
export const fetchAllMentors = async () => {
    const response = await umsApi.get('/api/v1/mentors');
    return response.data;
};

export const updateMentorActiveStatus = async (mentorId, active) => {
    const response = await umsApi.patch(`/api/v1/admins/mentors/${mentorId}/active`, {
        active,
    });
    return response.data;
};

export const createMentor = async (mentorData) => {
    const response = await umsApi.post('/api/v1/mentors', mentorData);
    return response.data;
};
