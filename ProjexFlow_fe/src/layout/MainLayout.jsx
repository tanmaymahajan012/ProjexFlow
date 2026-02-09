import React from 'react';
import { Outlet } from 'react-router-dom';

const MainLayout = ({ children }) => {
    return (
        <div className="main-layout" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
            <header style={{ padding: '1rem', background: '#1F2937', color: 'white', borderBottom: '1px solid #374151' }}>
                <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1 style={{ fontSize: '1.25rem' }}>ProjexFlow</h1>
                    <nav>
                        {/* Navigation links can go here */}
                    </nav>
                </div>
            </header>
            <main style={{ flex: 1, padding: '2rem 0' }}>
                <div className="container">
                    {children || <Outlet />}
                </div>
            </main>
            <footer style={{ padding: '1rem', textAlign: 'center', color: '#9CA3AF', background: '#111827', borderTop: '1px solid #374151' }}>
                &copy; {new Date().getFullYear()} ProjexFlow. All rights reserved.
            </footer>
        </div>
    );
};

export default MainLayout;
