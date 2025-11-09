package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.GameObjectModel;

import java.util.List;

public class SimpleCollisionDetector implements CollisionDetector {
    private final List<GameObjectModel> obstacles;
    private final List<GameObjectModel> allTanks;

    public SimpleCollisionDetector(List<GameObjectModel> obstacles, List<GameObjectModel> allTanks) {
        this.obstacles = obstacles;
        this.allTanks = allTanks;
    }

    @Override
    public boolean isCollision(GameObjectModel movingObject, GridPoint2 newDestination) {
        // Check collision with all obstacles
        for (GameObjectModel obstacle : obstacles) {
            if (obstacle.getCoordinates().equals(newDestination)) {
                return true;
            }
        }
        
        // Check collision with all tanks (both current positions and movement paths)
        for (GameObjectModel tank : allTanks) {
            // Skip the moving tank itself
            if (tank == movingObject) {
                continue;
            }
            
            // Check if the destination collides with the tank's current position
            if (tank.getCoordinates().equals(newDestination)) {
                return true;
            }
            
            // Check if the destination collides with a moving tank's path
            // If a tank is moving, both its current position and destination are occupied
            if (tank.getMovementProgress() < 1.0f) {
                // Check if destination collides with the moving tank's destination
                if (tank.getDestinationCoordinates().equals(newDestination)) {
                    return true;
                }
                // Check if destination collides with the moving tank's current position
                if (tank.getCoordinates().equals(newDestination)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
