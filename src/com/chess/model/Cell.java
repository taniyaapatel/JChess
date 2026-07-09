package com.chess.model;

import com.chess.model.piece.Piece;

/**
 * Represents a single cell (square) on the chess board.
 * A cell has a fixed position and may or may not contain a piece.
 * <p>
 * This class serves as a convenient data holder for board state
 * representation and API responses.
 * </p>
 *
 * @author Taniya
 */
public class Cell {

    private final Position position;
    private Piece piece;

    /**
     * Constructs a Cell at the given position with no piece.
     *
     * @param position the board position of this cell
     */
    public Cell(Position position) {
        this.position = position;
        this.piece = null;
    }

    /**
     * Constructs a Cell at the given position containing a piece.
     *
     * @param position the board position of this cell
     * @param piece    the piece occupying this cell (may be null)
     */
    public Cell(Position position, Piece piece) {
        this.position = position;
        this.piece = piece;
    }

    /**
     * @return the position of this cell on the board
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return the piece on this cell, or null if empty
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets or removes the piece on this cell.
     *
     * @param piece the piece to place, or null to clear
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * @return true if this cell has no piece
     */
    public boolean isEmpty() {
        return piece == null;
    }

    /**
     * Checks if this cell contains a piece of the given color.
     *
     * @param color the color to check
     * @return true if the cell has a piece of the specified color
     */
    public boolean hasPieceOfColor(Color color) {
        return piece != null && piece.getColor() == color;
    }
}
