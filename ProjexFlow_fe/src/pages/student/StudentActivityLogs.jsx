import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getMyActivityLogs } from '../../api/alsApi';

const StudentActivityLogs = () => {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [placeholder, setPlaceholder] = useState(false);

    useEffect(() => {
        const fetchLogs = async () => {
            setLoading(true);
            try {
                const data = await getMyActivityLogs();
                // Handle Spring Page object (has .content) or plain list
                const list = data.content ? data.content : (Array.isArray(data) ? data : []);
                setLogs(list);
            } catch (err) {
                console.error("Activity logs fetch failed", err);
                setPlaceholder(true); // Still use placeholder for error state, but could be improved
            } finally {
                setLoading(false);
            }
        };

        fetchLogs();
    }, []);

    return (
        <DashboardLayout>
            <div className="p-8">
                <h1 className="text-2xl font-bold mb-4">Activity Logs</h1>

                {loading ? (
                    <p>Loading activity...</p>
                ) : placeholder || logs.length === 0 ? (
                    <div style={{ padding: '3rem', textAlign: 'center', background: 'var(--card-bg)', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
                            Activity logs will appear once your project activity starts.
                        </p>
                    </div>
                ) : (
                    <div>
                        {/* Render logs list here if/when data is available */}
                        {logs.map((log, i) => (
                            <div key={i} className="p-4 border-b">
                                {JSON.stringify(log)}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </DashboardLayout>
    );
};

export default StudentActivityLogs;
