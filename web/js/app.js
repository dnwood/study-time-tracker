// API Base URL
const API_URL = 'http://localhost:8080/api';

// DOM Elements
const addForm = document.getElementById('add-form');
const tabBtns = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');
const toast = document.getElementById('toast');

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    console.log('Study Time Tracker initialized');
    setTodayDate();
    setupTabs();
    setupAddForm();
    setupHistoryTab();
    setupStatsTab();
});

// Set today's date as default
function setTodayDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('date').value = today;
}

// Tab switching functionality
function setupTabs() {
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.dataset.tab;
            
            // Update active states
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            btn.classList.add('active');
            document.getElementById(`${tabName}-tab`).classList.add('active');
            
            // Load data for the tab
            if (tabName === 'history') {
                loadSessions();
            } else if (tabName === 'stats') {
                loadStats();
            }
        });
    });
}

// Setup Add Session form
function setupAddForm() {
    addForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const session = {
            subject: document.getElementById('subject').value.trim(),
            durationMinutes: parseInt(document.getElementById('duration').value),
            date: document.getElementById('date').value,
            notes: document.getElementById('notes').value.trim() || null
        };
        
        // Validate
        if (!session.subject || session.durationMinutes <= 0 || !session.date) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        try {
            const response = await fetch(`${API_URL}/sessions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(session)
            });
            
            const data = await response.json();
            
            if (data.success) {
                showToast('✓ Session added successfully!', 'success');
                addForm.reset();
                setTodayDate();
                
                // If on history tab, reload
                if (document.getElementById('history-tab').classList.contains('active')) {
                    loadSessions();
                }
            } else {
                showToast('Error: ' + (data.error || 'Failed to add session'), 'error');
            }
        } catch (error) {
            console.error('Error adding session:', error);
            showToast('Error: Could not connect to server', 'error');
        }
    });
}

// Setup History tab
function setupHistoryTab() {
    const refreshBtn = document.getElementById('refresh-btn');
    const filterSubject = document.getElementById('filter-subject');
    
    refreshBtn.addEventListener('click', loadSessions);
    
    filterSubject.addEventListener('input', () => {
        filterSessions(filterSubject.value);
    });
}

// Setup Stats tab
function setupStatsTab() {
    // Stats will load when tab is clicked
}

// Load and display sessions
async function loadSessions() {
    const container = document.getElementById('sessions-container');
    container.innerHTML = '<p class="empty-state">Loading sessions...</p>';
    
    try {
        const response = await fetch(`${API_URL}/sessions`);
        const sessions = await response.json();
        
        if (sessions.length === 0) {
            container.innerHTML = '<p class="empty-state">No study sessions yet. Add your first session!</p>';
            return;
        }
        
        // Store sessions globally for filtering
        window.allSessions = sessions;
        
        displaySessions(sessions);
    } catch (error) {
        console.error('Error loading sessions:', error);
        container.innerHTML = '<p class="empty-state error">Error loading sessions. Make sure the server is running.</p>';
    }
}

// Display sessions in the UI
function displaySessions(sessions) {
    const container = document.getElementById('sessions-container');
    
    if (sessions.length === 0) {
        container.innerHTML = '<p class="empty-state">No sessions match your filter.</p>';
        return;
    }
    
    container.innerHTML = '';
    
    sessions.forEach(session => {
        const card = document.createElement('div');
        card.className = 'session-card';
        card.dataset.id = session.id;
        
        card.innerHTML = `
            <div class="session-header">
                <div class="session-subject">${escapeHtml(session.subject)}</div>
                <div class="session-duration">${session.durationMinutes}m</div>
            </div>
            <div class="session-date">📅 ${formatDate(session.date)}</div>
            ${session.notes ? `<div class="session-notes">${escapeHtml(session.notes)}</div>` : ''}
            <div class="session-actions">
                <button class="btn btn-small btn-danger" onclick="deleteSession('${session.id}')">Delete</button>
            </div>
        `;
        
        container.appendChild(card);
    });
}

// Filter sessions by subject
function filterSessions(searchTerm) {
    if (!window.allSessions) return;
    
    const filtered = window.allSessions.filter(session => 
        session.subject.toLowerCase().includes(searchTerm.toLowerCase())
    );
    
    displaySessions(filtered);
}

// Delete a session
async function deleteSession(id) {
    if (!confirm('Are you sure you want to delete this session?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/sessions?id=${id}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast('✓ Session deleted', 'success');
            loadSessions();
            
            // Update stats if visible
            if (document.getElementById('stats-tab').classList.contains('active')) {
                loadStats();
            }
        } else {
            showToast('Error: ' + (data.error || 'Failed to delete'), 'error');
        }
    } catch (error) {
        console.error('Error deleting session:', error);
        showToast('Error: Could not connect to server', 'error');
    }
}

// Load and display statistics
async function loadStats() {
    const container = document.getElementById('stats-container');
    container.innerHTML = '<p class="empty-state">Loading statistics...</p>';
    
    try {
        const response = await fetch(`${API_URL}/stats`);
        const stats = await response.json();
        
        displayStats(stats);
    } catch (error) {
        console.error('Error loading stats:', error);
        container.innerHTML = '<p class="empty-state error">Error loading statistics.</p>';
    }
}

// Display statistics in the UI
function displayStats(stats) {
    const container = document.getElementById('stats-container');
    
    const totalTime = formatDuration(stats.totalMinutes);
    const avgTime = Math.round(stats.averageMinutes);
    
    let html = `
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">⏱️</div>
                <div class="stat-value">${totalTime}</div>
                <div class="stat-label">Total Time</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📖</div>
                <div class="stat-value">${stats.sessionCount}</div>
                <div class="stat-label">Sessions</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📊</div>
                <div class="stat-value">${avgTime}m</div>
                <div class="stat-label">Avg Session</div>
            </div>
        </div>
    `;
    
    // Subject breakdown
    if (Object.keys(stats.bySubject).length > 0) {
        html += '<h3>Time by Subject</h3><div class="subject-breakdown">';
        
        // Sort subjects by time (descending)
        const sortedSubjects = Object.entries(stats.bySubject)
            .sort((a, b) => b[1] - a[1]);
        
        sortedSubjects.forEach(([subject, minutes]) => {
            const percentage = Math.round((minutes / stats.totalMinutes) * 100);
            html += `
                <div class="subject-item">
                    <div class="subject-name">${escapeHtml(subject)}</div>
                    <div class="subject-time">${formatDuration(minutes)} (${percentage}%)</div>
                </div>
            `;
        });
        
        html += '</div>';
    }
    
    container.innerHTML = html;
}

// Utility: Format duration in minutes to "Xh Ym"
function formatDuration(minutes) {
    if (minutes < 60) {
        return minutes + 'm';
    }
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    if (remainingMinutes === 0) {
        return hours + 'h';
    }
    return hours + 'h ' + remainingMinutes + 'm';
}

// Utility: Format date for display
function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString(undefined, options);
}

// Utility: Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Show toast notification
function showToast(message, type = 'success') {
    toast.textContent = message;
    toast.className = 'toast show ' + type;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

