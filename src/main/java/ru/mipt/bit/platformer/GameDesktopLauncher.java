package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mipt.bit.platformer.config.AppConfig;
import ru.mipt.bit.platformer.ai.AITankController;
import ru.mipt.bit.platformer.commands.Command;
import ru.mipt.bit.platformer.commands.MoveTankCommand;
import ru.mipt.bit.platformer.commands.ShootCommand;
import ru.mipt.bit.platformer.graphics.GraphicsComponent;
import ru.mipt.bit.platformer.graphics.GraphicsObserver;
import ru.mipt.bit.platformer.graphics.HealthBarDecorator;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameObjectManager;
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

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;
    private static final float BULLET_SPEED = 0.2f;

    private Batch batch;

    private Level level;
    private TileMovement tileMovement;
    private AnnotationConfigApplicationContext applicationContext;

    private TankModel playerTankModel;
    private GraphicsComponent playerTankGraphics;
    private java.util.List<TankModel> aiTankModels;
    private java.util.List<GraphicsComponent> aiTankGraphicsList;
    private TreeModel treeObstacleModel;
    private GraphicsComponent treeObstacleGraphics;
    private java.util.List<TreeModel> treeModels;
    private java.util.List<GraphicsComponent> treeGraphicsList;
    private CollisionDetector collisionDetector;
    private Random random;
    private AITankController aiTankController;
    private InputHandler inputHandler;
    private java.util.List<HealthBarDecorator> healthBarDecorators;
    private GameObjectManager gameObjectManager;
    private GraphicsObserver graphicsObserver;
    private boolean showHealthBars = false;
    private boolean playerDestroyed = false;

    @Override
    public void create() {
        healthBarDecorators = new java.util.ArrayList<>();
        try {
            applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
            batch = applicationContext.getBean(Batch.class);
            random = applicationContext.getBean(Random.class);
            inputHandler = applicationContext.getBean(InputHandler.class);
            level = applicationContext.getBean(Level.class);
            tileMovement = applicationContext.getBean(TileMovement.class);
            gameObjectManager = applicationContext.getBean(GameObjectManager.class);
            collisionDetector = applicationContext.getBean(CollisionDetector.class);
            aiTankController = applicationContext.getBean(AITankController.class);
            graphicsObserver = applicationContext.getBean(GraphicsObserver.class);

            if (level.getPlayerStartPosition() != null) {
                playerTankModel = new TankModel(level.getPlayerStartPosition().x, level.getPlayerStartPosition().y);
            } else {
                playerTankModel = new TankModel(1, 1);
            }
            TankGraphics playerTankBaseGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);
            playerTankGraphics = new HealthBarDecorator(playerTankBaseGraphics, playerTankModel);
            healthBarDecorators.add((HealthBarDecorator) playerTankGraphics);
            gameObjectManager.addTank(playerTankModel);

            this.treeModels = new java.util.ArrayList<>();
            this.treeGraphicsList = new java.util.ArrayList<>();

            if (level.getTreePositions() != null && !level.getTreePositions().isEmpty()) {
                for (GridPoint2 treePos : level.getTreePositions()) {
                    TreeModel treeModel = new TreeModel(treePos.x, treePos.y);
                    TreeGraphics treeGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), treeModel);
                    this.treeModels.add(treeModel);
                    this.treeGraphicsList.add(treeGraphics);
                    gameObjectManager.addTree(treeModel);
                }
            } else {
                treeObstacleModel = new TreeModel(1, 3);
                treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), treeObstacleModel);
                this.treeModels.add(treeObstacleModel);
                this.treeGraphicsList.add(treeObstacleGraphics);
                gameObjectManager.addTree(treeObstacleModel);
            }

            this.aiTankModels = new java.util.ArrayList<>();
            this.aiTankGraphicsList = new java.util.ArrayList<>();
            generateRandomAITanks(3);

            if (!this.treeGraphicsList.isEmpty()) {
                treeObstacleGraphics = this.treeGraphicsList.get(0);
                treeObstacleModel = this.treeModels.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to original implementation if there's an error
            if (batch == null) {
                batch = new SpriteBatch();
            }
            if (random == null) {
                random = new Random();
            }
            if (inputHandler == null) {
                inputHandler = new InputHandler();
            }
            level = new Level("level.tmx", batch);
            tileMovement = new TileMovement(level.getGroundLayer(), Interpolation.smooth);
            playerTankModel = new TankModel(1, 1);
            TankGraphics fallbackTankGraphics = new TankGraphics("images/tank_blue.png", level.getGroundLayer(), playerTankModel);
            playerTankGraphics = new HealthBarDecorator(fallbackTankGraphics, playerTankModel);
            healthBarDecorators.add((HealthBarDecorator) playerTankGraphics);
            treeObstacleModel = new TreeModel(1, 3);
            treeObstacleGraphics = new TreeGraphics("images/greenTree.png", level.getGroundLayer(), treeObstacleModel);
            aiTankModels = new java.util.ArrayList<>();
            aiTankGraphicsList = new java.util.ArrayList<>();
            treeModels = new java.util.ArrayList<>();
            treeModels.add(treeObstacleModel);
            treeGraphicsList = new java.util.ArrayList<>();
            treeGraphicsList.add(treeObstacleGraphics);
            gameObjectManager = new GameObjectManager();
            gameObjectManager.addTree(treeObstacleModel);
            gameObjectManager.addTank(playerTankModel);
            collisionDetector = new SimpleCollisionDetector(gameObjectManager);
            graphicsObserver = new GraphicsObserver(gameObjectManager, batch, level.getGroundLayer());
            gameObjectManager.addObserver(graphicsObserver);
            gameObjectManager.notifyObservers();
        }
        registerInputHandlers();
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
            HealthBarDecorator aiTankGraphics = new HealthBarDecorator(aiTankBaseGraphics, aiTankModel);
            healthBarDecorators.add(aiTankGraphics);
            
            this.aiTankModels.add(aiTankModel);
            this.aiTankGraphicsList.add(aiTankGraphics);
            gameObjectManager.addTank(aiTankModel);
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
        for (TankModel aiTank : aiTankModels) {
            occupiedPositions.add(aiTank.getCoordinates());
        }
        
        // Add tree positions
        for (TreeModel tree : treeModels) {
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

        // Process input handlers
        inputHandler.processInput();

        // Handle player input
        Direction direction = InputHandler.getDirectionFromInput();
        if (!playerDestroyed && direction != null && isEqual(playerTankModel.getMovementProgress(), 1f)) {
            // Create and execute command for player tank movement
            Command playerMoveCommand = new MoveTankCommand(playerTankModel, direction, collisionDetector, 
                                                           level.getLevelWidth(), level.getLevelHeight());
            playerMoveCommand.execute();
        }

        // Handle AI tank actions (movement or shooting)
        handleAITankActions();

        // Update player tank position
        if (!playerDestroyed && playerTankGraphics != null) {
            tileMovement.moveRectangleBetweenTileCenters(
                    playerTankGraphics.getRectangle(),
                    playerTankModel.getCoordinates(),
                    playerTankModel.getDestinationCoordinates(),
                    playerTankModel.getMovementProgress());

            float newMovementProgress = continueProgress(playerTankModel.getMovementProgress(), deltaTime, MOVEMENT_SPEED);
            playerTankModel.setMovementProgress(newMovementProgress);
            playerTankModel.updatePosition();
        }

        // Update AI tank positions
        for (int i = 0; i < aiTankModels.size(); i++) {
            TankModel aiTankModel = aiTankModels.get(i);
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

        if (!playerDestroyed && playerTankGraphics != null) {
            drawTextureRegionUnscaled(batch, playerTankGraphics.getGraphics(), playerTankGraphics.getRectangle(),
                    playerTankModel.getRotation());
        }

        // render AI tanks
        for (int i = 0; i < aiTankGraphicsList.size(); i++) {
            GraphicsComponent aiTankGraphics = aiTankGraphicsList.get(i);
            TankModel aiTankModel = aiTankModels.get(i);
            drawTextureRegionUnscaled(batch, aiTankGraphics.getGraphics(), aiTankGraphics.getRectangle(), 
                                     aiTankModel.getRotation());
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
            if (!playerDestroyed && playerTankGraphics instanceof HealthBarDecorator) {
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

    private void handleAITankActions() {
        for (TankModel aiTankModel : new java.util.ArrayList<>(aiTankModels)) {
            if (aiTankController.shouldShoot()) {
                Command aiShootCommand = new ShootCommand(aiTankModel, gameObjectManager,
                        level.getLevelWidth(), level.getLevelHeight());
                aiShootCommand.execute();
            } else {
                aiTankController.moveAITank(aiTankModel);
            }
        }
    }
    
    /**
     * Updates bullet positions and handles collisions
     */
    private void updateBullets(float deltaTime) {
        for (BulletModel bullet : new java.util.ArrayList<>(gameObjectManager.getBullets())) {
            if (!bullet.isActive()) {
                gameObjectManager.removeBullet(bullet);
                continue;
            }

            if (checkBulletCollisions(bullet)) {
                gameObjectManager.removeBullet(bullet);
                continue;
            }

            float newMovementProgress = continueProgress(bullet.getMovementProgress(), deltaTime, BULLET_SPEED);
            bullet.setMovementProgress(newMovementProgress);

            if (isEqual(newMovementProgress, 1f)) {
                bullet.updatePosition();
                if (checkBulletCollisions(bullet)) {
                    gameObjectManager.removeBullet(bullet);
                    continue;
                }
                bullet.move();
            }
        }
    }
    
    /**
     * Checks for collisions with a bullet
     */
    private boolean checkBulletCollisions(BulletModel bullet) {
        GridPoint2 bulletPos = bullet.getCoordinates();
        if (bulletPos.x < 0 || bulletPos.x >= level.getLevelWidth()
                || bulletPos.y < 0 || bulletPos.y >= level.getLevelHeight()) {
            bullet.setActive(false);
            return true;
        }

        for (TreeModel tree : gameObjectManager.getTrees()) {
            if (tree.getCoordinates().equals(bulletPos)) {
                bullet.setActive(false);
                return true;
            }
        }

        for (BulletModel other : new java.util.ArrayList<>(gameObjectManager.getBullets())) {
            if (other == bullet) {
                continue;
            }
            if (other.getCoordinates().equals(bulletPos)) {
                other.setActive(false);
                gameObjectManager.removeBullet(other);
                bullet.setActive(false);
                return true;
            }
        }

        for (TankModel tank : new java.util.ArrayList<>(gameObjectManager.getTanks())) {
            if (tank == bullet.getOwner()) {
                continue;
            }
            if (tank.getCoordinates().equals(bulletPos)) {
                applyDamageToTank(tank, bullet.getDamage());
                bullet.setActive(false);
                return true;
            }
        }

        return false;
    }

    private void applyDamageToTank(TankModel tank, int damage) {
        tank.takeDamage(damage);
        if (tank.getHealth() <= 0) {
            removeTank(tank);
        }
    }

    private void removeTank(TankModel tank) {
        gameObjectManager.removeTank(tank);

        int aiIndex = aiTankModels.indexOf(tank);
        if (aiIndex >= 0) {
            aiTankModels.remove(aiIndex);
            GraphicsComponent aiGraphics = aiTankGraphicsList.remove(aiIndex);
            if (aiGraphics instanceof HealthBarDecorator) {
                healthBarDecorators.remove(aiGraphics);
            }
            aiGraphics.dispose();
            return;
        }

        if (tank == playerTankModel) {
            playerDestroyed = true;
            if (playerTankGraphics instanceof HealthBarDecorator) {
                healthBarDecorators.remove(playerTankGraphics);
            }
            playerTankGraphics.dispose();
            playerTankGraphics = null;
        }
    }

    private void registerInputHandlers() {
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
                if (!playerDestroyed) {
                    Command playerShootCommand = new ShootCommand(playerTankModel, gameObjectManager,
                            level.getLevelWidth(), level.getLevelHeight());
                    playerShootCommand.execute();
                }
                return true;
            }
        });
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
        
        if (playerTankGraphics != null) {
            playerTankGraphics.dispose();
        }
        if (level != null) {
            level.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
