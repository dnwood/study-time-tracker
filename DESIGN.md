# Study Time Tracker - Design Document

## Overview
A Java web application for tracking study time with multi-user support. Users can log, edit, delete study sessions, view their history, and see a leaderboard through a modern web interface.

## Technology Stack
- **Backend**: Spring Boot 3.x (Java 17+)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla JS or React - TBD)
- **Database**: H2 (embedded) for development, PostgreSQL-ready for production
- **ORM**: Spring Data JPA (Hibernate)
- **API**: RESTful API
- **Testing**: JUnit 5, Mockito, Spring Test
- **Build**: Maven
- **Version Control**: Git

## Architecture

### Layer Architecture
```
┌─────────────────────────────────────┐
│     Presentation Layer (UI)         │  - Web Interface
│  - HTML/CSS/JavaScript              │  - Forms & Tables
│  - AJAX calls to backend            │  - Client-side validation
└─────────────────────────────────────┘
              ↓ (HTTP/REST)
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │  - HTTP endpoints
│  - UserController                   │  - Request/Response
│  - StudySessionController           │  - DTOs
│  - LeaderboardController            │  - Exception handling
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer                  │  - Business logic
│  - UserService                      │  - Calculations
│  - StudySessionService              │  - Aggregations
│  - LeaderboardService               │  - Validation
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Repository Layer (Spring Data)    │  - CRUD operations
│  - UserRepository                   │  - JPA queries
│  - StudySessionRepository           │  - Database access
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Database (H2/PostgreSQL)       │  - Persistent storage
│  - users table                      │
│  - study_sessions table             │
└─────────────────────────────────────┘
```

## Core Classes and Responsibilities

### 1. Domain Models (Package: `com.studytracker.model`)

#### `User`
JPA Entity representing a user in the system.
```java
@Entity
@Table(name = "users")
Fields:
- id: Long (auto-generated)
- username: String (unique, indexed)
- email: String (unique)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- sessions: List<StudySession> (OneToMany relationship)

Annotations:
- @Id, @GeneratedValue
- @Column(unique = true, nullable = false)
- @CreationTimestamp, @UpdateTimestamp
- @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)

Methods:
- getId(): Long
- getUsername(): String
- getEmail(): String
- getCreatedAt(): LocalDateTime
- getUpdatedAt(): LocalDateTime
- getSessions(): List<StudySession>
- setters for mutable fields
- equals(), hashCode(), toString()
```

#### `StudySession`
JPA Entity representing a single study session.
```java
@Entity
@Table(name = "study_sessions")
Fields:
- id: Long (auto-generated)
- user: User (ManyToOne relationship)
- subject: String
- durationMinutes: int
- date: LocalDate
- startTime: LocalTime (optional)
- endTime: LocalTime (optional)
- notes: String (optional)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

Annotations:
- @Id, @GeneratedValue
- @ManyToOne, @JoinColumn(name = "user_id")
- @Column(nullable = false)
- @CreationTimestamp, @UpdateTimestamp

Methods:
- getId(): Long
- getUser(): User
- getSubject(): String
- getDurationMinutes(): int
- getDate(): LocalDate
- getStartTime(): LocalTime
- getEndTime(): LocalTime
- getNotes(): String
- getCreatedAt(): LocalDateTime
- getUpdatedAt(): LocalDateTime
- setters for mutable fields
- equals(), hashCode(), toString()
```

### 2. Repository Layer (Package: `com.studytracker.repository`)

#### `UserRepository`
Spring Data JPA repository for User entities.
```java
@Repository
interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  
  // Custom queries using @Query if needed
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.sessions WHERE u.id = :id")
  Optional<User> findByIdWithSessions(@Param("id") Long id);
}
```

