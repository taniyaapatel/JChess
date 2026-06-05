package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 * <p>
 * Demonstrates key OOP concepts:
 * <ul>
 *   <li><b>Abstraction</b>: Defines common interface for all pieces</li>
 *   <li><b>Encapsulation</b>: Protects internal state (color, type, hasMoved)</li>
 *   <li><b>Polymorphism</b>: Each piece subclass implements its own movement logic</li>
 *   <li><b>Inheritance</b>: Concrete pieces extend this class</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public abstract class Piece {

    private final Color color;
    private final PieceType type;
    private boolean hasMoved;

    /**
     * Constructs a Piece with the given color and type.
     *
     * @param color the color of this piece
     * @param type  the type of this piece
     */
    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    /**
     * Returns all pseudo-legal moves for this piece.
     * These moves follow the piece's movement rules but do NOT
     * account for whether the move leaves the king in check.
     * Check validation is handled by {@code MoveValidator}.
     *
     * @param board    the current board state
     * @param position the current position of this piece
     * @param lastMove the last move made (needed for en passant)
     * @return a list of positions this piece can potentially move to
     */
    public abstract List<Position> getPossibleMoves(Board board, Position position, Move lastMove);

    /**
     * Returns all squares this piece attacks.
     * Used for check detection. For most pieces this is the same as
     * possible moves, but pawns attack differently from how they move.
     *
     * @param board    the current board state
     * @param position the current position of this piece
     * @return a list of positions this piece attacks
     */
    public abstract List<Position> getAttackedSquares(Board board, Position position);

    /**
     * Creates a deep copy of this piece.
     *
     * @return a new Piece with the same state
     */
    public abstract Piece copy();

    /** @return the color of this piece */
    public Color getColor() { return color; }

    /** @return the type of this piece */
    public PieceType getType() { return type; }

    /** @return true if this piece has moved at least once */
    public boolean hasMoved() { return hasMoved; }

    /**
     * Marks this piece as having moved.
     */
    public void setMoved(boolean moved) { this.hasMoved = moved; }

    /**
     * Returns the Unicode symbol for this piece.
     *
     * @return the Unicode chess character
     */
    public String getSymbol() {
        return type.getSymbol(color);
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
}
