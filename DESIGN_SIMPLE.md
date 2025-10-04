# Study Time Tracker - Simplified Design (Java Beginner Focus)

## Overview
A **console-based Java application** for tracking study time with file-based storage. This project focuses on core Java programming concepts without complex frameworks.

## Learning Focus
This simplified version emphasizes:
- ✅ **Core Java**: Classes, Objects, Methods
- ✅ **Collections**: ArrayList, HashMap, List operations
- ✅ **File I/O**: Reading/Writing data to files
- ✅ **Date/Time API**: LocalDate, LocalDateTime, Duration
- ✅ **OOP Principles**: Encapsulation, separation of concerns
- ✅ **Exception Handling**: Try-catch, custom exceptions
- ✅ **String Processing**: Formatting, parsing, validation

## Simplified Architecture

```
┌─────────────────────────────────────┐
│     Main Application (CLI)          │  - User interface
│  - Menu system                      │  - Input/Output
│  - Input validation                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Service Layer                   │  - Business logic
│  - StudySessionManager              │  - Calculations
│  - StatisticsCalculator             │  - Filtering
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Data Storage                    │  - File I/O
│  - FileManager                      │  - Read/Write
│  - sessions.txt                     │
└─────────────────────────────────────┘
```

**No Frameworks, No Database, Just Java!**

## Core Classes (7 classes total)

### 1. `StudySession.java` (Model)
**Purpose**: Represents a single study session

```java
public class StudySession {
    // Fields
    private String id;              // Unique ID (UUID)
    private String subject;         // What was studied
    private int durationMinutes;    // How long
    private LocalDate date;         // When
    private LocalTime startTime;    // Optional start time
    private LocalTime endTime;      // Optional end time
    private String notes;           // Optional notes
    
    // Constructor
    public StudySession(String subject, int durationMinutes, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.subject = subject;
        this.durationMinutes = durationMinutes;
        this.date = date;
    }
    
    // Getters and setters
    // toString() for displaying
    // equals() and hashCode() for comparisons
}
```

**Key Java Concepts**: 
- Class design
- Constructors
- Getters/Setters (encapsulation)
- UUID for unique IDs
- LocalDate and LocalTime from java.time

---

### 2. `StudySessionManager.java` (Service)
**Purpose**: Manages all study sessions (CRUD operations)

```java
public class StudySessionManager {
    // Field
    private List<StudySession> sessions;
    private FileManager fileManager;
    
    // Constructor
    public StudySessionManager(FileManager fileManager) {
        this.sessions = new ArrayList<>();
        this.fileManager = fileManager;
        loadSessions(); // Load from file on startup
    }
    
    // Methods for 5 core features
    public void addSession(String subject, int duration, LocalDate date, String notes) {
        // Create and add new session
        // Save to file
    }
    
    public void editSession(String id, String subject, Integer duration, 
                          LocalDate date, String notes) {
        // Find and update session
        // Save to file
    }
    
    public boolean deleteSession(String id) {
        // Remove session by ID
        // Save to file
        // Return true if found and deleted
    }
    
    public List<StudySession> getAllSessions() {
        // Return all sessions
    }
    
    public List<StudySession> getSessionsByDateRange(LocalDate start, LocalDate end) {
        // Filter sessions by date range
    }
    
    public List<StudySession> getSessionsBySubject(String subject) {
        // Filter sessions by subject
    }
    
    private void loadSessions() {
        // Load sessions from file on startup
    }
    
    private void saveSessions() {
        // Save sessions to file after each change
    }
}
```

**Key Java Concepts**:
- ArrayList operations (add, remove, find)
- Method parameters and return types
- Filtering with loops or streams
- Private helper methods

---

### 3. `StatisticsCalculator.java` (Service)
**Purpose**: Calculate statistics and leaderboard data

```java
public class StatisticsCalculator {
    
    public int getTotalMinutes(List<StudySession> sessions) {
        // Sum all durations
        // Learn: Stream API or traditional loop
    }
    
    public Map<String, Integer> getTimeBySubject(List<StudySession> sessions) {
        // Group by subject and sum durations
        // Return Map: {"Math" -> 120, "Java" -> 90, ...}
    }
    
    public double getAverageSessionMinutes(List<StudySession> sessions) {
        // Calculate average duration
    }
    
    public String formatDuration(int minutes) {
        // Convert minutes to "Xh Ym" format
        // Example: 125 -> "2h 5m"
    }
    
    public List<StudySession> sortByDate(List<StudySession> sessions, 
                                        boolean ascending) {
        // Sort sessions by date
        // Learn: Comparator
    }
    
    public List<StudySession> sortByDuration(List<StudySession> sessions, 
                                            boolean ascending) {
        // Sort sessions by duration
    }
}
```

