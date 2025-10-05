# Study Time Tracker - Simplified Web Version (Java Beginner Focus)

## Overview
A **simple web-based Java application** for tracking study time using a lightweight HTTP server and basic HTML/CSS/JS. This version provides a UI while still focusing on core Java concepts - **no Spring Boot or complex frameworks needed!**

## Why This Approach?

✅ **Visual UI**: Real web interface, not just console
✅ **Still Java-focused**: Backend is pure Java + HttpServer
✅ **Beginner-friendly**: No Spring Boot, Maven, or complex setup
✅ **Modern UX**: Students can actually use it daily
✅ **Progressive**: Can upgrade to Spring Boot later

## Technology Stack

- **Backend**: Java 17+ with `com.sun.net.httpserver.HttpServer` (built-in!)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Storage**: JSON file (`sessions.json`)
- **HTTP**: Simple REST-like endpoints
- **No external dependencies!**

## Architecture

```
┌─────────────────────────────────────┐
│   Web UI (HTML/CSS/JS)              │
│  - index.html (Main page)           │
│  - style.css (Styling)              │
│  - app.js (Frontend logic)          │
└─────────────────────────────────────┘
              ↓ HTTP (fetch API)
┌─────────────────────────────────────┐
│   HttpServer (Java)                 │
│  - Route handlers                   │
│  - Request/Response                 │
│  - CORS handling                    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Business Logic (Java)             │
│  - StudySessionManager              │
│  - StatisticsCalculator             │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Data Storage (JSON)               │
│  - JsonFileManager                  │
│  - sessions.json                    │
└─────────────────────────────────────┘
```

## Project Structure

```
study-time-tracker/
├── src/
│   ├── StudySession.java           # Model class
│   ├── StudySessionManager.java    # Business logic (CRUD + Statistics)
│   ├── JsonFileManager.java        # JSON file I/O
│   ├── WebServer.java              # HTTP server & routes
│   └── StudyTrackerApp.java        # Main entry point
├── web/
│   ├── index.html                  # Main page
│   ├── css/
│   │   └── style.css              # Styling
│   └── js/
│       └── app.js                 # Frontend logic
├── data/
│   └── sessions.json              # Data storage (auto-created)
└── README.md
```

**Just 5 Java files + 3 web files!** 
*(Note: Statistics functionality is integrated into StudySessionManager for simplicity)*

---

## Core Java Classes

### 1. `StudySession.java` (Model)
Same as before, but with JSON serialization methods:

```java
public class StudySession {
    private String id;
    private String subject;
    private int durationMinutes;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    
    // Constructors, getters, setters...
    
    // NEW: JSON serialization
    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"subject\":\"%s\",\"durationMinutes\":%d," +
            "\"date\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\",\"notes\":\"%s\"}",
            id, subject, durationMinutes, date, 
            startTime != null ? startTime : "",
            endTime != null ? endTime : "",
            notes != null ? notes : ""
        );
    }
    
    // NEW: Deserialize from JSON
    public static StudySession fromJson(String json) {
        // Simple JSON parsing (no library needed)
        // Learn: String manipulation, regex, parsing
    }
}
```

**Java Concepts**: String formatting, JSON structure, serialization

---

### 2. `JsonFileManager.java` (Storage)
Replaces FileManager, uses JSON instead of pipe-delimited:

```java
public class JsonFileManager {
    private static final String FILE_PATH = "data/sessions.json";
    
    public void saveSessions(List<StudySession> sessions) throws IOException {
        // Convert list to JSON array
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < sessions.size(); i++) {
            json.append(sessions.get(i).toJson());
            if (i < sessions.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        // Write to file
        Files.writeString(Paths.get(FILE_PATH), json.toString());
    }
    
    public List<StudySession> loadSessions() throws IOException {
        // Read file
        String content = Files.readString(Paths.get(FILE_PATH));
        
        // Parse JSON array
        List<StudySession> sessions = new ArrayList<>();
        // Simple parsing logic...
        return sessions;
    }
}
```

**Java Concepts**: JSON format, StringBuilder, Files API (NIO), parsing

---

### 3. `StudySessionManager.java` (Business Logic)
Manages CRUD operations AND statistics (combined for simplicity):

