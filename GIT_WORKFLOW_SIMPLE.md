# Git Workflow - Simplified Version (10 Commits)

## Overview
This simplified workflow has **10 focused commits** instead of 20, each representing a complete, testable feature.

## Commit Strategy
- Each commit builds one complete component
- Code compiles after each commit
- Features work end-to-end
- Clear, descriptive messages

---

## The 10 Commits

### Phase 1: Foundation (Commits 1-3)

#### Commit 1: Project Setup
**Files:**
- `README.md` - Project overview
- `DESIGN_SIMPLE.md` - Simplified design
- `.gitignore` - Git ignore rules
- `src/` folder structure

**Commit Message:**
```
chore: initialize Java project structure

- Create project README with setup instructions
- Add simplified design document
- Create .gitignore for Java projects (*.class, *.jar, IDE files)
- Set up src/ folder for source files
- Document 7-class architecture

Project: Console-based study time tracker
Focus: Core Java concepts without frameworks
```

**Verify:**
- [ ] Folders exist
- [ ] README is clear
- [ ] Design doc is complete

---

#### Commit 2: StudySession Model
**Files:**
- `src/StudySession.java`

**Commit Message:**
```
feat: implement StudySession model class

- Create StudySession class with all fields
  * id (String UUID)
  * subject, durationMinutes, date
  * startTime, endTime (optional)
  * notes (optional)
  
- Add constructors (with required fields only)
- Add all getters and setters
- Implement toString() for display
- Implement equals() and hashCode() based on id

Java concepts: Classes, constructors, encapsulation,
UUID, LocalDate, LocalTime from java.time API
```

**Test:**
```java
// In main() temporarily:
StudySession session = new StudySession("Java", 45, LocalDate.now());
System.out.println(session);
// Should print session details
```

**Verify:**
- [ ] Compiles without errors
- [ ] Creates object successfully
- [ ] toString() shows all fields
- [ ] UUID generates unique IDs

---

#### Commit 3: FileManager for Data Persistence
**Files:**
- `src/FileManager.java`

**Commit Message:**
```
feat: implement FileManager for file I/O

- Create FileManager class for reading/writing sessions
- Implement saveSessions(List<StudySession>) method
  * Write sessions to sessions.txt
  * Use pipe-delimited format
  * Handle IOException
  
- Implement loadSessions() method
  * Read from sessions.txt
  * Parse each line to StudySession
  * Return List<StudySession>
  
- Add helper methods:
  * sessionToString() - serialize session
  * stringToSession() - deserialize session
  * fileExists() - check file existence

Java concepts: File I/O, BufferedReader/Writer,
try-with-resources, String.split(), parsing,
exception handling
```

**Test:**
```java
// In main() temporarily:
List<StudySession> sessions = new ArrayList<>();
sessions.add(new StudySession("Java", 45, LocalDate.now()));
sessions.add(new StudySession("Math", 60, LocalDate.now()));

FileManager fm = new FileManager();
fm.saveSessions(sessions);

List<StudySession> loaded = fm.loadSessions();
System.out.println("Loaded " + loaded.size() + " sessions");
```

**Verify:**
- [ ] Compiles and runs
- [ ] Creates sessions.txt file
- [ ] File format is correct
- [ ] Loads sessions successfully
- [ ] No data loss

---

### Phase 2: Core Logic (Commits 4-5)

#### Commit 4: StudySessionManager (CRUD Operations)
**Files:**
- `src/StudySessionManager.java`

**Commit Message:**
```
feat: implement StudySessionManager with CRUD operations

- Create StudySessionManager class
- Initialize with FileManager dependency
- Load sessions from file on startup
- Auto-save after each modification

Methods implemented:
- addSession() - create new session
- editSession() - update existing session by ID
- deleteSession() - remove session by ID
- getAllSessions() - return all sessions
- getSessionById() - find session by ID

Java concepts: ArrayList operations, dependency injection,
finding elements in lists, CRUD pattern, encapsulation
```

