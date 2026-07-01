const API_BASE = '/api';

// DOM Elements
const authContainer = document.getElementById('auth-container');
const dashboardContainer = document.getElementById('dashboard-container');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const showRegisterBtn = document.getElementById('show-register');
const showLoginBtn = document.getElementById('show-login');
const logoutBtn = document.getElementById('logout-btn');
const authSubtitle = document.getElementById('auth-subtitle');
const userGreeting = document.getElementById('user-greeting');
const toast = document.getElementById('toast');

// Task DOM Elements
const createTaskForm = document.getElementById('create-task-form');
const editModal = document.getElementById('edit-modal');
const editTaskForm = document.getElementById('edit-task-form');
const closeModalBtn = document.getElementById('close-modal-btn');
const tasksTodo = document.getElementById('tasks-todo');
const tasksInProgress = document.getElementById('tasks-inprogress');
const tasksDone = document.getElementById('tasks-done');

// State
let currentToken = localStorage.getItem('token') || null;
let currentUsername = localStorage.getItem('username') || null;

// Initialization
function init() {
    if (currentToken) {
        showDashboard();
        fetchTasks();
    } else {
        showAuth();
    }
}

// UI Switchers
function showAuth() {
    authContainer.classList.remove('hidden');
    dashboardContainer.classList.add('hidden');
}

function showDashboard() {
    authContainer.classList.add('hidden');
    dashboardContainer.classList.remove('hidden');
    userGreeting.textContent = `Hello, ${currentUsername}`;
}

showRegisterBtn.addEventListener('click', (e) => {
    e.preventDefault();
    loginForm.classList.add('hidden');
    registerForm.classList.remove('hidden');
    authSubtitle.textContent = "Create a new account to get started.";
});

showLoginBtn.addEventListener('click', (e) => {
    e.preventDefault();
    registerForm.classList.add('hidden');
    loginForm.classList.remove('hidden');
    authSubtitle.textContent = "Welcome back! Please login to your account.";
});

// Toast Notification
function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => {
        toast.className = 'toast hidden';
    }, 3000);
}

// Auth Logic
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        
        if (res.ok && data.token) {
            currentToken = data.token;
            currentUsername = data.username; // Get the actual username from the backend
            localStorage.setItem('token', currentToken);
            localStorage.setItem('username', currentUsername);
            showDashboard();
            fetchTasks();
            showToast('Logged in successfully!');
        } else {
            showToast(data.error || 'Login failed', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        const data = await res.json();
        
        if (res.ok) {
            showToast('Registration successful! Please sign in.');
            showLoginBtn.click();
        } else {
            showToast(data.error || 'Registration failed', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
});

logoutBtn.addEventListener('click', () => {
    currentToken = null;
    currentUsername = null;
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    loginForm.reset();
    showAuth();
});

// Task Logic
async function fetchTasks() {
    try {
        const res = await fetch(`${API_BASE}/tasks`, {
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        if (res.status === 403) {
            logoutBtn.click();
            return;
        }
        const tasks = await res.json();
        renderTasks(tasks);
    } catch (err) {
        showToast('Error fetching tasks', 'error');
    }
}

function renderTasks(tasks) {
    tasksTodo.innerHTML = '';
    tasksInProgress.innerHTML = '';
    tasksDone.innerHTML = '';

    tasks.forEach(task => {
        const card = document.createElement('div');
        card.className = 'task-card';
        card.dataset.status = task.status;
        card.innerHTML = `
            <h5>${escapeHtml(task.title)}</h5>
            <p>${escapeHtml(task.description || '')}</p>
            <div class="task-meta">
                <span>📅 ${task.dueDate}</span>
                <span class="priority-badge priority-${task.priority}">Priority ${task.priority}</span>
            </div>
            <div class="task-actions">
                <button class="icon-btn edit-btn" onclick="openEditModal('${task.id}')">✏️</button>
                <button class="icon-btn delete-btn" onclick="deleteTask('${task.id}')">🗑️</button>
            </div>
        `;

        if (task.status === 'TODO') tasksTodo.appendChild(card);
        else if (task.status === 'IN_PROGRESS') tasksInProgress.appendChild(card);
        else if (task.status === 'DONE') tasksDone.appendChild(card);
    });
}

createTaskForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const task = {
        title: document.getElementById('task-title').value,
        description: document.getElementById('task-desc').value,
        priority: parseInt(document.getElementById('task-priority').value),
        dueDate: document.getElementById('task-date').value,
        status: document.getElementById('task-status').value
    };

    try {
        const res = await fetch(`${API_BASE}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify(task)
        });
        if (res.ok) {
            showToast('Task added!');
            createTaskForm.reset();
            fetchTasks();
        } else {
            showToast('Failed to add task', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
});

async function deleteTask(id) {
    if (!confirm('Are you sure you want to delete this task?')) return;
    try {
        const res = await fetch(`${API_BASE}/tasks/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        if (res.ok) {
            showToast('Task deleted');
            fetchTasks();
        } else {
            showToast('Failed to delete task', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
}

// Edit Modal Logic
window.openEditModal = async function(id) {
    // Fetch current tasks and find this one
    try {
        const res = await fetch(`${API_BASE}/tasks`, {
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        const tasks = await res.json();
        const task = tasks.find(t => t.id === id);
        if (task) {
            document.getElementById('edit-task-id').value = task.id;
            document.getElementById('edit-task-title').value = task.title;
            document.getElementById('edit-task-desc').value = task.description || '';
            document.getElementById('edit-task-priority').value = task.priority;
            document.getElementById('edit-task-date').value = task.dueDate;
            document.getElementById('edit-task-status').value = task.status;
            editModal.classList.remove('hidden');
        }
    } catch (err) {
        showToast('Error loading task', 'error');
    }
}

closeModalBtn.addEventListener('click', () => {
    editModal.classList.add('hidden');
});

editTaskForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-task-id').value;
    const task = {
        title: document.getElementById('edit-task-title').value,
        description: document.getElementById('edit-task-desc').value,
        priority: parseInt(document.getElementById('edit-task-priority').value),
        dueDate: document.getElementById('edit-task-date').value,
        status: document.getElementById('edit-task-status').value
    };

    try {
        const res = await fetch(`${API_BASE}/tasks/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify(task)
        });
        if (res.ok) {
            showToast('Task updated!');
            editModal.classList.add('hidden');
            fetchTasks();
        } else {
            showToast('Failed to update task', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
});

// Helper
function escapeHtml(unsafe) {
    return (unsafe || '').toString()
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

init();
