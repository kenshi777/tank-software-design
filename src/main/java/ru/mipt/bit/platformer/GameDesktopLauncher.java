package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.util.InputHandler;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;

    private Level level;
    private TileMovement tileMovement;

    private Tank playerTank;
    private Tree treeObstacle;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load level tiles
        level = new Level("level.tmx", batch);
        tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);

        // Create player tank
        playerTank = new Tank("images/tank_blue.png", level.getGroundLayer(), 1, 1);

        // Create tree obstacle
        treeObstacle = new Tree("images/greenTree.png", level.getGroundLayer(), 1, 3);
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
        if (direction != null && isEqual(playerTank.getMovementProgress(), 1f)) {
            // Check for collision with obstacles
            GridPoint2 newDestination = direction.applyTo(playerTank.getCoordinates());
            if (!treeObstacle.getCoordinates().equals(newDestination)) {
                playerTank.move(direction, level.getGroundLayer());
            }
        }

        // Update tank position
        tileMovement.moveRectangleBetweenTileCenters(
                playerTank.getRectangle(),
                playerTank.getCoordinates(),
                playerTank.getDestinationCoordinates(),
                playerTank.getMovementProgress());

        float newMovementProgress = continueProgress(playerTank.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
        playerTank.setMovementProgress(newMovementProgress);
        playerTank.updatePosition();

        // render each tile of the level
        level.render();

        // start recording all drawing commands
        batch.begin();

        // render player
        drawTextureRegionUnscaled(batch, playerTank.getGraphics(), playerTank.getRectangle(), playerTank.getRotation());

        // render tree obstacle
        drawTextureRegionUnscaled(batch, treeObstacle.getGraphics(), treeObstacle.getRectangle(), 0f);

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
        treeObstacle.dispose();
        playerTank.dispose();
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
