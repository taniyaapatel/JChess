package com.chess.model;

/**
 * Represents the two colors in a chess game.
 * Each player controls pieces of one color.
 *
 * @author Taniya
 */
public enum Color {

    /** White pieces - moves first by convention. */
    WHITE,

    /** Black pieces - moves second. */
    BLACK;

    /**
     * Returns the opposite color.
     *
     * @return BLACK if this is WHITE, WHITE if this is BLACK
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
