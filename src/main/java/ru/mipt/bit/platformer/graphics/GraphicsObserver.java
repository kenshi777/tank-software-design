package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.observer.Observer;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.BulletModel;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.util.Map;

import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

/**
 * Observer that handles graphics rendering updates when game objects change
 */
public class GraphicsObserver implements Observer {
    private final GameObjectManager gameObjectManager;
    private final Batch batch;
    private final TiledMapTileLayer groundLayer;
    private final Map<BulletModel, BulletGraphics> bulletGraphicsMap;

    public GraphicsObserver(GameObjectManager gameObjectManager, Batch batch, TiledMapTileLayer groundLayer) {
        this.gameObjectManager = gameObjectManager;
        this.batch = batch;
        this.groundLayer = groundLayer;
        this.bulletGraphicsMap = new HashMap<>();
    }

    @Override
    public void update() {
        syncBulletGraphics();
    }

    public void renderBullets() {
        syncBulletGraphics();

        for (BulletModel bullet : gameObjectManager.getBullets()) {
            if (!bullet.isActive()) {
                continue;
            }
            BulletGraphics bulletGraphics = bulletGraphicsMap.computeIfAbsent(
                    bullet, b -> new BulletGraphics(groundLayer, b));
            moveBulletRectangle(bulletGraphics.getRectangle(), bullet);
            drawTextureRegionUnscaled(batch, bulletGraphics.getGraphics(), bulletGraphics.getRectangle(), 0f);
        }
    }

    public void dispose() {
        // Dispose all bullet graphics
        for (BulletGraphics graphics : bulletGraphicsMap.values()) {
            graphics.dispose();
        }
        bulletGraphicsMap.clear();
    }

    private void syncBulletGraphics() {
        // Remove graphics for bullets that are gone or inactive
        bulletGraphicsMap.entrySet().removeIf(entry -> {
            boolean shouldRemove = !gameObjectManager.getBullets().contains(entry.getKey()) || !entry.getKey().isActive();
            if (shouldRemove) {
                entry.getValue().dispose();
            }
            return shouldRemove;
        });

        // Ensure graphics exist for every active bullet
        for (BulletModel bullet : gameObjectManager.getBullets()) {
            if (bullet.isActive() && !bulletGraphicsMap.containsKey(bullet)) {
                bulletGraphicsMap.put(bullet, new BulletGraphics(groundLayer, bullet));
            }
        }
    }

    private void moveBulletRectangle(Rectangle rectangle, BulletModel bullet) {
        int tileWidth = groundLayer.getTileWidth();
        int tileHeight = groundLayer.getTileHeight();
        float fromCenterX = (bullet.getCoordinates().x + 0.5f) * tileWidth;
        float fromCenterY = (bullet.getCoordinates().y + 0.5f) * tileHeight;
        float toCenterX = (bullet.getDestinationCoordinates().x + 0.5f) * tileWidth;
        float toCenterY = (bullet.getDestinationCoordinates().y + 0.5f) * tileHeight;

        float progress = bullet.getMovementProgress();
        float centerX = Interpolation.linear.apply(fromCenterX, toCenterX, progress);
        float centerY = Interpolation.linear.apply(fromCenterY, toCenterY, progress);
        rectangle.setCenter(centerX, centerY);
    }
}