**Test:**
```java
// In main() temporarily:
FileManager fm = new FileManager();
StudySessionManager manager = new StudySessionManager(fm);

manager.addSession("Java", 45, LocalDate.now(), "Learning classes");
manager.addSession("Math", 60, LocalDate.now(), "Calculus");

List<StudySession> all = manager.getAllSessions();
System.out.println("Total sessions: " + all.size());

// Test edit
String id = all.get(0).getId();
manager.editSession(id, "Java OOP", 50, null, "Updated notes");

// Test delete
manager.deleteSession(id);
```

**Verify:**
- [ ] Can add sessions
- [ ] Can edit sessions
- [ ] Can delete sessions
- [ ] Data persists to file
- [ ] Restarting loads data

---

#### Commit 5: StatisticsCalculator
**Files:**
- `src/StatisticsCalculator.java`

**Commit Message:**
```
feat: implement StatisticsCalculator for analytics

- Create StatisticsCalculator utility class
- All methods are stateless (work on provided lists)

Methods implemented:
- getTotalMinutes() - sum all durations
- getTimeBySubject() - group by subject, return Map
- getAverageSessionMinutes() - calculate average
- formatDuration() - convert minutes to "Xh Ym"
- sortByDate() - sort sessions by date
- sortByDuration() - sort sessions by duration
- filterByDateRange() - filter between dates
- filterBySubject() - filter by subject name

Java concepts: HashMap, grouping, aggregation,
Comparator, filtering, Stream API (optional),
calculations, formatting
```

**Test:**
```java
// Create test data
List<StudySession> sessions = Arrays.asList(
    new StudySession("Java", 45, LocalDate.now()),
    new StudySession("Math", 60, LocalDate.now()),
    new StudySession("Java", 30, LocalDate.now().minusDays(1))
);

StatisticsCalculator calc = new StatisticsCalculator();

int total = calc.getTotalMinutes(sessions);
System.out.println("Total: " + calc.formatDuration(total));

Map<String, Integer> bySubject = calc.getTimeBySubject(sessions);
System.out.println("By subject: " + bySubject);
```

**Verify:**
- [ ] Calculations are correct
- [ ] Formatting works
- [ ] Filtering works
- [ ] Sorting works
- [ ] Handles empty lists

---

### Phase 3: User Interface (Commits 6-7)

#### Commit 6: InputValidator
**Files:**
- `src/InputValidator.java`

**Commit Message:**
```
feat: implement InputValidator for input validation

- Create InputValidator utility class
- All methods are static (no instance needed)

Validation methods:
- isValidSubject() - check not empty, reasonable length
- isValidDuration() - check positive, range 1-600
- isValidDate() - check date format and validity
- parseDate() - parse string to LocalDate with error handling
- isValidId() - check UUID format
- isValidChoice() - check menu choice range

Error handling:
- Return boolean for validation checks
- Throw exceptions with clear messages
- Provide helpful error messages

Java concepts: Static methods, validation logic,
exception handling, regular expressions (optional),
DateTimeParseException
```

**Test:**
```java
// Test validations
System.out.println(InputValidator.isValidSubject("")); // false
System.out.println(InputValidator.isValidSubject("Java")); // true

System.out.println(InputValidator.isValidDuration(-5)); // false
System.out.println(InputValidator.isValidDuration(45)); // true

try {
    LocalDate date = InputValidator.parseDate("2024-10-04");
    System.out.println("Valid date: " + date);
} catch (DateTimeParseException e) {
    System.out.println("Invalid date");
}
```

**Verify:**
- [ ] All validations work correctly
- [ ] Error messages are clear
- [ ] Handles edge cases
- [ ] No exceptions for valid input

---

#### Commit 7: MenuManager (User Interface)
**Files:**
- `src/MenuManager.java`

