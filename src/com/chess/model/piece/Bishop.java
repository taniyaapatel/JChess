package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bishop piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Moves any number of squares diagonally</li>
 *   <li>Cannot jump over pieces</li>
 *   <li>Always stays on its starting color square</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        return getDiagonalMoves(board, position);
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        return getDiagonalMoves(board, position);
    }

    /**
     * Generates all sliding moves along the four diagonals.
     */
    private List<Position> getDiagonalMoves(Board board, Position position) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

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
        Bishop copy = new Bishop(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
