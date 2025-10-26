package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.graphics.GraphicsComponent;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.util.CollisionDetector;
import ru.mipt.bit.platformer.util.SimpleCollisionDetector;
import ru.mipt.bit.platformer.input.InputHandler;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

import java.util.Arrays;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;

    private Level level;
    private TileMovement tileMovement;

    private GameObjectModel playerTankModel;
    private GraphicsComponent playerTankGraphics;
    private GameObjectModel treeObstacleModel;
    private GraphicsComponent treeObstacleGraphics;
    private java.util.List<GameObjectModel> treeModels;
    private java.util.List<GraphicsComponent> treeGraphicsList;
    private CollisionDetector collisionDetector;

    @Override
    public void create() {
        batch = new SpriteBatch();

        try {
            // Load level from file
            level = new Level("levels/sample_level.txt", batch, true);
            
            // Or generate a random level
            // level = new Level(batch, 10, 8);
            
            // For now, keep the original behavior
            // level = new Level("level.tmx", batch);
            
            tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);

            // Create player tank at the start position defined in the level
            if (level.getPlayerStartPosition() != null) {
                playerTankModel = new TankModel(level.getPlayerStartPosition().x, level.getPlayerStartPosition().y);
            } else {
                // Fallback to default position
                playerTankModel = new TankModel(1, 1);
            }
            playerTankGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);

            // Create tree obstacles based on level data
            this.treeModels = new java.util.ArrayList<>();
            this.treeGraphicsList = new java.util.ArrayList<>();
            
            // For file-based or random levels, create trees from level data
            if (level.getTreePositions() != null && !level.getTreePositions().isEmpty()) {
                for (GridPoint2 treePos : level.getTreePositions()) {
                    TreeModel treeModel = new TreeModel(treePos.x, treePos.y);
                    TreeGraphics treeGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), treeModel);
                    this.treeModels.add(treeModel);
                    this.treeGraphicsList.add(treeGraphics);
                }
            } else {
                // Fallback to original single tree for TMX levels
                treeObstacleModel = new TreeModel(1, 3);
                treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), (TreeModel) treeObstacleModel);
                this.treeModels.add(treeObstacleModel);
                this.treeGraphicsList.add(treeObstacleGraphics);
            }
            
            // Store references for rendering
            if (!this.treeGraphicsList.isEmpty()) {
                treeObstacleGraphics = this.treeGraphicsList.get(0);
                treeObstacleModel = this.treeModels.get(0);
            }
            
            // Initialize collision detector with all trees
            collisionDetector = new SimpleCollisionDetector(treeModels);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to original implementation if there's an error
            level = new Level("level.tmx", batch);
            tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);
            playerTankModel = new TankModel(1, 1);
            playerTankGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);
            treeObstacleModel = new TreeModel(1, 3);
            treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), (TreeModel) treeObstacleModel);
            collisionDetector = new SimpleCollisionDetector(Arrays.asList(treeObstacleModel));
        }
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Handle input
        Direction direction = InputHandler.getDirectionFromInput();
        if (direction != null && isEqual(playerTankModel.getMovementProgress(), 1f)) {
            // Check for collision with obstacles using the collision detector
            GridPoint2 newDestination = direction.applyTo(playerTankModel.getCoordinates());
            if (!collisionDetector.isCollision(playerTankModel, newDestination)) {
                playerTankModel.move(direction);
            }
        }

        // Update tank position
        tileMovement.moveRectangleBetweenTileCenters(
                playerTankGraphics.getRectangle(),
                playerTankModel.getCoordinates(),
                playerTankModel.getDestinationCoordinates(),
                playerTankModel.getMovementProgress());

        float newMovementProgress = continueProgress(playerTankModel.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
        playerTankModel.setMovementProgress(newMovementProgress);
        playerTankModel.updatePosition();

        // render each tile of the level
        level.render();

        // start recording all drawing commands
        batch.begin();

        // render player
        drawTextureRegionUnscaled(batch, playerTankGraphics.getGraphics(), playerTankGraphics.getRectangle(), ((ru.mipt.bit.platformer.model.RotatingGameObject) playerTankModel).getRotation());

        // render tree obstacles
        if (treeGraphicsList != null) {
            for (GraphicsComponent treeGraphics : treeGraphicsList) {
                drawTextureRegionUnscaled(batch, treeGraphics.getGraphics(), treeGraphics.getRectangle(), 0f);
            }
        }

        // submit all drawing requests
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        if (treeGraphicsList != null) {
            for (GraphicsComponent treeGraphics : treeGraphicsList) {
                treeGraphics.dispose();
            }
        }
        playerTankGraphics.dispose();
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
