import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getGroupingStatus, updateGroupingStatus, getBatchIds, getGroupsByBatch } from '../../api/groupApi';
import Toast from '../../components/Toast';

const AdminGroups = () => {
    const [groupingStatus, setGroupingStatus] = useState(null);
    const [batchIds, setBatchIds] = useState([]);
    const [selectedBatchId, setSelectedBatchId] = useState('');
    const [groups, setGroups] = useState([]);
    const [expandedGroups, setExpandedGroups] = useState({});
    const [loading, setLoading] = useState(false);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [toast, setToast] = useState(null);

    // Fetch batch IDs on mount
    useEffect(() => {
        fetchBatchIds();
    }, []);

    // Fetch grouping status when batch is selected
    useEffect(() => {
        if (selectedBatchId) {
            fetchGroupingStatus(selectedBatchId);
        }
    }, [selectedBatchId]);

    const fetchBatchIds = async () => {
        try {
            const data = await getBatchIds();
            setBatchIds(data);
        } catch (err) {
            console.error('Failed to fetch batch IDs:', err);
            setToast({ message: 'Failed to load batch IDs', type: 'error' });
        }
    };

    const fetchGroupingStatus = async (batchId) => {
        try {
            const data = await getGroupingStatus(batchId);
            setGroupingStatus(data.status);
        } catch (err) {
            console.error('Failed to fetch grouping status:', err);
            setToast({ message: 'Failed to load grouping status', type: 'error' });
        }
    };

    const handleToggleGroupingStatus = () => {
        setShowConfirmation(true);
    };

    const confirmToggleStatus = async () => {
        try {
            const newStatus = groupingStatus === 'OPEN' ? 'CLOSED' : 'OPEN';
            const data = await updateGroupingStatus(selectedBatchId, newStatus);
            setGroupingStatus(data.status);
            setShowConfirmation(false);
            setToast({
                message: `Grouping phase ${newStatus === 'OPEN' ? 'opened' : 'closed'} successfully!`,
                type: 'success'
            });
        } catch (err) {
            console.error('Failed to update grouping status:', err);
            setToast({ message: 'Failed to update grouping status', type: 'error' });
            setShowConfirmation(false);
        }
    };

    const handleShowGroups = async () => {
        if (!selectedBatchId) return;

        try {
            setLoading(true);
            const data = await getGroupsByBatch(selectedBatchId);
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
            <div className="admin-groups-container">
                {/* Page Header */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        Group Management
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        Manage grouping phase and view student groups
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
                    <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-end', flexWrap: 'wrap' }}>
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

                        {/* Grouping Phase Toggle */}
                        {selectedBatchId && groupingStatus && (
                            <div style={{ flex: '0 0 auto' }}>
                                <label style={{
                                    display: 'block',
                                    fontSize: '0.875rem',
                                    fontWeight: '500',
                                    marginBottom: '0.5rem',
                                    color: 'var(--text-color)'
                                }}>
                                    Grouping Phase
                                </label>
                                <button
                                    onClick={handleToggleGroupingStatus}
                                    style={{
                                        padding: '0.75rem 1.5rem',
                                        borderRadius: 'var(--radius)',
                                        border: 'none',
                                        fontWeight: '600',
                                        fontSize: '0.875rem',
                                        cursor: 'pointer',
                                        background: groupingStatus === 'OPEN'
                                            ? 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
                                            : 'linear-gradient(135deg, #dc2626 0%, #991b1b 100%)',
                                        color: 'white',
                                        transition: 'all 0.2s',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem'
                                    }}
                                    onMouseEnter={(e) => e.target.style.opacity = '0.9'}
                                    onMouseLeave={(e) => e.target.style.opacity = '1'}
                                >
                                    <span style={{
                                        width: '12px',
                                        height: '12px',
                                        borderRadius: '50%',
                                        background: 'white',
                                        display: 'inline-block'
                                    }} />
                                    {groupingStatus}
                                </button>
                            </div>
                        )}

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
                            {loading ? 'Loading...' : 'Show Available Groups'}
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
                        {groups.map((group) => (
                            <div
                                key={group.groupId}
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
                                    onClick={() => toggleGroupExpansion(group.groupId)}
                                    style={{
                                        padding: '1.25rem',
                                        cursor: 'pointer',
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        background: expandedGroups[group.groupId] ? '#1f2937' : 'transparent',
                                        transition: 'background 0.2s'
                                    }}
                                    onMouseEnter={(e) => {
                                        if (!expandedGroups[group.groupId]) {
                                            e.currentTarget.style.background = '#1f2937';
                                        }
                                    }}
                                    onMouseLeave={(e) => {
                                        if (!expandedGroups[group.groupId]) {
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
                                            Group {group.groupId}
                                        </h3>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)'
                                        }}>
                                            {group.members?.length || 0} members
                                        </p>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)',
                                            marginBottom: '0.5rem'
                                        }}>
                                            {group.createdAt?.split('T')[0]}
                                        </p>
                                        <span style={{
                                            fontSize: '1.25rem',
                                            transition: 'transform 0.2s',
                                            display: 'inline-block',
                                            transform: expandedGroups[group.groupId] ? 'rotate(180deg)' : 'rotate(0deg)'
                                        }}>
                                            ▼
                                        </span>
                                    </div>
                                </div>

                                {/* Group Members (Expanded) */}
                                {expandedGroups[group.groupId] && (
                                    <div style={{
                                        padding: '1rem',
                                        borderTop: '1px solid var(--border-color)',
                                        background: 'var(--bg)'
                                    }}>
                                        {group.members && group.members.length > 0 ? (
                                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                                                {group.members.map((member) => (
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
                                                                    {member.student?.fullName || 'N/A'}
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
                                                                    Roll No:
                                                                </span>
                                                                <p style={{ fontWeight: '600', marginTop: '0.25rem' }}>
                                                                    {member.student?.rollNo || 'N/A'}
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
                            Click "Show Available Groups" to load groups
                        </p>
                    </div>
                )}

                {/* Confirmation Modal */}
                {showConfirmation && (
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
                            maxWidth: '400px',
                            width: '90%',
                            border: '1px solid var(--border-color)'
                        }}>
                            <h3 style={{
                                fontSize: '1.25rem',
                                fontWeight: '700',
                                marginBottom: '1rem'
                            }}>
                                Are you sure?
                            </h3>
                            <p style={{
                                color: 'var(--text-muted)',
                                marginBottom: '1.5rem',
                                fontSize: '0.9375rem'
                            }}>
                                Do you want to change the grouping phase from{' '}
                                <strong>{groupingStatus}</strong> to{' '}
                                <strong>{groupingStatus === 'OPEN' ? 'CLOSED' : 'OPEN'}</strong>?
                            </p>
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button
                                    onClick={() => setShowConfirmation(false)}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#6b7280',
                                        color: 'white',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600'
                                    }}
                                >
                                    No
                                </button>
                                <button
                                    onClick={confirmToggleStatus}
                                    className="btn"
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        fontSize: '0.875rem'
                                    }}
                                >
                                    Yes
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

export default AdminGroups;