**Commit Message:**
```
feat: implement MenuManager for user interaction

- Create MenuManager class for CLI interface
- Initialize with Scanner, SessionManager, Calculator
- Implement all menu handlers

Main methods:
- displayMainMenu() - show menu options
- getMenuChoice() - get and validate choice
- run() - main menu loop

Feature handlers:
- handleAddSession() - prompt and add session
- handleEditSession() - select and edit session
- handleDeleteSession() - select and delete session
- handleViewHistory() - show history with filters
- handleViewStatistics() - show statistics/leaderboard

Helper methods:
- promptString() - get string with validation
- promptInt() - get integer with validation
- promptDate() - get date with validation
- displaySessionsTable() - format and display sessions
- displayStatistics() - format and display stats

Java concepts: Scanner, input handling, loops,
switch statements, method organization, formatting,
error handling, user experience
```

**Test:**
```bash
# Run and test each menu option:
javac src/*.java
java -cp src StudyTrackerApp
# Try all menu options
```

**Verify:**
- [ ] Menu displays correctly
- [ ] All options work
- [ ] Input validation works
- [ ] Error messages are helpful
- [ ] Navigation is smooth

---

### Phase 4: Integration (Commits 8-10)

#### Commit 8: Main Application
**Files:**
- `src/StudyTrackerApp.java`

**Commit Message:**
```
feat: implement main application entry point

- Create StudyTrackerApp with main() method
- Initialize all components:
  * FileManager
  * StudySessionManager
  * StatisticsCalculator
  * MenuManager
  
- Implement application lifecycle:
  * Welcome message
  * Main menu loop
  * Handle menu choices
  * Exit gracefully
  
- Add error handling:
  * Try-catch for all operations
  * Display user-friendly errors
  * Log technical details to console
  
- Add shutdown hook (optional):
  * Save data on unexpected exit

Java concepts: main() method, program flow,
object initialization, application lifecycle,
exception handling, user messages
```

**Commit Message Details:**
```
Complete features:
1. ✅ Add Study Session
2. ✅ Edit Session
3. ✅ Delete Session
4. ✅ View History (with filters)
5. ✅ View Statistics/Leaderboard

Application is now fully functional!
```

**Test:**
```bash
# Full application test
javac src/*.java
java -cp src StudyTrackerApp

# Test all 5 features:
# 1. Add 3-4 sessions with different subjects
# 2. Edit one session
# 3. Delete one session
# 4. View history with different filters
# 5. View statistics
```

**Verify:**
- [ ] Application starts successfully
- [ ] All 5 features work
- [ ] Data persists across restarts
- [ ] No crashes on invalid input
- [ ] Exit saves data

---

#### Commit 9: Enhanced Output Formatting
**Files:**
- Update `src/MenuManager.java`
- Update `src/StatisticsCalculator.java`

**Commit Message:**
```
feat: enhance output formatting and user experience

Improvements to MenuManager:
- Better table formatting for history view
- Aligned columns with proper spacing
- Add table borders and headers
- Color console output (optional)
- Add separators between sections

Improvements to Statistics display:
- Format leaderboard/subject breakdown as table
- Add visual bars for percentages
- Sort subjects by time (descending)
- Add "This week" vs "All time" sections
- Show rankings and medals (🥇🥈🥉)

Additional enhancements:
- Clear screen between menu actions (optional)
- Add "Press Enter to continue" prompts
- Improve spacing and readability
- Add success/error symbols (✓ ✗)

Java concepts: printf formatting, String.format(),
alignment, padding, ASCII tables
```

**Example Output:**
```
╔═══════════════════════════════════════════════════════════╗
║                   STUDY HISTORY                           ║
╚═══════════════════════════════════════════════════════════╝

Date       │ Subject           │ Duration │ Notes
───────────┼───────────────────┼──────────┼─────────────────
2024-10-04 │ Java Programming  │   45 min │ Classes & Objects
2024-10-04 │ Mathematics       │   60 min │ Calculus problems
2024-10-03 │ English           │   30 min │ Essay writing
───────────┴───────────────────┴──────────┴─────────────────
Total: 3 sessions, 2h 15m
```

**Verify:**
- [ ] Tables look professional
- [ ] Alignment is correct
- [ ] Statistics are clear
- [ ] Visual hierarchy is good

---

#### Commit 10: Documentation and Polish
**Files:**
- Update `README.md`
- Create `USAGE_GUIDE.md`
- Add code comments
- Create sample data

