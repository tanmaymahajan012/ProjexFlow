import React, { useState } from 'react';

const AddStudentModal = ({ isOpen, onClose, onSubmit, loading }) => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
        profilePhotoUrl: '',
        active: true,
        rollNo: '',
        prn: '',
        githubUrl: '',
        course: '',
        batchId: ''
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : (name === 'batchId' ? parseInt(value) || '' : value)
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSubmit(formData);
    };

    const handleClear = () => {
        setFormData({
            fullName: '',
            email: '',
            password: '',
            profilePhotoUrl: '',
            active: true,
            rollNo: '',
            prn: '',
            githubUrl: '',
            course: '',
            batchId: ''
        });
    };

    if (!isOpen) return null;

    return (
        <div style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000
        }}>
            <div style={{
                background: 'var(--card-bg)',
                borderRadius: 'var(--radius)',
                padding: '2rem',
                maxWidth: '600px',
                width: '90%',
                maxHeight: '90vh',
                overflowY: 'auto',
                border: '1px solid var(--border-color)'
            }}>
                <h2 style={{ marginBottom: '1.5rem', fontSize: '1.5rem', fontWeight: '700' }}>
                    Add New Student
                </h2>

                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'grid', gap: '1rem' }}>
                        {/* Full Name */}
                        <div>
                            <label style={labelStyle}>Full Name *</label>
                            <input
                                type="text"
                                name="fullName"
                                value={formData.fullName}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="Enter full name"
                            />
                        </div>

                        {/* Email */}
                        <div>
                            <label style={labelStyle}>Email *</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="student@example.com"
                            />
                        </div>

                        {/* Password */}
                        <div>
                            <label style={labelStyle}>Password *</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="Enter password"
                            />
                        </div>

                        {/* Profile Photo URL */}
                        <div>
                            <label style={labelStyle}>Profile Photo URL *</label>
                            <input
                                type="url"
                                name="profilePhotoUrl"
                                value={formData.profilePhotoUrl}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="https://example.com/photo.jpg"
                            />
                        </div>

                        {/* Roll No */}
                        <div>
                            <label style={labelStyle}>Roll No *</label>
                            <input
                                type="text"
                                name="rollNo"
                                value={formData.rollNo}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="IT-24-003"
                            />
                        </div>

                        {/* PRN */}
                        <div>
                            <label style={labelStyle}>PRN *</label>
                            <input
                                type="text"
                                name="prn"
                                value={formData.prn}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="PRN2024IT0003"
                            />
                        </div>

                        {/* GitHub URL */}
                        <div>
                            <label style={labelStyle}>GitHub URL *</label>
                            <input
                                type="url"
                                name="githubUrl"
                                value={formData.githubUrl}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="https://github.com/username"
                            />
                        </div>

                        {/* Course */}
                        <div>
                            <label style={labelStyle}>Course *</label>
                            <input
                                type="text"
                                name="course"
                                value={formData.course}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="B.Tech IT"
                            />
                        </div>

                        {/* Batch ID */}
                        <div>
                            <label style={labelStyle}>Batch ID *</label>
                            <input
                                type="number"
                                name="batchId"
                                value={formData.batchId}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="2024"
                            />
                        </div>

                        {/* Active Status */}
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <input
                                type="checkbox"
                                name="active"
                                checked={formData.active}
                                onChange={handleChange}
                                id="active-student"
                            />
                            <label htmlFor="active-student" style={{ fontSize: '0.875rem', cursor: 'pointer' }}>
                                Active
                            </label>
                        </div>
                    </div>

                    {/* Buttons */}
                    <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                        <button
                            type="button"
                            onClick={handleClear}
                            disabled={loading}
                            style={{
                                flex: 1,
                                padding: '0.75rem',
                                borderRadius: 'var(--radius)',
                                border: '1px solid var(--border-color)',
                                background: 'transparent',
                                color: 'var(--text-color)',
                                cursor: loading ? 'not-allowed' : 'pointer',
                                fontSize: '0.875rem',
                                fontWeight: '600'
                            }}
                        >
                            Clear
                        </button>
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                            style={{
                                flex: 1,
                                padding: '0.75rem',
                                borderRadius: 'var(--radius)',
                                border: '1px solid var(--border-color)',
                                background: '#6b7280',
                                color: 'white',
                                cursor: loading ? 'not-allowed' : 'pointer',
                                fontSize: '0.875rem',
                                fontWeight: '600'
                            }}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="btn"
                            style={{
                                flex: 1,
                                padding: '0.75rem',
                                fontSize: '0.875rem',
                                opacity: loading ? 0.7 : 1,
                                cursor: loading ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? 'Creating...' : 'Submit'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const labelStyle = {
    display: 'block',
    fontSize: '0.875rem',
    fontWeight: '500',
    marginBottom: '0.5rem',
    color: 'var(--text-color)'
};

const inputStyle = {
    width: '100%',
    padding: '0.75rem',
    border: '1px solid var(--border-color)',
    borderRadius: 'var(--radius)',
    background: 'var(--input-bg)',
    color: 'var(--text-color)',
    fontSize: '0.875rem'
};

export default AddStudentModal;
