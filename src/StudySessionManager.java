import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages all study session operations (CRUD) and statistics.
 * 
 * This class serves as the main business logic layer, handling:
 * - Creating, reading, updating, and deleting sessions
 * - Filtering and searching sessions
 * - Calculating statistics
 * - Automatic persistence to file
 * 
 * Key concepts demonstrated:
 * - CRUD operations pattern
 * - List filtering with streams
 * - Map for grouping data
 * - Automatic data persistence
 * - Business logic separation
 */
public class StudySessionManager {
    
    private List<StudySession> sessions;
    private final JsonFileManager fileManager;
    
    /**
     * Constructor - initializes manager and loads existing sessions.
     * 
     * @param fileManager the file manager for persistence
     */
    public StudySessionManager(JsonFileManager fileManager) {
        this.fileManager = fileManager;
        this.sessions = new ArrayList<>();
        loadSessions();
    }
    
    /**
     * Adds a new study session.
     * 
     * @param subject the subject being studied
     * @param durationMinutes the duration in minutes
     * @param date the date of the session
     * @param notes optional notes
     * @return the created session
     */
    public StudySession addSession(String subject, int durationMinutes, LocalDate date, String notes) {
        StudySession session = new StudySession(subject, durationMinutes, date);
        if (notes != null && !notes.trim().isEmpty()) {
            session.setNotes(notes.trim());
        }
        
        sessions.add(session);
        saveSessions();
        
        return session;
    }
    
    /**
     * Adds a new study session with start and end times.
     * 
     * @param subject the subject being studied
     * @param durationMinutes the duration in minutes
     * @param date the date of the session
     * @param startTime optional start time
     * @param endTime optional end time
     * @param notes optional notes
     * @return the created session
     */
    public StudySession addSession(String subject, int durationMinutes, LocalDate date,
                                   LocalTime startTime, LocalTime endTime, String notes) {
        StudySession session = addSession(subject, durationMinutes, date, notes);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        saveSessions();
        
        return session;
    }
    
    /**
     * Updates an existing study session.
     * Only updates non-null values.
     * 
     * @param id the session ID
     * @param subject new subject (null to keep current)
     * @param durationMinutes new duration (null to keep current)
     * @param date new date (null to keep current)
     * @param startTime new start time (null to keep current)
     * @param endTime new end time (null to keep current)
     * @param notes new notes (null to keep current)
     * @return the updated session, or null if not found
     */
    public StudySession updateSession(String id, String subject, Integer durationMinutes,
                                     LocalDate date, LocalTime startTime, LocalTime endTime, String notes) {
        StudySession session = getSessionById(id);
        if (session == null) {
            return null;
        }
        
        if (subject != null) {
            session.setSubject(subject);
        }
        if (durationMinutes != null) {
            session.setDurationMinutes(durationMinutes);
        }
        if (date != null) {
            session.setDate(date);
        }
        if (startTime != null) {
            session.setStartTime(startTime);
        }
        if (endTime != null) {
            session.setEndTime(endTime);
        }
        if (notes != null) {
            session.setNotes(notes);
        }
        
        saveSessions();
        return session;
    }
    
    /**
     * Deletes a study session by ID.
     * 
     * @param id the session ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteSession(String id) {
        boolean removed = sessions.removeIf(session -> session.getId().equals(id));
        if (removed) {
            saveSessions();
        }
        return removed;
    }
    
    /**
     * Gets a session by ID.
     * 
     * @param id the session ID
     * @return the session, or null if not found
     */
    public StudySession getSessionById(String id) {
        return sessions.stream()
                      .filter(session -> session.getId().equals(id))
                      .findFirst()
                      .orElse(null);
    }
    
    /**
     * Gets all sessions.
     * 
     * @return list of all sessions
     */
    public List<StudySession> getAllSessions() {
        return new ArrayList<>(sessions);
    }
    
    /**
     * Gets sessions filtered by date range.
     * 
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return filtered list of sessions
     */
    public List<StudySession> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return sessions.stream()
                      .filter(session -> !session.getDate().isBefore(startDate) && 
                                       !session.getDate().isAfter(endDate))
                      .collect(Collectors.toList());
    }
    
    /**
     * Gets sessions filtered by subject.
     * 
     * @param subject the subject to filter by (case-insensitive partial match)
     * @return filtered list of sessions
     */
    public List<StudySession> getSessionsBySubject(String subject) {
        String lowerSubject = subject.toLowerCase();
        return sessions.stream()
                      .filter(session -> session.getSubject().toLowerCase().contains(lowerSubject))
                      .collect(Collectors.toList());
    }
    
    /**
     * Gets sessions sorted by date (newest first).
     * 
     * @return sorted list of sessions
     */
    public List<StudySession> getSessionsSortedByDate() {
        return sessions.stream()
                      .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                      .collect(Collectors.toList());
    }
    
    // Statistics Methods
    
    /**
     * Calculates total study time in minutes.
     * 
     * @return total minutes studied
     */
    public int getTotalMinutes() {
        return sessions.stream()
                      .mapToInt(StudySession::getDurationMinutes)
                      .sum();
    }
    
    /**
     * Calculates total study time for a date range.
     * 
     * @param startDate start date
     * @param endDate end date
     * @return total minutes in range
     */
    public int getTotalMinutes(LocalDate startDate, LocalDate endDate) {
        return getSessionsByDateRange(startDate, endDate)
              .stream()
              .mapToInt(StudySession::getDurationMinutes)
              .sum();
    }
    
    /**
     * Gets time breakdown by subject.
     * 
     * @return map of subject to total minutes
     */
    public Map<String, Integer> getTimeBySubject() {
        Map<String, Integer> subjectTime = new HashMap<>();
        
        for (StudySession session : sessions) {
            String subject = session.getSubject();
            int currentTime = subjectTime.getOrDefault(subject, 0);
            subjectTime.put(subject, currentTime + session.getDurationMinutes());
        }
        
        return subjectTime;
    }
    
    /**
     * Gets the number of sessions.
     * 
     * @return total session count
     */
    public int getSessionCount() {
        return sessions.size();
    }
    
    /**
     * Calculates average session duration.
     * 
     * @return average minutes per session (0 if no sessions)
     */
    public double getAverageSessionMinutes() {
        if (sessions.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalMinutes() / sessions.size();
    }
    
    /**
     * Formats duration in minutes to "Xh Ym" format.
     * 
     * @param minutes the duration in minutes
     * @return formatted string (e.g., "2h 30m" or "45m")
     */
    public static String formatDuration(int minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) {
            return hours + "h";
        }
        return hours + "h " + remainingMinutes + "m";
    }
    
    /**
     * Clears all sessions (for testing purposes).
     */
    public void clearAllSessions() {
        sessions.clear();
        saveSessions();
    }
    
    // Private helper methods
    
    /**
     * Loads sessions from file on startup.
     */
    private void loadSessions() {
        try {
            sessions = fileManager.loadSessions();
            System.out.println("Loaded " + sessions.size() + " sessions from file");
        } catch (IOException e) {
            System.err.println("Error loading sessions: " + e.getMessage());
            sessions = new ArrayList<>();
        }
    }
    
    /**
     * Saves sessions to file after each modification.
     */
    private void saveSessions() {
        try {
            fileManager.saveSessions(sessions);
        } catch (IOException e) {
            System.err.println("Error saving sessions: " + e.getMessage());
        }
    }
}

