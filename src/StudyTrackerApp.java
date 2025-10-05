/**
 * Main entry point for the Study Time Tracker application.
 * 
 * This application demonstrates:
 * - Pure Java web application (no Spring Boot)
 * - Built-in HttpServer for serving web pages and API
 * - JSON file storage (no database needed)
 * - Modern web UI with HTML/CSS/JavaScript
 * 
 * To run:
 *   javac src/*.java
 *   java -cp src StudyTrackerApp
 * 
 * Then open: http://localhost:8080
 */
public class StudyTrackerApp {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║   📚 Study Time Tracker - Web Version    ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Initialize components
            System.out.println("Initializing application...");
            JsonFileManager fileManager = new JsonFileManager();
            StudySessionManager sessionManager = new StudySessionManager(fileManager);
            
            // Start web server
            WebServer server = new WebServer(sessionManager);
            server.start();
            
            System.out.println();
            System.out.println("┌───────────────────────────────────────────┐");
            System.out.println("│  ✓ Server is running!                    │");
            System.out.println("│                                           │");
            System.out.println("│  Open your browser and go to:             │");
            System.out.println("│  → http://localhost:8080                  │");
            System.out.println("│                                           │");
            System.out.println("│  Press Ctrl+C to stop the server         │");
            System.out.println("└───────────────────────────────────────────┘");
            System.out.println();
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("\n❌ Error starting application:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

