package com.chess.controller;

import com.chess.model.Board;
import com.chess.model.Color;
import com.chess.model.Move;
import com.chess.model.PieceType;
import com.chess.model.Position;
import com.chess.model.piece.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates chess moves and detects game conditions.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Filtering pseudo-legal moves to legal moves (king safety)</li>
 *   <li>Check detection</li>
 *   <li>Checkmate detection</li>
 *   <li>Stalemate detection</li>
 *   <li>Castling validation (through-check and from-check rules)</li>
 * </ul>
 * </p>
 * <p>
 * Time complexity of move validation: O(N * M) where N is the number of
 * pieces and M is the average number of moves per piece. For a standard
 * chess game, this is bounded by constant factors since the board is 8x8.
 * </p>
 *
 * @author Taniya
 */
public class MoveValidator {

    /**
     * Returns all legal moves for the piece at the given position.
     * A legal move is one that does not leave the player's king in check.
     *
     * @param board    the current board state
     * @param position the position of the piece to check
     * @param lastMove the last move made (for en passant)
     * @return list of legal destination positions
     */
    public List<Position> getLegalMoves(Board board, Position position, Move lastMove) {
        Piece piece = board.getPiece(position);
        if (piece == null) {
            return new ArrayList<>();
        }

        List<Position> pseudoLegalMoves = piece.getPossibleMoves(board, position, lastMove);
        List<Position> legalMoves = new ArrayList<>();
        Color pieceColor = piece.getColor();

        for (Position target : pseudoLegalMoves) {
            // Special validation for castling moves
            if (piece.getType() == PieceType.KING && Math.abs(target.getCol() - position.getCol()) == 2) {
                if (isCastlingLegal(board, position, target, pieceColor)) {
                    legalMoves.add(target);
                }
                continue;
            }

            // Simulate the move and check if king is safe
            if (isMoveSafe(board, position, target, pieceColor, lastMove)) {
                legalMoves.add(target);
            }
        }

        return legalMoves;
    }

    /**
     * Simulates a move and checks if the player's king remains safe.
     *
     * @param board      the current board
     * @param from       source position
     * @param to         destination position
     * @param pieceColor the color of the moving piece
     * @param lastMove   the last move (for en passant capture removal)
     * @return true if the move doesn't leave the king in check
     */
    private boolean isMoveSafe(Board board, Position from, Position to,
                               Color pieceColor, Move lastMove) {
        Board simulated = board.deepCopy();
        Piece movingPiece = simulated.getPiece(from);

        // Handle en passant capture on simulated board
        if (movingPiece.getType() == PieceType.PAWN
                && from.getCol() != to.getCol()
                && simulated.getPiece(to) == null) {
            // En passant: remove the captured pawn
            simulated.setPiece(new Position(from.getRow(), to.getCol()), null);
        }

        simulated.movePiece(from, to);
        return !isInCheck(simulated, pieceColor);
    }

    /**
     * Validates castling with additional rules:
     * king must not be in check, must not pass through check,
     * and must not end in check.
     */
    private boolean isCastlingLegal(Board board, Position kingPos,
                                    Position targetPos, Color kingColor) {
        // King cannot castle out of check
        if (isInCheck(board, kingColor)) {
            return false;
        }

        // Determine direction and check intermediate squares
        int colDirection = (targetPos.getCol() > kingPos.getCol()) ? 1 : -1;
        int row = kingPos.getRow();

        // Check each square the king passes through (including destination)
        for (int col = kingPos.getCol() + colDirection;
             col != targetPos.getCol() + colDirection;
             col += colDirection) {
            Position intermediate = new Position(row, col);
            if (isSquareAttacked(board, intermediate, kingColor.opposite())) {
                return false;
            }
        }

        // Also verify the final position is safe (simulate full move)
        Board simulated = board.deepCopy();
        simulated.movePiece(kingPos, targetPos);

        // Move the rook too
        if (colDirection == 1) { // Kingside
            simulated.movePiece(new Position(row, 7), new Position(row, 5));
        } else { // Queenside
            simulated.movePiece(new Position(row, 0), new Position(row, 3));
        }

        return !isInCheck(simulated, kingColor);
    }

    /**
     * Checks if the given color's king is currently in check.
     *
     * @param board     the board state to check
     * @param kingColor the color of the king to check
     * @return true if the king is in check
     */
    public boolean isInCheck(Board board, Color kingColor) {
        Position kingPos = board.findKing(kingColor);
        return isSquareAttacked(board, kingPos, kingColor.opposite());
    }

    /**
     * Checks if a square is attacked by any piece of the given color.
     *
     * @param board         the board state
     * @param square        the square to check
     * @param attackerColor the color of potential attackers
     * @return true if the square is under attack
     */
    public boolean isSquareAttacked(Board board, Position square, Color attackerColor) {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getColor() == attackerColor) {
                    List<Position> attackedSquares = piece.getAttackedSquares(
                            board, new Position(r, c));
                    for (Position attacked : attackedSquares) {
                        if (attacked.equals(square)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given color is in checkmate.
     * Checkmate = king is in check AND no legal moves exist.
     *
     * @param board    the current board state
     * @param color    the color to check
     * @param lastMove the last move made
     * @return true if checkmate
     */
    public boolean isCheckmate(Board board, Color color, Move lastMove) {
        return isInCheck(board, color) && !hasAnyLegalMove(board, color, lastMove);
    }

    /**
     * Checks if the given color is in stalemate.
     * Stalemate = king is NOT in check AND no legal moves exist.
     *
     * @param board    the current board state
     * @param color    the color to check
     * @param lastMove the last move made
     * @return true if stalemate
     */
    public boolean isStalemate(Board board, Color color, Move lastMove) {
        return !isInCheck(board, color) && !hasAnyLegalMove(board, color, lastMove);
    }

    /**
     * Checks if any legal move exists for the given color.
     * Returns early as soon as one legal move is found for efficiency.
     *
     * @param board    the current board state
     * @param color    the color to check
     * @param lastMove the last move made
     * @return true if at least one legal move exists
     */
    private boolean hasAnyLegalMove(Board board, Color color, Move lastMove) {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getColor() == color) {
                    List<Position> legalMoves = getLegalMoves(
                            board, new Position(r, c), lastMove);
                    if (!legalMoves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
