package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TreeModel implements GameObjectModel {
    private GridPoint2 coordinates;

    public TreeModel(int x, int y) {
        this.coordinates = new GridPoint2(x, y);
    }

    // Getters
    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        // Trees don't move, so destination is the same as current coordinates
        return coordinates;
    }

    @Override
    public float getMovementProgress() {
        // Trees don't move, so movement progress is always complete
        return 1.0f;
    }

    @Override
    public void setMovementProgress(float movementProgress) {
        // Trees don't move, so this method does nothing
    }

    @Override
    public void updatePosition() {
        // Trees don't move, so this method does nothing
    }

    @Override
    public void move(Direction direction) {
        // Trees don't move, so this method does nothing
    }
}
