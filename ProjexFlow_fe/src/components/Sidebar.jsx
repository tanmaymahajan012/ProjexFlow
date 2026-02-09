import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Sidebar = ({ isOpen, toggleSidebar }) => {
    const { role } = useAuth();

    // Define navigation items based on user role
    const getNavItems = () => {
        const commonItems = [
            { name: 'Dashboard', path: '/dashboard', icon: '📊' },
            { name: 'Profile', path: '/profile', icon: '👤' },
        ];

        const roleSpecificItems = {
            ADMIN: [
                { name: 'User Management', path: '/admin/users', icon: '🛡️' },
                { name: 'Mentors', path: '/admin/mentors', icon: '📁' },
                { name: 'Students', path: '/admin/students', icon: '👥' },
                { name: 'Groups', path: '/admin/groups', icon: '📈' },
                { name: 'System Settings', path: '/admin/settings', icon: '⚙️' },
            ],
            STUDENT: [
                { name: 'My Projects', path: '/student/projects', icon: '📁' },
                { name: 'My Team', path: '/student/group', icon: '👥' },
                { name: 'Tasks', path: '/student/tasks', icon: '📤' },
                { name: 'Activity Logs', path: '/student/activity-logs', icon: '📚' },
            ],
            MENTOR: [
                { name: 'Groups', path: '/mentor/groups', icon: '📁' },
                { name: 'Tasks', path: '/mentor/tasks', icon: '👥' },
                { name: 'Submissions', path: '/mentor/submissions', icon: '📨' },
                { name: 'Status', path: '/mentor/status', icon: '💡' },
            ],
        };

        const specificItems = roleSpecificItems[role] || [];
        return [...commonItems, ...specificItems];
    };

    const navItems = getNavItems();

    return (
        <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
            <div className="sidebar-header">
                <span className="sidebar-logo">ProjexFlow</span>
            </div>
            <nav className="sidebar-nav">
                {navItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                        onClick={() => window.innerWidth < 768 && toggleSidebar()}
                    >
                        <span style={{ marginRight: '12px' }}>{item.icon}</span>
                        {item.name}
                    </NavLink>
                ))}
            </nav>
            <div className="sidebar-footer" style={{ padding: '1.5rem', borderTop: '1px solid var(--border-color)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    v1.0.0
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;