```java
public class StudySessionManager {
    private List<StudySession> sessions;
    private JsonFileManager fileManager;
    
    // CRUD Methods:
    // - addSession() - create new session
    // - updateSession() - modify existing session
    // - deleteSession() - remove session by ID
    // - getAllSessions() - get all sessions
    // - getSessionById() - find by ID
    
    // Filtering Methods:
    // - getSessionsByDateRange() - filter by date
    // - getSessionsBySubject() - filter by subject
    // - getSessionsSortedByDate() - sort by date
    
    // Statistics Methods (integrated):
    // - getTotalMinutes() - sum all durations
    // - getTotalMinutes(start, end) - sum for date range
    // - getTimeBySubject() - Map<Subject, TotalMinutes>
    // - getSessionCount() - total sessions
    // - getAverageSessionMinutes() - average duration
    // - formatDuration() - convert minutes to "Xh Ym"
}
```

**Java Concepts**: ArrayList, CRUD operations, filtering, statistics, HashMap

**Design Note**: Statistics functionality is integrated directly into StudySessionManager 
rather than a separate class. This simplifies the architecture for beginners while 
maintaining all functionality.

---

### 4. `WebServer.java` (HTTP Server) - NEW!
**The key new class** - handles HTTP requests:

```java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class WebServer {
    private HttpServer server;
    private StudySessionManager sessionManager;
    private StatisticsCalculator calculator;
    
    public WebServer(StudySessionManager manager, StatisticsCalculator calc) 
            throws IOException {
        this.sessionManager = manager;
        this.calculator = calc;
        
        // Create server on port 8080
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Set up routes
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/sessions", new SessionHandler(sessionManager));
        server.createContext("/api/stats", new StatsHandler(sessionManager, calculator));
        
        server.setExecutor(null); // Use default executor
    }
    
    public void start() {
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
    
    public void stop() {
        server.stop(0);
    }
    
    // Inner class: Handle static files (HTML, CSS, JS)
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            File file = new File("web" + path);
            if (file.exists()) {
                // Determine content type
                String contentType = getContentType(path);
                
                // Read file
                byte[] content = Files.readAllBytes(file.toPath());
                
                // Send response
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                OutputStream os = exchange.getResponseBody();
                os.write(content);
                os.close();
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            return "text/plain";
        }
    }
    
    // Inner class: Handle session API requests
    private class SessionHandler implements HttpHandler {
        private StudySessionManager manager;
        
        public SessionHandler(StudySessionManager manager) {
            this.manager = manager;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            
            String method = exchange.getRequestMethod();
            String response = "";
            
            switch (method) {
                case "GET":
                    response = handleGetSessions(exchange);
                    break;
                case "POST":
                    response = handleAddSession(exchange);
                    break;
                case "PUT":
                    response = handleUpdateSession(exchange);
                    break;
                case "DELETE":
                    response = handleDeleteSession(exchange);
                    break;
                case "OPTIONS":
                    exchange.sendResponseHeaders(200, -1);
                    return;
            }
            
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String handleGetSessions(HttpExchange exchange) {
            // Get all sessions and convert to JSON
            List<StudySession> sessions = manager.getAllSessions();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < sessions.size(); i++) {
                json.append(sessions.get(i).toJson());
                if (i < sessions.size() - 1) json.append(",");
            }
            json.append("]");
            return json.toString();
        }
        
        private String handleAddSession(HttpExchange exchange) throws IOException {
            // Read request body
            String body = new String(exchange.getRequestBody().readAllBytes());
            
            // Parse JSON (simple parsing)
            // Extract subject, duration, date, notes
            // Call manager.addSession()
            // Return success JSON
            
            return "{\"success\":true,\"message\":\"Session added\"}";
        }
        
        private String handleUpdateSession(HttpExchange exchange) throws IOException {
            // Similar to add, but update existing
            return "{\"success\":true,\"message\":\"Session updated\"}";
        }
        
        private String handleDeleteSession(HttpExchange exchange) throws IOException {
            // Get ID from query params
            // Call manager.deleteSession()
            return "{\"success\":true,\"message\":\"Session deleted\"}";
        }
    }
    
    // Inner class: Handle statistics API
    private class StatsHandler implements HttpHandler {
        private StudySessionManager manager;
        private StatisticsCalculator calculator;
        
        public StatsHandler(StudySessionManager manager, StatisticsCalculator calc) {
            this.manager = manager;
            this.calculator = calc;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            
            List<StudySession> sessions = manager.getAllSessions();
            int totalMinutes = calculator.getTotalMinutes(sessions);
            double avgMinutes = calculator.getAverageSessionMinutes(sessions);
            Map<String, Integer> bySubject = calculator.getTimeBySubject(sessions);
            
            // Build JSON response
            StringBuilder json = new StringBuilder("{");
            json.append("\"totalMinutes\":").append(totalMinutes).append(",");
            json.append("\"sessionCount\":").append(sessions.size()).append(",");
            json.append("\"averageMinutes\":").append(avgMinutes).append(",");
            json.append("\"bySubject\":{");
            // Add subject breakdown...
            json.append("}");
            json.append("}");
            
            String response = json.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}
```

