import React from 'react';
import '../index.css';

const Input = ({ label, type = 'text', name, value, onChange, placeholder, required = false, error }) => {
    return (
        <div className="form-group">
            {label && <label htmlFor={name} className="form-label">{label}</label>}
            <input
                type={type}
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                required={required}
                className={`form-input ${error ? 'error' : ''}`}
            />
            {error && <span className="error-msg">{error}</span>}
        </div>
    );
};

export default Input;
