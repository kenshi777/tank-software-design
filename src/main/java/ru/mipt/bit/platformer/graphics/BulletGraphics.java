package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
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

    public BulletGraphics(TiledMapTileLayer tileLayer, BulletModel bulletModel) {
        this.texture = createBulletTexture();
        this.graphics = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(graphics);

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

    private Texture createBulletTexture() {
        Pixmap pixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0.95f, 0.2f, 1f);
        pixmap.fillCircle(6, 6, 5);
        Texture generated = new Texture(pixmap);
        pixmap.dispose();
        return generated;
    }
}
