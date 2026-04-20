package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TankModel implements RotatingGameObject {
    private GridPoint2 coordinates;
    private GridPoint2 destinationCoordinates;
    private float movementProgress;
    private float rotation;
    private int health;
    private int maxHealth;

    public TankModel(int startX, int startY) {
        this.coordinates = new GridPoint2(startX, startY);
        this.destinationCoordinates = new GridPoint2(coordinates);
        this.movementProgress = 1f;
        this.rotation = 0f;
        // Initialize health with random value between 80 and 100
        this.maxHealth = 80 + (int)(Math.random() * 21); // 80 to 100 inclusive
        this.health = this.maxHealth;
    }
    
    public TankModel(int startX, int startY, int health) {
        this.coordinates = new GridPoint2(startX, startY);
        this.destinationCoordinates = new GridPoint2(coordinates);
        this.movementProgress = 1f;
        this.rotation = 0f;
        this.maxHealth = health;
        this.health = health;
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

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    // Setter for movement progress
    public void setMovementProgress(float movementProgress) {
        this.movementProgress = movementProgress;
    }
    
    // Methods for handling damage and health
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }
    
    public void setHealth(int health) {
        this.health = health;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }
}