**Java Concepts**: 
- HttpServer (built-in Java HTTP server)
- Inner classes
- HttpHandler interface
- Request/Response handling
- File serving
- Basic routing
- JSON responses
- CORS headers

---

### 5. `StudyTrackerApp.java` (Main)
Updated to start web server:

```java
public class StudyTrackerApp {
    public static void main(String[] args) {
        try {
            System.out.println("=== Study Time Tracker - Web Version ===");
            
            // Initialize components
            JsonFileManager fileManager = new JsonFileManager();
            StudySessionManager sessionManager = new StudySessionManager(fileManager);
            StatisticsCalculator calculator = new StatisticsCalculator();
            
            // Start web server
            WebServer server = new WebServer(sessionManager, calculator);
            server.start();
            
            System.out.println("\n✓ Server running!");
            System.out.println("Open your browser and go to:");
            System.out.println("    http://localhost:8080\n");
            System.out.println("Press Ctrl+C to stop the server.\n");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

---

## Frontend Files

### `web/index.html`
Main page with all features in one file:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Study Time Tracker</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>📚 Study Time Tracker</h1>
            <p class="subtitle">Track your learning journey</p>
        </header>

        <!-- Tab Navigation -->
        <nav class="tabs">
            <button class="tab-btn active" data-tab="add">Add Session</button>
            <button class="tab-btn" data-tab="history">History</button>
            <button class="tab-btn" data-tab="stats">Statistics</button>
        </nav>

        <!-- Add Session Tab -->
        <div id="add-tab" class="tab-content active">
            <h2>Add Study Session</h2>
            <form id="add-form">
                <div class="form-group">
                    <label for="subject">Subject *</label>
                    <input type="text" id="subject" required>
                </div>
                <div class="form-group">
                    <label for="duration">Duration (minutes) *</label>
                    <input type="number" id="duration" min="1" max="600" required>
                </div>
                <div class="form-group">
                    <label for="date">Date *</label>
                    <input type="date" id="date" required>
                </div>
                <div class="form-group">
                    <label for="notes">Notes</label>
                    <textarea id="notes" rows="3"></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Add Session</button>
            </form>
        </div>

        <!-- History Tab -->
        <div id="history-tab" class="tab-content">
            <h2>Study History</h2>
            <div class="filters">
                <input type="text" id="filter-subject" placeholder="Filter by subject...">
                <input type="date" id="filter-date">
                <button id="clear-filters" class="btn btn-secondary">Clear</button>
            </div>
            <table id="sessions-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Subject</th>
                        <th>Duration</th>
                        <th>Notes</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="sessions-body">
                    <!-- Populated by JavaScript -->
                </tbody>
            </table>
        </div>

        <!-- Statistics Tab -->
        <div id="stats-tab" class="tab-content">
            <h2>Statistics</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">⏱️</div>
                    <div class="stat-value" id="total-time">0h 0m</div>
                    <div class="stat-label">Total Time</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📖</div>
                    <div class="stat-value" id="session-count">0</div>
                    <div class="stat-label">Sessions</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📊</div>
                    <div class="stat-value" id="avg-session">0m</div>
                    <div class="stat-label">Avg Session</div>
                </div>
            </div>
            
            <h3>Time by Subject</h3>
            <div id="subject-chart">
                <!-- Populated by JavaScript -->
            </div>
        </div>

        <!-- Toast Notification -->
        <div id="toast" class="toast"></div>
    </div>

    <script src="/js/app.js"></script>
</body>
</html>
```

### `web/css/style.css`
Modern, responsive styling:

