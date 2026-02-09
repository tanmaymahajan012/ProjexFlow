import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getBatchIds, getMentorGroupsByBatch } from '../../api/groupApi';
import Toast from '../../components/Toast';

const MentorGroups = () => {
    const [batchIds, setBatchIds] = useState([]);
    const [selectedBatchId, setSelectedBatchId] = useState('');
    const [groups, setGroups] = useState([]);
    const [expandedGroups, setExpandedGroups] = useState({});
    const [loading, setLoading] = useState(false);
    const [toast, setToast] = useState(null);

    // Fetch batch IDs on mount
    useEffect(() => {
        fetchBatchIds();
    }, []);

    const fetchBatchIds = async () => {
        try {
            const data = await getBatchIds();
            setBatchIds(data);
        } catch (err) {
            console.error('Failed to fetch batch IDs:', err);
            setToast({ message: 'Failed to load batch IDs', type: 'error' });
        }
    };

    const handleShowGroups = async () => {
        if (!selectedBatchId) return;

        try {
            setLoading(true);
            const data = await getMentorGroupsByBatch(selectedBatchId);
            setGroups(data);
        } catch (err) {
            console.error('Failed to fetch groups:', err);
            setToast({ message: 'Failed to load groups', type: 'error' });
        } finally {
            setLoading(false);
        }
    };

    const toggleGroupExpansion = (groupId) => {
        setExpandedGroups(prev => ({
            ...prev,
            [groupId]: !prev[groupId]
        }));
    };

    return (
        <DashboardLayout>
            <div className="mentor-groups-container">
                {/* Page Header */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        My Groups
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        View and manage your assigned student groups
                    </p>
                </div>

                {/* Controls Section */}
                <div style={{
                    background: 'var(--card-bg)',
                    padding: '1.5rem',
                    borderRadius: 'var(--radius)',
                    border: '1px solid var(--border-color)',
                    marginBottom: '1.5rem'
                }}>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                        {/* Batch Selection */}
                        <div style={{ flex: '1', minWidth: '200px' }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                Select Batch ID
                            </label>
                            <select
                                value={selectedBatchId}
                                onChange={(e) => setSelectedBatchId(e.target.value)}
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
                                <option value="">Select a batch...</option>
                                {batchIds.map(batchId => (
                                    <option key={batchId} value={batchId}>
                                        Batch {batchId}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Show Groups Button */}
                        <button
                            onClick={handleShowGroups}
                            disabled={!selectedBatchId || loading}
                            className="btn"
                            style={{
                                padding: '0.75rem 1.5rem',
                                fontSize: '0.875rem',
                                opacity: (!selectedBatchId || loading) ? 0.5 : 1,
                                cursor: (!selectedBatchId || loading) ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? 'Loading...' : 'Show Groups'}
                        </button>
                    </div>
                </div>

                {/* Groups Display */}
                {groups.length > 0 && (
                    <div style={{
                        display: 'grid',
                        gap: '1rem',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))'
                    }}>
                        {groups.map((mentorGroup) => (
                            <div
                                key={mentorGroup.groupId}
                                style={{
                                    background: 'var(--card-bg)',
                                    borderRadius: 'var(--radius)',
                                    border: '1px solid var(--border-color)',
                                    overflow: 'hidden',
                                    transition: 'all 0.2s'
                                }}
                            >
                                {/* Group Header */}
                                <div
                                    onClick={() => toggleGroupExpansion(mentorGroup.groupId)}
                                    style={{
                                        padding: '1.25rem',
                                        cursor: 'pointer',
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        background: expandedGroups[mentorGroup.groupId] ? '#1f2937' : 'transparent',
                                        transition: 'background 0.2s'
                                    }}
                                    onMouseEnter={(e) => {
                                        if (!expandedGroups[mentorGroup.groupId]) {
                                            e.currentTarget.style.background = '#1f2937';
                                        }
                                    }}
                                    onMouseLeave={(e) => {
                                        if (!expandedGroups[mentorGroup.groupId]) {
                                            e.currentTarget.style.background = 'transparent';
                                        }
                                    }}
                                >
                                    <div>
                                        <h3 style={{
                                            fontSize: '1.125rem',
                                            fontWeight: '700',
                                            marginBottom: '0.25rem'
                                        }}>
                                            Group {mentorGroup.groupId}
                                        </h3>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)'
                                        }}>
                                            {mentorGroup.group?.members?.length || 0} members
                                        </p>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)',
                                            marginBottom: '0.5rem'
                                        }}>
                                            Assigned: {mentorGroup.assignedAt?.split('T')[0]}
                                        </p>
                                        <span style={{
                                            fontSize: '1.25rem',
                                            transition: 'transform 0.2s',
                                            display: 'inline-block',
                                            transform: expandedGroups[mentorGroup.groupId] ? 'rotate(180deg)' : 'rotate(0deg)'
                                        }}>
                                            ▼
                                        </span>
                                    </div>
                                </div>

                                {/* Group Members (Expanded) */}
                                {expandedGroups[mentorGroup.groupId] && (
                                    <div style={{
                                        padding: '1rem',
                                        borderTop: '1px solid var(--border-color)',
                                        background: 'var(--bg)'
                                    }}>
                                        {mentorGroup.group?.members && mentorGroup.group.members.length > 0 ? (
                                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                                                {mentorGroup.group.members.map((member) => (
                                                    <div
                                                        key={member.memberId}
                                                        style={{
                                                            padding: '1rem',
                                                            background: 'var(--card-bg)',
                                                            borderRadius: 'var(--radius)',
                                                            border: '1px solid var(--border-color)'
                                                        }}
                                                    >
                                                        <div style={{
                                                            display: 'grid',
                                                            gridTemplateColumns: '1fr 1fr',
                                                            gap: '0.5rem',
                                                            fontSize: '0.875rem'
                                                        }}>
                                                            <div>
                                                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                                                    Name:
                                                                </span>
                                                                <p style={{ fontWeight: '600', marginTop: '0.25rem' }}>
                                                                    {member.student?.name || 'N/A'}
                                                                </p>
                                                            </div>
                                                            <div>
                                                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                                                    PRN:
                                                                </span>
                                                                <p style={{ fontWeight: '600', marginTop: '0.25rem' }}>
                                                                    {member.student?.prn || 'N/A'}
                                                                </p>
                                                            </div>
                                                            <div>
                                                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                                                    Course:
                                                                </span>
                                                                <p style={{ fontWeight: '600', marginTop: '0.25rem' }}>
                                                                    {member.student?.course || 'N/A'}
                                                                </p>
                                                            </div>
                                                            <div>
                                                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                                                    Email:
                                                                </span>
                                                                <p style={{
                                                                    fontWeight: '600',
                                                                    marginTop: '0.25rem',
                                                                    wordBreak: 'break-all'
                                                                }}>
                                                                    {member.student?.email || 'N/A'}
                                                                </p>
                                                            </div>
                                                            <div>
                                                                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                                                                    Status:
                                                                </span>
                                                                <p style={{ fontWeight: '600', marginTop: '0.25rem' }}>
                                                                    <span style={{
                                                                        display: 'inline-block',
                                                                        padding: '0.25rem 0.5rem',
                                                                        borderRadius: '0.25rem',
                                                                        fontSize: '0.75rem',
                                                                        background: member.active ? '#10b981' : '#dc2626',
                                                                        color: 'white'
                                                                    }}>
                                                                        {member.active ? 'Active' : 'Inactive'}
                                                                    </span>
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <p style={{
                                                textAlign: 'center',
                                                color: 'var(--text-muted)',
                                                padding: '1rem'
                                            }}>
                                                No members in this group
                                            </p>
                                        )}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}

                {/* Empty State */}
                {!loading && groups.length === 0 && selectedBatchId && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>👥</div>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.125rem' }}>
                            No groups found for this batch
                        </p>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.5rem' }}>
                            Click "Show Groups" to load your assigned groups
                        </p>
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

export default MentorGroups;
