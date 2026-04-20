package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;

public class SimpleCollisionDetector implements CollisionDetector {
    private final GameObjectManager gameObjectManager;

    public SimpleCollisionDetector(GameObjectManager gameObjectManager) {
        this.gameObjectManager = gameObjectManager;
    }

    @Override
    public boolean isCollision(GameObjectModel movingObject, GridPoint2 newDestination) {
        for (TreeModel obstacle : gameObjectManager.getTrees()) {
            if (obstacle.getCoordinates().equals(newDestination)) {
                return true;
            }
        }

        for (TankModel tank : gameObjectManager.getTanks()) {
            if (tank == movingObject) {
                continue;
            }
            if (tank.getCoordinates().equals(newDestination)) {
                return true;
            }
            if (tank.getMovementProgress() < 1.0f && tank.getDestinationCoordinates().equals(newDestination)) {
                return true;
            }
        }

        for (BulletModel bullet : gameObjectManager.getBullets()) {
            if (bullet == movingObject) {
                continue;
            }
            if (movingObject instanceof TankModel && bullet.getOwner() == movingObject) {
                continue;
            }
            if (bullet.getCoordinates().equals(newDestination)) {
                return true;
            }
        }
        return false;
    }
}
