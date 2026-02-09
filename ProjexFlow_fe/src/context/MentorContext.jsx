import { createContext, useState, useContext, useEffect } from 'react';
import { fetchAllMentors, updateMentorActiveStatus, createMentor } from '../api/mentorApi';

const MentorContext = createContext();

export const MentorProvider = ({ children }) => {
    const [mentors, setMentors] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Fetch all mentors on mount (REMOVED: Fetching is now triggered manually)
    // useEffect(() => {
    //     loadMentors();
    // }, []);

    const loadMentors = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await fetchAllMentors();
            setMentors(data);
        } catch (err) {
            console.error('Failed to fetch mentors:', err);
            setError(err.message || 'Failed to fetch mentors');
        } finally {
            setLoading(false);
        }
    };

    const toggleMentorActiveStatus = async (mentorId) => {
        try {
            // Find the mentor to get current active status
            const mentor = mentors.find(m => m.id === mentorId);
            if (!mentor) {
                throw new Error('Mentor not found');
            }

            // Call API to update status
            const updatedMentor = await updateMentorActiveStatus(mentorId, !mentor.active);

            // Update local state with the response
            setMentors(prevMentors =>
                prevMentors.map(m =>
                    m.id === mentorId ? updatedMentor : m
                )
            );

            return updatedMentor;
        } catch (err) {
            console.error('Failed to update mentor status:', err);
            throw err;
        }
    };

    const addMentor = async (mentorData) => {
        try {
            setLoading(true);
            setError(null);
            const newMentor = await createMentor(mentorData);
            await loadMentors();
            return newMentor;
        } catch (err) {
            console.error('Failed to create mentor:', err);
            setError(err.message || 'Failed to create mentor');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return (
        <MentorContext.Provider value={{
            mentors,
            loading,
            error,
            loadMentors,
            toggleMentorActiveStatus,
            addMentor
        }}>
            {children}
        </MentorContext.Provider>
    );
};

export const useMentors = () => useContext(MentorContext);
