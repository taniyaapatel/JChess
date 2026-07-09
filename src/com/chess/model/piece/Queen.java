package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Combines Rook and Bishop movement</li>
 *   <li>Moves any number of squares horizontally, vertically, or diagonally</li>
 *   <li>Cannot jump over pieces</li>
 * </ul>
 * </p>
 * <p>
 * Design note: The Queen's movement is a combination of Rook and Bishop,
 * demonstrating the composite pattern. Rather than using multiple inheritance
 * (not supported in Java), we combine both movement patterns in one class.
 * </p>
 *
 * @author Taniya
 */
public class Queen extends Piece {

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        return getAllDirectionMoves(board, position);
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        return getAllDirectionMoves(board, position);
    }

    /**
     * Generates sliding moves in all eight directions (rook + bishop combined).
     */
    private List<Position> getAllDirectionMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},   // Rook directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}    // Bishop directions
        };

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
                        moves.add(target);
                    }
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }

    @Override
    public Piece copy() {
        Queen copy = new Queen(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
