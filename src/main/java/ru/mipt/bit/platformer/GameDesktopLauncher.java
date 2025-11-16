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
import ru.mipt.bit.platformer.ai.AITankController;
import ru.mipt.bit.platformer.commands.Command;
import ru.mipt.bit.platformer.commands.MoveTankCommand;
import ru.mipt.bit.platformer.commands.ShootCommand;
import ru.mipt.bit.platformer.commands.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.graphics.GraphicsComponent;
import ru.mipt.bit.platformer.graphics.GraphicsObserver;
import ru.mipt.bit.platformer.graphics.HealthBarDecorator;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.util.CollisionDetector;
import ru.mipt.bit.platformer.util.SimpleCollisionDetector;
import ru.mipt.bit.platformer.input.InputHandler;

import java.util.Random;

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
    private java.util.List<GameObjectModel> aiTankModels;
    private java.util.List<GraphicsComponent> aiTankGraphicsList;
    private GameObjectModel treeObstacleModel;
    private GraphicsComponent treeObstacleGraphics;
    private java.util.List<GameObjectModel> treeModels;
    private java.util.List<GraphicsComponent> treeGraphicsList;
    private CollisionDetector collisionDetector;
    private Random random;
    private AITankController aiTankController;
    private InputHandler inputHandler;
    private java.util.List<HealthBarDecorator> healthBarDecorators;
    private GameObjectManager gameObjectManager;
    private GraphicsObserver graphicsObserver;
    private boolean showHealthBars = false;
    private boolean lKeyPressed = false;
    private boolean spaceKeyPressed = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        random = new Random();
        inputHandler = new InputHandler();
        healthBarDecorators = new java.util.ArrayList<>();
        
        // Register handler for 'L' key to toggle health bars
        inputHandler.registerHandler(com.badlogic.gdx.Input.Keys.L, new ru.mipt.bit.platformer.input.ButtonHandler() {
            @Override
            public boolean handle() {
                showHealthBars = !showHealthBars;
                for (HealthBarDecorator decorator : healthBarDecorators) {
                    decorator.setShowHealthBar(showHealthBars);
                }
                return true;
            }
        });
        
        // Register handler for spacebar to shoot
        inputHandler.registerHandler(com.badlogic.gdx.Input.Keys.SPACE, new ru.mipt.bit.platformer.input.ButtonHandler() {
            @Override
            public boolean handle() {
                if (isEqual(playerTankModel.getMovementProgress(), 1f)) {
                    // Create and execute command for player tank shooting
                    Command playerShootCommand = new ShootCommand((TankModel) playerTankModel, gameObjectManager.getBullets(), collisionDetector, 
                                                                 level.getLevelWidth(), level.getLevelHeight());
                    playerShootCommand.execute();
                }
                return true;
            }
        });

        try {
            // Load level from file
            level = new Level("levels/sample_level.txt", batch, true);
            
            // Or generate a random level
            // level = new Level(batch, 10, 8);
            
            // For now, keep the original behavior
            // level = new Level("level.tmx", batch);
            
            tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);

            // Initialize game object manager
            gameObjectManager = new GameObjectManager();
            
            // Create player tank at the start position defined in the level
            if (level.getPlayerStartPosition() != null) {
                playerTankModel = new TankModel(level.getPlayerStartPosition().x, level.getPlayerStartPosition().y);
            } else {
                // Fallback to default position
                playerTankModel = new TankModel(1, 1);
            }
            TankGraphics playerTankBaseGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);
            TankModel playerTankModelTyped = (TankModel) playerTankModel;
            playerTankGraphics = new HealthBarDecorator(playerTankBaseGraphics, playerTankModelTyped.getHealth(), playerTankModelTyped.getMaxHealth());
            healthBarDecorators.add((HealthBarDecorator) playerTankGraphics);

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

            // Create AI tanks after trees are initialized
            this.aiTankModels = new java.util.ArrayList<>();
            this.aiTankGraphicsList = new java.util.ArrayList<>();
            generateRandomAITanks(3); // Generate 3 random AI tanks
            
            // Store references for rendering
            if (!this.treeGraphicsList.isEmpty()) {
                treeObstacleGraphics = this.treeGraphicsList.get(0);
                treeObstacleModel = this.treeModels.get(0);
            }
            
            // Initialize collision detector with all trees and all tanks
            java.util.List<GameObjectModel> allTanks = new java.util.ArrayList<>();
            allTanks.add(playerTankModel);
            allTanks.addAll(aiTankModels);
            collisionDetector = new SimpleCollisionDetector(treeModels, allTanks);
            
            // Initialize AI tank controller
            aiTankController = new AITankController(collisionDetector, level.getLevelWidth(), level.getLevelHeight());
            
            // Initialize graphics observer
            graphicsObserver = new GraphicsObserver(gameObjectManager, batch, level.getGroundLayer());
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to original implementation if there's an error
            level = new Level("level.tmx", batch);
            tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);
            playerTankModel = new TankModel(1, 1);
            playerTankGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);
            treeObstacleModel = new TreeModel(1, 3);
            treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), (TreeModel) treeObstacleModel);
            java.util.List<GameObjectModel> fallbackTanks = new java.util.ArrayList<>();
            fallbackTanks.add(playerTankModel);
            collisionDetector = new SimpleCollisionDetector(Arrays.asList(treeObstacleModel), fallbackTanks);
        }
    }

    /**
     * Generates random AI tanks at valid positions in the level
     * @param count Number of AI tanks to generate
     */
    private void generateRandomAITanks(int count) {
        for (int i = 0; i < count; i++) {
            // Find a valid position for the AI tank
            GridPoint2 tankPosition = findValidTankPosition();
            
            // Create the AI tank model and graphics
            TankModel aiTankModel = new TankModel(tankPosition.x, tankPosition.y);
            TankGraphics aiTankBaseGraphics = new TankGraphics("images/tank_red.png", level.getGroundLayer(), aiTankModel);
            HealthBarDecorator aiTankGraphics = new HealthBarDecorator(aiTankBaseGraphics, aiTankModel.getHealth(), aiTankModel.getMaxHealth());
            healthBarDecorators.add(aiTankGraphics);
            
            this.aiTankModels.add(aiTankModel);
            this.aiTankGraphicsList.add(aiTankGraphics);
        }
    }

    /**
     * Finds a valid position for a tank that is not occupied by other objects
     * @return A valid GridPoint2 position
     */
    private GridPoint2 findValidTankPosition() {
        // Collect all occupied positions
        java.util.Set<GridPoint2> occupiedPositions = new java.util.HashSet<>();
        
        // Add player tank position
        occupiedPositions.add(playerTankModel.getCoordinates());
        
        // Add AI tank positions
        for (GameObjectModel aiTank : aiTankModels) {
            occupiedPositions.add(aiTank.getCoordinates());
        }
        
        // Add tree positions
        for (GameObjectModel tree : treeModels) {
            occupiedPositions.add(tree.getCoordinates());
        }
        
        // Try to find an unoccupied position
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(level.getLevelWidth());
            int y = random.nextInt(level.getLevelHeight());
            GridPoint2 candidatePosition = new GridPoint2(x, y);
            
            if (!occupiedPositions.contains(candidatePosition)) {
                return candidatePosition;
            }
        }
        
        // If we couldn't find an unoccupied position, return a default position
        return new GridPoint2(0, 0);
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Handle 'L' key for toggling health bars
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.L)) {
            if (!lKeyPressed) {
                lKeyPressed = true;
                showHealthBars = !showHealthBars;
                for (HealthBarDecorator decorator : healthBarDecorators) {
                    decorator.setShowHealthBar(showHealthBars);
                }
            }
        } else {
            lKeyPressed = false;
        }

        // Process input handlers
        inputHandler.processInput();

        // Handle player input
        Direction direction = InputHandler.getDirectionFromInput();
        if (direction != null && isEqual(playerTankModel.getMovementProgress(), 1f)) {
            // Create and execute command for player tank movement
            Command playerMoveCommand = new MoveTankCommand(playerTankModel, direction, collisionDetector, 
                                                           level.getLevelWidth(), level.getLevelHeight());
            playerMoveCommand.execute();
        }

        // Handle AI tank movement (random movement)
        handleAITankMovement();
        
        // Handle AI tank shooting (random shooting)
        handleAITankShooting();

        // Update player tank position
        tileMovement.moveRectangleBetweenTileCenters(
                playerTankGraphics.getRectangle(),
                playerTankModel.getCoordinates(),
                playerTankModel.getDestinationCoordinates(),
                playerTankModel.getMovementProgress());

        float newMovementProgress = continueProgress(playerTankModel.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
        playerTankModel.setMovementProgress(newMovementProgress);
        playerTankModel.updatePosition();

        // Update AI tank positions
        for (int i = 0; i < aiTankModels.size(); i++) {
            GameObjectModel aiTankModel = aiTankModels.get(i);
            GraphicsComponent aiTankGraphics = aiTankGraphicsList.get(i);
            
            tileMovement.moveRectangleBetweenTileCenters(
                    aiTankGraphics.getRectangle(),
                    aiTankModel.getCoordinates(),
                    aiTankModel.getDestinationCoordinates(),
                    aiTankModel.getMovementProgress());

            float aiMovementProgress = continueProgress(aiTankModel.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
            aiTankModel.setMovementProgress(aiMovementProgress);
            aiTankModel.updatePosition();
        }
        
        // Update bullet positions and handle collisions
        updateBullets(deltaTime);

        // render each tile of the level
        level.render();

        // start recording all drawing commands
        batch.begin();

        // render player tank
        drawTextureRegionUnscaled(batch, playerTankGraphics.getGraphics(), playerTankGraphics.getRectangle(), 
                                 ((ru.mipt.bit.platformer.model.RotatingGameObject) playerTankModel).getRotation());

        // render AI tanks
        for (int i = 0; i < aiTankGraphicsList.size(); i++) {
            GraphicsComponent aiTankGraphics = aiTankGraphicsList.get(i);
            GameObjectModel aiTankModel = aiTankModels.get(i);
            drawTextureRegionUnscaled(batch, aiTankGraphics.getGraphics(), aiTankGraphics.getRectangle(), 
                                     ((ru.mipt.bit.platformer.model.RotatingGameObject) aiTankModel).getRotation());
        }

        // render tree obstacles
        if (treeGraphicsList != null) {
            for (GraphicsComponent treeGraphics : treeGraphicsList) {
                drawTextureRegionUnscaled(batch, treeGraphics.getGraphics(), treeGraphics.getRectangle(), 0f);
            }
        }
        
        // render bullets
        graphicsObserver.renderBullets();

        // render health bars for tanks if enabled
        if (showHealthBars) {
            // Render player tank health bar
            if (playerTankGraphics instanceof HealthBarDecorator) {
                ((HealthBarDecorator) playerTankGraphics).renderHealthBar(batch, playerTankGraphics.getRectangle());
            }

            // Render AI tank health bars
            for (GraphicsComponent aiTankGraphics : aiTankGraphicsList) {
                if (aiTankGraphics instanceof HealthBarDecorator) {
                    ((HealthBarDecorator) aiTankGraphics).renderHealthBar(batch, aiTankGraphics.getRectangle());
                }
            }
        }

        // submit all drawing requests
        batch.end();
    }

    /**
     * Handles movement for all AI tanks with random directions
     */
    private void handleAITankMovement() {
        for (GameObjectModel aiTankModel : aiTankModels) {
            aiTankController.moveAITank(aiTankModel);
        }
    }
    
    /**
     * Handles shooting for all AI tanks with random probability
     */
    private void handleAITankShooting() {
        // 5% chance for each AI tank to shoot on each frame
        for (GameObjectModel aiTankModel : aiTankModels) {
            if (random.nextFloat() < 0.05f && isEqual(aiTankModel.getMovementProgress(), 1f)) {
                // Create and execute command for AI tank shooting
                Command aiShootCommand = new ShootCommand((TankModel) aiTankModel, gameObjectManager.getBullets(), collisionDetector, 
                                                         level.getLevelWidth(), level.getLevelHeight());
                aiShootCommand.execute();
            }
        }
    }
    
    /**
     * Updates bullet positions and handles collisions
     */
    private void updateBullets(float deltaTime) {
        java.util.List<BulletModel> bullets = gameObjectManager.getBullets();
        
        // Update bullet positions
        for (BulletModel bullet : bullets) {
            if (bullet.isActive()) {
                // Move the bullet
                bullet.move();
                
                // Update bullet position
                float newMovementProgress = continueProgress(bullet.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
                bullet.setMovementProgress(newMovementProgress);
                bullet.updatePosition();
                
                // Check for collisions
                checkBulletCollisions(bullet);
            }
        }
        
        // Remove inactive bullets
        bullets.removeIf(bullet -> !bullet.isActive());
    }
    
    /**
     * Checks for collisions with a bullet
     */
    private void checkBulletCollisions(BulletModel bullet) {
        // Check boundaries
        GridPoint2 bulletPos = bullet.getCoordinates();
        if (bulletPos.x < 0 || bulletPos.x >= level.getLevelWidth() || 
            bulletPos.y < 0 || bulletPos.y >= level.getLevelHeight()) {
            bullet.setActive(false);
            return;
        }
        
        // Check for collisions with trees
        for (GameObjectModel tree : treeModels) {
            if (tree.getCoordinates().equals(bulletPos)) {
                bullet.setActive(false);
                return;
            }
        }
        
        // Check for collisions with tanks (including player)
        // Don't check against the tank that fired the bullet
        for (GameObjectModel tank : aiTankModels) {
            if (tank.getCoordinates().equals(bulletPos)) {
                bullet.setActive(false);
                // Apply damage to the tank
                if (tank instanceof TankModel) {
                    TankModel tankModel = (TankModel) tank;
                    tankModel.takeDamage(bullet.getDamage());
                    // Check if tank is destroyed
                    if (tankModel.getHealth() <= 0) {
                        // Remove destroyed tank
                        aiTankModels.remove(tank);
                        // Find and remove corresponding graphics
                        for (int i = 0; i < aiTankGraphicsList.size(); i++) {
                            if (aiTankGraphicsList.get(i) instanceof HealthBarDecorator) {
                                HealthBarDecorator decorator = (HealthBarDecorator) aiTankGraphicsList.get(i);
                                // This is a simplified check - in a real implementation we'd need a better way to match
                                // For now, we'll just remove the first one
                                aiTankGraphicsList.remove(i);
                                decorator.dispose();
                                break;
                            }
                        }
                    }
                }
                return;
            }
        }
        
        // Check for collisions with player tank
        if (playerTankModel.getCoordinates().equals(bulletPos)) {
            bullet.setActive(false);
            // Apply damage to the player tank
            if (playerTankModel instanceof TankModel) {
                TankModel tankModel = (TankModel) playerTankModel;
                tankModel.takeDamage(bullet.getDamage());
                // Check if player tank is destroyed
                if (tankModel.getHealth() <= 0) {
                    // In a real game, we would handle game over here
                    // For now, we'll just reset the player's health
                    tankModel.setHealth(tankModel.getMaxHealth());
                }
            }
        }
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
        
        // Dispose AI tank graphics
        if (aiTankGraphicsList != null) {
            for (GraphicsComponent aiTankGraphics : aiTankGraphicsList) {
                aiTankGraphics.dispose();
            }
        }
        
        // Dispose graphics observer (which disposes bullet graphics)
        if (graphicsObserver != null) {
            graphicsObserver.dispose();
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
