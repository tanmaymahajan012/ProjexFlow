import React from 'react';

const Toast = ({ message, type, onClose }) => {
    const getStyles = () => {
        const baseStyles = {
            position: 'fixed',
            top: '2rem',
            right: '2rem',
            padding: '1rem 1.5rem',
            borderRadius: 'var(--radius)',
            color: 'white',
            fontWeight: '600',
            fontSize: '0.875rem',
            zIndex: 9999,
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            minWidth: '300px',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.3)',
            animation: 'slideIn 0.3s ease-out'
        };

        const typeStyles = {
            success: {
                background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                border: '1px solid #059669'
            },
            error: {
                background: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
                border: '1px solid #dc2626'
            }
        };

        return { ...baseStyles, ...typeStyles[type] };
    };

    const getIcon = () => {
        return type === 'success' ? '✓' : '✕';
    };

    React.useEffect(() => {
        const timer = setTimeout(() => {
            onClose();
        }, 3000);

        return () => clearTimeout(timer);
    }, [onClose]);

    return (
        <>
            <div style={getStyles()}>
                <span style={{
                    fontSize: '1.25rem',
                    width: '24px',
                    height: '24px',
                    borderRadius: '50%',
                    background: 'rgba(255, 255, 255, 0.2)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    {getIcon()}
                </span>
                <span>{message}</span>
                <button
                    onClick={onClose}
                    style={{
                        marginLeft: 'auto',
                        background: 'transparent',
                        border: 'none',
                        color: 'white',
                        cursor: 'pointer',
                        fontSize: '1.25rem',
                        padding: '0',
                        opacity: 0.8
                    }}
                    onMouseEnter={(e) => e.target.style.opacity = '1'}
                    onMouseLeave={(e) => e.target.style.opacity = '0.8'}
                >
                    ×
                </button>
            </div>
            <style>{`
                @keyframes slideIn {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            `}</style>
        </>
    );
};

export default Toast;