```css
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    padding: 20px;
}

.container {
    max-width: 1000px;
    margin: 0 auto;
    background: white;
    border-radius: 16px;
    box-shadow: 0 10px 40px rgba(0,0,0,0.2);
    padding: 40px;
}

header {
    text-align: center;
    margin-bottom: 30px;
}

h1 {
    font-size: 2.5rem;
    color: #333;
    margin-bottom: 10px;
}

.subtitle {
    color: #666;
    font-size: 1.1rem;
}

/* Tabs */
.tabs {
    display: flex;
    gap: 10px;
    margin-bottom: 30px;
    border-bottom: 2px solid #eee;
}

.tab-btn {
    padding: 12px 24px;
    border: none;
    background: none;
    cursor: pointer;
    font-size: 1rem;
    color: #666;
    border-bottom: 3px solid transparent;
    transition: all 0.3s;
}

.tab-btn.active {
    color: #667eea;
    border-bottom-color: #667eea;
}

.tab-content {
    display: none;
}

.tab-content.active {
    display: block;
}

/* Forms */
.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-weight: 500;
}

input, textarea {
    width: 100%;
    padding: 12px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.3s;
}

input:focus, textarea:focus {
    outline: none;
    border-color: #667eea;
}

/* Buttons */
.btn {
    padding: 12px 24px;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.3s;
}

.btn-primary {
    background: #667eea;
    color: white;
}

.btn-primary:hover {
    background: #5568d3;
}

.btn-secondary {
    background: #f0f0f0;
    color: #333;
}

/* Table */
table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

th, td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #eee;
}

th {
    background: #f8f9fa;
    font-weight: 600;
    color: #333;
}

/* Stats Grid */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin: 20px 0;
}

.stat-card {
    background: #f8f9fa;
    padding: 24px;
    border-radius: 12px;
    text-align: center;
}

.stat-icon {
    font-size: 2.5rem;
    margin-bottom: 10px;
}

.stat-value {
    font-size: 2rem;
    font-weight: bold;
    color: #667eea;
    margin-bottom: 5px;
}

.stat-label {
    color: #666;
    font-size: 0.9rem;
}

/* Toast */
.toast {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background: #333;
    color: white;
    padding: 16px 24px;
    border-radius: 8px;
    display: none;
    animation: slideIn 0.3s;
}

@keyframes slideIn {
    from {
        transform: translateX(100%);
    }
    to {
        transform: translateX(0);
    }
}
```

### `web/js/app.js`
Frontend logic with fetch API:

```javascript
// API Base URL
const API_URL = 'http://localhost:8080/api';

// DOM Elements
const addForm = document.getElementById('add-form');
const sessionsBody = document.getElementById('sessions-body');
const tabBtns = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    setTodayDate();
    setupTabs();
    setupForm();
    loadSessions();
    loadStats();
});

// Set today's date as default
function setTodayDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('date').value = today;
}

// Tab switching
function setupTabs() {
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.dataset.tab;
            
            // Update active states
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            btn.classList.add('active');
            document.getElementById(`${tabName}-tab`).classList.add('active');
            
            // Reload data for the tab
            if (tabName === 'history') loadSessions();
            if (tabName === 'stats') loadStats();
        });
    });
}

// Handle form submission
function setupForm() {
    addForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const session = {
            subject: document.getElementById('subject').value,
            durationMinutes: parseInt(document.getElementById('duration').value),
            date: document.getElementById('date').value,
            notes: document.getElementById('notes').value
        };
        
        try {
            const response = await fetch(`${API_URL}/sessions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(session)
            });
            
            if (response.ok) {
                showToast('Session added successfully!');
                addForm.reset();
                setTodayDate();
            }
        } catch (error) {
            showToast('Error adding session', 'error');
        }
    });
}

