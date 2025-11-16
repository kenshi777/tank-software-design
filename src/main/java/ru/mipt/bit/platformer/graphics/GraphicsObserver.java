package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.observer.Observer;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.BulletModel;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import java.util.HashMap;
import java.util.Map;

import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

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
        // This method is called when game objects change
        // For now, we'll handle bullet graphics creation/deletion in the render loop
        // This is a simplified approach - in a more robust implementation, we would
        // create/destroy graphics objects here
    }

    public void renderBullets() {
        // Render all active bullets
        for (BulletModel bullet : gameObjectManager.getBullets()) {
            if (bullet.isActive()) {
                BulletGraphics bulletGraphics = bulletGraphicsMap.get(bullet);
                if (bulletGraphics == null) {
                    // Create graphics for new bullet
                    bulletGraphics = new BulletGraphics("images/bullet.png", groundLayer, bullet);
                    bulletGraphicsMap.put(bullet, bulletGraphics);
                }
                
                // Update bullet position
                GdxGameUtils.moveRectangleAtTileCenter(groundLayer, bulletGraphics.getRectangle(), bullet.getCoordinates());
                
                // Render the bullet
                drawTextureRegionUnscaled(batch, bulletGraphics.getGraphics(), bulletGraphics.getRectangle(), 0f);
            }
        }
        
        // Remove graphics for inactive bullets
        bulletGraphicsMap.entrySet().removeIf(entry -> {
            if (!entry.getKey().isActive()) {
                entry.getValue().dispose();
                return true;
            }
            return false;
        });
    }

    public void dispose() {
        // Dispose all bullet graphics
        for (BulletGraphics graphics : bulletGraphicsMap.values()) {
            graphics.dispose();
        }
        bulletGraphicsMap.clear();
    }
}