**Key Java Concepts**:
- Maps (HashMap)
- Grouping and aggregation
- Sorting with Comparator
- Stream API (optional but recommended)
- String formatting

---

### 4. `FileManager.java` (Storage)
**Purpose**: Handle reading/writing sessions to a text file

```java
public class FileManager {
    private static final String FILE_NAME = "sessions.txt";
    
    public void saveSessions(List<StudySession> sessions) throws IOException {
        // Write sessions to file
        // Format: CSV or simple format
        // Example: id|subject|duration|date|startTime|endTime|notes
    }
    
    public List<StudySession> loadSessions() throws IOException {
        // Read sessions from file
        // Parse each line and create StudySession objects
        // Return list of sessions
    }
    
    public boolean fileExists() {
        // Check if data file exists
    }
    
    private String sessionToString(StudySession session) {
        // Convert session to string for saving
    }
    
    private StudySession stringToSession(String line) {
        // Parse string and create session
    }
}
```

**Key Java Concepts**:
- File I/O with BufferedReader/BufferedWriter
- Try-with-resources
- String.split() for parsing
- Exception handling
- Data serialization (simple format)

---

### 5. `MenuManager.java` (UI)
**Purpose**: Display menus and handle user input

```java
public class MenuManager {
    private Scanner scanner;
    private StudySessionManager sessionManager;
    private StatisticsCalculator calculator;
    
    public MenuManager(StudySessionManager sessionManager, 
                      StatisticsCalculator calculator) {
        this.scanner = new Scanner(System.in);
        this.sessionManager = sessionManager;
        this.calculator = calculator;
    }
    
    public void displayMainMenu() {
        // Show menu options
    }
    
    public void handleAddSession() {
        // Prompt for subject, duration, date, notes
        // Call sessionManager.addSession()
    }
    
    public void handleEditSession() {
        // Show sessions, get ID
        // Prompt for new values
        // Call sessionManager.editSession()
    }
    
    public void handleDeleteSession() {
        // Show sessions, get ID
        // Confirm deletion
        // Call sessionManager.deleteSession()
    }
    
    public void handleViewHistory() {
        // Show filtering options
        // Display sessions in table format
    }
    
    public void handleViewLeaderboard() {
        // Show summary by subject
        // Display formatted statistics
    }
    
    private String promptString(String message) {
        // Helper: get string input
    }
    
    private int promptInt(String message) {
        // Helper: get integer input with validation
    }
    
    private LocalDate promptDate(String message) {
        // Helper: get date input with validation
    }
}
```

**Key Java Concepts**:
- Scanner for user input
- Input validation
- While loops for menu
- Method organization
- User interaction patterns

---

### 6. `InputValidator.java` (Utility)
**Purpose**: Validate user input

```java
public class InputValidator {
    
    public static boolean isValidSubject(String subject) {
        // Check not null, not empty, reasonable length
    }
    
    public static boolean isValidDuration(int minutes) {
        // Check positive number, reasonable range (1-600)
    }
    
    public static boolean isValidDate(String dateStr) {
        // Check valid date format (yyyy-MM-dd)
    }
    
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        // Parse string to LocalDate
    }
    
    public static boolean isValidId(String id) {
        // Check valid UUID format
    }
}
```

**Key Java Concepts**:
- Static methods
- Validation logic
- Exception handling
- Regular expressions (optional)

---

### 7. `StudyTrackerApp.java` (Main)
**Purpose**: Application entry point

