# Git Workflow & Commit Plan

## Repository Setup

This will be stored in a **private Git repository** with incremental, test-driven commits.

## Commit Strategy

Each commit represents a **complete, testable feature** that:
- ✅ Builds successfully
- ✅ Passes all tests
- ✅ Has descriptive commit messages
- ✅ Includes both implementation and tests
- ✅ Represents a logical unit of work

## 20 Planned Commits

### Phase 1: Foundation (Commits 1-3)

#### Commit 1: Project Setup & Configuration
**Files:**
- `pom.xml` - Spring Boot dependencies
- `.gitignore` - Git ignore rules
- `README.md` - Project documentation
- `DESIGN.md` - Architecture documentation
- `src/main/resources/application.properties` - Spring configuration
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

**Commit Message:**
```
chore: initialize Spring Boot project with configuration

- Add Spring Boot 3.2 parent POM with dependencies
- Configure H2 and PostgreSQL database support
- Add JUnit 5, Mockito for testing
- Include Lombok for reducing boilerplate
- Create comprehensive .gitignore
- Document project structure and architecture

Dependencies: Spring Web, Spring Data JPA, H2, PostgreSQL,
Validation, DevTools, Lombok
```

#### Commit 2: Domain Models with Tests
**Files:**
- `src/main/java/com/studytracker/model/User.java`
- `src/main/java/com/studytracker/model/StudySession.java`
- `src/test/java/com/studytracker/model/UserTest.java`
- `src/test/java/com/studytracker/model/StudySessionTest.java`

**Commit Message:**
```
feat: implement User and StudySession JPA entities

- Create User entity with JPA annotations
  * Fields: id, username, email, timestamps
  * OneToMany relationship with StudySession
  * Unique constraints on username and email
  
- Create StudySession entity with JPA annotations
  * Fields: id, user, subject, duration, date, times, notes
  * ManyToOne relationship with User
  * Timestamps for audit tracking

- Add comprehensive unit tests
  * Test entity creation and getters/setters
  * Test relationship mappings
  * Test equals() and hashCode()
  * Test validation constraints

Tests: 100% coverage on model classes
```

#### Commit 3: Repository Layer with Tests
**Files:**
- `src/main/java/com/studytracker/repository/UserRepository.java`
- `src/main/java/com/studytracker/repository/StudySessionRepository.java`
- `src/test/java/com/studytracker/repository/UserRepositoryTest.java`
- `src/test/java/com/studytracker/repository/StudySessionRepositoryTest.java`
- `src/main/java/com/studytracker/StudyTrackerApplication.java` (main class)

**Commit Message:**
```
feat: implement Spring Data JPA repositories

- Create UserRepository interface
  * Methods: findByUsername, existsByUsername, existsByEmail
  * Custom query for fetching user with sessions
  
- Create StudySessionRepository interface
  * Methods: findByUserId, findByUserIdAndDateBetween
  * Custom queries for subject filtering and aggregations
  * Leaderboard query for user rankings

- Add integration tests using @DataJpaTest
  * Test CRUD operations
  * Test custom query methods
  * Test relationships and cascading
  * Test data integrity constraints

- Create main Spring Boot application class

Tests: All repository methods tested with H2 in-memory DB
```

### Phase 2: Business Logic (Commits 4-6)

#### Commit 4: User Service Layer
**Files:**
- `src/main/java/com/studytracker/service/UserService.java`
- `src/test/java/com/studytracker/service/UserServiceTest.java`

**Commit Message:**
```
feat: implement UserService with business logic

- Implement user registration
  * Validate unique username and email
  * Create and persist user
  
- Implement user login
  * Find user by username
  * Return user details

- Add validation methods
  * Username validation (length, format)
  * Email validation (format)
  
- Write comprehensive unit tests with Mockito
  * Test successful registration
  * Test duplicate username/email handling
  * Test login success and failure
  * Test validation logic

Tests: 95%+ coverage with mocked repositories
```

#### Commit 5: StudySession Service Layer
**Files:**
- `src/main/java/com/studytracker/service/StudySessionService.java`
- `src/test/java/com/studytracker/service/StudySessionServiceTest.java`

**Commit Message:**
```
feat: implement StudySessionService with CRUD operations

- Implement session management
  * Create new study session
  * Update existing session
  * Delete session
  * Retrieve sessions by user and filters
  
- Add filtering capabilities
  * Filter by date range
  * Filter by subject
  * Sort by date/duration
  
- Add aggregation methods
  * Calculate total study time
  * Group time by subject
  * Calculate session statistics

- Write unit tests with Mockito
  * Test all CRUD operations
  * Test filtering logic
  * Test aggregation calculations
  * Test error scenarios

Tests: 95%+ coverage with mocked repositories
```

