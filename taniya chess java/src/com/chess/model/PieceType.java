package com.chess.model;

/**
 * Enumeration of all chess piece types.
 * Each type has an associated Unicode symbol for both colors.
 *
 * @author Taniya
 */
public enum PieceType {

    PAWN("♙", "♟"),
    ROOK("♖", "♜"),
    KNIGHT("♘", "♞"),
    BISHOP("♗", "♝"),
    QUEEN("♕", "♛"),
    KING("♔", "♚");

    private final String whiteSymbol;
    private final String blackSymbol;

    PieceType(String whiteSymbol, String blackSymbol) {
        this.whiteSymbol = whiteSymbol;
        this.blackSymbol = blackSymbol;
    }

    /**
     * Returns the Unicode chess symbol for the given color.
     *
     * @param color the piece color
     * @return the Unicode symbol string
     */
    public String getSymbol(Color color) {
        return color == Color.WHITE ? whiteSymbol : blackSymbol;
    }
}
