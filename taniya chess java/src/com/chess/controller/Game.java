package com.chess.controller;

import com.chess.model.*;
import com.chess.model.piece.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller that manages the chess game lifecycle.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Managing turns between two players</li>
 *   <li>Executing validated moves (including special moves)</li>
 *   <li>Tracking game state (active, check, checkmate, stalemate)</li>
 *   <li>Handling pawn promotion flow</li>
 *   <li>Maintaining move history and captured pieces</li>
 * </ul>
 * </p>
 *
 * @author Taniya
 */
public class Game {

    private Board board;
    private final Player[] players;
    private int currentTurnIndex;
    private GameState gameState;
    private final MoveValidator moveValidator;
    private final List<Move> moveHistory;
    private final List<Piece> capturedByWhite;
    private final List<Piece> capturedByBlack;
    private Position pendingPromotionPosition;

    /**
     * Constructs a new Game with two players.
     */
    public Game() {
        this.board = new Board();
        this.players = new Player[]{
            new Player("White", Color.WHITE),
            new Player("Black", Color.BLACK)
        };
        this.currentTurnIndex = 0; // White moves first
        this.gameState = GameState.ACTIVE;
        this.moveValidator = new MoveValidator();
        this.moveHistory = new ArrayList<>();
        this.capturedByWhite = new ArrayList<>();
        this.capturedByBlack = new ArrayList<>();
        this.pendingPromotionPosition = null;
    }