#### Commit 6: Leaderboard Service Layer
**Files:**
- `src/main/java/com/studytracker/service/LeaderboardService.java`
- `src/test/java/com/studytracker/service/LeaderboardServiceTest.java`

**Commit Message:**
```
feat: implement LeaderboardService with ranking logic

- Implement leaderboard calculations
  * Get top users by total time
  * Get top users by session count
  * Filter by time period (week, month, all-time)
  * Calculate user rank
  
- Add statistics aggregation
  * User statistics (total time, sessions, average)
  * Subject breakdown
  * Study streak calculation
  
- Write unit tests with Mockito
  * Test ranking calculations
  * Test period filtering
  * Test statistics accuracy
  * Test edge cases (no data, ties)

Tests: 95%+ coverage with mocked repositories
```

### Phase 3: REST API (Commits 7-10)

#### Commit 7: DTOs and Exception Handling
**Files:**
- `src/main/java/com/studytracker/dto/request/*.java` (4 files)
- `src/main/java/com/studytracker/dto/response/*.java` (4 files)
- `src/main/java/com/studytracker/exception/*.java` (3 files)
- `src/test/java/com/studytracker/dto/ValidationTest.java`

**Commit Message:**
```
feat: add DTOs and global exception handling

- Create request DTOs
  * UserRegistrationDto, LoginDto
  * SessionCreateDto, SessionUpdateDto
  * Add validation annotations (@NotNull, @Size, etc.)
  
- Create response DTOs
  * UserResponseDto, SessionResponseDto
  * UserStatsDto, LeaderboardEntryDto
  * Hide sensitive data, format dates
  
- Implement exception handling
  * ResourceNotFoundException
  * DuplicateResourceException
  * GlobalExceptionHandler with @ControllerAdvice
  * Return proper HTTP status codes and error messages

- Add DTO validation tests
  * Test validation constraints
  * Test DTO mapping
  * Test error response format

Tests: All validation scenarios covered
```

#### Commit 8: User REST API
**Files:**
- `src/main/java/com/studytracker/controller/UserController.java`
- `src/test/java/com/studytracker/controller/UserControllerTest.java`

**Commit Message:**
```
feat: implement User REST API endpoints

Endpoints:
- POST /api/users/register - Register new user
- POST /api/users/login - User login
- GET /api/users/{id} - Get user details
- GET /api/users - Get all users
- GET /api/users/{id}/stats - Get user statistics

Features:
- Request/response validation
- Proper HTTP status codes
- Exception handling
- CORS configuration

Tests:
- Integration tests using @WebMvcTest
- Test all endpoints (success and error cases)
- Test request validation
- Test response format

Tests: 95%+ coverage with MockMvc
```

#### Commit 9: StudySession REST API
**Files:**
- `src/main/java/com/studytracker/controller/StudySessionController.java`
- `src/test/java/com/studytracker/controller/StudySessionControllerTest.java`

**Commit Message:**
```
feat: implement StudySession REST API endpoints

Endpoints:
- POST /api/sessions - Create session
- PUT /api/sessions/{id} - Update session
- DELETE /api/sessions/{id} - Delete session
- GET /api/sessions/{id} - Get session by ID
- GET /api/sessions/user/{userId} - Get user sessions
- GET /api/sessions/user/{userId}/filter - Filter sessions

Features:
- Full CRUD operations
- Advanced filtering (date range, subject)
- Sorting options
- Pagination support (optional)

Tests:
- Integration tests with MockMvc
- Test all CRUD operations
- Test filtering and sorting
- Test authorization (user can only edit own sessions)

Tests: 95%+ coverage with MockMvc
```

#### Commit 10: Leaderboard REST API
**Files:**
- `src/main/java/com/studytracker/controller/LeaderboardController.java`
- `src/main/java/com/studytracker/util/DateTimeUtil.java`
- `src/test/java/com/studytracker/controller/LeaderboardControllerTest.java`
- `src/test/java/com/studytracker/util/DateTimeUtilTest.java`

**Commit Message:**
```
feat: implement Leaderboard REST API endpoints

Endpoints:
- GET /api/leaderboard - Get leaderboard
- GET /api/leaderboard/top/{limit} - Get top N users
- GET /api/leaderboard/user/{id}/rank - Get user rank

Features:
- Period filtering (all-time, week, month)
- Configurable limit
- Include rank, username, time, sessions
- Format duration (e.g., "5h 30m")

Utility:
- DateTimeUtil for date/time formatting
- Duration formatting helper
- Date range calculations

Tests:
- Integration tests with MockMvc
- Test all leaderboard endpoints
- Test period filtering
- Test ranking accuracy
- Test utility methods

Tests: 95%+ coverage
```

