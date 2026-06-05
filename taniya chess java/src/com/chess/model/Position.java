package com.chess.model;

/**
 * Represents a position on the chess board using row and column indices.
 * <p>
 * Row 0 corresponds to rank 8 (Black's back rank),
 * Row 7 corresponds to rank 1 (White's back rank).
 * Column 0 corresponds to file 'a', Column 7 to file 'h'.
 * </p>
 * <p>
 * This class is immutable to ensure thread safety and prevent
 * accidental modification of board coordinates.
 * </p>
 *
 * @author Taniya
 */
public class Position {

    private final int row;
    private final int col;

    /**
     * Constructs a Position with the given row and column.
     *
     * @param row the row index (0-7)
     * @param col the column index (0-7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return the row index (0-7)
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index (0-7)
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks whether this position is within the 8x8 board boundaries.
     *
     * @return true if both row and col are in [0, 7]
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}
