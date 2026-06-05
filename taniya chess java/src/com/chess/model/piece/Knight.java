package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Moves in an L-shape: two squares in one direction and one square perpendicular</li>
 *   <li>Can jump over other pieces</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class Knight extends Piece {

    /** All eight possible L-shaped offsets for a knight. */
    private static final int[][] OFFSETS = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        return getKnightMoves(board, position);
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        return getKnightMoves(board, position);
    }

    /**
     * Generates all valid L-shaped moves.
     */
    private List<Position> getKnightMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();

        for (int[] offset : OFFSETS) {
            Position target = new Position(
                position.getRow() + offset[0],
                position.getCol() + offset[1]
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

    @Override
    public Piece copy() {
        Knight copy = new Knight(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
