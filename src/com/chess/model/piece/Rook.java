package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rook piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Moves any number of squares horizontally or vertically</li>
 *   <li>Cannot jump over pieces</li>
 *   <li>Participates in castling with the King</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class Rook extends Piece {

    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        return getSlidingMoves(board, position);
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        return getSlidingMoves(board, position);
    }

    /**
     * Generates all sliding moves along horizontal and vertical axes.
     */
    private List<Position> getSlidingMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int row = position.getRow() + dir[0];
            int col = position.getCol() + dir[1];

            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                Position target = new Position(row, col);
                Piece piece = board.getPiece(target);

                if (piece == null) {
                    moves.add(target);
                } else {
                    if (piece.getColor() != getColor()) {
                        moves.add(target); // Can capture
                    }
                    break; // Blocked by piece
                }

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }

    @Override
    public Piece copy() {
        Rook copy = new Rook(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
