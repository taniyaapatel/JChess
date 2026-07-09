package com.chess;

import com.chess.controller.Game;
import com.chess.view.ChessServer;

import java.io.File;
import java.io.IOException;

/**
 * Application entry point for the Chess Engine.
 * <p>
 * Creates a new Game instance and starts the HTTP server
 * to serve the browser-based user interface.
 * </p>
 *
 * @author Taniya
 */
public class Main {

    /** Default server port. */
    private static final int DEFAULT_PORT = 8080;

    /**
     * Main method - starts the chess application.
     *
     * @param args optional: port number as first argument
     */
    public static void main(String[] args) {
         int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8080")
            );

        // Determine the public directory path
        String publicDir = findPublicDir();

        System.out.println("Starting Chess Engine...");
        System.out.println("Public directory: " + publicDir);

        Game game = new Game();
        ChessServer server = new ChessServer(game, port, publicDir);

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.err.println("Port " + port + " may already be in use.");
            System.exit(1);
        }
    }

    /**
     * Finds the public directory relative to the current working directory
     * or the JAR location.
     *
     * @return absolute path to the public directory
     */
    private static String findPublicDir() {
        // Try current working directory first
        File publicDir = new File("public");
        if (publicDir.exists() && publicDir.isDirectory()) {
            return publicDir.getAbsolutePath();
        }

        // Try relative to project root
        File projectPublic = new File("../public");
        if (projectPublic.exists() && projectPublic.isDirectory()) {
            return projectPublic.getAbsolutePath();
        }

        // Fallback to current directory
        System.err.println("Warning: 'public' directory not found. Static files may not load.");
        return new File("public").getAbsolutePath();
    }
}
