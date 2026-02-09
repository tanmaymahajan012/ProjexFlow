import axios from 'axios';

// Create axios instance for GMS (Group Management Service) on port 8080
const gmsApi = axios.create({
    baseURL: '/gms',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Create axios instance for UMS service
const umsApi = axios.create({
    baseURL: '/ums',
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
addAuthInterceptor(gmsApi);
addAuthInterceptor(umsApi);

// Get grouping status for a batch
export const getGroupingStatus = async (batchId) => {
    const response = await gmsApi.get(`/internal/batches/${batchId}/grouping-status`);
    return response.data;
};

// Update grouping status for a batch
export const updateGroupingStatus = async (batchId, status) => {
    const response = await gmsApi.put(`/internal/batches/${batchId}/grouping-status`, {
        status
    });
    return response.data;
};

// Get list of batch IDs
export const getBatchIds = async () => {
    const response = await umsApi.get('/api/v1/students/batch-ids');
    return response.data;
};

// Get groups for a specific batch
export const getGroupsByBatch = async (batchId) => {
    const response = await gmsApi.get(`/batches/${batchId}/groups`);
    return response.data;
};

// Get mentor groups for a specific batch (MAMS API)
export const getMentorGroupsByBatch = async (batchId) => {
    const response = await axios.get(`/mams/mentor/batches/${batchId}/groups`, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
        }
    });
    return response.data;
};

// Get my group for a specific batch/student
export const getMyGroup = async (batchId, studentId) => {
    // Workaround: Fetch all groups for the batch and find the one the user belongs to
    // This avoids the 500 error from the non-existent /active-group endpoint
    try {
        const response = await gmsApi.get(`/batches/${batchId}/groups`);
        const allGroups = response.data;

        if (!studentId) return null;

        // Find group where members list contains the studentId
        return allGroups.find(group =>
            group.members && group.members.some(m =>
                String(m.id) === String(studentId) ||
                String(m.studentId) === String(studentId) ||
                String(m.userId) === String(studentId)
            )
        );
    } catch (error) {
        console.error("Error fetching group in workaround:", error);
        throw error;
    }
};

// Send a group request to another student
export const sendGroupRequest = async (batchId, receiverEmail) => {
    const response = await gmsApi.post(`/requests`, {
        toEmail: receiverEmail
    });
    return response.data;
};

// Get incoming group requests
export const getIncomingRequests = async (batchId) => {
    const response = await gmsApi.get(`/batches/${batchId}/requests/incoming`);
    return response.data;
};

// Get sent group requests
export const getSentRequests = async (batchId) => {
    const response = await gmsApi.get(`/batches/${batchId}/requests/sent`);
    return response.data;
};

// Accept a group request
export const acceptRequest = async (requestId) => {
    const response = await gmsApi.post(`/requests/${requestId}/accept`);
    return response.data;
};

// Reject a group request
export const rejectRequest = async (requestId) => {
    const response = await gmsApi.post(`/requests/${requestId}/reject`);
    return response.data;
};

// Get specific group details
export const getGroupDetails = async (groupId) => {
    const response = await gmsApi.get(`/groups/${groupId}`);
    return response.data;
};