#### `StudySessionRepository`
Spring Data JPA repository for StudySession entities.
```java
@Repository
interface StudySessionRepository extends JpaRepository<StudySession, Long> {
  List<StudySession> findByUserIdOrderByDateDesc(Long userId);
  
  List<StudySession> findByUserIdAndDateBetweenOrderByDateDesc(
    Long userId, LocalDate startDate, LocalDate endDate);
  
  @Query("SELECT s FROM StudySession s WHERE s.user.id = :userId AND s.subject = :subject")
  List<StudySession> findByUserIdAndSubject(
    @Param("userId") Long userId, @Param("subject") String subject);
  
  @Query("SELECT s.subject, SUM(s.durationMinutes) FROM StudySession s " +
         "WHERE s.user.id = :userId GROUP BY s.subject")
  List<Object[]> getTotalTimeBySubject(@Param("userId") Long userId);
  
  @Query("SELECT u, SUM(s.durationMinutes) as totalTime FROM StudySession s " +
         "JOIN s.user u GROUP BY u.id ORDER BY totalTime DESC")
  List<Object[]> getLeaderboardData();
}
```

### 3. Service Layer (Package: `com.studytracker.service`)

#### `UserService`
Business logic for user management.
```
Methods:
- registerUser(String username, String email): User
- loginUser(String username): Optional<User>
- getUserById(String id): Optional<User>
- getAllUsers(): List<User>
- validateUsername(String username): boolean
- validateEmail(String email): boolean
```

#### `StudySessionService`
Business logic for study session management.
```
Methods:
- addSession(String userId, String subject, int durationMinutes, LocalDate date, String notes): StudySession
- editSession(String sessionId, String subject, Integer durationMinutes, LocalDate date, String notes): StudySession
- deleteSession(String sessionId): boolean
- getSessionById(String sessionId): Optional<StudySession>
- getUserSessions(String userId): List<StudySession>
- getUserSessionsByDateRange(String userId, LocalDate start, LocalDate end): List<StudySession>
- getTotalStudyTime(String userId): int
- getTotalStudyTimeByDateRange(String userId, LocalDate start, LocalDate end): int
- getSessionCountBySubject(String userId): Map<String, Integer>
- getTotalTimeBySubject(String userId): Map<String, Integer>
```

#### `LeaderboardService`
Business logic for leaderboard and statistics.
```
Methods:
- getTopUsersByTotalTime(int limit): List<LeaderboardEntry>
- getTopUsersBySessionCount(int limit): List<LeaderboardEntry>
- getTopUsersThisWeek(int limit): List<LeaderboardEntry>
- getTopUsersThisMonth(int limit): List<LeaderboardEntry>
- getUserRank(String userId): int
- getUserStats(String userId): UserStats
```

#### `LeaderboardEntry` (Helper Class)
```
Fields:
- rank: int
- username: String
- totalMinutes: int
- sessionCount: int
- formattedTime: String (e.g., "5h 30m")

Methods:
- getters and setters
- compareTo() for sorting
```

#### `UserStats` (Helper Class)
```
Fields:
- totalMinutes: int
- sessionCount: int
- averageSessionMinutes: double
- subjectBreakdown: Map<String, Integer>
- studyStreak: int (consecutive days)
- rank: int

Methods:
- getters and setters
```

### 4. Controller Layer (Package: `com.studytracker.controller`)

#### `UserController`
REST API endpoints for user management.
```java
@RestController
@RequestMapping("/api/users")
Endpoints:
- POST   /api/users/register     -> registerUser(@RequestBody UserRegistrationDto)
- POST   /api/users/login        -> loginUser(@RequestBody LoginDto)
- GET    /api/users/{id}         -> getUserById(@PathVariable Long id)
- GET    /api/users              -> getAllUsers()
- GET    /api/users/{id}/stats   -> getUserStats(@PathVariable Long id)
```

#### `StudySessionController`
REST API endpoints for study session management.
```java
@RestController
@RequestMapping("/api/sessions")
Endpoints:
- POST   /api/sessions                    -> createSession(@RequestBody SessionCreateDto)
- PUT    /api/sessions/{id}               -> updateSession(@PathVariable Long id, @RequestBody SessionUpdateDto)
- DELETE /api/sessions/{id}               -> deleteSession(@PathVariable Long id)
- GET    /api/sessions/{id}               -> getSessionById(@PathVariable Long id)
- GET    /api/sessions/user/{userId}      -> getUserSessions(@PathVariable Long userId)
- GET    /api/sessions/user/{userId}/filter -> filterSessions(@PathVariable Long userId, @RequestParam params)
```

