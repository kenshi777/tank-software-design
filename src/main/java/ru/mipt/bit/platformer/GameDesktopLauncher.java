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
    private CollisionDetector collisionDetector;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load level tiles
        level = new Level("level.tmx", batch);
        tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);

        // Create player tank
        playerTankModel = new TankModel(1, 1);
        playerTankGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);

        // Create tree obstacle
        treeObstacleModel = new TreeModel(1, 3);
        treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), (TreeModel) treeObstacleModel);
        
        // Initialize collision detector
        collisionDetector = new SimpleCollisionDetector(Arrays.asList(treeObstacleModel));
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

        // render tree obstacle
        drawTextureRegionUnscaled(batch, treeObstacleGraphics.getGraphics(), treeObstacleGraphics.getRectangle(), 0f);

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
        treeObstacleGraphics.dispose();
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
