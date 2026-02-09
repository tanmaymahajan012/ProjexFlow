import { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [role, setRole] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token') || null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const initializeUser = async () => {
            if (token) {
                try {
                    // Fetch user details if token exists
                    const response = await api.get('/api/v1/me');
                    setUser(response.data);
                    // Assuming role is part of user details from /me or stored separately
                    // But login response gives role. If refresh, we need to know role for layout 
                    // or just rely on user details having role.
                    // For now, let's assume /me returns role too. If not, we might need to store role in localStorage.
                    // But the prompt says "UI should store token, role... email... after successful login". 
                    // Let's store role in localStorage for persistence across refreshes if needed, 
                    // or just rely on /me. Let's persist role to be safe.
                    const storedRole = localStorage.getItem('role');
                    if (storedRole) setRole(storedRole);

                } catch (error) {
                    console.error("Failed to fetch user details", error);
                    logout();
                }
            }
            setLoading(false);
        };

        initializeUser();
    }, [token]);

    const login = async (email, password) => {
        try {
            const response = await api.post('/auth/login', { email, password });
            const { token, role: userRole, email: userEmail } = response.data;

            localStorage.setItem('token', token);
            localStorage.setItem('role', userRole);

            setToken(token);
            setRole(userRole);

            // Fetch full user details
            const userDetailsResponse = await api.get('/api/v1/me');
            setUser(userDetailsResponse.data);

            // Redirect logic will be handled by the component or here.
            // Let's return true or the role to the component to handle redirect.
            return userRole;
        } catch (error) {
            console.error("Login failed", error);
            throw error;
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        setRole(null);
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        navigate('/login');
    };

    return (
        <AuthContext.Provider value={{ user, role, token, login, logout, loading, isAuthenticated: !!token }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