**Commit Message:**
```
docs: add comprehensive documentation and polish

Documentation:
- Update README with complete setup instructions
- Add compilation and run commands
- Document system requirements (Java 11+)
- Add troubleshooting section

New files:
- USAGE_GUIDE.md - detailed user guide
- sample_sessions.txt - sample data for testing
- CONTRIBUTING.md - how to extend the project

Code improvements:
- Add JavaDoc comments to all public methods
- Add inline comments for complex logic
- Improve variable names
- Add constants for magic numbers
- Final code cleanup

Polish:
- Remove debug print statements
- Add error messages for edge cases
- Test all edge cases
- Fix any remaining bugs

Ready for use! 🎉
```

**USAGE_GUIDE.md** includes:
- Getting started tutorial
- Step-by-step guide for each feature
- Screenshots of output
- FAQ section
- Extension ideas

**Verify:**
- [ ] README is complete and clear
- [ ] All code has comments
- [ ] No debug statements remain
- [ ] Sample data works
- [ ] Everything is polished

---

## Development Timeline

### Week 1: Foundation
- **Day 1-2**: Commits 1-2 (Setup, Model)
- **Day 3-4**: Commit 3 (FileManager)
- **Day 5**: Test and review

### Week 2: Core Logic
- **Day 1-3**: Commit 4 (StudySessionManager)
- **Day 4-5**: Commit 5 (StatisticsCalculator)

### Week 3: User Interface
- **Day 1-2**: Commit 6 (InputValidator)
- **Day 3-5**: Commit 7 (MenuManager)

### Week 4: Integration & Polish
- **Day 1-2**: Commit 8 (Main App)
- **Day 3**: Commit 9 (Formatting)
- **Day 4-5**: Commit 10 (Documentation)

**Total: ~4 weeks for a beginner**

---

## How to Compile and Run (After Each Commit)

### Compile:
```bash
cd /Users/derek.chung/projects/study-time-tracker
javac src/*.java
```

### Run:
```bash
java -cp src StudyTrackerApp
```

### Clean:
```bash
rm src/*.class
```

---

## Git Commands

### Initial Setup:
```bash
git init
git add README.md DESIGN_SIMPLE.md .gitignore
git commit -m "chore: initialize Java project structure"
```

### For Each Subsequent Commit:
```bash
# Make your changes
git add src/FileName.java
git commit -m "feat: implement FileName class"
```

### Check Status:
```bash
git status
git log --oneline
```

---

## Testing Checklist (Before Each Commit)

- [ ] Code compiles without errors
- [ ] No warnings
- [ ] Feature works as expected
- [ ] Edge cases handled
- [ ] Error messages are clear
- [ ] Code is commented
- [ ] Variable names are clear
- [ ] Commit message is descriptive

---

## Comparison: Simple vs Full Version

| Aspect | Simple Version | Full Spring Boot Version |
|--------|---------------|-------------------------|
| **Commits** | 10 | 20 |
| **Classes** | 7 | 20+ |
| **Lines of Code** | ~700 | ~2000+ |
| **Frameworks** | None | Spring Boot, JPA, Hibernate |
| **Database** | Text file | H2/PostgreSQL |
| **UI** | Console | Web (HTML/CSS/JS) |
| **Testing** | Manual | JUnit, Mockito, Integration |
| **Time to Complete** | 3-4 weeks | 8-10 weeks |
| **Prerequisite** | Basic Java | Spring, Web, DB, REST APIs |
| **Focus** | Core Java | Enterprise Architecture |
| **Good for** | Beginners | Intermediate/Advanced |

---

## Next Steps After Completing Simple Version

Once you've mastered this version, you can:

1. **Add Unit Tests**: Learn JUnit
2. **Add More Features**: Categories, goals, streaks
3. **Improve UI**: Use Swing or JavaFX
4. **Add Database**: Migrate to SQLite
5. **Learn Web**: Build the full Spring Boot version!

---

## Key Takeaway

**10 focused commits** that teach core Java concepts without the complexity of frameworks. Each commit is a milestone. Take your time with each one! 🚀

---

Good luck with your simplified study time tracker! 📚✨

