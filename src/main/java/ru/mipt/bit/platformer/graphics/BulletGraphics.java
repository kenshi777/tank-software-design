package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;

/**
 * Graphics component for rendering a bullet
 */
public class BulletGraphics implements GraphicsComponent {
    private Texture texture;
    private TextureRegion graphics;
    private Rectangle rectangle;

    public BulletGraphics(String texturePath, TiledMapTileLayer tileLayer, BulletModel bulletModel) {
        this.texture = new Texture(texturePath);
        this.graphics = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(graphics);
        
        // Position the bullet at its initial coordinates
        GdxGameUtils.moveRectangleAtTileCenter(tileLayer, rectangle, bulletModel.getCoordinates());
    }

    @Override
    public TextureRegion getGraphics() {
        return graphics;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
