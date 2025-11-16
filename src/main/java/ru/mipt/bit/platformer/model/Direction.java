package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Represents movement direction in the game
 */
public enum Direction {
    UP(0, 1, 90f),
    DOWN(0, -1, -90f),
    LEFT(-1, 0, -180f),
    RIGHT(1, 0, 0f);

    private final int dx;
    private final int dy;
    private final float rotation;

    Direction(int dx, int dy, float rotation) {
        this.dx = dx;
        this.dy = dy;
        this.rotation = rotation;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public float getRotation() {
        return rotation;
    }

    public GridPoint2 applyTo(GridPoint2 point) {
        return new GridPoint2(point.x + dx, point.y + dy);
    }
    
    /**
     * Gets the direction corresponding to the given rotation angle
     * @param rotation The rotation angle in degrees
     * @return The corresponding Direction enum value
     */
    public static Direction fromRotation(float rotation) {
        // Normalize rotation to [0, 360) range
        rotation = rotation % 360;
        if (rotation < 0) {
            rotation += 360;
        }
        
        // Match rotation to closest direction
        if (Math.abs(rotation - 90f) < 45f) {
            return UP;
        } else if (Math.abs(rotation - 270f) < 45f || Math.abs(rotation - (-90f)) < 45f) {
            return DOWN;
        } else if (Math.abs(rotation - 180f) < 45f || Math.abs(rotation - (-180f)) < 45f) {
            return LEFT;
        } else {
            return RIGHT;
        }
    }
}
