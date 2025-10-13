package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.GameObjectModel;

import java.util.List;

public class SimpleCollisionDetector implements CollisionDetector {
    private final List<GameObjectModel> obstacles;

    public SimpleCollisionDetector(List<GameObjectModel> obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public boolean isCollision(GameObjectModel movingObject, GridPoint2 newDestination) {
        // Check collision with all obstacles
        for (GameObjectModel obstacle : obstacles) {
            if (obstacle.getCoordinates().equals(newDestination)) {
                return true;
            }
        }
        return false;
    }
}