```java
public class StudyTrackerApp {
    public static void main(String[] args) {
        System.out.println("=== Study Time Tracker ===");
        
        try {
            // Initialize components
            FileManager fileManager = new FileManager();
            StudySessionManager sessionManager = new StudySessionManager(fileManager);
            StatisticsCalculator calculator = new StatisticsCalculator();
            MenuManager menuManager = new MenuManager(sessionManager, calculator);
            
            // Run application
            boolean running = true;
            while (running) {
                menuManager.displayMainMenu();
                int choice = menuManager.getMenuChoice();
                
                switch (choice) {
                    case 1: menuManager.handleAddSession(); break;
                    case 2: menuManager.handleEditSession(); break;
                    case 3: menuManager.handleDeleteSession(); break;
                    case 4: menuManager.handleViewHistory(); break;
                    case 5: menuManager.handleViewLeaderboard(); break;
                    case 0: running = false; break;
                    default: System.out.println("Invalid choice!");
                }
            }
            
            System.out.println("Thank you for using Study Time Tracker!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**Key Java Concepts**:
- main() method
- Program flow control
- Exception handling
- Object creation and initialization

---

## Project Structure (Simplified)

```
study-time-tracker/
├── src/
│   ├── StudySession.java           # Model class
│   ├── StudySessionManager.java    # Business logic
│   ├── StatisticsCalculator.java   # Calculations
│   ├── FileManager.java            # File I/O
│   ├── MenuManager.java            # User interface
│   ├── InputValidator.java         # Validation
│   └── StudyTrackerApp.java        # Main entry point
├── sessions.txt                     # Data file (created at runtime)
├── README.md
└── DESIGN_SIMPLE.md (this file)
```

**No complex folder structure, just 7 Java files!**

---

## File Format for sessions.txt

Simple pipe-delimited format:
```
id|subject|durationMinutes|date|startTime|endTime|notes
a1b2c3|Java Programming|45|2024-10-04|09:00|09:45|Learned about classes
d4e5f6|Mathematics|60|2024-10-04|10:00|11:00|Calculus problems
g7h8i9|English|30|2024-10-03|14:30|15:00|Essay writing
```

---

## Feature Implementation (5 Core Features)

### 1. Add Your Time
**Flow:**
1. User selects "Add Session" from menu
2. System prompts: "Enter subject:"
3. User enters: "Java Programming"
4. System prompts: "Enter duration (minutes):"
5. User enters: "45"
6. System prompts: "Enter date (YYYY-MM-DD) or press Enter for today:"
7. User enters date or presses Enter
8. System prompts: "Enter notes (optional):"
9. User enters notes or skips
10. System creates StudySession, adds to list, saves to file
11. Display: "✓ Session added successfully!"

**Java Concepts Used:**
- Scanner for input
- String validation
- Integer parsing with error handling
- LocalDate.now() for default date
- ArrayList.add()
- File writing

---

### 2. Edit Your Time
**Flow:**
1. User selects "Edit Session"
2. System displays recent sessions with IDs
3. User enters session ID to edit
4. System displays current values
5. For each field, prompt: "New subject (or Enter to keep 'Java Programming'):"
6. User enters new value or presses Enter to keep current
7. System updates session, saves to file
8. Display: "✓ Session updated successfully!"

**Java Concepts Used:**
- Displaying formatted lists
- Finding object by ID
- Conditional updates (null checks)
- Object modification
- File writing

---

### 3. Delete Your Time
**Flow:**
1. User selects "Delete Session"
2. System displays sessions with IDs
3. User enters session ID to delete
4. System asks: "Delete session 'Java Programming' (45 min)? (y/n):"
5. User confirms
6. System removes from list, saves to file
7. Display: "✓ Session deleted successfully!"

**Java Concepts Used:**
- List display
- Confirmation logic
- ArrayList.remove()
- Boolean returns
- File writing

---

### 4. Fetch Your History
**Flow:**
1. User selects "View History"
2. System shows filter options:
   - All sessions
   - By date range
   - By subject
   - This week / This month
3. User selects filter
4. System applies filter and displays sessions in table:
```
ID       | Date       | Subject        | Duration | Notes
---------|------------|----------------|----------|------------------
a1b2c3   | 2024-10-04 | Java           | 45 min   | Learned classes
d4e5f6   | 2024-10-04 | Math           | 60 min   | Calculus
---------|------------|----------------|----------|------------------
Total: 2 sessions, 1h 45m
```

**Java Concepts Used:**
- Filtering lists (loops or streams)
- Date comparisons
- String comparison
- Formatted output (printf)
- List aggregation

---

### 5. Leaderboard Inspect
**Flow:**
1. User selects "Leaderboard / Statistics"
2. System displays:
```
=== Study Statistics ===

Total Study Time: 5h 30m
Total Sessions: 12
Average Session: 27 min

--- Time by Subject ---
1. Java Programming    : 2h 30m (45%)
2. Mathematics         : 1h 45m (32%)
3. English Literature  : 1h 15m (23%)

