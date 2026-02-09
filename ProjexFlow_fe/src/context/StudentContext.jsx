import { createContext, useState, useContext } from 'react';
import { fetchAllStudents, updateStudentActiveStatus, createStudent } from '../api/studentApi';

const StudentContext = createContext();

export const StudentProvider = ({ children }) => {
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Fetch is triggered manually, not on mount
    const loadStudents = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await fetchAllStudents();
            setStudents(data);
        } catch (err) {
            console.error('Failed to fetch students:', err);
            setError(err.message || 'Failed to fetch students');
        } finally {
            setLoading(false);
        }
    };

    const toggleStudentActiveStatus = async (studentId) => {
        try {
            // Find the student to get current active status
            const student = students.find(s => s.id === studentId);
            if (!student) {
                throw new Error('Student not found');
            }

            // Call API to update status
            const updatedStudent = await updateStudentActiveStatus(studentId, !student.active);

            // Update local state with the response
            setStudents(prevStudents =>
                prevStudents.map(s =>
                    s.id === studentId ? updatedStudent : s
                )
            );

            return updatedStudent;
        } catch (err) {
            console.error('Failed to update student status:', err);
            throw err;
        }
    };

    const addStudent = async (studentData) => {
        try {
            setLoading(true);
            setError(null);
            const newStudent = await createStudent(studentData);
            await loadStudents();
            return newStudent;
        } catch (err) {
            console.error('Failed to create student:', err);
            setError(err.message || 'Failed to create student');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return (
        <StudentContext.Provider value={{
            students,
            loading,
            error,
            loadStudents,
            toggleStudentActiveStatus,
            addStudent
        }}>
            {children}
        </StudentContext.Provider>
    );
};

export const useStudents = () => useContext(StudentContext);