### Phase 4: Frontend (Commits 11-17)

#### Commit 11: Frontend Base Structure
**Files:**
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/js/api.js`
- `src/main/resources/static/js/utils.js`
- `src/main/resources/static/js/app.js`
- Common HTML components

**Commit Message:**
```
feat: create frontend base structure and utilities

CSS:
- Modern, responsive design
- CSS variables for theming
- Flexbox/Grid layouts
- Mobile-first approach
- Animation and transitions

JavaScript:
- API client for backend calls
- Utility functions (date formatting, validation)
- Session management (localStorage)
- Error handling and toast notifications
- Loading states

Features:
- Reusable UI components
- Consistent styling
- Accessible design
```

#### Commit 12: Login and Registration Pages
**Files:**
- `src/main/resources/static/index.html`
- `src/main/resources/static/js/auth.js`

**Commit Message:**
```
feat: implement login and registration pages

Features:
- Login form with username input
- Registration form with username and email
- Toggle between login/register
- Client-side validation
- Error message display
- Success redirect to dashboard

Functionality:
- Call /api/users/login and /api/users/register
- Store user info in localStorage
- Handle authentication errors
- Redirect authenticated users

UI/UX:
- Clean, modern form design
- Real-time validation feedback
- Loading states during API calls
- Keyboard shortcuts (Enter to submit)
```

#### Commit 13: Dashboard Page
**Files:**
- `src/main/resources/static/dashboard.html`
- `src/main/resources/static/js/dashboard.js`

**Commit Message:**
```
feat: implement dashboard page

Components:
- Navigation sidebar with menu
- User info display
- Stats cards (total time, sessions, average)
- Recent sessions table (last 5-10)
- Quick action buttons

Features:
- Fetch user statistics from API
- Display formatted data
- Real-time updates
- Responsive layout

Navigation:
- Links to all major pages
- Active page highlighting
- User profile dropdown
- Logout functionality
```

#### Commit 14: Add/Edit Session Forms
**Files:**
- `src/main/resources/static/session-form.html`
- `src/main/resources/static/js/session-form.js`

**Commit Message:**
```
feat: implement add/edit session forms

Form Fields:
- Subject/Topic (required)
- Duration in minutes (required)
- Date (default: today)
- Start time (optional)
- End time (optional)
- Notes (textarea, optional)

Features:
- Create new session (POST /api/sessions)
- Edit existing session (PUT /api/sessions/{id})
- Client-side validation
- Date picker
- Time picker
- Character count for notes
- Cancel button (go back)

Validation:
- Required field checks
- Duration must be positive
- Date cannot be in future
- Start time before end time

UI/UX:
- Auto-calculate duration from times
- Save button disabled until valid
- Success message and redirect
- Error handling
```

#### Commit 15: History Page with Filtering
**Files:**
- `src/main/resources/static/history.html`
- `src/main/resources/static/js/history.js`

**Commit Message:**
```
feat: implement history page with filtering

Components:
- Filter controls (date range, subject)
- Sessions table with columns:
  * Date, Subject, Duration, Start/End Time, Notes
  * Edit and Delete action buttons
- Pagination controls
- Summary statistics (total time, avg session)

Features:
- Fetch sessions from API
- Apply filters (date range, subject)
- Sort by column (date, duration)
- Edit session (navigate to form)
- Delete session with confirmation
- Refresh after actions

