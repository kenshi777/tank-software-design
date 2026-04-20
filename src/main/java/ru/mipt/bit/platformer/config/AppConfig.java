package ru.mipt.bit.platformer.config;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.ai.AITankController;
import ru.mipt.bit.platformer.graphics.GraphicsObserver;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.util.CollisionDetector;
import ru.mipt.bit.platformer.util.SimpleCollisionDetector;
import ru.mipt.bit.platformer.util.TileMovement;

import java.io.IOException;
import java.util.Random;

@Configuration
public class AppConfig {

    @Bean
    public Batch batch() {
        return new SpriteBatch();
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public InputHandler inputHandler() {
        return new InputHandler();
    }

    @Bean
    public Level level(Batch batch) throws IOException {
        return new Level("levels/sample_level.txt", batch, true);
    }

    @Bean
    public TileMovement tileMovement(Level level) {
        return new TileMovement(level.getGroundLayer(), Interpolation.smooth);
    }

    @Bean
    public GameObjectManager gameObjectManager() {
        return new GameObjectManager();
    }

    @Bean
    public CollisionDetector collisionDetector(GameObjectManager gameObjectManager) {
        return new SimpleCollisionDetector(gameObjectManager);
    }

    @Bean
    public AITankController aiTankController(CollisionDetector collisionDetector, Level level) {
        return new AITankController(collisionDetector, level.getLevelWidth(), level.getLevelHeight());
    }

    @Bean
    public GraphicsObserver graphicsObserver(GameObjectManager gameObjectManager, Batch batch, Level level) {
        GraphicsObserver observer = new GraphicsObserver(gameObjectManager, batch, level.getGroundLayer());
        gameObjectManager.addObserver(observer);
        return observer;
    }
}
