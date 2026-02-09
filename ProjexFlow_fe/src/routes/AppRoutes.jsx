import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Profile from '../pages/Profile';
import AdminMentors from '../pages/admin/AdminMentors';
import AdminStudents from '../pages/admin/AdminStudents';
import AdminGroups from '../pages/admin/AdminGroups';
import MentorGroups from '../pages/mentor/MentorGroups';
import MentorSubmissions from '../pages/mentor/MentorSubmissions';
import MentorTasks from '../pages/mentor/MentorTasks';
import StudentTeam from '../pages/student/StudentTeam';
import StudentTasks from '../pages/student/StudentTasks';
import StudentProjects from '../pages/student/StudentProjects';
import StudentActivityLogs from '../pages/student/StudentActivityLogs';



const ProtectedRoute = ({ children }) => {
    const { isAuthenticated, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return <div>Loading...</div>; // Or a spinner component
    }

    if (!isAuthenticated) {
        // Redirect to login page but remember where they were trying to go
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    return children;
};

const PublicRoute = ({ children }) => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return <div>Loading...</div>;
    }

    if (isAuthenticated) {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
};

const AppRoutes = () => {
    return (
        <Routes>
            {/* Public Routes */}
            <Route path="/login" element={
                <PublicRoute>
                    <Login />
                </PublicRoute>
            } />

            {/* Protected Routes */}
            <Route path="/dashboard" element={
                <ProtectedRoute>
                    <Dashboard />
                </ProtectedRoute>
            } />

            <Route path="/profile" element={
                <ProtectedRoute>
                    <Profile />
                </ProtectedRoute>
            } />

            <Route path="/admin/mentors" element={
                <ProtectedRoute>
                    <AdminMentors />
                </ProtectedRoute>
            } />

            <Route path="/admin/students" element={
                <ProtectedRoute>
                    <AdminStudents />
                </ProtectedRoute>
            } />

            <Route path="/admin/groups" element={
                <ProtectedRoute>
                    <AdminGroups />
                </ProtectedRoute>
            } />

            <Route path="/mentor/groups" element={
                <ProtectedRoute>
                    <MentorGroups />
                </ProtectedRoute>
            } />

            <Route path="/mentor/tasks" element={
                <ProtectedRoute>
                    <MentorTasks />
                </ProtectedRoute>
            } />

            <Route path="/mentor/submissions" element={
                <ProtectedRoute>
                    <MentorSubmissions />
                </ProtectedRoute>
            } />

            <Route path="/student/group" element={
                <ProtectedRoute>
                    <StudentTeam />
                </ProtectedRoute>
            } />

            <Route path="/student/tasks" element={
                <ProtectedRoute>
                    <StudentTasks />
                </ProtectedRoute>
            } />

            <Route path="/student/projects" element={
                <ProtectedRoute>
                    <StudentProjects />
                </ProtectedRoute>
            } />

            <Route path="/student/activity-logs" element={
                <ProtectedRoute>
                    <StudentActivityLogs />
                </ProtectedRoute>
            } />


            {/* Default Route */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />

            {/* Catch-all */}
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
};

export default AppRoutes;
