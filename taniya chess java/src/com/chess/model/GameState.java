package com.chess.model;

/**
 * Represents the current state of the chess game.
 *
 * @author Taniya
 */
public enum GameState {

    /** Game is in progress, no special conditions. */
    ACTIVE,

    /** Current player's king is in check. */
    CHECK,

    /** Current player is in checkmate - game over. */
    CHECKMATE,

    /** Current player has no legal moves but is not in check - draw. */
    STALEMATE,

    /** A pawn has reached the promotion rank and awaits promotion choice. */
    PROMOTION_PENDING
}
