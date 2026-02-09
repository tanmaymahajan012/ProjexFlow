import React, { useState, useMemo } from 'react';
import { useStudents } from '../../context/StudentContext';
import DashboardLayout from '../../layout/DashboardLayout';
import AddStudentModal from '../../components/AddStudentModal';
import Toast from '../../components/Toast';

const AdminStudents = () => {
    const { students, loading, error, loadStudents, toggleStudentActiveStatus, addStudent } = useStudents();
    const [courseFilter, setCourseFilter] = useState('');
    const [batchIdFilter, setBatchIdFilter] = useState('');
    const [showFiltered, setShowFiltered] = useState(false);
    const [toggleLoading, setToggleLoading] = useState({});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [toast, setToast] = useState(null);

    // Filter students based on course (case-insensitive) AND batchId (exact match)
    const filteredStudents = useMemo(() => {
        if (!showFiltered) {
            return [];
        }

        return students.filter(student => {
            const courseMatch = !courseFilter.trim() ||
                student.course?.toLowerCase().includes(courseFilter.toLowerCase());

            const batchIdMatch = !batchIdFilter.trim() ||
                student.batchId?.toString() === batchIdFilter.trim();

            return courseMatch && batchIdMatch;
        });
    }, [students, courseFilter, batchIdFilter, showFiltered]);

    const handleShowList = async () => {
        // Fetch data if not already loaded
        if (students.length === 0) {
            await loadStudents();
        }
        setShowFiltered(true);
    };

    const handleToggleActive = async (studentId) => {
        try {
            setToggleLoading(prev => ({ ...prev, [studentId]: true }));
            await toggleStudentActiveStatus(studentId);
        } catch (err) {
            alert('Failed to update student status: ' + err.message);
        } finally {
            setToggleLoading(prev => ({ ...prev, [studentId]: false }));
        }
    };

    const handleCourseFilterChange = (e) => {
        setCourseFilter(e.target.value);
        setShowFiltered(false);
    };

    const handleBatchIdFilterChange = (e) => {
        setBatchIdFilter(e.target.value);
        setShowFiltered(false);
    };

    return (
        <DashboardLayout>
            <div className="admin-students-container">
                <div className="dashboard-header" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                        <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                            Student Management
                        </h1>
                        <p style={{ color: 'var(--text-muted)' }}>
                            Manage students and their active status
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
                        Add Student
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
                                readOnly={false}
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
                        <div style={{ flex: 1 }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                Filter by Batch ID
                            </label>
                            <input
                                type="text"
                                placeholder="BatchId"
                                value={batchIdFilter}
                                onChange={handleBatchIdFilterChange}
                                readOnly={false}
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
                            onClick={handleShowList}
                            disabled={loading}
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
                        <p style={{ color: 'var(--text-muted)' }}>Loading students...</p>
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

                {/* Students Table - Always show with headers */}
                {!loading && !error && (
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
                                        <th style={tableHeaderStyle}>Roll No</th>
                                        <th style={tableHeaderStyle}>Name</th>
                                        <th style={tableHeaderStyle}>Email</th>
                                        <th style={tableHeaderStyle}>Course</th>
                                        <th style={tableHeaderStyle}>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredStudents.length > 0 ? (
                                        filteredStudents.map((student) => (
                                            <tr
                                                key={student.id}
                                                style={{
                                                    borderBottom: '1px solid var(--border-color)',
                                                    transition: 'background 0.2s'
                                                }}
                                                onMouseEnter={(e) => e.currentTarget.style.background = '#1f2937'}
                                                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                                            >
                                                <td style={tableCellStyle}>
                                                    <span style={{ fontFamily: 'monospace', fontWeight: '500' }}>
                                                        {student.rollNo}
                                                    </span>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <span style={{ fontWeight: '500' }}>{student.fullName}</span>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <span style={{ color: 'var(--text-muted)' }}>{student.email}</span>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <span style={{
                                                        background: '#374151',
                                                        padding: '0.25rem 0.75rem',
                                                        borderRadius: '0.375rem',
                                                        fontSize: '0.875rem'
                                                    }}>
                                                        {student.course}
                                                    </span>
                                                </td>
                                                <td style={tableCellStyle}>
                                                    <button
                                                        onClick={() => handleToggleActive(student.id)}
                                                        disabled={toggleLoading[student.id]}
                                                        style={{
                                                            padding: '0.5rem 1rem',
                                                            borderRadius: '0.375rem',
                                                            border: 'none',
                                                            fontWeight: '600',
                                                            fontSize: '0.875rem',
                                                            cursor: toggleLoading[student.id] ? 'wait' : 'pointer',
                                                            background: student.active ? '#10b981' : '#ef4444',
                                                            color: 'white',
                                                            transition: 'all 0.2s',
                                                            opacity: toggleLoading[student.id] ? 0.7 : 1
                                                        }}
                                                        onMouseEnter={(e) => {
                                                            if (!toggleLoading[student.id]) {
                                                                e.target.style.opacity = '0.8';
                                                            }
                                                        }}
                                                        onMouseLeave={(e) => {
                                                            if (!toggleLoading[student.id]) {
                                                                e.target.style.opacity = '1';
                                                            }
                                                        }}
                                                    >
                                                        {toggleLoading[student.id] ? 'Updating...' : (student.active ? 'Active' : 'Inactive')}
                                                    </button>
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="5" style={{
                                                padding: '3rem',
                                                textAlign: 'center',
                                                color: 'var(--text-muted)'
                                            }}>
                                                {showFiltered ? (
                                                    <>
                                                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>🔍</div>
                                                        <p>No students found matching the filters</p>
                                                    </>
                                                ) : (
                                                    <>
                                                        <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>👨‍🎓</div>
                                                        <p>Click "Show List" to load students</p>
                                                    </>
                                                )}
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                        {showFiltered && filteredStudents.length > 0 && (
                            <div style={{
                                padding: '1rem 1.5rem',
                                borderTop: '1px solid var(--border-color)',
                                fontSize: '0.875rem',
                                color: 'var(--text-muted)'
                            }}>
                                Showing {filteredStudents.length} of {students.length} students
                            </div>
                        )}
                    </div>
                )}

                {/* Add Student Modal */}
                <AddStudentModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    loading={loading}
                    onSubmit={async (studentData) => {
                        try {
                            await addStudent(studentData);
                            setIsModalOpen(false);
                            setToast({ message: 'Student added successfully!', type: 'success' });
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

export default AdminStudents;
