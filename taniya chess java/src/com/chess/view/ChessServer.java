package com.chess.view;

import com.chess.controller.Game;
import com.chess.model.Position;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Lightweight HTTP server for the chess game UI.
 * <p>
 * Uses {@code com.sun.net.httpserver.HttpServer} (built into the JDK)
 * to avoid any external dependencies. Serves static files and
 * provides a REST-like JSON API for game interaction.
 * </p>
 * <p>
 * API Endpoints:
 * <ul>
 *   <li>GET  /             - Serves index.html</li>
 *   <li>GET  /api/state    - Returns current game state as JSON</li>
 *   <li>POST /api/move     - Makes a move (body: fromRow,fromCol,toRow,toCol)</li>
 *   <li>POST /api/promote  - Promotes a pawn (body: pieceType)</li>
 *   <li>POST /api/restart  - Restarts the game</li>
 *   <li>GET  /api/valid-moves?row=X&col=Y - Returns valid moves for a piece</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class ChessServer {

    private final Game game;
    private final int port;
    private final String publicDir;

    /**
     * Constructs a ChessServer.
     *
     * @param game      the game instance to serve
     * @param port      the port to listen on
     * @param publicDir the directory containing static files
     */
    public ChessServer(Game game, int port, String publicDir) {
        this.game = game;
        this.port = port;
        this.publicDir = publicDir;
    }

    /**
     * Starts the HTTP server and begins listening for requests.
     *
     * @throws IOException if the server fails to start
     */
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // API endpoints
        server.createContext("/api/state", this::handleState);
        server.createContext("/api/move", this::handleMove);
        server.createContext("/api/promote", this::handlePromote);
        server.createContext("/api/restart", this::handleRestart);
        server.createContext("/api/valid-moves", this::handleValidMoves);

        // Static file serving (must be last - catches all other paths)
        server.createContext("/", this::handleStaticFile);

        server.setExecutor(null);
        server.start();

        System.out.println("==============================================");
        System.out.println("  Chess Server started on port " + port);
        System.out.println("  Open http://localhost:" + port + " in your browser");
        System.out.println("==============================================");
    }

    // ==================== API Handlers ====================

    /**
     * GET /api/state - Returns the current game state as JSON.
     */
    private void handleState(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        String json = game.toJson();
        sendJsonResponse(exchange, 200, json);
    }

    /**
     * POST /api/move - Makes a move.
     * Body format: {"fromRow":r,"fromCol":c,"toRow":r,"toCol":c}
     */
    private void handleMove(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = readRequestBody(exchange);
        try {
            int fromRow = extractInt(body, "fromRow");
            int fromCol = extractInt(body, "fromCol");
            int toRow = extractInt(body, "toRow");
            int toCol = extractInt(body, "toCol");

            String result = game.makeMove(fromRow, fromCol, toRow, toCol);
            boolean success = !result.contains("Illegal") && !result.contains("not your")
                    && !result.contains("No piece") && !result.contains("Game is over")
                    && !result.contains("Please select");

            String json = "{\"success\":" + success
                    + ",\"message\":\"" + escapeJson(result)
                    + "\",\"state\":" + game.toJson() + "}";
            sendJsonResponse(exchange, 200, json);

        } catch (Exception e) {
            sendJsonResponse(exchange, 400,
                    "{\"error\":\"Invalid request: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * POST /api/promote - Promotes a pending pawn.
     * Body format: {"pieceType":"QUEEN"}
     */
    private void handlePromote(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = readRequestBody(exchange);
        try {
            String pieceType = extractString(body, "pieceType");
            String result = game.promotePawn(pieceType);
            boolean success = !result.contains("Invalid") && !result.contains("No pending")
                    && !result.contains("Error");

            String json = "{\"success\":" + success
                    + ",\"message\":\"" + escapeJson(result)
                    + "\",\"state\":" + game.toJson() + "}";
            sendJsonResponse(exchange, 200, json);

        } catch (Exception e) {
            sendJsonResponse(exchange, 400,
                    "{\"error\":\"Invalid request: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * POST /api/restart - Resets the game.
     */
    private void handleRestart(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        game.restart();
        String json = "{\"success\":true,\"message\":\"Game restarted.\",\"state\":"
                + game.toJson() + "}";
        sendJsonResponse(exchange, 200, json);
    }

    /**
     * GET /api/valid-moves?row=X&col=Y - Returns legal moves for a piece.
     */
    private void handleValidMoves(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        try {
            int row = extractQueryParam(query, "row");
            int col = extractQueryParam(query, "col");

            List<Position> moves = game.getValidMoves(row, col);
            StringBuilder json = new StringBuilder("{\"moves\":[");
            for (int i = 0; i < moves.size(); i++) {
                Position p = moves.get(i);
                json.append("{\"row\":").append(p.getRow())
                    .append(",\"col\":").append(p.getCol()).append("}");
                if (i < moves.size() - 1) json.append(",");
            }
            json.append("]}");

            sendJsonResponse(exchange, 200, json.toString());

        } catch (Exception e) {
            sendJsonResponse(exchange, 400,
                    "{\"error\":\"Invalid parameters: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ==================== Static File Handler ====================

    /**
     * Serves static files from the public directory.
     */
    private void handleStaticFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) {
            path = "/index.html";
        }

        Path filePath = Paths.get(publicDir, path);
        File file = filePath.toFile();

        if (!file.exists() || !file.isFile()) {
            sendResponse(exchange, 404, "File not found");
            return;
        }

        String contentType = getContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);

        byte[] fileBytes = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, fileBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(fileBytes);
        }
    }

    // ==================== Helper Methods ====================

    private void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /** Simple JSON integer extraction (avoids external JSON library). */
    private int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) throw new IllegalArgumentException("Missing key: " + key);
        idx += search.length();
        StringBuilder num = new StringBuilder();
        while (idx < json.length() && (Character.isDigit(json.charAt(idx)) || json.charAt(idx) == '-')) {
            num.append(json.charAt(idx));
            idx++;
        }
        return Integer.parseInt(num.toString());
    }

    /** Simple JSON string extraction. */
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx == -1) throw new IllegalArgumentException("Missing key: " + key);
        idx += search.length();
        int end = json.indexOf("\"", idx);
        return json.substring(idx, end);
    }

    /** Extracts a query parameter value. */
    private int extractQueryParam(String query, String key) {
        if (query == null) throw new IllegalArgumentException("No query parameters");
        for (String param : query.split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2 && parts[0].equals(key)) {
                return Integer.parseInt(parts[1]);
            }
        }
        throw new IllegalArgumentException("Missing parameter: " + key);
    }

    /** Returns the MIME content type for a file extension. */
    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }

    /** Escapes special characters in JSON string values. */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
