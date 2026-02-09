import React, { useState, useMemo } from 'react';
import { useMentors } from '../../context/MentorContext';
import DashboardLayout from '../../layout/DashboardLayout';
import AddMentorModal from '../../components/AddMentorModal';
import Toast from '../../components/Toast';

const AdminMentors = () => {
    const { mentors, loading, error, loadMentors, toggleMentorActiveStatus, addMentor } = useMentors();
    const [courseFilter, setCourseFilter] = useState('');
    const [showFiltered, setShowFiltered] = useState(false);
    const [toggleLoading, setToggleLoading] = useState({});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [toast, setToast] = useState(null);

    // Filter mentors based on course (case-insensitive)
    const filteredMentors = useMemo(() => {
        if (!showFiltered) {
            return mentors;
        }
        if (!courseFilter.trim()) {
            return mentors;
        }
        return mentors.filter(mentor =>
            mentor.course?.toLowerCase().includes(courseFilter.toLowerCase())
        );
    }, [mentors, courseFilter, showFiltered]);

    const handleShowList = async () => {
        // Always fetch latest data when "Show List" is clicked
        if (mentors.length === 0) {
            await loadMentors();
        }
        setShowFiltered(true);
    };

    const handleToggleActive = async (mentorId) => {
        try {
            setToggleLoading(prev => ({ ...prev, [mentorId]: true }));
            await toggleMentorActiveStatus(mentorId);
        } catch (err) {
            alert('Failed to update mentor status: ' + err.message);
        } finally {
            setToggleLoading(prev => ({ ...prev, [mentorId]: false }));
        }
    };

    const handleCourseFilterChange = (e) => {
        setCourseFilter(e.target.value);
        setShowFiltered(false); // Reset filter when input changes
    };

    return (
        <DashboardLayout>
            <div className="admin-mentors-container">
                <div className="dashboard-header" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                        <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                            Mentor Management
                        </h1>
                        <p style={{ color: 'var(--text-muted)' }}>
                            Manage mentors and their active status
                        </p>
                    </div>
                    <button
                        onClick={() => setIsModalOpen(true)}
                        className="btn"
                        style={{
                            padding: '0.75rem 1.5rem',
                            fontSize: '0.875rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                        }}
                    >
                        <span style={{ fontSize: '1.25rem' }}>+</span>
                        Add Mentor
                    </button>
                </div>

                {/* Filter Section */}
                <div style={{
                    background: 'var(--card-bg)',
                    padding: '1.5rem',
                    borderRadius: 'var(--radius)',
                    border: '1px solid var(--border-color)',
                    marginBottom: '1.5rem'
                }}>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
                        <div style={{ flex: 1 }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                Filter by Course
                            </label>
                            <input
                                type="text"
                                placeholder="course"
                                value={courseFilter}
                                onChange={handleCourseFilterChange}
                                disabled={mentors.length === 0}
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '1px solid var(--border-color)',
                                    borderRadius: 'var(--radius)',
                                    background: mentors.length === 0 ? '#1f2937' : 'var(--input-bg)',
                                    color: 'var(--text-color)',
                                    fontSize: '0.875rem',
                                    cursor: mentors.length === 0 ? 'not-allowed' : 'text',
                                    opacity: mentors.length === 0 ? 0.5 : 1
                                }}
                            />
                        </div>
                        <button
                            onClick={handleShowList}
                            disabled={loading} // Only disable while loading
                            className="btn"
                            style={{
                                padding: '0.75rem 1.5rem',
                                fontSize: '0.875rem',
                                opacity: loading ? 0.5 : 1,
                                cursor: loading ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? 'Loading...' : 'Show List'}
                        </button>
                    </div>
                </div>

                {/* Loading State */}
                {loading && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>⏳</div>
                        <p style={{ color: 'var(--text-muted)' }}>Loading mentors...</p>
                    </div>
                )}

                {/* Error State */}
                {error && !loading && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '1.5rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid #ef4444',
                        color: '#ef4444'
                    }}>
                        <strong>Error:</strong> {error}
                    </div>
                )}

                {/* Empty State */}
                {!loading && !error && mentors.length === 0 && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>👨‍🏫</div>
                        <p style={{ color: 'var(--text-muted)' }}>No mentors found</p>
                    </div>
                )}

                {/* Mentors Table */}
                {!loading && !error && filteredMentors.length > 0 && (
                    <div style={{
                        background: 'var(--card-bg)',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        overflow: 'hidden'
                    }}>
                        <div style={{ overflowX: 'auto' }}>
                            <table style={{
                                width: '100%',
                                borderCollapse: 'collapse'
                            }}>
                                <thead>
                                    <tr style={{
                                        background: '#1f2937',
                                        borderBottom: '1px solid var(--border-color)'
                                    }}>
                                        <th style={tableHeaderStyle}>Profile</th>
                                        <th style={tableHeaderStyle}>Name</th>
                                        <th style={tableHeaderStyle}>Email</th>
                                        <th style={tableHeaderStyle}>Course</th>
                                        <th style={tableHeaderStyle}>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredMentors.map((mentor) => (
                                        <tr
                                            key={mentor.id}
                                            style={{
                                                borderBottom: '1px solid var(--border-color)',
                                                transition: 'background 0.2s'
                                            }}
                                            onMouseEnter={(e) => e.currentTarget.style.background = '#1f2937'}
                                            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                                        >
                                            <td style={tableCellStyle}>
                                                <div style={{
                                                    width: '40px',
                                                    height: '40px',
                                                    borderRadius: '50%',
                                                    overflow: 'hidden',
                                                    background: '#374151',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center'
                                                }}>
                                                    {mentor.profilePhotoUrl ? (
                                                        <img
                                                            src={mentor.profilePhotoUrl}
                                                            alt={mentor.fullName}
                                                            style={{
                                                                width: '100%',
                                                                height: '100%',
                                                                objectFit: 'cover'
                                                            }}
                                                            onError={(e) => {
                                                                e.target.style.display = 'none';
                                                                e.target.parentElement.innerHTML = '👨‍🏫';
                                                            }}
                                                        />
                                                    ) : (
                                                        <span style={{ fontSize: '1.5rem' }}>👨‍🏫</span>
                                                    )}
                                                </div>
                                            </td>
                                            <td style={tableCellStyle}>
                                                <span style={{ fontWeight: '500' }}>{mentor.fullName}</span>
                                            </td>
                                            <td style={tableCellStyle}>
                                                <span style={{ color: 'var(--text-muted)' }}>{mentor.email}</span>
                                            </td>
                                            <td style={tableCellStyle}>
                                                <span style={{
                                                    background: '#374151',
                                                    padding: '0.25rem 0.75rem',
                                                    borderRadius: '0.375rem',
                                                    fontSize: '0.875rem'
                                                }}>
                                                    {mentor.course}
                                                </span>
                                            </td>
                                            <td style={tableCellStyle}>
                                                <button
                                                    onClick={() => handleToggleActive(mentor.id)}
                                                    disabled={toggleLoading[mentor.id]}
                                                    style={{
                                                        padding: '0.5rem 1rem',
                                                        borderRadius: '0.375rem',
                                                        border: 'none',
                                                        fontWeight: '600',
                                                        fontSize: '0.875rem',
                                                        cursor: toggleLoading[mentor.id] ? 'wait' : 'pointer',
                                                        background: mentor.active ? '#10b981' : '#ef4444',
                                                        color: 'white',
                                                        transition: 'all 0.2s',
                                                        opacity: toggleLoading[mentor.id] ? 0.7 : 1
                                                    }}
                                                    onMouseEnter={(e) => {
                                                        if (!toggleLoading[mentor.id]) {
                                                            e.target.style.opacity = '0.8';
                                                        }
                                                    }}
                                                    onMouseLeave={(e) => {
                                                        if (!toggleLoading[mentor.id]) {
                                                            e.target.style.opacity = '1';
                                                        }
                                                    }}
                                                >
                                                    {toggleLoading[mentor.id] ? 'Updating...' : (mentor.active ? 'Active' : 'Inactive')}
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        <div style={{
                            padding: '1rem 1.5rem',
                            borderTop: '1px solid var(--border-color)',
                            fontSize: '0.875rem',
                            color: 'var(--text-muted)'
                        }}>
                            Showing {filteredMentors.length} of {mentors.length} mentors
                        </div>
                    </div>
                )}

                {/* No Results After Filter */}
                {!loading && !error && showFiltered && filteredMentors.length === 0 && mentors.length > 0 && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>🔍</div>
                        <p style={{ color: 'var(--text-muted)' }}>
                            No mentors found for course: <strong>{courseFilter}</strong>
                        </p>
                        <button
                            onClick={() => {
                                setCourseFilter('');
                                setShowFiltered(false);
                            }}
                            className="btn"
                            style={{ marginTop: '1rem', padding: '0.5rem 1rem', fontSize: '0.875rem' }}
                        >
                            Clear Filter
                        </button>
                    </div>
                )}

                {/* Add Mentor Modal */}
                <AddMentorModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    loading={loading}
                    onSubmit={async (mentorData) => {
                        try {
                            await addMentor(mentorData);
                            setIsModalOpen(false);
                            setToast({ message: 'Mentor added successfully!', type: 'success' });
                        } catch (err) {
                            setToast({ message: 'Something went wrong', type: 'error' });
                        }
                    }}
                />

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
    textTransform: 'uppercase',
    letterSpacing: '0.05em'
};

const tableCellStyle = {
    padding: '1rem 1.5rem',
    fontSize: '0.875rem',
    color: 'var(--text-color)'
};

export default AdminMentors;
