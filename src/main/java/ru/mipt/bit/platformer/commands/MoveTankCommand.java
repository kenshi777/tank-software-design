package ru.mipt.bit.platformer.commands;

import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.util.CollisionDetector;
import com.badlogic.gdx.math.GridPoint2;

/**
 * Concrete command for moving a tank in a specific direction
 */
public class MoveTankCommand implements Command {
    private final GameObjectModel tank;
    private final Direction direction;
    private final CollisionDetector collisionDetector;
    private final int levelWidth;
    private final int levelHeight;

    public MoveTankCommand(GameObjectModel tank, Direction direction, 
                          CollisionDetector collisionDetector, int levelWidth, int levelHeight) {
        this.tank = tank;
        this.direction = direction;
        this.collisionDetector = collisionDetector;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }

    @Override
    public void execute() {
        // Check if tank is already moving
        if (tank.getMovementProgress() != 1f) {
            return;
        }

        // Calculate new destination
        GridPoint2 newDestination = direction.applyTo(tank.getCoordinates());

        // Check boundaries
        if (newDestination.x < 0 || newDestination.x >= levelWidth || 
            newDestination.y < 0 || newDestination.y >= levelHeight) {
            return;
        }

        // Check for collisions with obstacles
        if (collisionDetector.isCollision(tank, newDestination)) {
            return;
        }

        // Move the tank
        tank.move(direction);
    }
}
