import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { useAuth } from '../../context/AuthContext';
import { getMyGroup, sendGroupRequest, getIncomingRequests, getSentRequests, acceptRequest, rejectRequest, getGroupDetails } from '../../api/groupApi';
import Toast from '../../components/Toast';

const StudentTeam = () => {
    const { user } = useAuth();
    const [group, setGroup] = useState(null);
    const [invitations, setInvitations] = useState([]);
    const [sentRequests, setSentRequests] = useState([]);
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(true);
    const [sending, setSending] = useState(false);
    const [actionLoading, setActionLoading] = useState(null);
    const [toast, setToast] = useState(null);

    // Determine batchId from user object
    const batchId = user?.batchId || user?.student?.batchId || (user?.batches && user.batches[0]?.id);

    useEffect(() => {
        if (user && batchId) {
            initializePage();
        } else if (user && !batchId) {
            setLoading(false);
        }
    }, [user, batchId]);

    const initializePage = async () => {
        try {
            setLoading(true);
            await fetchGroupStatus();
        } catch (error) {
            console.error("Initialization failed", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchGroupStatus = async () => {
        try {
            const studentId = user?.student?.id || user?.id;
            let groupData = await getMyGroup(batchId, studentId);
            const groupId = groupData?.id || groupData?.groupId;

            // If we have a groupId but missing members, try fetching full details
            if (groupId && (!groupData.members || groupData.members.length === 0)) {
                try {
                    console.log("Group ID found but members missing, fetching details for:", groupId);
                    const details = await getGroupDetails(groupId);
                    if (details) {
                        // Normalize members from various potential keys
                        const membersList = details.members || details.students || details.users || [];
                        groupData = { ...groupData, ...details, members: membersList };

                        // Ensure ID is consistent
                        if (!groupData.id && groupData.groupId) groupData.id = groupData.groupId;
                    }
                } catch (detailsError) {
                    console.error("Failed to fetch extra group details", detailsError);
                }
            } else if (groupData) {
                // Also check initial response for alternative keys
                if (!groupData.members && (groupData.students || groupData.users)) {
                    groupData.members = groupData.students || groupData.users;
                }
            }

            // Stricter check: Group is formed if we have an ID.
            if (groupId) {
                setGroup(groupData);
            } else {
                setGroup(null);
                await fetchInvitations();
                await fetchSentRequests();
            }
        } catch (error) {
            if (error.response && error.response.status === 404) {
                setGroup(null);
                await fetchInvitations();
                await fetchSentRequests();
            } else {
                console.error("Failed to fetch group status", error);
                setToast({ message: "Failed to load group status", type: "error" });
                setGroup(null);
            }
        }
    };

    const fetchInvitations = async () => {
        try {
            const data = await getIncomingRequests(batchId);
            // Filter: Only allow PENDING/REQUESTED and actionable items
            const pending = (data || []).filter(invite => {
                const isPending = (invite.status === 'PENDING' || invite.status === 'REQUESTED');
                const isActionable = invite.actionable !== false; // if actionable is undefined, assume TRUE
                return isPending && isActionable;
            });
            setInvitations(pending);
        } catch (error) {
            console.error("Failed to fetch invitations", error);
            setInvitations([]);
        }
    };

    const fetchSentRequests = async () => {
        try {
            const data = await getSentRequests(batchId);
            setSentRequests(data || []);
        } catch (error) {
            console.error("Failed to fetch sent requests", error);
            setSentRequests([]);
        }
    };

    const handleSendInvite = async (e) => {
        e.preventDefault();
        if (!email) return;

        setSending(true);
        try {
            await sendGroupRequest(batchId, email);
            setToast({ message: 'Invitation sent successfully!', type: 'success' });
            setEmail('');
            // Refresh sent requests
            await fetchSentRequests();
        } catch (error) {
            console.error("Failed to send invitation", error);
            const msg = error.response?.data?.message || 'Failed to send invitation';
            setToast({ message: msg, type: 'error' });
        } finally {
            setSending(false);
        }
    };

    const handleAccept = async (requestId) => {
        setActionLoading(requestId);
        try {
            await acceptRequest(requestId);
            setToast({ message: 'Invitation accepted! You joined the group.', type: 'success' });
            await fetchGroupStatus();
        } catch (error) {
            console.error("Failed to accept invitation", error);
            const msg = error.response?.data?.message || 'Failed to accept invitation';
            setToast({ message: msg, type: 'error' });
        } finally {
            setActionLoading(null);
        }
    };

    const handleReject = async (requestId) => {
        setActionLoading(requestId);
        try {
            await rejectRequest(requestId);
            setToast({ message: 'Invitation rejected.', type: 'info' });
            await fetchInvitations();
        } catch (error) {
            console.error("Failed to reject invitation", error);
            setToast({ message: 'Failed to reject invitation', type: 'error' });
        } finally {
            setActionLoading(null);
        }
    };

    const getStatusBadge = (status) => {
        const statusColors = {
            'PENDING': { bg: '#fbbf24', text: 'white' },
            'ACCEPTED': { bg: '#10b981', text: 'white' },
            'REJECTED': { bg: '#ef4444', text: 'white' },
        };
        const colors = statusColors[status] || { bg: '#6b7280', text: 'white' };
        return (
            <span style={{
                padding: '0.25rem 0.5rem',
                borderRadius: '4px',
                fontSize: '0.75rem',
                fontWeight: '600',
                background: colors.bg,
                color: colors.text,
            }}>
                {status}
            </span>
        );
    };

    return (
        <DashboardLayout>
            <div className="student-group-container">
                {/* Page Header - Matches MentorGroups/AdminGroups style */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        My Group
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        Manage your group status and invitations
                    </p>
                </div>

                {!batchId && !loading && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <p style={{ color: 'var(--text-muted)' }}>No active batch found for your account.</p>
                    </div>
                )}

                {loading ? (
                    <div className="text-center p-5">
                        <p>Loading...</p>
                    </div>
                ) : (
                    <>
                        {group ? (
                            /* GROUP FORMED UI: Single Card View */
                            <div style={{
                                background: 'var(--card-bg)',
                                borderRadius: 'var(--radius)',
                                border: '1px solid var(--border-color)',
                                overflow: 'hidden'
                            }}>
                                <div style={{
                                    padding: '1.5rem',
                                    borderBottom: '1px solid var(--border-color)',
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center'
                                }}>
                                    <h3 style={{ fontSize: '1.25rem', fontWeight: '700' }}>
                                        Group #{group.id || group.groupId}
                                    </h3>
                                    <span style={{
                                        padding: '0.25rem 0.75rem',
                                        borderRadius: '9999px',
                                        fontSize: '0.875rem',
                                        background: '#10b981',
                                        color: 'white'
                                    }}>
                                        Active
                                    </span>
                                </div>
                                <div style={{ padding: '1.5rem' }}>
                                    <div style={{ marginBottom: '2rem' }}>
                                        <h4 style={{ fontSize: '0.875rem', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '1rem', letterSpacing: '0.05em' }}>
                                            Mentor
                                        </h4>
                                        {group.mentor ? (
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                                <div style={{
                                                    width: '40px', height: '40px', borderRadius: '50%', background: '#3b82f6',
                                                    display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontWeight: 'bold'
                                                }}>
                                                    {group.mentor.name?.charAt(0) || 'M'}
                                                </div>
                                                <div>
                                                    <div style={{ fontWeight: '600' }}>{group.mentor.name}</div>
                                                    <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>{group.mentor.email}</div>
                                                </div>
                                            </div>
                                        ) : (
                                            <p style={{ fontStyle: 'italic', color: 'var(--text-muted)' }}>Not assigned yet</p>
                                        )}
                                    </div>

                                    <div>
                                        <h4 style={{ fontSize: '0.875rem', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '1rem', letterSpacing: '0.05em' }}>
                                            Members
                                        </h4>
                                        <div style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
                                            {group.members?.map((member, index) => {
                                                // Resolve fields
                                                const memberId = member.id || member.studentId || member.userId;
                                                // Resolve name with extensive fallback
                                                let memberName = member.name || member.fullName || member.student?.name;
                                                if (!memberName && (member.email || member.student?.email)) {
                                                    const em = member.email || member.student?.email;
                                                    memberName = em.split('@')[0].replace(/[._]/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
                                                }
                                                if (!memberName) memberName = "Unknown Member";

                                                const memberEmail = member.email || member.student?.email || "No Email";

                                                return (
                                                    <div key={memberId || index} style={{
                                                        padding: '1rem',
                                                        background: 'var(--bg)',
                                                        borderRadius: 'var(--radius)',
                                                        border: '1px solid var(--border-color)'
                                                    }}>
                                                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                                                            <div style={{
                                                                width: '32px', height: '32px', borderRadius: '50%', background: '#6366f1',
                                                                display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontSize: '0.875rem', fontWeight: 'bold'
                                                            }}>
                                                                {memberName.charAt(0)}
                                                            </div>
                                                            <div style={{ flex: 1 }}>
                                                                <div style={{ fontWeight: '600', fontSize: '0.9375rem' }}>
                                                                    {memberName}
                                                                    {user.id && memberId && String(user.id) === String(memberId) && (
                                                                        <span style={{ marginLeft: '0.5rem', fontSize: '0.75rem', background: '#64748b', color: 'white', padding: '0.125rem 0.375rem', borderRadius: '4px' }}>YOU</span>
                                                                    )}
                                                                </div>
                                                                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{memberEmail}</div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                )
                                            })}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            /* NOT IN GROUP UI: Grid Layout matching functionality requirement */
                            <>
                                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '1.5rem', marginBottom: '1.5rem' }}>
                                    {/* Left Card: Send Invitation */}
                                    <div style={{
                                        background: 'var(--card-bg)',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        padding: '1.5rem'
                                    }}>
                                        <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '1.5rem' }}>Send Invitation</h3>
                                        <form onSubmit={handleSendInvite}>
                                            <div style={{ marginBottom: '1.25rem' }}>
                                                <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: 'var(--text-color)' }}>
                                                    Peer Email
                                                </label>
                                                <input
                                                    type="email"
                                                    required
                                                    placeholder="student@example.com"
                                                    value={email}
                                                    onChange={(e) => setEmail(e.target.value)}
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
                                            <button
                                                type="submit"
                                                disabled={sending}
                                                className="btn"
                                                style={{
                                                    width: '100%',
                                                    padding: '0.75rem',
                                                    fontSize: '0.875rem',
                                                    opacity: sending ? 0.7 : 1,
                                                    cursor: sending ? 'not-allowed' : 'pointer'
                                                }}
                                            >
                                                {sending ? 'Sending...' : 'Send Invitation'}
                                            </button>
                                        </form>
                                    </div>

                                    {/* Right Card: Received Invitations */}
                                    <div style={{
                                        background: 'var(--card-bg)',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        padding: '1.5rem'
                                    }}>
                                        <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '1.5rem' }}>Received Invitations</h3>

                                        {invitations.length === 0 ? (
                                            <div style={{ padding: '2rem 0', textAlign: 'center', color: 'var(--text-muted)' }}>
                                                <p>No pending invitations.</p>
                                            </div>
                                        ) : (
                                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                                {invitations.map((invite) => {
                                                    // 1. Determine correct request ID
                                                    const requestId = invite.id || invite.requestId || invite.groupRequestId || invite.uuid || invite._id;

                                                    // 2. Determine sender label
                                                    const senderLabel =
                                                        invite.senderName ||
                                                        invite.senderEmail ||
                                                        invite.fromEmail ||
                                                        invite.fromUserEmail ||
                                                        invite.requestedByEmail ||
                                                        invite.sender?.email ||
                                                        invite.sender?.name ||
                                                        invite.createdBy?.email ||
                                                        "Sender";

                                                    const isActionable = !!requestId;

                                                    return (
                                                        <div key={requestId || Math.random()} style={{
                                                            padding: '1rem',
                                                            border: '1px solid var(--border-color)',
                                                            borderRadius: 'var(--radius)',
                                                            background: 'var(--bg)'
                                                        }}>
                                                            <div style={{ marginBottom: '0.75rem' }}>
                                                                <div style={{ fontWeight: '600', fontSize: '0.9375rem' }}>
                                                                    {senderLabel}
                                                                </div>
                                                                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                                                                    Invited you to join their group
                                                                </div>
                                                            </div>
                                                            <div style={{ display: 'flex', gap: '0.75rem' }}>
                                                                <button
                                                                    onClick={async () => {
                                                                        if (!isActionable) {
                                                                            setToast({ message: "Missing request id in invitation", type: "error" });
                                                                            return;
                                                                        }
                                                                        await handleAccept(requestId);
                                                                    }}
                                                                    disabled={actionLoading === requestId || !isActionable}
                                                                    style={{
                                                                        flex: 1,
                                                                        padding: '0.5rem',
                                                                        borderRadius: 'var(--radius)',
                                                                        border: 'none',
                                                                        background: isActionable ? '#10b981' : '#6b7280',
                                                                        color: 'white',
                                                                        fontSize: '0.875rem',
                                                                        fontWeight: '500',
                                                                        cursor: isActionable ? 'pointer' : 'not-allowed',
                                                                        opacity: actionLoading === requestId ? 0.5 : 1
                                                                    }}
                                                                >
                                                                    Accept
                                                                </button>
                                                                <button
                                                                    onClick={async () => {
                                                                        if (!isActionable) {
                                                                            setToast({ message: "Missing request id in invitation", type: "error" });
                                                                            return;
                                                                        }
                                                                        await handleReject(requestId);
                                                                    }}
                                                                    disabled={actionLoading === requestId || !isActionable}
                                                                    style={{
                                                                        flex: 1,
                                                                        padding: '0.5rem',
                                                                        borderRadius: 'var(--radius)',
                                                                        border: '1px solid var(--border-color)',
                                                                        background: 'transparent',
                                                                        color: 'var(--text-color)',
                                                                        fontSize: '0.875rem',
                                                                        fontWeight: '500',
                                                                        cursor: isActionable ? 'pointer' : 'not-allowed',
                                                                        opacity: actionLoading === requestId ? 0.5 : 1
                                                                    }}
                                                                >
                                                                    Reject
                                                                </button>
                                                            </div>
                                                        </div>
                                                    );
                                                })}
                                            </div>
                                        )}
                                    </div>
                                </div>

                                {/* Sent Requests Card */}
                                {sentRequests.length > 0 && (
                                    <div style={{
                                        background: 'var(--card-bg)',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        padding: '1.5rem'
                                    }}>
                                        <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '1.5rem' }}>Sent Requests</h3>
                                        <div style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
                                            {sentRequests.map((request) => (
                                                <div key={request.id} style={{
                                                    padding: '1rem',
                                                    border: '1px solid var(--border-color)',
                                                    borderRadius: 'var(--radius)',
                                                    background: 'var(--bg)'
                                                }}>
                                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '0.5rem' }}>
                                                        <div>
                                                            <div style={{ fontWeight: '600', fontSize: '0.9375rem' }}>
                                                                {request.toStudentName || request.toStudentEmail}
                                                            </div>
                                                            {request.toStudentName && (
                                                                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                                                                    {request.toStudentEmail}
                                                                </div>
                                                            )}
                                                        </div>
                                                        {getStatusBadge(request.status)}
                                                    </div>
                                                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                                                        Sent {new Date(request.createdAt).toLocaleDateString()}
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}
                            </>
                        )}
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

export default StudentTeam;

