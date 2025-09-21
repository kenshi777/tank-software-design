package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.util.GdxGameUtils;

/**
 * Represents the game level
 */
public class Level {
    private TiledMap map;
    private MapRenderer renderer;
    private TiledMapTileLayer groundLayer;

    public Level(String mapPath, Batch batch) {
        this.map = new TmxMapLoader().load(mapPath);
        this.renderer = GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        this.groundLayer = GdxGameUtils.getSingleLayer(map);
    }

    public void render() {
        renderer.render();
    }

    public void dispose() {
        map.dispose();
    }

    // Getters
    public TiledMap getMap() {
        return map;
    }

    public TiledMapTileLayer getGroundLayer() {
        return groundLayer;
    }
}
