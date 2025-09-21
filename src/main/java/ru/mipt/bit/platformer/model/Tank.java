package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GdxGameUtils;

/**
 * Represents a tank in the game
 */
public class Tank {
    private Texture texture;
    private TextureRegion graphics;
    private Rectangle rectangle;
    private GridPoint2 coordinates;
    private GridPoint2 destinationCoordinates;
    private float movementProgress;
    private float rotation;

    public Tank(String texturePath, TiledMapTileLayer tileLayer, int startX, int startY) {
        this.texture = new Texture(texturePath);
        this.graphics = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(graphics);
        this.coordinates = new GridPoint2(startX, startY);
        this.destinationCoordinates = new GridPoint2(coordinates);
        this.movementProgress = 1f;
        this.rotation = 0f;
        
        // Position the tank at its initial coordinates
        GdxGameUtils.moveRectangleAtTileCenter(tileLayer, rectangle, coordinates);
    }

    public void move(Direction direction, TiledMapTileLayer tileLayer) {
        if (movementProgress == 1f) {
            GridPoint2 newDestination = direction.applyTo(coordinates);
            // In the refactored version, collision checking would be handled externally
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

    public void dispose() {
        texture.dispose();
    }

    // Getters
    public TextureRegion getGraphics() {
        return graphics;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

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
