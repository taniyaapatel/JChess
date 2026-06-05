package com.chess.model;

/**
 * Represents a chess player with a name and assigned color.
 *
 * @author Taniya
 */
public class Player {

    private final String name;
    private final Color color;

    /**
     * Constructs a Player with the given name and color.
     *
     * @param name  the player's display name
     * @param color the color of pieces this player controls
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /** @return the player's name */
    public String getName() { return name; }

    /** @return the color this player controls */
    public Color getColor() { return color; }

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
