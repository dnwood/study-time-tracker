# Study Time Tracker

A Java web application for tracking study time with a modern UI, focusing on core Java programming concepts without complex frameworks.

> **📌 Note**: This repository contains **two beginner-friendly versions** plus a full production version. 
> The **Simple Web Version** (recommended) uses Java's built-in HttpServer - no Spring Boot needed!

## Two Versions Available

1. **📚 Simple Web Version** (Recommended for Beginners)
   - Pure Java with built-in HttpServer (no Spring Boot!)
   - Modern web interface (HTML/CSS/JS)
   - JSON file storage
   - 6 Java classes + 3 web files, ~900 lines total
   - See [DESIGN_SIMPLE_WEB.md](DESIGN_SIMPLE_WEB.md) and [GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md)

2. **🚀 Full Web Version** (For Advanced Learners)
   - Spring Boot 3.2 with web UI
   - REST API, database (H2/PostgreSQL)
   - 20+ classes, ~2000+ lines of code
   - See [DESIGN.md](DESIGN.md) and [GIT_WORKFLOW.md](GIT_WORKFLOW.md)

---

## Simple Version Overview (This Repository)

### Features

- 📝 **Add Time**: Log study sessions with subject, duration, date, and notes through web interface
- ✏️ **Edit Time**: Modify existing study sessions by ID
- 🗑️ **Delete Time**: Remove study sessions with confirmation
- 📊 **Fetch History**: View complete study history with filtering by date/subject
- 🏆 **Statistics**: View time by subject and study patterns

### Technology Stack

- **Backend**: Java 17+ with built-in HttpServer
- **Frontend**: HTML5, CSS3, Vanilla JavaScript  
- **Storage**: JSON file (sessions.json)
- **Server**: com.sun.net.httpserver (built into Java!)
- **No Spring Boot, Maven, or complex frameworks needed!**

## Project Structure (Simple Version)

```
study-time-tracker/
├── src/
│   ├── StudySession.java           # Model class
│   ├── StudySessionManager.java    # Business logic (CRUD)
│   ├── StatisticsCalculator.java   # Calculations & stats
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
├── README.md                       # This file
├── DESIGN_SIMPLE_WEB.md           # Simple web version design
└── GIT_WORKFLOW_SIMPLE.md         # Development plan
```

**Just 6 Java files + 3 web files!**

## Architecture (Simple Version)

```
Web UI (HTML/CSS/JS)
        ↓ HTTP (fetch API)
WebServer (Java HttpServer)
        ↓
StudySessionManager (Business Logic)
        ↓
JsonFileManager (Data Storage)
        ↓
sessions.json (JSON File)
```

See [DESIGN_SIMPLE_WEB.md](DESIGN_SIMPLE_WEB.md) for complete class designs and [GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md) for the development plan.

## Getting Started (Simple Version)

### Prerequisites

- Java 11 or higher (Java 17 recommended)
- A text editor or IDE (VS Code, IntelliJ IDEA, Eclipse)
- Terminal/Command Prompt

### Compiling the Application

```bash
# Navigate to project directory
cd /Users/derek.chung/projects/study-time-tracker

# Compile all Java files
javac src/*.java
```

### Running the Application

```bash
# Start the server
java -cp src StudyTrackerApp

# Open your browser and go to:
# http://localhost:8080
```

### Stopping the Server

```bash
# Press Ctrl+C in the terminal
```

### No Complex Setup Required!
Just compile, run, and open in browser - that's it!

## Usage (Simple Web Version)

### Accessing the Application

1. Compile and run the server (see above)
2. Open your browser to `http://localhost:8080`
3. Click tabs to navigate between features

### Main Features

#### 1. Add Your Time
- Click "Add Session" tab
- Fill in the form:
  - Subject (required)
  - Duration in minutes (required)
  - Date (defaults to today)
  - Notes (optional)
- Click "Add Session" button
- Session is saved automatically

#### 2. Edit Your Time
- Click "History" tab
- Click the ✏️ edit button on any session
- Modify the fields in the popup
- Click "Update" to save changes

#### 3. Delete Your Time
- Click "History" tab
- Click the 🗑️ delete button on any session
- Confirm deletion in the dialog
- Session is removed immediately

#### 4. Fetch Your History
- Click "History" tab
- Use filters at the top:
  - Filter by subject (type to search)
  - Filter by date
  - Click "Clear" to reset filters
- View all sessions in the table
- Sorted by date (newest first)

#### 5. View Statistics
- Click "Statistics" tab
- See overview cards:
  - Total study time
  - Number of sessions
  - Average session length
- View time breakdown by subject
- Visual representation with progress bars