#### `LeaderboardController`
REST API endpoints for leaderboard and statistics.
```java
@RestController
@RequestMapping("/api/leaderboard")
Endpoints:
- GET    /api/leaderboard                 -> getLeaderboard(@RequestParam(optional) String period)
- GET    /api/leaderboard/top/{limit}     -> getTopUsers(@PathVariable int limit)
- GET    /api/leaderboard/user/{id}/rank  -> getUserRank(@PathVariable Long id)
```

### 5. DTO Layer (Package: `com.studytracker.dto`)

#### Request DTOs
```java
- UserRegistrationDto: username, email
- LoginDto: username
- SessionCreateDto: userId, subject, durationMinutes, date, startTime, endTime, notes
- SessionUpdateDto: subject, durationMinutes, date, startTime, endTime, notes
```

#### Response DTOs
```java
- UserResponseDto: id, username, email, createdAt
- SessionResponseDto: id, userId, username, subject, durationMinutes, date, startTime, endTime, notes, createdAt
- UserStatsDto: totalMinutes, sessionCount, averageSessionMinutes, subjectBreakdown, rank
- LeaderboardEntryDto: rank, userId, username, totalMinutes, sessionCount, formattedTime
```

### 5. Utility Classes (Package: `com.studytracker.util`)

#### `DateTimeUtil`
Helper methods for date/time formatting and calculations.
```
Methods:
- formatDuration(int minutes): String (e.g., "2h 30m")
- formatDate(LocalDate date): String
- formatDateTime(LocalDateTime dateTime): String
- getStartOfWeek(LocalDate date): LocalDate
- getEndOfWeek(LocalDate date): LocalDate
- getStartOfMonth(LocalDate date): LocalDate
- getEndOfMonth(LocalDate date): LocalDate
- calculateStreak(List<LocalDate> studyDates): int
```

#### `FileUtil`
Helper methods for file I/O operations.
```
Methods:
- ensureDataDirectoryExists(): void
- readObjectFromFile(String filename): Object
- writeObjectToFile(Object obj, String filename): void
- fileExists(String filename): boolean
- createBackup(String filename): void
```

### 6. Main Application (Package: `com.studytracker`)

#### `StudyTrackerApp`
Application entry point.
```
Methods:
- main(String[] args): void
- initializeApplication(): void
- shutdownApplication(): void
```

## Data Storage Strategy

### Database Schema

#### `users` table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (username)
);
```

#### `study_sessions` table
```sql
CREATE TABLE study_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  subject VARCHAR(100) NOT NULL,
  duration_minutes INT NOT NULL,
  date DATE NOT NULL,
  start_time TIME,
  end_time TIME,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_date (user_id, date),
  INDEX idx_subject (subject)
);
```

### Configuration
- **Development**: H2 in-memory database (auto-configured)
- **Production**: PostgreSQL (configurable via application.properties)
- **Data Initialization**: Sample data loaded via data.sql (optional)

## Feature Requirements Mapping

### 1. Add Your Time
```
Flow:
1. User selects "Add Session" from menu
2. System prompts for:
   - Subject/Topic
   - Duration (in minutes)
   - Date (default: today)
   - Optional: Notes
3. System validates input
4. StudySessionService.addSession() creates new session
5. Repository saves to file
6. Display success message
```

### 2. Edit Your Time
```
Flow:
1. User selects "Edit Session"
2. System displays user's recent sessions
3. User selects session ID to edit
4. System displays current values
5. User enters new values (blank to keep current)
6. System validates and updates
7. Display success message
```

### 3. Delete Your Time
```
Flow:
1. User selects "Delete Session"
2. System displays user's sessions
3. User selects session ID to delete
4. System asks for confirmation
5. StudySessionService.deleteSession() removes session
6. Display success message
```

### 4. Fetch Your History
```
Flow:
1. User selects "View History"
2. System offers filter options:
   - All time
   - This week
   - This month
   - Custom date range
   - By subject
3. Display filtered sessions in table format
4. Show summary statistics
```

### 5. Leaderboard Inspect
```
Flow:
1. User selects "View Leaderboard"
2. System offers leaderboard options:
   - All-time top users
   - This week's top users
   - This month's top users
   - By session count
3. Display ranked list with:
   - Rank
   - Username
   - Total time
   - Session count
