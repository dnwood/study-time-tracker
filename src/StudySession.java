import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Objects;

/**
 * Represents a single study session.
 * 
 * This class models a study session with all relevant information including
 * subject, duration, date/time, and optional notes. It provides JSON serialization
 * methods for data persistence.
 * 
 * Key concepts demonstrated:
 * - Encapsulation (private fields with public getters/setters)
 * - Immutable ID (UUID generated on creation)
 * - Optional fields (startTime, endTime, notes can be null)
 * - JSON serialization without external libraries
 * - Object equality based on ID
 */
public class StudySession {
    
    // Fields
    private final String id;              // Unique identifier (immutable)
    private String subject;               // What was studied (required)
    private int durationMinutes;          // How long (required)
    private LocalDate date;               // When (required)
    private LocalTime startTime;          // Optional start time
    private LocalTime endTime;            // Optional end time
    private String notes;                 // Optional notes
    
    // Formatters for JSON serialization
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    
    /**
     * Constructor for creating a new study session.
     * Automatically generates a unique ID.
     * 
     * @param subject the subject/topic being studied (required)
     * @param durationMinutes the duration in minutes (required, must be positive)
     * @param date the date of the study session (required)
     */
    public StudySession(String subject, int durationMinutes, LocalDate date) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        this.id = UUID.randomUUID().toString();
        this.subject = subject.trim();
        this.durationMinutes = durationMinutes;
        this.date = date;
        this.startTime = null;
        this.endTime = null;
        this.notes = null;
    }
    
    /**
     * Constructor for creating a study session with all fields.
     * Used primarily when deserializing from JSON.
     * 
     * @param id the unique identifier
     * @param subject the subject/topic
     * @param durationMinutes the duration in minutes
     * @param date the date of the session
     * @param startTime optional start time
     * @param endTime optional end time
     * @param notes optional notes
     */
    public StudySession(String id, String subject, int durationMinutes, LocalDate date,
                       LocalTime startTime, LocalTime endTime, String notes) {
        this.id = id;
        this.subject = subject;
        this.durationMinutes = durationMinutes;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }
    
    // Getters
    
    public String getId() {
        return id;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public String getNotes() {
        return notes;
    }
    
    // Setters (ID is immutable, so no setter for it)
    
    public void setSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        this.subject = subject.trim();
    }
    
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.durationMinutes = durationMinutes;
    }
    
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // JSON Serialization Methods
    
    /**
     * Converts this StudySession to a JSON string.
     * 
     * This method manually constructs JSON without external libraries,
     * demonstrating string manipulation and formatting.
     * 
     * Example output:
     * {"id":"abc123","subject":"Java","durationMinutes":45,"date":"2024-10-04",...}
     * 
     * @return JSON representation of this session
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        // Required fields
        json.append("\"id\":\"").append(escapeJson(id)).append("\",");
        json.append("\"subject\":\"").append(escapeJson(subject)).append("\",");
        json.append("\"durationMinutes\":").append(durationMinutes).append(",");
        json.append("\"date\":\"").append(date.format(DATE_FORMATTER)).append("\"");
        
        // Optional fields
        if (startTime != null) {
            json.append(",\"startTime\":\"").append(startTime.format(TIME_FORMATTER)).append("\"");
        } else {
            json.append(",\"startTime\":null");
        }
        
        if (endTime != null) {
            json.append(",\"endTime\":\"").append(endTime.format(TIME_FORMATTER)).append("\"");
        } else {
            json.append(",\"endTime\":null");
        }
        
        if (notes != null && !notes.isEmpty()) {
            json.append(",\"notes\":\"").append(escapeJson(notes)).append("\"");
        } else {
            json.append(",\"notes\":null");
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Creates a StudySession from a JSON string.
     * 
     * This method manually parses JSON without external libraries,
     * demonstrating string parsing and data extraction.
     * 
     * @param json the JSON string to parse
     * @return a new StudySession object
     * @throws IllegalArgumentException if JSON is invalid or missing required fields
     */
    public static StudySession fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        
        // Remove outer braces and whitespace
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
        json = json.substring(1, json.length() - 1);
        
        // Extract field values
        String id = extractJsonValue(json, "id");
        String subject = extractJsonValue(json, "subject");
        String durationStr = extractJsonValue(json, "durationMinutes");
        String dateStr = extractJsonValue(json, "date");
        String startTimeStr = extractJsonValue(json, "startTime");
        String endTimeStr = extractJsonValue(json, "endTime");
        String notes = extractJsonValue(json, "notes");
        
        // Validate and parse required fields
        if (id == null || subject == null || durationStr == null || dateStr == null) {
            throw new IllegalArgumentException("Missing required fields in JSON");
        }
        
        int durationMinutes;
        try {
            durationMinutes = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration format: " + durationStr);
        }
        
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
        
        // Parse optional fields
        LocalTime startTime = null;
        if (startTimeStr != null && !startTimeStr.equals("null")) {
            try {
                startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
            } catch (Exception e) {
                // Ignore invalid start time
            }
        }
        
        LocalTime endTime = null;
        if (endTimeStr != null && !endTimeStr.equals("null")) {
            try {
                endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
            } catch (Exception e) {
                // Ignore invalid end time
            }
        }
        
        // Handle null notes
        if (notes != null && notes.equals("null")) {
            notes = null;
        }
        
        return new StudySession(id, subject, durationMinutes, date, startTime, endTime, notes);
    }
    
    /**
     * Helper method to extract a value from a JSON string.
     * 
     * @param json the JSON string
     * @param key the key to extract
     * @return the value, or null if not found
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += searchKey.length();
        
        // Skip whitespace
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }
        
        // Check if value is null
        if (json.startsWith("null", startIndex)) {
            return "null";
        }
        
        // Check if value is a string (starts with quote)
        if (json.charAt(startIndex) == '"') {
            startIndex++; // Skip opening quote
            int endIndex = startIndex;
            
            // Find closing quote, handling escaped quotes
            while (endIndex < json.length()) {
                if (json.charAt(endIndex) == '"' && (endIndex == 0 || json.charAt(endIndex - 1) != '\\')) {
                    return unescapeJson(json.substring(startIndex, endIndex));
                }
                endIndex++;
            }
            return null;
        } else {
            // Value is a number or boolean
            int endIndex = startIndex;
            while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
                endIndex++;
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
    
    /**
     * Escapes special characters for JSON strings.
     * 
     * @param str the string to escape
     * @return the escaped string
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Unescapes JSON special characters.
     * 
     * @param str the string to unescape
     * @return the unescaped string
     */
    private static String unescapeJson(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
    
    // Object methods
    
    /**
     * Returns a string representation of this session.
     * Useful for debugging and console output.
     */
    @Override
    public String toString() {
        return String.format("StudySession[id=%s, subject='%s', duration=%d min, date=%s]",
                           id, subject, durationMinutes, date);
    }
    
    /**
     * Two sessions are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySession that = (StudySession) o;
        return Objects.equals(id, that.id);
    }
    
    /**
     * Hash code based on ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

