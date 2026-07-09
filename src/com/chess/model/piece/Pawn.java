package com.chess.model.piece;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn piece.
 * <p>
 * Movement rules:
 * <ul>
 *   <li>Moves forward one square (if unoccupied)</li>
 *   <li>Moves forward two squares from its starting position</li>
 *   <li>Captures diagonally forward</li>
 *   <li>En passant capture</li>
 *   <li>Promotion when reaching the last rank</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class Pawn extends Piece {

    /**
     * Constructs a Pawn of the given color.
     *
     * @param color the piece color
     */
    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position position, Move lastMove) {
        List<Position> moves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1;
        int startRow = (getColor() == Color.WHITE) ? 6 : 1;
        int row = position.getRow();
        int col = position.getCol();

        // Single step forward
        Position oneStep = new Position(row + direction, col);
        if (oneStep.isValid() && board.getPiece(oneStep) == null) {
            moves.add(oneStep);

            // Double step from starting position
            if (row == startRow) {
                Position twoStep = new Position(row + 2 * direction, col);
                if (twoStep.isValid() && board.getPiece(twoStep) == null) {
                    moves.add(twoStep);
                }
            }
        }

        // Diagonal captures
        int[] captureCols = {col - 1, col + 1};
        for (int captureCol : captureCols) {
            Position capturePos = new Position(row + direction, captureCol);
            if (capturePos.isValid()) {
                Piece target = board.getPiece(capturePos);
                if (target != null && target.getColor() != getColor()) {
                    moves.add(capturePos);
                }
            }
        }

        // En passant
        if (lastMove != null
                && lastMove.getMoveType() == Move.MoveType.DOUBLE_PAWN_PUSH
                && lastMove.getTo().getRow() == row
                && Math.abs(lastMove.getTo().getCol() - col) == 1) {
            Position enPassantTarget = new Position(row + direction, lastMove.getTo().getCol());
            moves.add(enPassantTarget);
        }

        return moves;
    }

    @Override
    public List<Position> getAttackedSquares(Board board, Position position) {
        List<Position> attacked = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1;
        int row = position.getRow();
        int col = position.getCol();

        // Pawns attack diagonally regardless of whether a piece is there
        Position leftDiag = new Position(row + direction, col - 1);
        if (leftDiag.isValid()) {
            attacked.add(leftDiag);
        }

        Position rightDiag = new Position(row + direction, col + 1);
        if (rightDiag.isValid()) {
            attacked.add(rightDiag);
        }

        return attacked;
    }

    @Override
    public Piece copy() {
        Pawn copy = new Pawn(getColor());
        copy.setMoved(hasMoved());
        return copy;
    }
}
