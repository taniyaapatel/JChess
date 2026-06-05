package com.chess.model;

import com.chess.model.piece.*;

/**
 * Represents the 8x8 chess board.
 * <p>
 * Uses a {@code Piece[8][8]} array as the primary data structure
 * for O(1) access to any square. This is optimal for a fixed-size
 * chess board and is easy to explain in technical interviews.
 * </p>
 *
 * @author Taniya
 */
public class Board {

    /** The 8x8 grid storing piece references. Null means empty square. */
    private final Piece[][] board;

    /** Board dimension constant. */
    public static final int SIZE = 8;

    /**
     * Constructs a Board with the standard chess starting position.
     */
    public Board() {
        this.board = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    /**
     * Private constructor for creating board copies.
     *
     * @param board the piece array to copy from
     */
    private Board(Piece[][] board) {
        this.board = new Piece[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != null) {
                    this.board[r][c] = board[r][c].copy();
                }
            }
        }
    }

    /**
     * Sets up the standard chess starting position.
     * <pre>
     * Row 0: Black major pieces
     * Row 1: Black pawns
     * Row 6: White pawns
     * Row 7: White major pieces
     * </pre>
     */
    private void initializeBoard() {
        // Black pieces (top - row 0)
        board[0][0] = new Rook(Color.BLACK);
        board[0][1] = new Knight(Color.BLACK);
        board[0][2] = new Bishop(Color.BLACK);
        board[0][3] = new Queen(Color.BLACK);
        board[0][4] = new King(Color.BLACK);
        board[0][5] = new Bishop(Color.BLACK);
        board[0][6] = new Knight(Color.BLACK);
        board[0][7] = new Rook(Color.BLACK);

        // Black pawns (row 1)
        for (int c = 0; c < SIZE; c++) {
            board[1][c] = new Pawn(Color.BLACK);
        }

        // White pawns (row 6)
        for (int c = 0; c < SIZE; c++) {
            board[6][c] = new Pawn(Color.WHITE);
        }

        // White pieces (bottom - row 7)
        board[7][0] = new Rook(Color.WHITE);
        board[7][1] = new Knight(Color.WHITE);
        board[7][2] = new Bishop(Color.WHITE);
        board[7][3] = new Queen(Color.WHITE);
        board[7][4] = new King(Color.WHITE);
        board[7][5] = new Bishop(Color.WHITE);
        board[7][6] = new Knight(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);
    }

    /**
     * Gets the piece at the given position.
     *
     * @param position the board position
     * @return the piece at that position, or null if empty
     */
    public Piece getPiece(Position position) {
        return getPiece(position.getRow(), position.getCol());
    }

    /**
     * Gets the piece at the given row and column.
     *
     * @param row the row index (0-7)
     * @param col the column index (0-7)
     * @return the piece, or null if empty or out of bounds
     */
    public Piece getPiece(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return board[row][col];
    }

    /**
     * Places a piece at the given position.
     *
     * @param position the target position
     * @param piece    the piece to place (null to clear)
     */
    public void setPiece(Position position, Piece piece) {
        setPiece(position.getRow(), position.getCol(), piece);
    }

    /**
     * Places a piece at the given row and column.
     *
     * @param row   the row index
     * @param col   the column index
     * @param piece the piece to place (null to clear)
     */
    public void setPiece(int row, int col, Piece piece) {
        if (isValidPosition(row, col)) {
            board[row][col] = piece;
        }
    }

    /**
     * Removes the piece at the given position.
     *
     * @param position the position to clear
     * @return the removed piece, or null if empty
     */
    public Piece removePiece(Position position) {
        Piece piece = getPiece(position);
        setPiece(position, null);
        return piece;
    }

    /**
     * Checks if the given row and column are within board boundaries.
     *
     * @param row the row to check
     * @param col the column to check
     * @return true if valid
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Finds the position of the King of the given color.
     *
     * @param color the king's color
     * @return the king's position
     * @throws IllegalStateException if the king is not found
     */
    public Position findKing(Color color) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece piece = board[r][c];
                if (piece != null
                        && piece.getType() == PieceType.KING
                        && piece.getColor() == color) {
                    return new Position(r, c);
                }
            }
        }
        throw new IllegalStateException("King not found for " + color);
    }

    /**
     * Creates a deep copy of this board.
     * Used for move simulation during check/checkmate detection.
     *
     * @return a new Board with copied pieces
     */
    public Board deepCopy() {
        return new Board(this.board);
    }

    /**
     * Moves a piece from one position to another on this board.
     * This is a low-level operation that does not validate the move.
     *
     * @param from the source position
     * @param to   the destination position
     */
    public void movePiece(Position from, Position to) {
        Piece piece = removePiece(from);
        if (piece != null) {
            setPiece(to, piece);
        }
    }
}
