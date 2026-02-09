import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { useAuth } from '../../context/AuthContext';
import {
    listStudentAssignments,
    getAssignmentDetail,
    submitAssignment,
    listMySubmissions
} from '../../api/tmsApi';
import Toast from '../../components/Toast';

const StudentTasks = () => {
    const { user } = useAuth();
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [waitingState, setWaitingState] = useState(null); // 'NO_GROUP' | 'MENTOR_PENDING' | 'GENERIC_WAIT'
    const [debugMsg, setDebugMsg] = useState(''); // New state for debugging 403 reasons
    const [errorData, setErrorData] = useState(null);
    const [toast, setToast] = useState(null);
    const [filter, setFilter] = useState('ALL');
    const [sortOrder, setSortOrder] = useState('DUE_AT');

    // Detailed view states
    const [expandedId, setExpandedId] = useState(null);
    const [detailData, setDetailData] = useState({});
    const [historyData, setHistoryData] = useState({});
    const [loadingDetails, setLoadingDetails] = useState({});

    // Submission form states
    const [submissionForms, setSubmissionForms] = useState({});
    const [submitting, setSubmitting] = useState({});

    useEffect(() => {
        fetchAssignments();
    }, []);

    const fetchAssignments = async () => {
        setLoading(true);
        setErrorData(null);
        setWaitingState(null);

        try {
            const data = await listStudentAssignments();
            setAssignments(data || []);
        } catch (error) {
            console.error("Failed to fetch assignments", error);
            if (error.response) {
                if (error.response.status === 403) {
                    // Friendly 403 Handling
                    const msg = error.response.data?.message || "";
                    if (msg.includes("not assigned to any active group")) {
                        setWaitingState('NO_GROUP');
                    } else if (msg.includes("grouping") || msg.toLowerCase().includes("grouping is not completed")) {
                        setWaitingState('GROUPING_OPEN');
                    } else if (msg.includes("mentor")) { // Heuristic: if message mentions mentor
                        setWaitingState('MENTOR_PENDING');
                    } else {
                        setWaitingState('GENERIC_WAIT');
                    }
                    setAssignments([]);
                } else if (error.response.status === 401) {
                    setToast({ message: "Session expired, please login again", type: "error" });
                    // Redirect logic usually handled by AuthContext or global interceptor, but good to show toast
                } else {
                    setErrorData({ type: 'other', message: `Failed to load tasks. Status: ${error.response.status}` });
                    setToast({ message: "Service unavailable", type: "error" });
                }
            } else {
                setErrorData({ type: 'other', message: "Network error." });
                setToast({ message: "Service unavailable", type: "error" });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleExpand = async (assignmentId) => {
        if (expandedId === assignmentId) {
            setExpandedId(null);
            return;
        }
        setExpandedId(assignmentId);

        if (!detailData[assignmentId]) {
            await fetchDetail(assignmentId);
        }
    };

    const fetchDetail = async (id) => {
        setLoadingDetails(prev => ({ ...prev, [id]: true }));
        try {
            const detail = await getAssignmentDetail(id);
            setDetailData(prev => ({ ...prev, [id]: detail }));

            if (detail.latestSubmission) {
                setSubmissionForms(prev => ({
                    ...prev,
                    [id]: {
                        repoUrl: detail.latestSubmission.repoUrl || '',
                        prUrl: detail.latestSubmission.prUrl || ''
                    }
                }));
            }
        } catch (error) {
            console.error("Failed to fetch detail", error);
            setToast({ message: "Failed to load task details", type: "error" });
        } finally {
            setLoadingDetails(prev => ({ ...prev, [id]: false }));
        }
    };

    const fetchHistory = async (id) => {
        try {
            const history = await listMySubmissions(id);
            setHistoryData(prev => ({ ...prev, [id]: history }));
        } catch (error) {
            console.error("Failed to fetch history", error);
            setToast({ message: "Failed to load submission history", type: "error" });
        }
    };

    const handleSubmissionChange = (id, field, value) => {
        setSubmissionForms(prev => ({
            ...prev,
            [id]: { ...prev[id], [field]: value }
        }));
    };

    const handleSubmit = async (id) => {
        const form = submissionForms[id] || {};
        const { repoUrl, prUrl } = form;

        if (!repoUrl && !prUrl) {
            setToast({ message: "Please provide at least a Repo URL or a PR URL.", type: "warning" });
            return;
        }
        if ((repoUrl && !repoUrl.includes('github.com')) || (prUrl && !prUrl.includes('github.com'))) {
            setToast({ message: "URLs must be valid GitHub links.", type: "warning" });
            return;
        }

        setSubmitting(prev => ({ ...prev, [id]: true }));
        try {
            await submitAssignment(id, { repoUrl, prUrl });
            setToast({ message: "Submitted. Waiting for mentor review.", type: "success" });

            // Optimistic update or refresh
            await fetchDetail(id);
            fetchAssignments();
        } catch (error) {
            console.error("Submission failed", error);
            const msg = error.response?.data?.message || "Failed to submit work";
            setToast({ message: msg, type: "error" });
        } finally {
            setSubmitting(prev => ({ ...prev, [id]: false }));
        }
    };

    // --- Helpers ---
    const getStatusStyle = (state) => {
        switch (state) {
            case 'NOT_SUBMITTED': return { bg: '#f1f5f9', color: '#64748b', label: 'Not Submitted' };
            case 'PENDING_REVIEW': return { bg: '#fff7ed', color: '#c2410c', label: 'In Review' };
            case 'CHANGES_REQUESTED': return { bg: '#fef2f2', color: '#dc2626', label: 'Changes Requested' };
            case 'VERIFIED': return { bg: '#ecfccb', color: '#4d7c0f', label: 'Verified' };
            case 'REJECTED': return { bg: '#fee2e2', color: '#991b1b', label: 'Rejected' };
            default: return { bg: '#f1f5f9', color: '#64748b', label: state };
        }
    };

    const canSubmit = (state) => {
        return state === 'NOT_SUBMITTED' || state === 'CHANGES_REQUESTED';
    };

    const getFilteredAssignments = () => {
        return assignments.filter(task => {
            if (filter === 'ALL') return true;
            if (filter === 'PENDING_ACTION') return task.state === 'NOT_SUBMITTED' || task.state === 'CHANGES_REQUESTED';
            if (filter === 'WAITING_REVIEW') return task.state === 'PENDING_REVIEW';
            if (filter === 'DONE') return task.state === 'VERIFIED' || task.state === 'REJECTED';
            return true;
        });
    };

    const sortedAssignments = getFilteredAssignments().sort((a, b) => {
        if (sortOrder === 'DUE_AT') {
            return new Date(a.dueAt) - new Date(b.dueAt);
        } else {
            return a.state.localeCompare(b.state);
        }
    });

    // Render Waiting States
    const renderWaitingState = () => {
        let msg = "Tasks are not available yet.";
        if (waitingState === 'NO_GROUP') msg = "Task assignments will be available after group finalisation.";
        if (waitingState === 'GROUPING_OPEN') msg = "Grouping is still in progress. Tasks will be available once grouping is finalized.";
        if (waitingState === 'MENTOR_PENDING') msg = "Mentor assignment pending. Tasks will appear once assigned.";
        if (waitingState === 'GENERIC_WAIT') msg = "Tasks will be available once your mentor assigns them.";

        return (
            <div style={{ padding: '3rem', textAlign: 'center', background: 'var(--card-bg)', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>{msg}</p>
            </div>
        );
    };

    if (loading && !waitingState && assignments.length === 0) {
        return (
            <DashboardLayout>
                <div className="p-8 text-center text-gray-500">Loading tasks...</div>
            </DashboardLayout>
        );
    }

    return (
        <DashboardLayout>
            <div className="student-tasks-container">
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        My Tasks
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        Manage your assignments and submissions.
                    </p>
                </div>

                {waitingState ? (
                    renderWaitingState()
                ) : errorData ? (
                    <div style={{ padding: '2rem', textAlign: 'center', background: 'var(--card-bg)', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>{errorData.message}</p>
                        <button onClick={fetchAssignments} className="btn mt-4">Retry</button>
                    </div>
                ) : assignments.length === 0 ? (
                    <div style={{ padding: '3rem', textAlign: 'center', background: 'var(--card-bg)', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)' }}>
                        <p style={{ color: 'var(--text-muted)' }}>No tasks have been assigned by your mentor yet.</p>
                    </div>
                ) : (
                    <>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                {['ALL', 'PENDING_ACTION', 'WAITING_REVIEW', 'DONE'].map(f => (
                                    <button
                                        key={f}
                                        onClick={() => setFilter(f)}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            borderRadius: '9999px',
                                            border: 'none',
                                            background: filter === f ? '#6366f1' : 'transparent',
                                            color: filter === f ? 'white' : 'var(--text-muted)',
                                            fontWeight: '500',
                                            cursor: 'pointer',
                                            fontSize: '0.875rem'
                                        }}
                                    >
                                        {f.replace('_', ' ')}
                                    </button>
                                ))}
                            </div>

                            <select
                                value={sortOrder}
                                onChange={(e) => setSortOrder(e.target.value)}
                                style={{ padding: '0.5rem', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)', background: 'var(--input-bg)', color: 'var(--text-color)' }}
                            >
                                <option value="DUE_AT">Sort by Due Date</option>
                                <option value="STATE">Sort by Status</option>
                            </select>
                        </div>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {sortedAssignments.map(task => {
                                const status = getStatusStyle(task.state);
                                const isExpanded = expandedId === task.id;
                                const detail = detailData[task.id];
                                const submissionForm = submissionForms[task.id] || { repoUrl: '', prUrl: '' };
                                const loadingDetail = loadingDetails[task.id];

                                return (
                                    <div key={task.id} style={{
                                        background: 'var(--card-bg)',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        overflow: 'hidden'
                                    }}>
                                        <div
                                            onClick={() => handleExpand(task.id)}
                                            style={{
                                                padding: '1.25rem',
                                                display: 'flex',
                                                justifyContent: 'space-between',
                                                alignItems: 'center',
                                                cursor: 'pointer',
                                                background: isExpanded ? 'var(--bg)' : 'transparent'
                                            }}
                                        >
                                            <div>
                                                <h3 style={{ fontWeight: '600', fontSize: '1.1rem', marginBottom: '0.25rem' }}>{task.title}</h3>
                                                <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                                                    Due: {new Date(task.dueAt).toLocaleDateString()} {new Date(task.dueAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                                </div>
                                            </div>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                                <span style={{
                                                    padding: '0.25rem 0.75rem',
                                                    borderRadius: '9999px',
                                                    fontSize: '0.75rem',
                                                    fontWeight: '600',
                                                    background: status.bg,
                                                    color: status.color,
                                                    textTransform: 'uppercase'
                                                }}>
                                                    {status.label}
                                                </span>
                                                <span style={{ fontSize: '1.25rem', transform: isExpanded ? 'rotate(180deg)' : 'rotate(0)', transition: 'transform 0.2s' }}>
                                                    ▼
                                                </span>
                                            </div>
                                        </div>

                                        {isExpanded && (
                                            <div style={{ padding: '1.5rem', borderTop: '1px solid var(--border-color)' }}>
                                                {loadingDetail ? (
                                                    <p>Loading details...</p>
                                                ) : (
                                                    <>
                                                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
                                                            <div>
                                                                <h4 style={{ fontSize: '0.9rem', fontWeight: '700', marginBottom: '1rem', textTransform: 'uppercase', color: 'var(--text-muted)' }}>
                                                                    Submission
                                                                </h4>

                                                                {canSubmit(task.state) ? (
                                                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                                                        <input
                                                                            type="text"
                                                                            placeholder="Repository URL (e.g., github.com/user/repo)"
                                                                            value={submissionForm.repoUrl || ''}
                                                                            onChange={(e) => handleSubmissionChange(task.id, 'repoUrl', e.target.value)}
                                                                            style={{ padding: '0.75rem', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)', background: 'var(--input-bg)', color: 'var(--text-color)' }}
                                                                        />
                                                                        <input
                                                                            type="text"
                                                                            placeholder="Pull Request URL (Optional)"
                                                                            value={submissionForm.prUrl || ''}
                                                                            onChange={(e) => handleSubmissionChange(task.id, 'prUrl', e.target.value)}
                                                                            style={{ padding: '0.75rem', borderRadius: 'var(--radius)', border: '1px solid var(--border-color)', background: 'var(--input-bg)', color: 'var(--text-color)' }}
                                                                        />
                                                                        <button
                                                                            className="btn btn-primary"
                                                                            disabled={submitting[task.id]}
                                                                            onClick={() => handleSubmit(task.id)}
                                                                            style={{ marginTop: '0.5rem' }}
                                                                        >
                                                                            {submitting[task.id] ? 'Submitting...' : 'Submit Work'}
                                                                        </button>
                                                                    </div>
                                                                ) : (
                                                                    <div style={{ padding: '1rem', background: 'var(--bg)', borderRadius: 'var(--radius)', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                                                                        {task.state === 'PENDING_REVIEW' && "Waiting for mentor review. You cannot make changes now."}
                                                                        {task.state === 'VERIFIED' && "Task verified! Good job."}
                                                                        {task.state === 'REJECTED' && "Task rejected. This decision is final."}
                                                                    </div>
                                                                )}
                                                            </div>

                                                            <div>
                                                                <h4 style={{ fontSize: '0.9rem', fontWeight: '700', marginBottom: '1rem', textTransform: 'uppercase', color: 'var(--text-muted)' }}>
                                                                    Mentor Feedback
                                                                </h4>
                                                                {detail?.latestSubmission?.mentorFeedback ? (
                                                                    <div style={{ padding: '1rem', background: '#fefce8', border: '1px solid #fde047', borderRadius: 'var(--radius)', color: '#854d0e' }}>
                                                                        <p style={{ fontWeight: '600' }}>Mentor said:</p>
                                                                        <p>{detail.latestSubmission.mentorFeedback}</p>
                                                                    </div>
                                                                ) : (
                                                                    <p style={{ color: 'var(--text-muted)' }}>No feedback yet.</p>
                                                                )}

                                                                <div style={{ marginTop: '2rem' }}>
                                                                    <button
                                                                        onClick={() => fetchHistory(task.id)}
                                                                        style={{ background: 'transparent', border: 'none', color: 'var(--primary)', textDecoration: 'underline', cursor: 'pointer', padding: 0 }}
                                                                    >
                                                                        View Submission History
                                                                    </button>

                                                                    {historyData[task.id] && (
                                                                        <div style={{ marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                                                            {historyData[task.id].map((sub, idx) => (
                                                                                <div key={idx} style={{ fontSize: '0.85rem', padding: '0.5rem', background: 'var(--bg)', borderRadius: 'var(--radius)' }}>
                                                                                    <span style={{ fontWeight: '600' }}>{new Date(sub.submittedAt).toLocaleDateString()}</span>:
                                                                                    <a href={sub.repoUrl} target="_blank" rel="noopener noreferrer" style={{ marginLeft: '0.5rem', color: 'var(--primary)' }}>Repo</a>
                                                                                    {sub.prUrl && <a href={sub.prUrl} target="_blank" rel="noopener noreferrer" style={{ marginLeft: '0.5rem', color: 'var(--primary)' }}>PR</a>}
                                                                                </div>
                                                                            ))}
                                                                            {historyData[task.id].length === 0 && <p style={{ fontSize: '0.85rem' }}>No history found.</p>}
                                                                        </div>
                                                                    )}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    </>
                )}

                {toast && (
                    <Toast
                        message={toast.message}
                        type={toast.type}
                        onClose={() => setToast(null)}
                    />
                )}
            </div>
        </DashboardLayout>
    );
};

export default StudentTasks;
