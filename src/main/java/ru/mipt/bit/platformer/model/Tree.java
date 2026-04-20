package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GdxGameUtils;

/**
 * Represents a tree obstacle in the game
 */
public class Tree {
    private Texture texture;
    private TextureRegion graphics;
    private Rectangle rectangle;
    private GridPoint2 coordinates;

    public Tree(String texturePath, TiledMapTileLayer tileLayer, int x, int y) {
        this.texture = new Texture(texturePath);
        this.graphics = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(graphics);
        this.coordinates = new GridPoint2(x, y);
        
        // Position the tree at its coordinates
        GdxGameUtils.moveRectangleAtTileCenter(tileLayer, rectangle, coordinates);
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
}