Filtering:
- Date range picker (from/to)
- Subject dropdown (from user's subjects)
- Clear filters button
- Apply filters button

UI/UX:
- Table with hover effects
- Responsive design (cards on mobile)
- Loading skeleton
- Empty state when no sessions
- Confirmation modal for delete
```

#### Commit 16: Leaderboard Page
**Files:**
- `src/main/resources/static/leaderboard.html`
- `src/main/resources/static/js/leaderboard.js`

**Commit Message:**
```
feat: implement leaderboard page

Components:
- Period filter (All-time, This Week, This Month)
- Leaderboard table:
  * Rank, Username, Total Time, Sessions
  * Visual medals for top 3 (🥇🥈🥉)
  * Highlight current user
- User's own rank display

Features:
- Fetch leaderboard from API
- Filter by time period
- Show formatted time (e.g., "10h 30m")
- Auto-refresh option
- View top 50 users

UI/UX:
- Podium design for top 3
- Gradient colors for ranks
- Current user highlighted
- Smooth transitions
- Responsive layout
```

#### Commit 17: Statistics Page
**Files:**
- `src/main/resources/static/statistics.html`
- `src/main/resources/static/js/statistics.js`
- `src/main/resources/static/js/chart.js` (simple charting)

**Commit Message:**
```
feat: implement statistics page

Components:
- Overview cards (total time, sessions, streak)
- Time by subject breakdown
  * Table or simple bar chart
  * Percentage of total
- Study patterns
  * Most productive day of week
  * Most productive time of day
  * Average session length
- Study streak visualization

Features:
- Fetch statistics from API
- Display aggregated data
- Calculate percentages
- Format durations
- Show trends

Visualization:
- Simple bar charts (CSS or Canvas)
- Progress bars for subject breakdown
- Calendar heatmap for streak (optional)
- Responsive charts

UI/UX:
- Card-based layout
- Color-coded categories
- Interactive hover states
- Print-friendly view
```

### Phase 5: Testing & Polish (Commits 18-20)

#### Commit 18: Integration Tests
**Files:**
- `src/test/java/com/studytracker/integration/*.java`
- Test data setup utilities

**Commit Message:**
```
test: add end-to-end integration tests

Test Scenarios:
- User registration and login flow
- Create, read, update, delete sessions
- Filter and search sessions
- View leaderboard with different periods
- Calculate statistics accurately

Test Setup:
- Use @SpringBootTest for full context
- H2 in-memory database for isolation
- Test data builders for setup
- Cleanup after each test

Coverage:
- Complete user workflows
- API endpoint integration
- Database operations
- Error handling paths

Tests: >90% integration coverage
```

#### Commit 19: Documentation and API Docs
**Files:**
- Update `README.md` with complete instructions
- `API_DOCUMENTATION.md` with endpoint details
- `DEPLOYMENT.md` with deployment guide
- Code comments and JavaDoc

**Commit Message:**
```
docs: add comprehensive documentation

README.md:
- Complete setup instructions
- Usage guide with screenshots
- API endpoint reference
- Troubleshooting section

API_DOCUMENTATION.md:
- All endpoints documented
- Request/response examples
- Error codes and messages
- Authentication flow

DEPLOYMENT.md:
- Local deployment steps
- Production deployment (Docker, Heroku, AWS)
- Database setup instructions
- Environment configuration

Code Documentation:
- JavaDoc for all public methods
- Inline comments for complex logic
- Architecture decision records
```

#### Commit 20: Production Readiness
**Files:**
- Production configuration
- Logging configuration
- Security enhancements
- Performance optimizations

**Commit Message:**
```
chore: prepare application for production

Configuration:
- Production application.properties
- Environment variable support
- Logging configuration (Logback)
- CORS configuration

Security:
- Input sanitization
- SQL injection prevention (JPA handles this)
- XSS protection headers
- Rate limiting considerations

Performance:
- Database connection pooling
- Query optimization
- Caching strategies (optional)
- Static resource optimization

Deployment:
- Docker support (Dockerfile)
- Health check endpoint
- Graceful shutdown
- Database migration strategy

Ready for deployment! 🚀
```

## Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

### Types:
- **feat**: New feature
- **fix**: Bug fix
- **test**: Adding tests
- **refactor**: Code refactoring
- **docs**: Documentation
- **chore**: Maintenance tasks
- **style**: Code formatting

### Examples:

**Good commit message:**
```
feat: implement User entity with JPA annotations

- Add User model with id, username, email fields
- Configure JPA relationships and constraints
- Add timestamps for audit tracking
- Include comprehensive unit tests

Tests: UserTest.java covers all entity methods
```

**Bad commit message:**
```
Update files
```

## Git Commands

### Initial Setup
```bash
git init
git add .
git commit -m "chore: initialize Spring Boot project with configuration"
git remote add origin <your-private-repo-url>
git push -u origin main
```

### For Each Commit
```bash
# Make changes
git add <files>
git commit -m "<type>: <subject>"
git push origin main
```

### Before Each Commit
```bash
# Ensure tests pass
./mvnw test

# Ensure build succeeds
./mvnw clean install
```

## Branch Strategy (Optional)

For larger features, consider feature branches:
```bash
git checkout -b feature/user-service
# Make changes and commits
git checkout main
git merge feature/user-service
git push origin main
```

## Review Checklist

Before each commit, verify:
- [ ] All tests pass (`./mvnw test`)
- [ ] Build succeeds (`./mvnw clean install`)
- [ ] Code is formatted consistently
- [ ] No debug code or console.log()
- [ ] Commit message is descriptive
- [ ] Related tests are included
- [ ] Documentation is updated if needed

---

This workflow ensures a clean, professional Git history that demonstrates your development process and makes the project easy to understand and maintain.