4. Highlight current user's position
```

## UI Pages and Components

### Page Structure

#### 1. Login/Register Page (`/index.html`)
- User login form
- User registration form
- Toggle between login and register
- Client-side validation

#### 2. Dashboard Page (`/dashboard.html`)
- Navigation sidebar
- Current user info
- Quick stats cards (total time, sessions, avg)
- Recent sessions table

#### 3. Add/Edit Session Page (`/session-form.html`)
- Form fields: subject, duration, date, time, notes
- Client-side validation
- Submit button
- Cancel button

#### 4. History Page (`/history.html`)
- Filter controls (date range, subject)
- Sessions table with edit/delete actions
- Pagination
- Export button (future)

#### 5. Leaderboard Page (`/leaderboard.html`)
- Period filter (all-time, week, month)
- Ranked user list
- Current user highlight
- Visual indicators (medals for top 3)

#### 6. Statistics Page (`/statistics.html`)
- Time by subject chart (text-based or canvas)
- Study streak
- Best day/time
- Study patterns

### UI Components (Reusable)
- Navigation bar
- User profile dropdown
- Session card
- Stats card
- Data table
- Modal dialogs
- Loading spinner
- Toast notifications

## Error Handling Strategy

1. **Input Validation**: Validate all user inputs before processing
2. **Exception Handling**: Catch and handle specific exceptions
3. **User-Friendly Messages**: Display clear error messages
4. **Graceful Degradation**: Continue operation even if some features fail
5. **Data Integrity**: Validate data before saving to files

## Future Enhancement Ideas

1. **Categories/Tags**: Allow multiple tags per session
2. **Goals**: Set daily/weekly study goals
3. **Reminders**: Study reminders based on schedule
4. **Statistics**: Detailed analytics and charts (text-based)
5. **Export**: Export data to CSV/PDF
6. **Import**: Import data from other sources
7. **Study Streaks**: Track consecutive study days
8. **Pomodoro Integration**: Built-in timer with breaks
9. **Team/Group Features**: Study groups and shared goals
10. **API Integration**: Connect to calendar apps

## Project Structure

```
study-time-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/studytracker/
│   │   │   ├── StudyTrackerApplication.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   └── StudySession.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── StudySessionRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── StudySessionService.java
│   │   │   │   └── LeaderboardService.java
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java
│   │   │   │   ├── StudySessionController.java
│   │   │   │   └── LeaderboardController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── UserRegistrationDto.java
│   │   │   │   │   ├── LoginDto.java
│   │   │   │   │   ├── SessionCreateDto.java
│   │   │   │   │   └── SessionUpdateDto.java
│   │   │   │   └── response/
│   │   │   │       ├── UserResponseDto.java
│   │   │   │       ├── SessionResponseDto.java
│   │   │   │       ├── UserStatsDto.java
│   │   │   │       └── LeaderboardEntryDto.java
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── util/
│   │   │       └── DateTimeUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── data.sql (optional sample data)
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── js/
│   │       │   │   ├── app.js
│   │       │   │   ├── api.js
│   │       │   │   └── utils.js
│   │       │   └── img/
│   │       └── templates/ (if using Thymeleaf)
│   │           ├── index.html
│   │           ├── dashboard.html
│   │           ├── session-form.html
│   │           ├── history.html
│   │           ├── leaderboard.html
│   │           └── statistics.html
│   └── test/
│       └── java/com/studytracker/
│           ├── model/
│           │   ├── UserTest.java
│           │   └── StudySessionTest.java
│           ├── repository/
│           │   ├── UserRepositoryTest.java
│           │   └── StudySessionRepositoryTest.java
│           ├── service/
│           │   ├── UserServiceTest.java
│           │   ├── StudySessionServiceTest.java
│           │   └── LeaderboardServiceTest.java
│           └── controller/
│               ├── UserControllerTest.java
│               ├── StudySessionControllerTest.java
│               └── LeaderboardControllerTest.java
├── .gitignore
├── DESIGN.md
├── README.md
└── pom.xml
```

## Development Approach & Git Workflow

### Commit Strategy
Each commit should represent a **complete, testable feature or component**. Commits should be:
- Focused on one logical change
- Include tests for the code
- Have descriptive commit messages
- Build successfully
- Pass all tests

### Planned Commits

#### Commit 1: Project Setup
- Initialize Spring Boot project
- Configure pom.xml dependencies
- Set up application.properties
- Create .gitignore
- Update README with setup instructions

#### Commit 2: Domain Models
- Implement User entity with JPA annotations
- Implement StudySession entity with JPA annotations
- Add entity tests (UserTest, StudySessionTest)
- Test entity relationships

#### Commit 3: Repository Layer
- Create UserRepository interface
- Create StudySessionRepository interface
- Add custom query methods
- Write repository integration tests

#### Commit 4: Service Layer - User Management
- Implement UserService
- Add user registration logic
- Add user login logic
- Write UserService unit tests

#### Commit 5: Service Layer - Session Management
- Implement StudySessionService
- Add CRUD operations
- Add filtering and search
- Write StudySessionService unit tests

#### Commit 6: Service Layer - Leaderboard
- Implement LeaderboardService
- Add ranking calculations
- Add statistics aggregation
- Write LeaderboardService unit tests

#### Commit 7: DTOs and Exception Handling
- Create request DTOs
- Create response DTOs
- Implement custom exceptions
- Add GlobalExceptionHandler
- Write DTO validation tests

#### Commit 8: REST API - User Endpoints
- Implement UserController
- Add user registration endpoint
- Add user login endpoint
- Add user info endpoints
- Write UserController integration tests

#### Commit 9: REST API - Session Endpoints
- Implement StudySessionController
- Add CRUD endpoints
- Add filter/search endpoints
- Write StudySessionController integration tests

#### Commit 10: REST API - Leaderboard Endpoints
- Implement LeaderboardController
- Add leaderboard endpoints
- Add statistics endpoints
- Write LeaderboardController integration tests

#### Commit 11: Frontend - Base UI Structure
- Create HTML templates
- Add CSS styling (responsive design)
- Create navigation components
- Add client-side utilities

#### Commit 12: Frontend - User Pages
- Implement login/register page
- Add form validation
- Connect to user API endpoints
- Add session management (localStorage)

#### Commit 13: Frontend - Dashboard
- Implement dashboard page
- Add stats cards
- Show recent sessions
- Connect to API endpoints

#### Commit 14: Frontend - Session Management
- Implement add session form
- Implement edit session form
- Add delete confirmation
- Connect to session API endpoints

#### Commit 15: Frontend - History Page
- Implement history view
- Add filtering controls
- Add pagination
- Connect to API endpoints

#### Commit 16: Frontend - Leaderboard
- Implement leaderboard page
- Add period filters
- Add visual enhancements
- Connect to leaderboard API

#### Commit 17: Frontend - Statistics
- Implement statistics page
- Add subject breakdown
- Add study patterns
- Add visualizations

#### Commit 18: Integration Testing
- Add end-to-end tests
- Test complete workflows
- Test error scenarios
- Add test documentation

#### Commit 19: Documentation & Polish
- Update README with full documentation
- Add API documentation
- Add deployment guide
- Code cleanup and refactoring

#### Commit 20: Production Readiness
- Add production configuration
- Add logging
- Add security considerations
- Final testing

### Git Commit Message Format
```
<type>: <subject>

<body (optional)>

<footer (optional)>
```

Types:
- **feat**: New feature
- **fix**: Bug fix
- **refactor**: Code refactoring
- **test**: Adding tests
- **docs**: Documentation
- **chore**: Maintenance tasks

Examples:
```
feat: implement User entity with JPA annotations

- Add User model with id, username, email fields
- Configure JPA relationships and constraints
- Add timestamps for audit tracking
- Include comprehensive unit tests

Test: UserTest.java covers all entity methods
```

## Key Design Decisions

1. **Layered Architecture**: Separates concerns and makes testing easier
2. **UUID for IDs**: Ensures uniqueness across distributed scenarios
3. **File-based Storage**: Simple, no database setup required
4. **Builder Pattern**: For complex object creation (optional enhancement)
5. **Strategy Pattern**: For different leaderboard calculations
6. **Command Pattern**: For menu actions (optional enhancement)

## Testing Strategy

1. **Unit Tests**: Test individual methods in isolation
2. **Integration Tests**: Test service layer with repositories
3. **Manual Testing**: CLI interaction testing
4. **Edge Cases**: Empty data, invalid inputs, boundary conditions

This design provides a solid foundation while remaining flexible for future enhancements!

