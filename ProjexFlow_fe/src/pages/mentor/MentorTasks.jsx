import React, { useState, useEffect } from 'react';
import DashboardLayout from '../../layout/DashboardLayout';
import { getBatchIds } from '../../api/groupApi';
import {
    createTask,
    getTasksByBatch,
    assignTaskToAll,
    getMentorGroupIds,
    assignTaskToGroups
} from '../../api/taskApi';
import { useAuth } from '../../context/AuthContext';
import Toast from '../../components/Toast';

const MentorTasks = () => {
    const { user } = useAuth();
    const [batchIds, setBatchIds] = useState([]);
    const [selectedBatchId, setSelectedBatchId] = useState('');
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [toast, setToast] = useState(null);

    // Modal states
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showAssignAllModal, setShowAssignAllModal] = useState(false);
    const [showAssignGroupsModal, setShowAssignGroupsModal] = useState(false);
    const [selectedTask, setSelectedTask] = useState(null);
    const [activeDropdown, setActiveDropdown] = useState(null);

    // Form states
    const [newTask, setNewTask] = useState({
        batchId: '',
        title: '',
        description: '',
        instructions: '',
        defaultDueAt: ''
    });

    const [availableGroups, setAvailableGroups] = useState([]);
    const [selectedGroups, setSelectedGroups] = useState([]);

    // Fetch batch IDs on mount
    useEffect(() => {
        fetchBatchIds();
    }, []);

    // Update newTask batchId when selectedBatchId changes
    useEffect(() => {
        if (selectedBatchId) {
            setNewTask(prev => ({ ...prev, batchId: parseInt(selectedBatchId) }));
        }
    }, [selectedBatchId]);

    const fetchBatchIds = async () => {
        try {
            const data = await getBatchIds();
            setBatchIds(data);
        } catch (err) {
            console.error('Failed to fetch batch IDs:', err);
            setToast({ message: 'Failed to load batch IDs', type: 'error' });
        }
    };

    const handleShowTasks = async () => {
        if (!selectedBatchId) return;

        try {
            setLoading(true);
            const data = await getTasksByBatch(parseInt(selectedBatchId));
            setTasks(data);
        } catch (err) {
            console.error('Failed to fetch tasks:', err);
            const msg = err.response?.data?.message || '';
            if (msg.toLowerCase().includes('grouping')) {
                setToast({ message: 'Grouping is not completed for this batch yet.', type: 'warning' });
            } else {
                setToast({ message: 'Failed to load tasks', type: 'error' });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleCreateTask = async () => {
        if (!newTask.title || !newTask.description || !newTask.instructions || !newTask.defaultDueAt) {
            setToast({ message: 'Please fill all fields', type: 'error' });
            return;
        }

        try {
            await createTask(newTask);
            setToast({ message: 'Task created successfully!', type: 'success' });
            setShowCreateModal(false);
            clearForm();
            // Refresh tasks if a batch is selected
            if (selectedBatchId) {
                handleShowTasks();
            }
        } catch (err) {
            console.error('Failed to create task:', err);
            setToast({ message: 'Failed to create task', type: 'error' });
        }
    };

    const clearForm = () => {
        setNewTask({
            batchId: selectedBatchId ? parseInt(selectedBatchId) : '',
            title: '',
            description: '',
            instructions: '',
            defaultDueAt: ''
        });
    };

    const handleAssignToAll = async () => {
        if (!selectedTask || !selectedBatchId) return;

        try {
            const result = await assignTaskToAll(selectedTask.id, selectedBatchId);
            setToast({
                message: `Task assigned to ${result.created} groups successfully!`,
                type: 'success'
            });
            setShowAssignAllModal(false);
            setSelectedTask(null);
            setActiveDropdown(null);
        } catch (err) {
            console.error('Failed to assign task to all:', err);
            setToast({ message: 'Failed to assign task', type: 'error' });
        }
    };

    const handleOpenAssignGroups = async (task) => {
        setSelectedTask(task);
        setActiveDropdown(null);

        if (!user?.id || !selectedBatchId) {
            setToast({ message: 'Missing user or batch information', type: 'error' });
            return;
        }

        try {
            const groupIds = await getMentorGroupIds(user.id, selectedBatchId);
            setAvailableGroups(groupIds);
            setSelectedGroups([]);
            setShowAssignGroupsModal(true);
        } catch (err) {
            console.error('Failed to fetch group IDs:', err);
            setToast({ message: 'Failed to load groups', type: 'error' });
        }
    };

    const handleAssignToGroups = async () => {
        if (!selectedTask || selectedGroups.length === 0) {
            setToast({ message: 'Please select at least one group', type: 'error' });
            return;
        }

        try {
            const result = await assignTaskToGroups(selectedTask.id, {
                batchId: parseInt(selectedBatchId),
                groupIds: selectedGroups
            });
            setToast({
                message: `Task assigned to ${result.created} groups successfully!`,
                type: 'success'
            });
            setShowAssignGroupsModal(false);
            setSelectedTask(null);
            setSelectedGroups([]);
        } catch (err) {
            console.error('Failed to assign task to groups:', err);
            setToast({ message: 'Failed to assign task', type: 'error' });
        }
    };

    const toggleGroupSelection = (groupId) => {
        setSelectedGroups(prev =>
            prev.includes(groupId)
                ? prev.filter(id => id !== groupId)
                : [...prev, groupId]
        );
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        return dateString.split('T')[0];
    };

    return (
        <DashboardLayout>
            <div className="mentor-tasks-container">
                {/* Page Header */}
                <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.25rem' }}>
                        Task Management
                    </h1>
                    <p style={{ color: 'var(--text-muted)' }}>
                        Create and assign tasks to your student groups
                    </p>
                </div>

                {/* Controls Section */}
                <div style={{
                    background: 'var(--card-bg)',
                    padding: '1.5rem',
                    borderRadius: 'var(--radius)',
                    border: '1px solid var(--border-color)',
                    marginBottom: '1.5rem'
                }}>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                        {/* Batch Selection */}
                        <div style={{ flex: '1', minWidth: '200px' }}>
                            <label style={{
                                display: 'block',
                                fontSize: '0.875rem',
                                fontWeight: '500',
                                marginBottom: '0.5rem',
                                color: 'var(--text-color)'
                            }}>
                                Select Batch ID
                            </label>
                            <select
                                value={selectedBatchId}
                                onChange={(e) => setSelectedBatchId(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '1px solid var(--border-color)',
                                    borderRadius: 'var(--radius)',
                                    background: 'var(--input-bg)',
                                    color: 'var(--text-color)',
                                    fontSize: '0.875rem'
                                }}
                            >
                                <option value="">Select a batch...</option>
                                {batchIds.map(batchId => (
                                    <option key={batchId} value={batchId}>
                                        Batch {batchId}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Create Task Button */}
                        <button
                            onClick={() => setShowCreateModal(true)}
                            className="btn"
                            style={{
                                padding: '0.75rem 1.5rem',
                                fontSize: '0.875rem',
                                background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
                            }}
                        >
                            + Create New Task
                        </button>

                        {/* Show Tasks Button */}
                        <button
                            onClick={handleShowTasks}
                            disabled={!selectedBatchId || loading}
                            className="btn"
                            style={{
                                padding: '0.75rem 1.5rem',
                                fontSize: '0.875rem',
                                opacity: (!selectedBatchId || loading) ? 0.5 : 1,
                                cursor: (!selectedBatchId || loading) ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? 'Loading...' : 'Show Tasks'}
                        </button>
                    </div>
                </div>

                {/* Tasks Display */}
                {tasks.length > 0 && (
                    <div style={{
                        display: 'grid',
                        gap: '1rem',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))'
                    }}>
                        {tasks.map((task) => (
                            <div
                                key={task.id}
                                style={{
                                    background: 'var(--card-bg)',
                                    borderRadius: 'var(--radius)',
                                    border: '1px solid var(--border-color)',
                                    padding: '1.25rem',
                                    position: 'relative',
                                    transition: 'all 0.2s'
                                }}
                            >
                                {/* Dropdown Menu */}
                                <div style={{ position: 'absolute', top: '1rem', right: '1rem' }}>
                                    <button
                                        onClick={() => setActiveDropdown(activeDropdown === task.id ? null : task.id)}
                                        style={{
                                            background: 'var(--input-bg)',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            padding: '0.5rem',
                                            cursor: 'pointer',
                                            fontSize: '1rem'
                                        }}
                                    >
                                        ⋮
                                    </button>

                                    {activeDropdown === task.id && (
                                        <div style={{
                                            position: 'absolute',
                                            top: '100%',
                                            right: 0,
                                            marginTop: '0.5rem',
                                            background: 'var(--card-bg)',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            boxShadow: '0 4px 6px rgba(0, 0, 0, 0.3)',
                                            zIndex: 10,
                                            minWidth: '150px'
                                        }}>
                                            <button
                                                onClick={() => {
                                                    setSelectedTask(task);
                                                    setShowAssignAllModal(true);
                                                    setActiveDropdown(null);
                                                }}
                                                style={{
                                                    width: '100%',
                                                    padding: '0.75rem 1rem',
                                                    background: 'transparent',
                                                    border: 'none',
                                                    textAlign: 'left',
                                                    cursor: 'pointer',
                                                    fontSize: '0.875rem',
                                                    color: 'var(--text-color)',
                                                    borderBottom: '1px solid var(--border-color)'
                                                }}
                                                onMouseEnter={(e) => e.target.style.background = '#1f2937'}
                                                onMouseLeave={(e) => e.target.style.background = 'transparent'}
                                            >
                                                Assign to All
                                            </button>
                                            <button
                                                onClick={() => handleOpenAssignGroups(task)}
                                                style={{
                                                    width: '100%',
                                                    padding: '0.75rem 1rem',
                                                    background: 'transparent',
                                                    border: 'none',
                                                    textAlign: 'left',
                                                    cursor: 'pointer',
                                                    fontSize: '0.875rem',
                                                    color: 'var(--text-color)'
                                                }}
                                                onMouseEnter={(e) => e.target.style.background = '#1f2937'}
                                                onMouseLeave={(e) => e.target.style.background = 'transparent'}
                                            >
                                                Assign to Groups
                                            </button>
                                        </div>
                                    )}
                                </div>

                                {/* Task Details */}
                                <div style={{ paddingRight: '2rem' }}>
                                    <div style={{
                                        display: 'inline-block',
                                        padding: '0.25rem 0.75rem',
                                        background: '#374151',
                                        borderRadius: '0.25rem',
                                        fontSize: '0.75rem',
                                        fontWeight: '600',
                                        marginBottom: '0.75rem'
                                    }}>
                                        Task #{task.id}
                                    </div>

                                    <h3 style={{
                                        fontSize: '1.25rem',
                                        fontWeight: '700',
                                        marginBottom: '0.75rem'
                                    }}>
                                        {task.title}
                                    </h3>

                                    <div style={{ marginBottom: '0.75rem' }}>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)',
                                            marginBottom: '0.25rem'
                                        }}>
                                            Description:
                                        </p>
                                        <p style={{ fontSize: '0.875rem' }}>
                                            {task.description}
                                        </p>
                                    </div>

                                    <div style={{ marginBottom: '0.75rem' }}>
                                        <p style={{
                                            fontSize: '0.75rem',
                                            color: 'var(--text-muted)',
                                            marginBottom: '0.25rem'
                                        }}>
                                            Instructions:
                                        </p>
                                        <p style={{ fontSize: '0.875rem' }}>
                                            {task.instructions}
                                        </p>
                                    </div>

                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        marginTop: '1rem',
                                        paddingTop: '1rem',
                                        borderTop: '1px solid var(--border-color)'
                                    }}>
                                        <div>
                                            <p style={{
                                                fontSize: '0.75rem',
                                                color: 'var(--text-muted)'
                                            }}>
                                                Due Date:
                                            </p>
                                            <p style={{
                                                fontSize: '0.875rem',
                                                fontWeight: '600',
                                                marginTop: '0.25rem'
                                            }}>
                                                {formatDate(task.defaultDueAt)}
                                            </p>
                                        </div>
                                        <div>
                                            <span style={{
                                                display: 'inline-block',
                                                padding: '0.25rem 0.75rem',
                                                borderRadius: '0.25rem',
                                                fontSize: '0.75rem',
                                                fontWeight: '600',
                                                background: task.active ? '#10b981' : '#dc2626',
                                                color: 'white'
                                            }}>
                                                {task.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* Empty State */}
                {!loading && tasks.length === 0 && selectedBatchId && (
                    <div style={{
                        background: 'var(--card-bg)',
                        padding: '3rem',
                        borderRadius: 'var(--radius)',
                        border: '1px solid var(--border-color)',
                        textAlign: 'center'
                    }}>
                        <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📋</div>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.125rem' }}>
                            No tasks found for this batch
                        </p>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.5rem' }}>
                            Create a new task or click "Show Tasks" to load existing tasks
                        </p>
                    </div>
                )}

                {/* Create Task Modal */}
                {showCreateModal && (
                    <div style={{
                        position: 'fixed',
                        inset: 0,
                        background: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            background: 'var(--card-bg)',
                            borderRadius: 'var(--radius)',
                            padding: '2rem',
                            maxWidth: '500px',
                            width: '90%',
                            border: '1px solid var(--border-color)',
                            maxHeight: '90vh',
                            overflowY: 'auto'
                        }}>
                            <h3 style={{
                                fontSize: '1.5rem',
                                fontWeight: '700',
                                marginBottom: '1.5rem'
                            }}>
                                Create New Task
                            </h3>

                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                <div>
                                    <label style={{
                                        display: 'block',
                                        fontSize: '0.875rem',
                                        fontWeight: '500',
                                        marginBottom: '0.5rem'
                                    }}>
                                        Title *
                                    </label>
                                    <input
                                        type="text"
                                        value={newTask.title}
                                        onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            background: 'var(--input-bg)',
                                            color: 'var(--text-color)',
                                            fontSize: '0.875rem'
                                        }}
                                        placeholder="Enter task title"
                                    />
                                </div>

                                <div>
                                    <label style={{
                                        display: 'block',
                                        fontSize: '0.875rem',
                                        fontWeight: '500',
                                        marginBottom: '0.5rem'
                                    }}>
                                        Description *
                                    </label>
                                    <textarea
                                        value={newTask.description}
                                        onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
                                        rows={3}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            background: 'var(--input-bg)',
                                            color: 'var(--text-color)',
                                            fontSize: '0.875rem',
                                            resize: 'vertical'
                                        }}
                                        placeholder="Enter task description"
                                    />
                                </div>

                                <div>
                                    <label style={{
                                        display: 'block',
                                        fontSize: '0.875rem',
                                        fontWeight: '500',
                                        marginBottom: '0.5rem'
                                    }}>
                                        Instructions *
                                    </label>
                                    <textarea
                                        value={newTask.instructions}
                                        onChange={(e) => setNewTask({ ...newTask, instructions: e.target.value })}
                                        rows={3}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            background: 'var(--input-bg)',
                                            color: 'var(--text-color)',
                                            fontSize: '0.875rem',
                                            resize: 'vertical'
                                        }}
                                        placeholder="Enter task instructions"
                                    />
                                </div>

                                <div>
                                    <label style={{
                                        display: 'block',
                                        fontSize: '0.875rem',
                                        fontWeight: '500',
                                        marginBottom: '0.5rem'
                                    }}>
                                        Due Date *
                                    </label>
                                    <input
                                        type="datetime-local"
                                        value={newTask.defaultDueAt}
                                        onChange={(e) => setNewTask({ ...newTask, defaultDueAt: e.target.value })}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid var(--border-color)',
                                            borderRadius: 'var(--radius)',
                                            background: 'var(--input-bg)',
                                            color: 'var(--text-color)',
                                            fontSize: '0.875rem'
                                        }}
                                    />
                                </div>
                            </div>

                            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                                <button
                                    onClick={clearForm}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#6b7280',
                                        color: 'white',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600'
                                    }}
                                >
                                    Clear
                                </button>
                                <button
                                    onClick={() => {
                                        setShowCreateModal(false);
                                        clearForm();
                                    }}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#374151',
                                        color: 'white',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600'
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleCreateTask}
                                    className="btn"
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        fontSize: '0.875rem'
                                    }}
                                >
                                    Add
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Assign to All Modal */}
                {showAssignAllModal && selectedTask && (
                    <div style={{
                        position: 'fixed',
                        inset: 0,
                        background: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            background: 'var(--card-bg)',
                            borderRadius: 'var(--radius)',
                            padding: '2rem',
                            maxWidth: '400px',
                            width: '90%',
                            border: '1px solid var(--border-color)'
                        }}>
                            <h3 style={{
                                fontSize: '1.25rem',
                                fontWeight: '700',
                                marginBottom: '1rem'
                            }}>
                                Assign to All Groups
                            </h3>
                            <p style={{
                                color: 'var(--text-muted)',
                                marginBottom: '1.5rem',
                                fontSize: '0.9375rem'
                            }}>
                                Are you sure you want to assign "<strong>{selectedTask.title}</strong>" to all groups in Batch {selectedBatchId}?
                            </p>
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button
                                    onClick={() => {
                                        setShowAssignAllModal(false);
                                        setSelectedTask(null);
                                    }}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#6b7280',
                                        color: 'white',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600'
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleAssignToAll}
                                    className="btn"
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        fontSize: '0.875rem'
                                    }}
                                >
                                    Confirm
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Assign to Groups Modal */}
                {showAssignGroupsModal && selectedTask && (
                    <div style={{
                        position: 'fixed',
                        inset: 0,
                        background: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            background: 'var(--card-bg)',
                            borderRadius: 'var(--radius)',
                            padding: '2rem',
                            maxWidth: '400px',
                            width: '90%',
                            border: '1px solid var(--border-color)',
                            maxHeight: '80vh',
                            overflowY: 'auto'
                        }}>
                            <h3 style={{
                                fontSize: '1.25rem',
                                fontWeight: '700',
                                marginBottom: '1rem'
                            }}>
                                Assign to Groups
                            </h3>
                            <p style={{
                                color: 'var(--text-muted)',
                                marginBottom: '1rem',
                                fontSize: '0.875rem'
                            }}>
                                Select groups to assign "<strong>{selectedTask.title}</strong>"
                            </p>

                            {availableGroups.length > 0 ? (
                                <div style={{
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '0.75rem',
                                    marginBottom: '1.5rem'
                                }}>
                                    {availableGroups.map(groupId => (
                                        <label
                                            key={groupId}
                                            style={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                padding: '0.75rem',
                                                background: selectedGroups.includes(groupId) ? '#1f2937' : 'var(--input-bg)',
                                                border: '1px solid var(--border-color)',
                                                borderRadius: 'var(--radius)',
                                                cursor: 'pointer',
                                                transition: 'all 0.2s'
                                            }}
                                        >
                                            <input
                                                type="checkbox"
                                                checked={selectedGroups.includes(groupId)}
                                                onChange={() => toggleGroupSelection(groupId)}
                                                style={{
                                                    marginRight: '0.75rem',
                                                    width: '1.25rem',
                                                    height: '1.25rem',
                                                    cursor: 'pointer'
                                                }}
                                            />
                                            <span style={{ fontSize: '0.875rem', fontWeight: '600' }}>
                                                Group {groupId}
                                            </span>
                                        </label>
                                    ))}
                                </div>
                            ) : (
                                <p style={{
                                    textAlign: 'center',
                                    color: 'var(--text-muted)',
                                    padding: '2rem',
                                    marginBottom: '1.5rem'
                                }}>
                                    No groups available
                                </p>
                            )}

                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button
                                    onClick={() => {
                                        setShowAssignGroupsModal(false);
                                        setSelectedTask(null);
                                        setSelectedGroups([]);
                                    }}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        borderRadius: 'var(--radius)',
                                        border: '1px solid var(--border-color)',
                                        background: '#6b7280',
                                        color: 'white',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        fontWeight: '600'
                                    }}
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleAssignToGroups}
                                    className="btn"
                                    disabled={selectedGroups.length === 0}
                                    style={{
                                        flex: 1,
                                        padding: '0.75rem',
                                        fontSize: '0.875rem',
                                        opacity: selectedGroups.length === 0 ? 0.5 : 1,
                                        cursor: selectedGroups.length === 0 ? 'not-allowed' : 'pointer'
                                    }}
                                >
                                    Assign
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Toast Notification */}
                {toast && (
                    <Toast
                        message={toast.message}
                        type={toast.type}
                        onClose={() => setToast(null)}
                    />
                )}
            </div>
        </DashboardLayout>
    );
};

export default MentorTasks;
