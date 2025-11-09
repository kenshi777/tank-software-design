package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.commands.Command;
import ru.mipt.bit.platformer.commands.MoveTankCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.util.CollisionDetector;
import com.badlogic.gdx.math.MathUtils;

import java.util.Random;

/**
 * Controller for AI tanks that handles their movement logic
 */
public class AITankController {
    private final Random random;
    private final CollisionDetector collisionDetector;
    private final int levelWidth;
    private final int levelHeight;

    public AITankController(CollisionDetector collisionDetector, int levelWidth, int levelHeight) {
        this.random = new Random();
        this.collisionDetector = collisionDetector;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }

    /**
     * Moves an AI tank in a random valid direction
     * @param aiTankModel The AI tank to move
     */
    public void moveAITank(GameObjectModel aiTankModel) {
        // Only move if the tank is not already moving
        if (MathUtils.isEqual(aiTankModel.getMovementProgress(), 1f)) {
            // Choose a random direction
            Direction[] directions = Direction.values();
            Direction randomDirection = directions[random.nextInt(directions.length)];
            
            // Create and execute command for AI tank movement
            Command aiMoveCommand = new MoveTankCommand(aiTankModel, randomDirection, collisionDetector, 
                                                       levelWidth, levelHeight);
            aiMoveCommand.execute();
        }
    }
}
