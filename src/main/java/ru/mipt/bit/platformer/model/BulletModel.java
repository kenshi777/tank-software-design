package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Model for a bullet object in the game
 */
public class BulletModel implements GameObjectModel {
    private GridPoint2 coordinates;
    private GridPoint2 destinationCoordinates;
    private float movementProgress;
    private Direction direction;
    private int damage;
    private boolean active;
    private TankModel owner;

    public BulletModel(int startX, int startY, Direction direction, int damage, TankModel owner) {
        this.coordinates = new GridPoint2(startX, startY);
        this.destinationCoordinates = direction.applyTo(coordinates);
        this.movementProgress = 0f;
        this.direction = direction;
        this.damage = damage;
        this.active = true;
        this.owner = owner;
    }

    @Override
    public void move(Direction direction) {
        // For bullets, direction is fixed at creation time
        // This method is required by the interface but won't be used for bullets
        if (movementProgress == 1f) {
            coordinates.set(destinationCoordinates);
            destinationCoordinates = this.direction.applyTo(coordinates);
            movementProgress = 0f;
        }
    }

    public void move() {
        move(this.direction);
    }

    public void updatePosition() {
        if (movementProgress == 1f) {
            coordinates.set(destinationCoordinates);
        }
    }

    // Getters
    @Override
    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return destinationCoordinates;
    }

    @Override
    public float getMovementProgress() {
        return movementProgress;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isActive() {
        return active;
    }

    public TankModel getOwner() {
        return owner;
    }

    // Setter for movement progress
    @Override
    public void setMovementProgress(float movementProgress) {
        this.movementProgress = movementProgress;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
