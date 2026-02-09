import React, { useState } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getAssignments, getSubmissions, submitReview } from '../../api/submissionApi';
import Toast from '../../components/Toast';

const MentorSubmissions = () => {
    const [batchId, setBatchId] = useState('');
    const [stateFilter, setStateFilter] = useState('All');
    const [assignments, setAssignments] = useState([]);
    const [loadingAssignments, setLoadingAssignments] = useState(false);
    const [expandedAssignments, setExpandedAssignments] = useState({});
    const [submissionsByAssignment, setSubmissionsByAssignment] = useState({});
    const [submissionsLoading, setSubmissionsLoading] = useState({});
    const [reviewingAssignment, setReviewingAssignment] = useState(null);
    const [reviewLoading, setReviewLoading] = useState(false);
    const [reviewForm, setReviewForm] = useState({
        decision: 'VERIFIED',
        reasonCode: 'NEEDS_RECHECK',
        comments: ''
    });
    const [toast, setToast] = useState(null);
    const [error, setError] = useState(null);

    const stateOptions = [
        'All',
        'NOT_SUBMITTED',
        'PENDING_REVIEW',
        'CHANGES_REQUESTED',
        'VERIFIED',
        'REJECTED'
    ];

    const decisionOptions = ['VERIFIED', 'CHANGES_REQUESTED', 'REJECTED'];
    const reasonCodeOptions = [
        'NOT_AS_DIRECTED',
        'NEEDS_REUPLOAD',
        'NEEDS_RECHECK',
        'INCOMPLETE',
        'INVALID_LINK'
    ];

    const handleShowList = async () => {
        if (!batchId.trim()) return;

        try {
            setLoadingAssignments(true);
            setError(null);
            const data = await getAssignments(batchId, stateFilter);

            // Client-side filtering if state is not 'All' and backend doesn't filter
            let filteredData = data;
            if (stateFilter !== 'All') {
                filteredData = data.filter(a => a.state === stateFilter);
            }

            setAssignments(filteredData);
        } catch (err) {
            console.error('Failed to fetch assignments:', err);
            setError('Failed to load assignments. Please try again.');
            setToast({ message: 'Failed to load assignments', type: 'error' });
        } finally {
            setLoadingAssignments(false);
        }
    };

    const toggleAssignmentExpansion = async (assignmentId) => {
        const isExpanded = expandedAssignments[assignmentId];

        setExpandedAssignments(prev => ({
            ...prev,
            [assignmentId]: !isExpanded
        }));

        // Fetch submissions if expanding and not already cached
        if (!isExpanded && !submissionsByAssignment[assignmentId]) {
            try {
                setSubmissionsLoading(prev => ({ ...prev, [assignmentId]: true }));
                const data = await getSubmissions(assignmentId);
                setSubmissionsByAssignment(prev => ({
                    ...prev,
                    [assignmentId]: data
                }));
            } catch (err) {
                console.error('Failed to fetch submissions:', err);
                setToast({ message: 'Failed to load submissions', type: 'error' });
            } finally {
                setSubmissionsLoading(prev => ({ ...prev, [assignmentId]: false }));
            }
        }
    };

    const openReviewPanel = (assignmentId) => {
        setReviewingAssignment(assignmentId);
        setReviewForm({
            decision: 'VERIFIED',
            reasonCode: 'NEEDS_RECHECK',
            comments: ''
        });
    };

    const closeReviewPanel = () => {
        setReviewingAssignment(null);
        setReviewForm({
            decision: 'VERIFIED',
            reasonCode: 'NEEDS_RECHECK',
            comments: ''
        });
    };

    const handleSubmitReview = async () => {
        if (!reviewingAssignment) return;

        try {
            setReviewLoading(true);
            await submitReview(reviewingAssignment, reviewForm);
            setToast({ message: 'Review submitted successfully!', type: 'success' });
            closeReviewPanel();

            // Refresh assignments list
            await handleShowList();
        } catch (err) {
            console.error('Failed to submit review:', err);
            setToast({ message: 'Failed to submit review', type: 'error' });
        } finally {
            setReviewLoading(false);
        }
    };

    const getStateBadgeColor = (state) => {
        const colors = {
            NOT_SUBMITTED: '#6b7280',
            PENDING_REVIEW: '#f59e0b',
            CHANGES_REQUESTED: '#ef4444',
            VERIFIED: '#10b981',
            REJECTED: '#dc2626'
        };
        return colors[state] || '#6b7280';
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <DashboardLayout>
            <div className="mentor-submissions-container">
                {/* Page Header */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        Submissions
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        View and review student submissions
                    </p>
                </div>

                {/* Filter Card */}
                <div style={{
                    background: 'var(--card-bg)',
                    padding: '1.5rem',
                    borderRadius: 'var(--radius)',
                    border: '1px solid var(--border-color)',
                    marginBottom: '1.5rem'
                }}>
                    <div style={{
                        display: 'flex',
                        gap: '1rem',
                        alignItems: 'flex-end',
                        flexWrap: 'wrap'
                    }}>
                        {/* Batch ID Input */}
                        <div style={{ flex: '1', minWidth: '200px' }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                Batch ID
                            </label>
                            <input
                                type="text"
                                value={batchId}
                                onChange={(e) => setBatchId(e.target.value)}
                                placeholder="batch id"
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '1px solid var(--border-color)',
                                    borderRadius: 'var(--radius)',
                                    background: 'var(--input-bg)',
                                    color: 'var(--text-color)',
                                    fontSize: '0.875rem'
                                }}
                            />
                        </div>

                        {/* State Filter */}
                        <div style={{ flex: '1', minWidth: '200px' }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                State
                            </label>
                            <select
                                value={stateFilter}
                                onChange={(e) => setStateFilter(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '1px solid var(--border-color)',
                                    borderRadius: 'var(--radius)',
                                    background: 'var(--input-bg)',
                                    color: 'var(--text-color)',
                                    fontSize: '0.875rem'
                                }}
                            >
                                {stateOptions.map(option => (
                                    <option key={option} value={option}>
                                        {option.replace(/_/g, ' ')}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Show List Button */}
                        <button
                            onClick={handleShowList}
                            disabled={!batchId.trim() || loadingAssignments}
                            className="btn"
                            style={{
                                padding: '0.75rem 1.5rem',
                                fontSize: '0.875rem',
                                opacity: (!batchId.trim() || loadingAssignments) ? 0.5 : 1,
                                cursor: (!batchId.trim() || loadingAssignments) ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loadingAssignments ? 'Loading...' : 'Show List'}
                        </button>
                    </div>
                </div>

                {/* Error Message */}
                {error && (
                    <div style={{
                        background: '#dc2626',
                        color: 'white',
                        padding: '1rem',
                        borderRadius: 'var(--radius)',
                        marginBottom: '1.5rem'
                    }}>
                        {error}
                    </div>
                )}

                {/* Assignments Table */}
                {!loadingAssignments && assignments.length > 0 && (
                    <div style={{
                        background: 'var(--card-bg)',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        overflow: 'hidden'
                    }}>
                        <div style={{ overflowX: 'auto' }}>
                            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                                <thead>
                                    <tr style={{ background: '#1f2937' }}>
                                        <th style={tableHeaderStyle}>Task Title</th>
                                        <th style={tableHeaderStyle}>Group ID</th>
                                        <th style={tableHeaderStyle}>Due At</th>
                                        <th style={tableHeaderStyle}>State</th>
                                        <th style={tableHeaderStyle}>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {assignments.map((assignment) => (
                                        <React.Fragment key={assignment.assignmentId}>
                                            <tr style={{
                                                borderBottom: '1px solid var(--border-color)',
                                                transition: 'background 0.2s'
                                            }}
                                                onMouseEnter={(e) => e.currentTarget.style.background = '#1f2937'}
                                                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                                            >
                                                <td style={tableCellStyle}>
                                                    <strong>{assignment.taskTitle || 'N/A'}</strong>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    {assignment.groupId || 'N/A'}
                                                </td>
                                                <td style={tableCellStyle}>
                                                    {formatDate(assignment.dueAt)}
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <span style={{
                                                        padding: '0.25rem 0.75rem',
                                                        borderRadius: '12px',
                                                        fontSize: '0.75rem',
                                                        fontWeight: '600',
                                                        background: getStateBadgeColor(assignment.state),
                                                        color: 'white',
                                                        display: 'inline-block'
                                                    }}>
                                                        {assignment.state?.replace(/_/g, ' ')}
                                                    </span>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                                                        <button
                                                            onClick={() => toggleAssignmentExpansion(assignment.assignmentId)}
                                                            style={{
                                                                padding: '0.5rem 1rem',
                                                                fontSize: '0.75rem',
                                                                borderRadius: 'var(--radius)',
                                                                border: '1px solid var(--border-color)',
                                                                background: expandedAssignments[assignment.assignmentId]
                                                                    ? 'var(--primary)'
                                                                    : '#374151',
                                                                color: 'white',
                                                                cursor: 'pointer',
                                                                fontWeight: '500'
                                                            }}
                                                        >
                                                            {expandedAssignments[assignment.assignmentId] ? '▲ Hide' : '▼ View'} Submissions
                                                        </button>
                                                        <button
                                                            onClick={() => openReviewPanel(assignment.assignmentId)}
                                                            className="btn"
                                                            style={{
                                                                padding: '0.5rem 1rem',
                                                                fontSize: '0.75rem'
                                                            }}
                                                        >
                                                            Review Latest
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>

                                            {/* Expanded Submissions Row */}
                                            {expandedAssignments[assignment.assignmentId] && (
                                                <tr>
                                                    <td colSpan="5" style={{
                                                        padding: '1.5rem',
                                                        background: 'var(--bg)',
                                                        borderBottom: '1px solid var(--border-color)'
                                                    }}>
                                                        {submissionsLoading[assignment.assignmentId] ? (
                                                            <div style={{
                                                                textAlign: 'center',
                                                                padding: '2rem',
                                                                color: 'var(--text-muted)'
                                                            }}>
                                                                Loading submissions...
                                                            </div>
                                                        ) : submissionsByAssignment[assignment.assignmentId]?.length > 0 ? (
                                                            <div style={{
                                                                display: 'grid',
                                                                gap: '1rem',
                                                                gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))'
                                                            }}>
                                                                {submissionsByAssignment[assignment.assignmentId].map((submission) => (
                                                                    <div
                                                                        key={submission.submissionId}
                                                                        style={{
                                                                            background: 'var(--card-bg)',
                                                                            padding: '1rem',
                                                                            borderRadius: 'var(--radius)',
                                                                            border: '1px solid var(--border-color)'
                                                                        }}
                                                                    >
                                                                        <div style={{
                                                                            display: 'flex',
                                                                            alignItems: 'center',
                                                                            gap: '0.75rem',
                                                                            marginBottom: '0.75rem'
                                                                        }}>
                                                                            {/* Student Photo */}
                                                                            {submission.submittedByStudent?.profilePhotoUrl ? (
                                                                                <img
                                                                                    src={submission.submittedByStudent.profilePhotoUrl}
                                                                                    alt={submission.submittedByStudent.fullName}
                                                                                    style={{
                                                                                        width: '40px',
                                                                                        height: '40px',
                                                                                        borderRadius: '50%',
                                                                                        objectFit: 'cover'
                                                                                    }}
                                                                                    onError={(e) => {
                                                                                        e.target.style.display = 'none';
                                                                                        e.target.nextSibling.style.display = 'flex';
                                                                                    }}
                                                                                />
                                                                            ) : null}
                                                                            <div style={{
                                                                                width: '40px',
                                                                                height: '40px',
                                                                                borderRadius: '50%',
                                                                                background: 'var(--primary)',
                                                                                display: submission.submittedByStudent?.profilePhotoUrl ? 'none' : 'flex',
                                                                                alignItems: 'center',
                                                                                justifyContent: 'center',
                                                                                color: 'white',
                                                                                fontWeight: '600',
                                                                                fontSize: '1rem'
                                                                            }}>
                                                                                {submission.submittedByStudent?.fullName?.charAt(0)?.toUpperCase() || 'S'}
                                                                            </div>
                                                                            <div>
                                                                                <p style={{
                                                                                    fontWeight: '600',
                                                                                    fontSize: '0.875rem',
                                                                                    marginBottom: '0.125rem'
                                                                                }}>
                                                                                    {submission.submittedByStudent?.fullName || 'Unknown'}
                                                                                </p>
                                                                                <p style={{
                                                                                    fontSize: '0.75rem',
                                                                                    color: 'var(--text-muted)'
                                                                                }}>
                                                                                    {submission.submittedByStudent?.email || 'N/A'}
                                                                                </p>
                                                                            </div>
                                                                        </div>

                                                                        <div style={{
                                                                            fontSize: '0.75rem',
                                                                            color: 'var(--text-muted)',
                                                                            marginBottom: '0.75rem'
                                                                        }}>
                                                                            Submitted: {formatDate(submission.submittedAt)}
                                                                        </div>

                                                                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                                                            {submission.repoUrl && (
                                                                                <a
                                                                                    href={submission.repoUrl}
                                                                                    target="_blank"
                                                                                    rel="noopener noreferrer"
                                                                                    style={{
                                                                                        color: 'var(--primary)',
                                                                                        fontSize: '0.75rem',
                                                                                        textDecoration: 'none',
                                                                                        display: 'flex',
                                                                                        alignItems: 'center',
                                                                                        gap: '0.25rem'
                                                                                    }}
                                                                                >
                                                                                    📁 Repository URL
                                                                                </a>
                                                                            )}
                                                                            {submission.prUrl && (
                                                                                <a
                                                                                    href={submission.prUrl}
                                                                                    target="_blank"
                                                                                    rel="noopener noreferrer"
                                                                                    style={{
                                                                                        color: 'var(--primary)',
                                                                                        fontSize: '0.75rem',
                                                                                        textDecoration: 'none',
                                                                                        display: 'flex',
                                                                                        alignItems: 'center',
                                                                                        gap: '0.25rem'
                                                                                    }}
                                                                                >
                                                                                    🔗 Pull Request URL
                                                                                </a>
                                                                            )}
                                                                        </div>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        ) : (
                                                            <div style={{
                                                                textAlign: 'center',
                                                                padding: '2rem',
                                                                color: 'var(--text-muted)'
                                                            }}>
                                                                No submissions found for this assignment
                                                            </div>
                                                        )}
                                                    </td>
                                                </tr>
                                            )}
                                        </React.Fragment>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Empty State */}
                {!loadingAssignments && assignments.length === 0 && batchId && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📨</div>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.125rem' }}>
                            No assignments found
                        </p>
                    </div>
                )}

                {/* Review Modal */}
                {reviewingAssignment && (
                    <div style={{
                        position: 'fixed',
                        inset: 0,
                        background: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            background: 'var(--card-bg)',
                            borderRadius: 'var(--radius)',
                            padding: '2rem',
                            maxWidth: '500px',
                            width: '90%',
                            border: '1px solid var(--border-color)'
                        }}>
                            <h3 style={{
                                fontSize: '1.25rem',
                                fontWeight: '700',
                                marginBottom: '1.5rem'
                            }}>
                                Review Assignment
                            </h3>

                            {/* Decision */}
                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{
                                    display: 'block',
                                    fontSize: '0.875rem',
                                    fontWeight: '500',
                                    marginBottom: '0.5rem',
                                    color: 'var(--text-color)'
                                }}>
                                    Decision
                                </label>
                                <select
                                    value={reviewForm.decision}
                                    onChange={(e) => setReviewForm({ ...reviewForm, decision: e.target.value })}
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem',
                                        border: '1px solid var(--border-color)',
                                        borderRadius: 'var(--radius)',
                                        background: 'var(--input-bg)',
                                        color: 'var(--text-color)',
                                        fontSize: '0.875rem'
                                    }}
                                >
                                    {decisionOptions.map(option => (
                                        <option key={option} value={option}>
                                            {option.replace(/_/g, ' ')}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Reason Code */}
                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{
                                    display: 'block',
                                    fontSize: '0.875rem',
                                    fontWeight: '500',
                                    marginBottom: '0.5rem',
                                    color: 'var(--text-color)'
                                }}>
                                    Reason Code
                                </label>
                                <select
                                    value={reviewForm.reasonCode}
                                    onChange={(e) => setReviewForm({ ...reviewForm, reasonCode: e.target.value })}
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem',
                                        border: '1px solid var(--border-color)',
                                        borderRadius: 'var(--radius)',
                                        background: 'var(--input-bg)',
                                        color: 'var(--text-color)',
                                        fontSize: '0.875rem'
                                    }}
                                >
                                    {reasonCodeOptions.map(option => (
                                        <option key={option} value={option}>
                                            {option.replace(/_/g, ' ')}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Comments */}
                            <div style={{ marginBottom: '1.5rem' }}>
                                <label style={{
                                    display: 'block',
                                    fontSize: '0.875rem',
                                    fontWeight: '500',
                                    marginBottom: '0.5rem',
                                    color: 'var(--text-color)'
                                }}>
                                    Comments
                                </label>
                                <textarea
                                    value={reviewForm.comments}
                                    onChange={(e) => setReviewForm({ ...reviewForm, comments: e.target.value })}
                                    rows="4"
                                    placeholder="Enter your review comments..."
                                    style={{
                                        width: '100%',
                                        padding: '0.75rem',
                                        border: '1px solid var(--border-color)',
                                        borderRadius: 'var(--radius)',
                                        background: 'var(--input-bg)',
                                        color: 'var(--text-color)',
                                        fontSize: '0.875rem',
                                        resize: 'vertical'
                                    }}
                                />
                            </div>

                            {/* Buttons */}
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button
                                    onClick={closeReviewPanel}
                                    disabled={reviewLoading}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#6b7280',
                                        color: 'white',
                                        cursor: reviewLoading ? 'not-allowed' : 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600',
                                        opacity: reviewLoading ? 0.5 : 1
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleSubmitReview}
                                    disabled={reviewLoading}
                                    className="btn"
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        fontSize: '0.875rem',
                                        opacity: reviewLoading ? 0.5 : 1,
                                        cursor: reviewLoading ? 'not-allowed' : 'pointer'
                                    }}
                                >
                                    {reviewLoading ? 'Submitting...' : 'Submit Review'}
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Toast Notification */}
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

const tableHeaderStyle = {
    padding: '1rem 1.5rem',
    textAlign: 'left',
    fontSize: '0.875rem',
    fontWeight: '600',
    color: 'var(--text-color)',
    borderBottom: '1px solid var(--border-color)'
};

const tableCellStyle = {
    padding: '1rem 1.5rem',
    fontSize: '0.875rem',
    color: 'var(--text-color)'
};

export default MentorSubmissions;
