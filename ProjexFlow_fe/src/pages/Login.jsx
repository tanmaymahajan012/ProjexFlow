import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';
import Input from '../components/Input';
import '../index.css';

const Login = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [errors, setErrors] = useState({});
    const { login, loading } = useAuth();
    const navigate = useNavigate();
    const [submitError, setSubmitError] = useState('');

    const validate = () => {
        let tempErrors = {};
        if (!formData.email) tempErrors.email = "Email is required";
        else if (!/\S+@\S+\.\S+/.test(formData.email)) tempErrors.email = "Email is invalid";

        if (!formData.password) tempErrors.password = "Password is required";

        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        // Clear error when user types
        if (errors[e.target.name]) {
            setErrors({ ...errors, [e.target.name]: '' });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitError('');
        if (validate()) {
            try {
                await login(formData.email, formData.password);
                Swal.fire({
                    title: 'Welcome Back!',
                    text: 'Login Successful',
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    navigate('/dashboard');
                });
            } catch (error) {
                console.error("Login failed", error);

                if (error.code === "ERR_NETWORK" || error.message === "Network Error") {
                    Swal.fire({
                        title: 'Website Under Maintenance',
                        text: 'Please try again later.',
                        icon: 'error',
                        confirmButtonText: 'OK'
                    });
                } else if (error.response && (error.response.status === 401 || error.response.status === 403)) {
                    Swal.fire({
                        title: 'Invalid Email or Password',
                        text: 'Please check your credentials and try again.',
                        icon: 'error',
                        confirmButtonText: 'Retry'
                    });
                } else {
                    Swal.fire({
                        title: 'Error',
                        text: 'Something went wrong. Please try again.',
                        icon: 'error',
                        confirmButtonText: 'OK'
                    });
                }
            }
        }
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-card">
                <h1 className="auth-title">ProjexFlow Login</h1>
                {submitError && <div className="error-msg mb-4 text-center">{submitError}</div>}

                <form onSubmit={handleSubmit}>
                    <Input
                        label="Email Address"
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Enter your email"
                        error={errors.email}
                        required
                    />

                    <Input
                        label="Password"
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Enter your password"
                        error={errors.password}
                        required
                    />

                    <button type="submit" className="btn mt-4" disabled={loading}>
                        {loading ? 'Logging in...' : 'Login'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;