### Sample Interface

```
┌─────────────────────────────────────────────────┐
│         📚 Study Time Tracker                   │
│         Track your learning journey             │
├─────────────────────────────────────────────────┤
│  [Add Session] [History] [Statistics]          │
├─────────────────────────────────────────────────┤
│                                                 │
│  Add Study Session                              │
│                                                 │
│  Subject: [Java Programming_____________]       │
│  Duration: [45___] minutes                      │
│  Date: [2024-10-04__]                          │
│  Notes: [Learning about HttpServer_______]      │
│                                                 │
│  [Add Session]                                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Data Storage

Sessions are stored in `data/sessions.json` in JSON format:
```json
[
  {
    "id": "a1b2c3",
    "subject": "Java Programming",
    "durationMinutes": 45,
    "date": "2024-10-04",
    "startTime": "",
    "endTime": "",
    "notes": "Learned about HttpServer"
  }
]
```

**No database setup required!** The file is created automatically on first run.

## Development Progress (Simple Version)

This simplified version follows a **12-commit plan** focusing on core Java + basic web:

### Phase 1: Backend Foundation (Commits 1-4)
1. ✅ Project Setup
2. ⏳ StudySession Model (with JSON methods)
3. ⏳ JsonFileManager
4. ⏳ StudySessionManager & StatisticsCalculator

### Phase 2: Web Server (Commits 5-6)
5. ⏳ WebServer class with routing
6. ⏳ API endpoints (REST-like)

### Phase 3: Frontend (Commits 7-10)
7. ⏳ HTML structure
8. ⏳ CSS styling
9. ⏳ JavaScript - Add session
10. ⏳ JavaScript - History & delete

### Phase 4: Polish (Commits 11-12)
11. ⏳ Statistics page
12. ⏳ Error handling & polish

See [GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md) for detailed commit plans.

## Learning Objectives (Simple Version)

This project teaches core Java concepts:

### Java - Beginner Level ✅
- Classes and Objects
- Constructors, Getters, Setters
- ArrayList and HashMap
- JSON serialization (manual)
- File I/O with NIO

### Java - Intermediate Level ✅
- HttpServer (built-in Java HTTP server)
- Request/Response handling
- Inner classes
- Exception handling
- LocalDate and LocalTime
- CRUD operations

### Web - Beginner Level ✅
- HTML structure
- CSS styling (modern, responsive)
- JavaScript basics
- fetch API for HTTP requests
- DOM manipulation
- Event handling

**Perfect for Java beginners who want to learn web basics too!** 🎓

## Future Enhancements

After mastering the simple version, try adding:
1. **Multiple Users**: Add User class and login system
2. **Categories**: Group subjects by category
3. **Goals**: Set daily/weekly study goals
4. **Streaks**: Track consecutive study days
5. **Export**: Export data to CSV
6. **GUI**: Build Swing or JavaFX interface
7. **Database**: Migrate to SQLite
8. **Web Version**: Learn Spring Boot and build the full version!

## Why Two Versions?

| Question | Simple Web Version | Full Spring Boot Version |
|----------|-------------------|-------------------------|
| **Who is it for?** | Java beginners | Intermediate+ developers |
| **What will I learn?** | Core Java + Basic Web | Spring Boot, REST APIs, JPA, Advanced Web |
| **How long?** | 4-5 weeks | 8-10 weeks |
| **Complexity?** | Low-Medium (6 Java + 3 web files) | High (20+ classes) |
| **UI?** | Yes (HTML/CSS/JS) | Yes (more advanced) |
| **Best for?** | Learning Java + Web basics | Building production apps |

**Start with the Simple Version**, then progress to the Full Version once comfortable!

## Contributing

This is a learning project. Feel free to:
- Suggest new features
- Report bugs
- Submit improvements
- Ask questions about the design

## License

This is an educational project. Use freely for learning purposes.

---

## Getting Started

**For Beginners**: Start with the Simple Web Version
1. Read [DESIGN_SIMPLE_WEB.md](DESIGN_SIMPLE_WEB.md) to understand the architecture
2. Follow [GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md) for step-by-step commits
3. Build backend first, then add frontend
4. Learn core Java + basic web concepts hands-on

**For Advanced Learners**: Try the Full Spring Boot Version
1. Read [DESIGN.md](DESIGN.md) for complete architecture
2. Follow [GIT_WORKFLOW.md](GIT_WORKFLOW.md) for 20 planned commits
3. Learn Spring Boot, REST APIs, and web development
4. Build a production-ready application

---

**Ready to start coding?** Pick your version and let's begin! 🚀📚

