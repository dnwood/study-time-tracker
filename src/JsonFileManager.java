import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages reading and writing StudySession objects to/from a JSON file.
 * 
 * This class handles all file I/O operations for persisting study sessions.
 * It uses JSON format for human-readable storage and compatibility with
 * the web frontend.
 * 
 * Key concepts demonstrated:
 * - File I/O with NIO (java.nio.file)
 * - JSON array handling
 * - Error handling with IOException
 * - Directory creation
 * - List to/from JSON conversion
 */
public class JsonFileManager {
    
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "sessions.json";
    private final Path filePath;
    
    /**
     * Constructor - initializes the file path and ensures data directory exists.
     */
    public JsonFileManager() {
        this.filePath = Paths.get(DATA_DIR, FILE_NAME);
        ensureDataDirectoryExists();
    }
    
    /**
     * Constructor with custom file path (useful for testing).
     * 
     * @param filePath custom path to the JSON file
     */
    public JsonFileManager(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureDataDirectoryExists();
    }
    
    /**
     * Saves a list of StudySession objects to the JSON file.
     * 
     * The sessions are stored as a JSON array:
     * [
     *   {"id":"...", "subject":"...", ...},
     *   {"id":"...", "subject":"...", ...}
     * ]
     * 
     * @param sessions the list of sessions to save
     * @throws IOException if file writing fails
     */
    public void saveSessions(List<StudySession> sessions) throws IOException {
        if (sessions == null) {
            sessions = new ArrayList<>();
        }
        
        // Build JSON array
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < sessions.size(); i++) {
            json.append("  ").append(sessions.get(i).toJson());
            if (i < sessions.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]");
        
        // Write to file
        Files.write(filePath, json.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Loads StudySession objects from the JSON file.
     * 
     * If the file doesn't exist, returns an empty list.
     * 
     * @return list of loaded sessions (empty if file doesn't exist)
     * @throws IOException if file reading fails
     */
    public List<StudySession> loadSessions() throws IOException {
        // If file doesn't exist, return empty list
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        
        // Read file content
        byte[] bytes = Files.readAllBytes(filePath);
        String content = new String(bytes, StandardCharsets.UTF_8);
        
        // Parse JSON array
        return parseJsonArray(content);
    }
    
    /**
     * Parses a JSON array string into a list of StudySession objects.
     * 
     * @param json the JSON array string
     * @return list of parsed sessions
     * @throws IllegalArgumentException if JSON is invalid
     */
    private List<StudySession> parseJsonArray(String json) {
        List<StudySession> sessions = new ArrayList<>();
        
        if (json == null || json.trim().isEmpty()) {
            return sessions;
        }
        
        json = json.trim();
        
        // Check for empty array
        if (json.equals("[]")) {
            return sessions;
        }
        
        // Remove outer brackets
        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array format");
        }
        
        json = json.substring(1, json.length() - 1).trim();
        
        if (json.isEmpty()) {
            return sessions;
        }
        
        // Split into individual JSON objects
        List<String> jsonObjects = splitJsonObjects(json);
        
        // Parse each object
        for (String jsonObject : jsonObjects) {
            try {
                StudySession session = StudySession.fromJson(jsonObject);
                sessions.add(session);
            } catch (Exception e) {
                // Skip invalid sessions but log the error
                System.err.println("Warning: Failed to parse session: " + e.getMessage());
            }
        }
        
        return sessions;
    }
    
    /**
     * Splits a JSON array content into individual JSON objects.
     * Handles nested braces correctly.
     * 
     * @param json the JSON array content (without outer brackets)
     * @return list of individual JSON object strings
     */
    private List<String> splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int startIndex = 0;
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            // Handle escape sequences
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                continue;
            }
            
            // Handle strings (ignore braces inside strings)
            if (c == '"') {
                inString = !inString;
                continue;
            }
            
            if (inString) {
                continue;
            }
            
            // Count braces
            if (c == '{') {
                if (braceCount == 0) {
                    startIndex = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // Found complete object
                    String obj = json.substring(startIndex, i + 1).trim();
                    objects.add(obj);
                }
            }
        }
        
        return objects;
    }
    
    /**
     * Checks if the sessions file exists.
     * 
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists() {
        return Files.exists(filePath);
    }
    
    /**
     * Deletes the sessions file.
     * Useful for testing or clearing all data.
     * 
     * @throws IOException if deletion fails
     */
    public void deleteFile() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    /**
     * Gets the file path being used.
     * 
     * @return the path to the JSON file
     */
    public Path getFilePath() {
        return filePath;
    }
    
    /**
     * Ensures the data directory exists, creating it if necessary.
     */
    private void ensureDataDirectoryExists() {
        try {
            Path dir = filePath.getParent();
            if (dir != null && !Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }
}