    /**
     * Attempts to make a move from one position to another.
     *
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     * @return a result message describing what happened
     */
    public String makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Cannot move if game is over or promotion pending
        if (gameState == GameState.CHECKMATE || gameState == GameState.STALEMATE) {
            return "Game is over!";
        }
        if (gameState == GameState.PROMOTION_PENDING) {
            return "Please select a piece for pawn promotion.";
        }

        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);
        Piece piece = board.getPiece(from);

        // Validate piece exists and belongs to current player
        if (piece == null) {
            return "No piece at the selected square.";
        }
        if (piece.getColor() != getCurrentPlayerColor()) {
            return "That's not your piece!";
        }

        // Check if move is legal
        Move lastMove = getLastMove();
        List<Position> legalMoves = moveValidator.getLegalMoves(board, from, lastMove);

        boolean isLegal = false;
        for (Position legal : legalMoves) {
            if (legal.equals(to)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            return "Illegal move!";
        }

        // Execute the move
        return executeMove(from, to, piece);
    }

    /**
     * Executes a validated move, handling all special move types.
     */
    private String executeMove(Position from, Position to, Piece piece) {
        Move lastMove = getLastMove();
        Piece capturedPiece = board.getPiece(to);
        Move.MoveType moveType = determineMoveType(from, to, piece, lastMove);
        String message = "";

        // Handle special moves
        switch (moveType) {
            case CASTLING_KINGSIDE:
                executeCastling(from, to, true);
                message = "Castling kingside!";
                break;

            case CASTLING_QUEENSIDE:
                executeCastling(from, to, false);
                message = "Castling queenside!";
                break;

            case EN_PASSANT:
                Position capturedPawnPos = new Position(from.getRow(), to.getCol());
                capturedPiece = board.removePiece(capturedPawnPos);
                board.movePiece(from, to);
                piece.setMoved(true);
                message = "En passant!";
                break;

            case DOUBLE_PAWN_PUSH:
            case NORMAL:
            case PROMOTION:
                board.movePiece(from, to);
                piece.setMoved(true);
                break;
        }

        // Track captured piece
        if (capturedPiece != null) {
            if (piece.getColor() == Color.WHITE) {
                capturedByWhite.add(capturedPiece);
            } else {
                capturedByBlack.add(capturedPiece);
            }
        }

        // Record move
        Move move = new Move(from, to, piece, capturedPiece, moveType);
        moveHistory.add(move);

        // Check for pawn promotion
        if (moveType == Move.MoveType.PROMOTION) {
            pendingPromotionPosition = to;
            gameState = GameState.PROMOTION_PENDING;
            return "Pawn promotion! Select a piece.";
        }

        // Switch turns and update game state
        switchTurn();
        updateGameState();

        if (gameState == GameState.CHECKMATE) {
            // The player who just moved wins
            Color winner = piece.getColor();
            return "Checkmate! " + (winner == Color.WHITE ? "White" : "Black") + " wins!";
        } else if (gameState == GameState.STALEMATE) {
            return "Stalemate! The game is a draw.";
        } else if (gameState == GameState.CHECK) {
            return message.isEmpty() ? "Check!" : message + " Check!";
        }

        return message.isEmpty() ? "Move successful." : message;
    }

    /**
     * Determines the type of move being made.
     */
    private Move.MoveType determineMoveType(Position from, Position to,
                                             Piece piece, Move lastMove) {
        // Castling
        if (piece.getType() == PieceType.KING && Math.abs(to.getCol() - from.getCol()) == 2) {
            return to.getCol() > from.getCol()
                    ? Move.MoveType.CASTLING_KINGSIDE
                    : Move.MoveType.CASTLING_QUEENSIDE;
        }

        // Pawn special moves
        if (piece.getType() == PieceType.PAWN) {
            // Double push
            if (Math.abs(to.getRow() - from.getRow()) == 2) {
                return Move.MoveType.DOUBLE_PAWN_PUSH;
            }

            // En passant
            if (from.getCol() != to.getCol() && board.getPiece(to) == null) {
                return Move.MoveType.EN_PASSANT;
            }

            // Promotion
            int promotionRank = (piece.getColor() == Color.WHITE) ? 0 : 7;
            if (to.getRow() == promotionRank) {
                return Move.MoveType.PROMOTION;
            }
        }

        return Move.MoveType.NORMAL;
    }

    /**
     * Executes a castling move, moving both king and rook.
     */
    private void executeCastling(Position kingFrom, Position kingTo, boolean kingside) {
        int row = kingFrom.getRow();
        board.movePiece(kingFrom, kingTo);
        board.getPiece(kingTo).setMoved(true);

        if (kingside) {
            Position rookFrom = new Position(row, 7);
            Position rookTo = new Position(row, 5);
            board.movePiece(rookFrom, rookTo);
            board.getPiece(rookTo).setMoved(true);
        } else {
            Position rookFrom = new Position(row, 0);
            Position rookTo = new Position(row, 3);
            board.movePiece(rookFrom, rookTo);
            board.getPiece(rookTo).setMoved(true);
        }
    }

    /**
     * Promotes a pawn to the selected piece type.
     *
     * @param pieceType the type to promote to (QUEEN, ROOK, BISHOP, KNIGHT)
     * @return result message
     */
    public String promotePawn(String pieceType) {
        if (gameState != GameState.PROMOTION_PENDING || pendingPromotionPosition == null) {
            return "No pending promotion.";
        }

        Piece currentPawn = board.getPiece(pendingPromotionPosition);
        if (currentPawn == null) {
            return "Error: No pawn at promotion position.";
        }

        Color color = currentPawn.getColor();
        Piece promoted;

        switch (pieceType.toUpperCase()) {
            case "QUEEN":
                promoted = new Queen(color);
                break;
            case "ROOK":
                promoted = new Rook(color);
                break;
            case "BISHOP":
                promoted = new Bishop(color);
                break;
            case "KNIGHT":
                promoted = new Knight(color);
                break;
            default:
                return "Invalid piece type. Choose QUEEN, ROOK, BISHOP, or KNIGHT.";
        }

        promoted.setMoved(true);
        board.setPiece(pendingPromotionPosition, promoted);
        pendingPromotionPosition = null;

        // Update last move's promotion type
        if (!moveHistory.isEmpty()) {
            Move lastRecorded = moveHistory.get(moveHistory.size() - 1);
            lastRecorded.setPromotionPieceType(promoted.getType());
        }

        switchTurn();
        updateGameState();

        if (gameState == GameState.CHECKMATE) {
            return "Promoted to " + pieceType + "! Checkmate! "
                    + (color == Color.WHITE ? "White" : "Black") + " wins!";
        } else if (gameState == GameState.STALEMATE) {
            return "Promoted to " + pieceType + "! Stalemate! Draw.";
        } else if (gameState == GameState.CHECK) {
            return "Promoted to " + pieceType + "! Check!";
        }

        return "Promoted to " + pieceType + "!";
    }

    /**
     * Updates the game state after a move.
     */
    private void updateGameState() {
        Color currentColor = getCurrentPlayerColor();
        Move lastMove = getLastMove();

        if (moveValidator.isCheckmate(board, currentColor, lastMove)) {
            gameState = GameState.CHECKMATE;
        } else if (moveValidator.isStalemate(board, currentColor, lastMove)) {
            gameState = GameState.STALEMATE;
        } else if (moveValidator.isInCheck(board, currentColor)) {
            gameState = GameState.CHECK;
        } else {
            gameState = GameState.ACTIVE;
        }
    }

    /**
     * Switches the turn to the other player.
     */
    private void switchTurn() {
        currentTurnIndex = 1 - currentTurnIndex;
    }

    /**
     * Returns valid moves for the piece at the given position.
     *
     * @param row the row
     * @param col the column
     * @return list of legal destination positions
     */
    public List<Position> getValidMoves(int row, int col) {
        Position pos = new Position(row, col);
        Piece piece = board.getPiece(pos);

        if (piece == null || piece.getColor() != getCurrentPlayerColor()) {
            return new ArrayList<>();
        }
        if (gameState == GameState.CHECKMATE || gameState == GameState.STALEMATE
                || gameState == GameState.PROMOTION_PENDING) {
            return new ArrayList<>();
        }

        return moveValidator.getLegalMoves(board, pos, getLastMove());
    }

    /**
     * Resets the game to its initial state.
     */
    public void restart() {
        board = new Board();
        currentTurnIndex = 0;
        gameState = GameState.ACTIVE;
        moveHistory.clear();
        capturedByWhite.clear();
        capturedByBlack.clear();
        pendingPromotionPosition = null;
    }

    // ========== Getters ==========

    public Board getBoard() { return board; }

    public Color getCurrentPlayerColor() {
        return players[currentTurnIndex].getColor();
    }

    public Player getCurrentPlayer() {
        return players[currentTurnIndex];
    }

    public GameState getGameState() { return gameState; }

    public Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);
    }

    public List<Piece> getCapturedByWhite() { return capturedByWhite; }

    public List<Piece> getCapturedByBlack() { return capturedByBlack; }

    public boolean isPromotionPending() {
        return gameState == GameState.PROMOTION_PENDING;
    }

    public int getMoveCount() { return moveHistory.size(); }

    // ========== JSON Serialization ==========

    /**
     * Serializes the complete game state to JSON.
     * Manual serialization avoids external library dependencies.
     *
     * @return JSON string representation of the game state
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // Board
        sb.append("\"board\":[");
        for (int r = 0; r < Board.SIZE; r++) {
            sb.append("[");
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.getPiece(r, c);
                if (p == null) {
                    sb.append("null");
                } else {
                    sb.append("{\"type\":\"").append(p.getType())
                      .append("\",\"color\":\"").append(p.getColor())
                      .append("\",\"symbol\":\"").append(p.getSymbol())
                      .append("\"}");
                }
                if (c < 7) sb.append(",");
            }
            sb.append("]");
            if (r < 7) sb.append(",");
        }
        sb.append("],");

        // Turn
        sb.append("\"currentTurn\":\"").append(getCurrentPlayerColor()).append("\",");

        // State
        sb.append("\"gameState\":\"").append(gameState).append("\",");

        // Check status
        boolean inCheck = (gameState == GameState.CHECK || gameState == GameState.CHECKMATE);
        sb.append("\"inCheck\":").append(inCheck).append(",");

        // Last move
        sb.append("\"lastMove\":");
        Move last = getLastMove();
        if (last != null) {
            sb.append("{\"fromRow\":").append(last.getFrom().getRow())
              .append(",\"fromCol\":").append(last.getFrom().getCol())
              .append(",\"toRow\":").append(last.getTo().getRow())
              .append(",\"toCol\":").append(last.getTo().getCol())
              .append("}");
        } else {
            sb.append("null");
        }
        sb.append(",");

        // Captured pieces
        sb.append("\"capturedByWhite\":").append(piecesToJson(capturedByWhite)).append(",");
        sb.append("\"capturedByBlack\":").append(piecesToJson(capturedByBlack)).append(",");

        // Promotion pending
        sb.append("\"promotionPending\":").append(gameState == GameState.PROMOTION_PENDING).append(",");

        // Move count
        sb.append("\"moveCount\":").append(moveHistory.size());

        sb.append("}");
        return sb.toString();
    }

    /**
     * Serializes a list of captured pieces to a JSON array.
     */
    private String piecesToJson(List<Piece> pieces) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            sb.append("{\"type\":\"").append(p.getType())
              .append("\",\"color\":\"").append(p.getColor())
              .append("\",\"symbol\":\"").append(p.getSymbol())
              .append("\"}");
            if (i < pieces.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
