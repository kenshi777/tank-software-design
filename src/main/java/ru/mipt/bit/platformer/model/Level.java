package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.util.GdxGameUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game level
 */
public class Level {
    private TiledMap map;
    private MapRenderer renderer;
    private TiledMapTileLayer groundLayer;
    private GridPoint2 playerStartPosition;
    private List<GridPoint2> treePositions;
    private int levelWidth;
    private int levelHeight;
    private Texture tileTexture1;
    private Texture tileTexture2;

    // Constructor for loading from TMX file
    public Level(String mapPath, Batch batch) {
        this.map = new TmxMapLoader().load(mapPath);
        this.renderer = GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        this.groundLayer = GdxGameUtils.getSingleLayer(map);
        this.levelWidth = groundLayer.getWidth();
        this.levelHeight = groundLayer.getHeight();
        this.treePositions = new ArrayList<>();
        // Default player start position for TMX levels
        this.playerStartPosition = new GridPoint2(1, 1);
    }

    // Constructor for loading from text file
    public Level(String levelFilePath, Batch batch, boolean loadFromFile) throws IOException {
        if (loadFromFile) {
            loadLevelFromFile(levelFilePath);
        } else {
            // This constructor is for random generation
            generateRandomLevel();
        }
        
        // Create a basic tiled map for rendering
        createBasicMap(batch);
    }

    // Constructor for random level generation
    public Level(Batch batch, int width, int height) throws IOException {
        this.levelWidth = width;
        this.levelHeight = height;
        generateRandomLevel();
        createBasicMap(batch);
    }

    private void loadLevelFromFile(String filePath) throws IOException {
        treePositions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            levelHeight = lines.size();
            levelWidth = levelHeight > 0 ? lines.get(0).length() : 0;
            
            // Parse the level data
            for (int y = 0; y < levelHeight; y++) {
                String row = lines.get(y);
                for (int x = 0; x < Math.min(row.length(), levelWidth); x++) {
                    char cell = row.charAt(x);
                    switch (cell) {
                        case 'T':
                            treePositions.add(new GridPoint2(x, levelHeight - 1 - y));
                            break;
                        case 'X':
                            playerStartPosition = new GridPoint2(x, levelHeight - 1 - y);
                            break;
                        // '_' represents empty cell, no action needed
                    }
                }
            }
        }
    }

    private void generateRandomLevel() {
        treePositions = new ArrayList<>();
        Random random = new Random();
        
        // Generate random trees (about 20% of cells)
        int treeCount = (int) (levelWidth * levelHeight * 0.2);
        for (int i = 0; i < treeCount; i++) {
            int x = random.nextInt(levelWidth);
            int y = random.nextInt(levelHeight);
            treePositions.add(new GridPoint2(x, y));
        }
        
        // Generate random player start position (avoiding trees)
        boolean positionFound = false;
        while (!positionFound) {
            int x = random.nextInt(levelWidth);
            int y = random.nextInt(levelHeight);
            GridPoint2 candidatePosition = new GridPoint2(x, y);
            
            // Check if this position is occupied by a tree
            boolean occupied = false;
            for (GridPoint2 treePos : treePositions) {
                if (treePos.equals(candidatePosition)) {
                    occupied = true;
                    break;
                }
            }
            
            if (!occupied) {
                playerStartPosition = candidatePosition;
                positionFound = true;
            }
        }
    }

    private void createBasicMap(Batch batch) {
        // Generate a simple single-layer map matching the logical level size
        try {
            this.map = new TiledMap();
            this.tileTexture1 = new Texture("images/tileGrass1.png");
            this.tileTexture2 = new Texture("images/tileGrass2.png");
            StaticTiledMapTile grass1 = new StaticTiledMapTile(new com.badlogic.gdx.graphics.g2d.TextureRegion(tileTexture1));
            StaticTiledMapTile grass2 = new StaticTiledMapTile(new com.badlogic.gdx.graphics.g2d.TextureRegion(tileTexture2));

            int tileSize = 128;
            TiledMapTileLayer layer = new TiledMapTileLayer(levelWidth, levelHeight, tileSize, tileSize);
            for (int x = 0; x < levelWidth; x++) {
                for (int y = 0; y < levelHeight; y++) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    // Simple checkerboard so the grid is visible
                    cell.setTile(((x + y) % 2 == 0) ? grass1 : grass2);
                    layer.setCell(x, y, cell);
                }
            }
            map.getLayers().add(layer);
            this.groundLayer = layer;
            this.renderer = GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create map for level", e);
        }
    }

    public void render() {
        renderer.render();
    }

    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (tileTexture1 != null) {
            tileTexture1.dispose();
        }
        if (tileTexture2 != null) {
            tileTexture2.dispose();
        }
    }

    // Getters
    public TiledMap getMap() {
        return map;
    }

    public TiledMapTileLayer getGroundLayer() {
        return groundLayer;
    }

    public GridPoint2 getPlayerStartPosition() {
        return playerStartPosition;
    }

    public List<GridPoint2> getTreePositions() {
        return treePositions;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public int getLevelHeight() {
        return levelHeight;
    }
}
