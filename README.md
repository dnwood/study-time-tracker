# Study Time Tracker

A Java web application for tracking study time with a modern UI, focusing on core Java programming concepts without complex frameworks.

> **✨ This is a beginner-friendly implementation** using Java's built-in HttpServer - no Spring Boot, Maven, or database needed!

## What This Repository Contains

This repository contains a **fully working web application** built with pure Java:
- ✅ **Backend**: Java with built-in HttpServer (no frameworks!)
- ✅ **Frontend**: Modern HTML/CSS/JavaScript UI
- ✅ **Storage**: Simple JSON file persistence
- ✅ **5 Java classes + 3 web files** (~850 lines total)

**Status: Fully functional and ready to use!** 🚀

---

## Features

- 📝 **Add Time**: Log study sessions with subject, duration, date, and notes through web interface
- ✏️ **Edit Time**: Modify existing study sessions by ID
- 🗑️ **Delete Time**: Remove study sessions with confirmation
- 📊 **Fetch History**: View complete study history with filtering by date/subject
- 🏆 **Statistics**: View time by subject and study patterns

## Technology Stack

- **Backend**: Java 17+ with built-in HttpServer
- **Frontend**: HTML5, CSS3, Vanilla JavaScript  
- **Storage**: JSON file (sessions.json)
- **Server**: com.sun.net.httpserver (built into Java!)
- **No Spring Boot, Maven, or complex frameworks needed!**

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
├── README.md                       # This file
├── DESIGN_SIMPLE_WEB.md           # Simple web version design
└── GIT_WORKFLOW_SIMPLE.md         # Development plan
```

**Just 5 Java files + 3 web files!** 
*(Statistics integrated into StudySessionManager for simplicity)*

## Architecture

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

## Getting Started

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

## Usage

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

## Development Progress

This application was built incrementally with clean git commits:

### Completed Implementation (6 Commits)
1. ✅ Project Setup & Documentation
2. ✅ StudySession Model (with JSON methods)
3. ✅ JsonFileManager (File I/O)
4. ✅ StudySessionManager (CRUD + Statistics)
5. ✅ WebServer + Main App + Full Web UI
   - HTTP server with API endpoints
   - HTML/CSS/JavaScript frontend
   - Add, view, delete sessions
   - Statistics dashboard
   
**Status: Fully functional and deployed!**

See [GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md) for detailed commit history and development approach.

## Learning Objectives

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

Try adding these features to extend the application:
1. **Multiple Users**: Add User class and login system
2. **Categories**: Group subjects by category
3. **Goals**: Set daily/weekly study goals
4. **Streaks**: Track consecutive study days
5. **Export**: Export data to CSV
6. **GUI**: Build Swing or JavaFX desktop interface
7. **Database**: Migrate to SQLite or PostgreSQL
8. **Framework Migration**: Rebuild with Spring Boot (see [DESIGN.md](DESIGN.md) for a Spring Boot design)

## Contributing

This is a learning project. Feel free to:
- Suggest new features
- Report bugs
- Submit improvements
- Ask questions about the design

## License

This is an educational project. Use freely for learning purposes.

---

## Documentation

- **[DESIGN_SIMPLE_WEB.md](DESIGN_SIMPLE_WEB.md)** - Complete architecture and class designs
- **[GIT_WORKFLOW_SIMPLE.md](GIT_WORKFLOW_SIMPLE.md)** - Development approach and commit history
- **[DESIGN.md](DESIGN.md)** - Spring Boot version design (future reference)
- **[SUMMARY.md](SUMMARY.md)** - Project overview and comparisons

---

## Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/dnwood/study-time-tracker.git
   cd study-time-tracker
   ```

2. **Compile the Java code**
   ```bash
   javac src/*.java
   ```

3. **Run the application**
   ```bash
   java -cp src StudyTrackerApp
   ```

4. **Open in browser**
   ```
   http://localhost:8080
   ```

That's it! Start tracking your study time! 🚀📚

