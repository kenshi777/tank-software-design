package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TankModel {
    private GridPoint2 coordinates;
    private GridPoint2 destinationCoordinates;
    private float movementProgress;
    private float rotation;

    public TankModel(int startX, int startY) {
        this.coordinates = new GridPoint2(startX, startY);
        this.destinationCoordinates = new GridPoint2(coordinates);
        this.movementProgress = 1f;
        this.rotation = 0f;
    }

    public void move(Direction direction) {
        if (movementProgress == 1f) {
            GridPoint2 newDestination = direction.applyTo(coordinates);
            this.destinationCoordinates = newDestination;
            this.movementProgress = 0f;
            this.rotation = direction.getRotation();
        }
    }

    public void updatePosition() {
        if (movementProgress == 1f) {
            coordinates.set(destinationCoordinates);
        }
    }

    // Getters
    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public GridPoint2 getDestinationCoordinates() {
        return destinationCoordinates;
    }

    public float getMovementProgress() {
        return movementProgress;
    }

    public float getRotation() {
        return rotation;
    }

    // Setter for movement progress
    public void setMovementProgress(float movementProgress) {
        this.movementProgress = movementProgress;
    }
}
