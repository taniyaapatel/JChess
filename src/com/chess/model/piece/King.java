package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Moves one square in any direction</li>
 *   <li>Can castle with a Rook (kingside or queenside) under specific conditions</li>
 *   <li>Cannot move into check</li>
 * </ul>
 * </p>
 * <p>
 * Castling conditions (checked at pseudo-legal level here):
 * <ol>
 *   <li>King has not moved</li>
 *   <li>Rook has not moved</li>
 *   <li>No pieces between king and rook</li>
 * </ol>
 * Additional castling checks (king not in check, doesn't pass through check)
 * are handled by {@code MoveValidator}.
 * </p>
 *
 * @author Taniya
 */
public class King extends Piece {

    /** All eight directions a king can move. */
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        List<Position> moves = getBasicMoves(board, position);

        // Add castling moves if king hasn't moved
        if (!hasMoved()) {
            addCastlingMoves(board, position, moves);
        }

        return moves;
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        // King attacks adjacent squares only (not castling squares)
        return getBasicMoves(board, position);
    }

    /**
     * Generates basic one-square moves in all directions.
     */
    private List<Position> getBasicMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            Position target = new Position(
                position.getRow() + dir[0],
                position.getCol() + dir[1]
            );

            if (target.isValid()) {
                Piece piece = board.getPiece(target);
                if (piece == null || piece.getColor() != getColor()) {
                    moves.add(target);
                }
            }
        }

        return moves;
    }

    /**
     * Adds castling moves if basic conditions are met.
     * Full castling validation (check, pass-through-check) is done by MoveValidator.
     */
    private void addCastlingMoves(Board board, Position position, List<Position> moves) {
        int row = position.getRow();

        // Kingside castling (king moves to col 6, rook at col 7)
        Piece kingsideRook = board.getPiece(new Position(row, 7));
        if (kingsideRook != null
                && kingsideRook.getType() == PieceType.ROOK
                && !kingsideRook.hasMoved()) {
            // Check path is clear (cols 5 and 6)
            if (board.getPiece(new Position(row, 5)) == null
                    && board.getPiece(new Position(row, 6)) == null) {
                moves.add(new Position(row, 6));
            }
        }

        // Queenside castling (king moves to col 2, rook at col 0)
        Piece queensideRook = board.getPiece(new Position(row, 0));
        if (queensideRook != null
                && queensideRook.getType() == PieceType.ROOK
                && !queensideRook.hasMoved()) {
            // Check path is clear (cols 1, 2, 3)
            if (board.getPiece(new Position(row, 1)) == null
                    && board.getPiece(new Position(row, 2)) == null
                    && board.getPiece(new Position(row, 3)) == null) {
                moves.add(new Position(row, 2));
            }
        }
    }

    @Override
    public Piece copy() {
        King copy = new King(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
