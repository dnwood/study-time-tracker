import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTTP server for the Study Time Tracker web application.
 * 
 * Uses Java's built-in HttpServer (no external frameworks needed!)
 * Serves static files (HTML, CSS, JS) and provides REST-like API endpoints.
 * 
 * Key concepts demonstrated:
 * - HttpServer setup and configuration
 * - Request routing
 * - HTTP methods (GET, POST, PUT, DELETE)
 * - CORS handling
 * - Static file serving
 * - JSON request/response handling
 * - Query parameter parsing
 */
public class WebServer {
    
    private final HttpServer server;
    private final StudySessionManager sessionManager;
    private static final int PORT = 8080;
    
    /**
     * Constructor - sets up the HTTP server with all routes.
     * 
     * @param sessionManager the session manager for business logic
     * @throws IOException if server creation fails
     */
    public WebServer(StudySessionManager sessionManager) throws IOException {
        this.sessionManager = sessionManager;
        
        // Create HTTP server
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Set up routes
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/sessions", new SessionHandler());
        server.createContext("/api/stats", new StatsHandler());
        
        // Use default executor
        server.setExecutor(null);
    }
    
    /**
     * Starts the HTTP server.
     */
    public void start() {
        server.start();
        System.out.println("✓ Server started on http://localhost:" + PORT);
    }
    
    /**
     * Stops the HTTP server.
     */
    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
    
    // ===== STATIC FILE HANDLER =====
    
    /**
     * Handles requests for static files (HTML, CSS, JS).
     */
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Map to file in web/ directory
            File file = new File("web" + path);
            
            if (file.exists() && file.isFile()) {
                // Determine content type
                String contentType = getContentType(path);
                
                // Read file
                byte[] content = Files.readAllBytes(file.toPath());
                
                // Send response
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                OutputStream os = exchange.getResponseBody();
                os.write(content);
                os.close();
            } else {
                // 404 Not Found
                String response = "404 Not Found: " + path;
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain; charset=UTF-8";
        }
    }
    
    // ===== SESSION API HANDLER =====
    
    /**
     * Handles /api/sessions endpoint for CRUD operations.
     */
    private class SessionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            String method = exchange.getRequestMethod();
            
            // Handle OPTIONS (CORS preflight)
            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            // Route based on HTTP method
            String response;
            int statusCode = 200;
            
            try {
                switch (method) {
                    case "GET":
                        response = handleGetSessions(exchange);
                        break;
                    case "POST":
                        response = handleAddSession(exchange);
                        break;
                    case "PUT":
                        response = handleUpdateSession(exchange);
                        break;
                    case "DELETE":
                        response = handleDeleteSession(exchange);
                        break;
                    default:
                        response = "{\"error\":\"Method not allowed\"}";
                        statusCode = 405;
                }
            } catch (Exception e) {
                response = "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}";
                statusCode = 400;
            }
            
            // Send response
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String handleGetSessions(HttpExchange exchange) {
            List<StudySession> sessions = sessionManager.getSessionsSortedByDate();
            
            // Convert to JSON array
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < sessions.size(); i++) {
                json.append(sessions.get(i).toJson());
                if (i < sessions.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            return json.toString();
        }
        
        private String handleAddSession(HttpExchange exchange) throws IOException {
            // Read request body
            String body = readRequestBody(exchange);
            
            // Parse JSON manually (simple parsing for our needs)
            Map<String, String> data = parseJsonObject(body);
            
            // Extract fields
            String subject = data.get("subject");
            String durationStr = data.get("durationMinutes");
            String dateStr = data.get("date");
            String notes = data.get("notes");
            
            // Validate required fields
            if (subject == null || durationStr == null || dateStr == null) {
                return "{\"success\":false,\"error\":\"Missing required fields\"}";
            }
            
            // Parse values
            int durationMinutes = Integer.parseInt(durationStr);
            LocalDate date = LocalDate.parse(dateStr);
            
            // Create session
            StudySession session = sessionManager.addSession(subject, durationMinutes, date, notes);
            
            return "{\"success\":true,\"session\":" + session.toJson() + "}";
        }
        
        private String handleUpdateSession(HttpExchange exchange) throws IOException {
            // Parse query parameters to get ID
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);
            String id = params.get("id");
            
            if (id == null) {
                return "{\"success\":false,\"error\":\"Missing session ID\"}";
            }
            
            // Read request body
            String body = readRequestBody(exchange);
            Map<String, String> data = parseJsonObject(body);
            
            // Extract fields (all optional for update)
            String subject = data.get("subject");
            String durationStr = data.get("durationMinutes");
            String dateStr = data.get("date");
            String notes = data.get("notes");
            
            Integer durationMinutes = durationStr != null ? Integer.parseInt(durationStr) : null;
            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : null;
            
            // Update session
            StudySession session = sessionManager.updateSession(id, subject, durationMinutes, 
                                                               date, null, null, notes);
            
            if (session == null) {
                return "{\"success\":false,\"error\":\"Session not found\"}";
            }
            
            return "{\"success\":true,\"session\":" + session.toJson() + "}";
        }
        
        private String handleDeleteSession(HttpExchange exchange) {
            // Parse query parameters to get ID
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);
            String id = params.get("id");
            
            if (id == null) {
                return "{\"success\":false,\"error\":\"Missing session ID\"}";
            }
            
            // Delete session
            boolean deleted = sessionManager.deleteSession(id);
            
            if (!deleted) {
                return "{\"success\":false,\"error\":\"Session not found\"}";
            }
            
            return "{\"success\":true,\"message\":\"Session deleted\"}";
        }
    }
    
    // ===== STATS API HANDLER =====
    
    /**
     * Handles /api/stats endpoint for statistics.
     */
    private class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            
            // Calculate statistics
            int totalMinutes = sessionManager.getTotalMinutes();
            int sessionCount = sessionManager.getSessionCount();
            double avgMinutes = sessionManager.getAverageSessionMinutes();
            Map<String, Integer> bySubject = sessionManager.getTimeBySubject();
            
            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"totalMinutes\":").append(totalMinutes).append(",");
            json.append("\"sessionCount\":").append(sessionCount).append(",");
            json.append("\"averageMinutes\":").append(avgMinutes).append(",");
            json.append("\"bySubject\":{");
            
            int i = 0;
            for (Map.Entry<String, Integer> entry : bySubject.entrySet()) {
                json.append("\"").append(escapeJson(entry.getKey())).append("\":")
                    .append(entry.getValue());
                if (i < bySubject.size() - 1) {
                    json.append(",");
                }
                i++;
            }
            
            json.append("}}");
            
            String response = json.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Parses simple JSON object into a Map.
     * Only handles string values and simple structure.
     */
    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> map = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return map;
        }
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        // Split by commas (outside of quotes)
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                String value = keyValue[1].trim();
                
                // Remove quotes from string values
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                // Handle null
                if (value.equals("null")) {
                    value = null;
                }
                
                map.put(key, value);
            }
        }
        
        return map;
    }
    
    /**
     * Parses query parameters from URL.
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        
        if (query == null || query.isEmpty()) {
            return params;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        
        return params;
    }
    
    /**
     * Reads request body from HttpExchange.
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(exchange.getRequestBody(), "UTF-8")
        );
        return reader.lines().collect(Collectors.joining("\n"));
    }
    
    /**
     * Escapes special characters for JSON strings.
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

