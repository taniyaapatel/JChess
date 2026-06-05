package com.chess.model;

import com.chess.model.piece.Piece;

/**
 * Represents a chess move from one position to another.
 * Stores metadata about captures, special moves, and promotions
 * to support move history and undo functionality.
 *
 * @author Taniya
 */
public class Move {

    /**
     * Classifies the type of move for special handling.
     */
    public enum MoveType {
        NORMAL,
        DOUBLE_PAWN_PUSH,
        CASTLING_KINGSIDE,
        CASTLING_QUEENSIDE,
        EN_PASSANT,
        PROMOTION
    }

    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final MoveType moveType;
    private PieceType promotionPieceType;

    /**
     * Constructs a Move with all details.
     *
     * @param from          the source position
     * @param to            the destination position
     * @param movedPiece    the piece being moved
     * @param capturedPiece the captured piece (null if no capture)
     * @param moveType      the classification of this move
     */
    public Move(Position from, Position to, Piece movedPiece,
                Piece capturedPiece, MoveType moveType) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.moveType = moveType;
    }

    /** @return the source position */
    public Position getFrom() { return from; }

    /** @return the destination position */
    public Position getTo() { return to; }

    /** @return the piece that was moved */
    public Piece getMovedPiece() { return movedPiece; }

    /** @return the piece that was captured, or null */
    public Piece getCapturedPiece() { return capturedPiece; }

    /** @return the type of this move */
    public MoveType getMoveType() { return moveType; }

    /** @return the piece type chosen for pawn promotion, or null */
    public PieceType getPromotionPieceType() { return promotionPieceType; }

    /**
     * Sets the promotion piece type when a pawn reaches the last rank.
     *
     * @param type the piece type to promote to
     */
    public void setPromotionPieceType(PieceType type) {
        this.promotionPieceType = type;
    }

    @Override
    public String toString() {
        return from.toString() + " -> " + to.toString()
                + (capturedPiece != null ? " x" + capturedPiece.getType() : "")
                + (moveType != MoveType.NORMAL ? " [" + moveType + "]" : "");
    }
}
