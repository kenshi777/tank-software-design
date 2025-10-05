package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public class TreeGraphics {
    private Texture texture;
    private TextureRegion graphics;
    private Rectangle rectangle;

    public TreeGraphics(String texturePath, TiledMapTileLayer tileLayer, TreeModel treeModel) {
        this.texture = new Texture(texturePath);
        this.graphics = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(graphics);
        
        // Position the tree at its coordinates
        GdxGameUtils.moveRectangleAtTileCenter(tileLayer, rectangle, treeModel.getCoordinates());
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
}