--- Recent Activity ---
This Week: 3h 15m (7 sessions)
This Month: 5h 30m (12 sessions)
```

**Java Concepts Used:**
- HashMap for grouping
- Aggregation calculations
- Sorting (Collections.sort or stream)
- Percentage calculations
- Formatted output

---

## Core Java Concepts Covered

### Beginner Level
✅ Variables and data types
✅ Classes and objects
✅ Methods (parameters, return types)
✅ Constructors
✅ Getters and setters
✅ if-else statements
✅ while and for loops
✅ Scanner for input
✅ System.out for output
✅ String operations

### Intermediate Level
✅ ArrayList operations
✅ HashMap operations
✅ File I/O (BufferedReader/Writer)
✅ Try-catch-finally
✅ LocalDate and LocalTime (java.time)
✅ UUID generation
✅ Method overloading
✅ Comparator for sorting
✅ Exception handling
✅ Input validation

### Advanced (Optional)
✅ Stream API for filtering/mapping
✅ Lambda expressions
✅ Method references
✅ Optional<T> for null safety

---

## Development Approach (10 Steps)

### Step 1: Create StudySession Model
- Define class with fields
- Add constructor
- Add getters/setters
- Add toString()
- **Test**: Create object, print it

### Step 2: Create FileManager (Simple Version)
- Implement save method (write to file)
- Implement load method (read from file)
- **Test**: Save and load a list of sessions

### Step 3: Create StudySessionManager (CRUD basics)
- Implement addSession()
- Implement getAllSessions()
- Connect with FileManager
- **Test**: Add sessions, verify file contents

### Step 4: Create MenuManager (Basic Menu)
- Display menu
- Get user choice
- Handle Add Session
- **Test**: Run and add a session

### Step 5: Complete CRUD Operations
- Implement editSession()
- Implement deleteSession()
- Add menu handlers
- **Test**: Edit and delete sessions

### Step 6: Add Input Validation
- Create InputValidator
- Validate all inputs
- Add error messages
- **Test**: Try invalid inputs

### Step 7: Implement Filtering (History)
- Add filter methods to manager
- Create date range filter
- Create subject filter
- Display filtered results
- **Test**: Filter by various criteria

### Step 8: Create StatisticsCalculator
- Implement total time calculation
- Implement time by subject
- Implement average calculation
- **Test**: Verify calculations

### Step 9: Display Leaderboard/Stats
- Format statistics output
- Create table display
- Add menu handler
- **Test**: View formatted stats

### Step 10: Polish and Error Handling
- Add try-catch blocks
- Improve error messages
- Add input validation everywhere
- Test edge cases
- **Test**: Try to break it!

---

## Sample Output

```
=== Study Time Tracker ===

Main Menu:
1. Add Study Session
2. Edit Session
3. Delete Session
4. View History
5. View Statistics
0. Exit

Enter choice: 1

--- Add Study Session ---
Enter subject: Java Programming
Enter duration (minutes): 45
Enter date (YYYY-MM-DD) or Enter for today: 
Enter notes (optional): Learned about classes and objects

✓ Session added successfully!

Press Enter to continue...
```

---

## Why This Approach is Better for Beginners

### ✅ Advantages
1. **No Framework Overhead**: Pure Java, no Spring/Hibernate to learn
2. **See Everything**: All code is visible and understandable
3. **Console-Based**: Focus on logic, not UI complexity
4. **File-Based Storage**: Understand I/O without database complexity
5. **7 Simple Classes**: Easy to understand relationships
6. **Immediate Feedback**: Compile and run instantly
7. **Progressive Learning**: Build features step by step
8. **Core Java Focus**: ArrayList, HashMap, File I/O, Date/Time

### 📚 What Beginners Learn
- **Week 1**: Classes, objects, basic I/O
- **Week 2**: Collections (ArrayList, HashMap)
- **Week 3**: File I/O and data persistence
- **Week 4**: Advanced features and polishing

### 🎯 Realistic Project Size
- **Lines of Code**: ~500-700 total (manageable)
- **Time to Complete**: 2-3 weeks for beginners
- **Classes**: 7 (vs. 20+ in Spring Boot version)
- **Concepts**: Core Java only

---

## Extension Ideas (After Mastering Basics)

Once comfortable with the simple version, add:
1. **Multiple Users**: Add User class and login
2. **Categories**: Group subjects by category
3. **Goals**: Set study goals and track progress
4. **Export**: Export data to CSV
5. **Advanced Filtering**: More filter options
6. **Data Validation**: More robust validation
7. **Unit Tests**: Learn JUnit basics
8. **GUI**: Swing or JavaFX UI (optional)
9. **Database**: Migrate to SQLite
10. **Web Version**: Learn Spring Boot (full circle!)

---

## Next Steps

Ready to start? Begin with:
1. Create basic project structure
2. Implement StudySession class
3. Test by creating and printing objects
4. Continue with Step 2 (FileManager)

**Focus**: Understand each concept before moving to the next!

---

This simplified version teaches **real Java programming** without the complexity of frameworks, databases, and web technologies. Perfect for beginners! 🚀

