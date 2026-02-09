import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getMyProject } from '../../api/pmsApi';
import Toast from '../../components/Toast';

const StudentProjects = () => {
    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);
    const [waitingMessage, setWaitingMessage] = useState(null); // 'NOT_CREATED'
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProject = async () => {
            setLoading(true);
            try {
                const data = await getMyProject();
                setProject(data);
            } catch (err) {
                console.error("Fetch project failed", err);
                // User Request: Check for 404 or specific "not created yet" text
                if (err.response && (err.response.status === 404 || (err.response.data && JSON.stringify(err.response.data).includes("not created yet")))) {
                    // Friendly handling: Project not ready yet
                    setWaitingMessage("Your project will be visible after your group creates it.");
                } else if (err.response && err.response.status === 403) {
                    setWaitingMessage("You do not have access to view this project.");
                } else {
                    // Show detailed error for debugging (User Request)
                    const details = err.response?.data ? JSON.stringify(err.response.data) : err.message;
                    setError(`Failed to load project. Details: ${details}`);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchProject();
    }, []);

    return (
        <DashboardLayout>
            <div className="p-8">
                <h1 className="text-2xl font-bold mb-4">My Project</h1>

                {loading ? (
                    <p>Loading project details...</p>
                ) : waitingMessage ? (
                    <div style={{ padding: '3rem', textAlign: 'center', background: 'var(--card-bg)', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>{waitingMessage}</p>
                    </div>
                ) : project ? (
                    <div className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold">{project.title}</h2>
                        <p className="mt-2 text-gray-600">{project.description}</p>
                        {/* Add more fields as needed */}
                    </div>
                ) : (
                    <div className="text-red-500">{error || "Project not found"}</div>
                )}
            </div>
        </DashboardLayout>
    );
};

export default StudentProjects;
