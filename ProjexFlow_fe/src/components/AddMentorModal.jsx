import React, { useState } from 'react';

const AddMentorModal = ({ isOpen, onClose, onSubmit, loading }) => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
        profilePhotoUrl: '',
        active: true,
        course: '',
        empId: '',
        department: ''
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
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
            course: '',
            empId: '',
            department: ''
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
                    Add New Mentor
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
                                placeholder="mentor@example.com"
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

                        {/* Employee ID */}
                        <div>
                            <label style={labelStyle}>Employee ID *</label>
                            <input
                                type="text"
                                name="empId"
                                value={formData.empId}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="EMP-IT-1021"
                            />
                        </div>

                        {/* Department */}
                        <div>
                            <label style={labelStyle}>Department *</label>
                            <input
                                type="text"
                                name="department"
                                value={formData.department}
                                onChange={handleChange}
                                required
                                style={inputStyle}
                                placeholder="Information Technology"
                            />
                        </div>

                        {/* Active Status */}
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <input
                                type="checkbox"
                                name="active"
                                checked={formData.active}
                                onChange={handleChange}
                                id="active"
                            />
                            <label htmlFor="active" style={{ fontSize: '0.875rem', cursor: 'pointer' }}>
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

export default AddMentorModal;