// Load and display sessions
async function loadSessions() {
    try {
        const response = await fetch(`${API_URL}/sessions`);
        const sessions = await response.json();
        
        sessionsBody.innerHTML = '';
        
        sessions.forEach(session => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${session.date}</td>
                <td>${session.subject}</td>
                <td>${session.durationMinutes} min</td>
                <td>${session.notes || '-'}</td>
                <td>
                    <button class="btn-icon" onclick="editSession('${session.id}')">✏️</button>
                    <button class="btn-icon" onclick="deleteSession('${session.id}')">🗑️</button>
                </td>
            `;
            sessionsBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading sessions:', error);
    }
}

// Load and display statistics
async function loadStats() {
    try {
        const response = await fetch(`${API_URL}/stats`);
        const stats = await response.json();
        
        document.getElementById('total-time').textContent = formatDuration(stats.totalMinutes);
        document.getElementById('session-count').textContent = stats.sessionCount;
        document.getElementById('avg-session').textContent = `${Math.round(stats.averageMinutes)}m`;
        
        displaySubjectChart(stats.bySubject);
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Display subject breakdown
function displaySubjectChart(bySubject) {
    const chartDiv = document.getElementById('subject-chart');
    chartDiv.innerHTML = '';
    
    Object.entries(bySubject).forEach(([subject, minutes]) => {
        const bar = document.createElement('div');
        bar.className = 'subject-bar';
        bar.innerHTML = `
            <div class="subject-name">${subject}</div>
            <div class="subject-time">${formatDuration(minutes)}</div>
        `;
        chartDiv.appendChild(bar);
    });
}

// Delete session
async function deleteSession(id) {
    if (!confirm('Delete this session?')) return;
    
    try {
        await fetch(`${API_URL}/sessions?id=${id}`, {
            method: 'DELETE'
        });
        showToast('Session deleted');
        loadSessions();
    } catch (error) {
        showToast('Error deleting session', 'error');
    }
}

// Utility: Format duration
function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
}

// Show toast notification
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.style.display = 'block';
    toast.style.background = type === 'error' ? '#e74c3c' : '#333';
    
    setTimeout(() => {
        toast.style.display = 'none';
    }, 3000);
}
```

---

## Comparison: Console vs Web Version

| Aspect | Console Version | Web Version |
|--------|----------------|-------------|
| **UI** | Terminal only | Modern web interface |
| **Classes** | 7 | 5 (+ 3 web files) |
| **Usability** | Developer-friendly | User-friendly |
| **Complexity** | Simpler | Slightly more complex |
| **HTTP Server** | No | Yes (built-in HttpServer) |
| **JSON** | No | Yes |
| **JavaScript** | No | Yes (basic) |
| **Deployment** | Run locally | Can be accessed by browser |

---

## Development Plan (12 Commits)

### Actual Implementation (Commits 1-6)

**Commit 1**: Project Setup & Documentation
- Comprehensive design documents
- README and workflow plans
- .gitignore and structure

**Commit 2**: StudySession Model
- Core model class with JSON serialization
- Manual JSON parsing without libraries
- Full validation and error handling

**Commit 3**: JsonFileManager
- File I/O for JSON persistence
- Load/save sessions to data/sessions.json
- Automatic directory creation

**Commit 4**: StudySessionManager
- CRUD operations (add, update, delete, get)
- Filtering and sorting methods
- Statistics methods (integrated)
- Auto-load and auto-save

**Commits 5-9** (Combined): Complete Web Application
- WebServer with HttpServer
- StudyTrackerApp main entry point
- HTML structure (index.html)
- CSS styling (style.css)
- JavaScript frontend (app.js)

**Timeline: Completed in 1 intensive session!**

### Simplifications Made:
1. **Merged StatisticsCalculator into StudySessionManager** - Fewer classes, same functionality
2. **Combined frontend commits** - Web UI built as cohesive unit
3. **Streamlined for beginners** - Less complexity, easier to understand

**Final Result: 5 Java files + 3 web files = fully functional app!**

---

## Running the Application

```bash
# 1. Compile Java files
javac src/*.java

# 2. Run the server
java -cp src StudyTrackerApp

# 3. Open browser
open http://localhost:8080
```

---

## Key Learning Outcomes

### Java Concepts:
✅ HttpServer (built-in Java HTTP server)
✅ Request/Response handling
✅ JSON serialization (manual)
✅ File I/O with NIO
✅ Inner classes
✅ HTTP methods (GET, POST, PUT, DELETE)

### Web Concepts:
✅ HTML structure
✅ CSS styling
✅ JavaScript fetch API
✅ DOM manipulation
✅ Event handling
✅ AJAX requests

### Integration:
✅ Backend-Frontend communication
✅ REST-like API design
✅ CORS handling
✅ JSON data format

---

## Why This is Better Than Console + Better Than Spring Boot

### Better Than Console:
✅ **Real UI**: Students can actually use it daily
✅ **Modern**: Looks like real applications
✅ **Engaging**: More motivating to build
✅ **Portfolio-ready**: Can show to others

### Better Than Spring Boot for Beginners:
✅ **No framework magic**: See exactly how HTTP works
✅ **Built-in HttpServer**: No external dependencies
✅ **Simple JSON**: Manual parsing teaches the concepts
✅ **Direct control**: Understand request/response cycle
✅ **No Maven required**: Can use just javac

---

This version gives students a **real web application** while still focusing on **core Java fundamentals** and basic web concepts! 🚀

