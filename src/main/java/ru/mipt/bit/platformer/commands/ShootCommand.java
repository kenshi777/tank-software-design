package ru.mipt.bit.platformer.commands;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.TankModel;

/**
 * Concrete command for shooting a bullet from a tank
 */
public class ShootCommand implements Command {
    private final TankModel tank;
    private final GameObjectManager gameObjectManager;
    private final int levelWidth;
    private final int levelHeight;

    public ShootCommand(TankModel tank, GameObjectManager gameObjectManager, int levelWidth, int levelHeight) {
        this.tank = tank;
        this.gameObjectManager = gameObjectManager;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }

    @Override
    public void execute() {
        // Create a new bullet at the tank's position, moving in the tank's direction
        Direction tankDirection = Direction.fromRotation(tank.getRotation());
        GridPoint2 bulletStartPos = tankDirection.applyTo(tank.getCoordinates());
        
        // Check boundaries for bullet start position
        if (bulletStartPos.x < 0 || bulletStartPos.x >= levelWidth || 
            bulletStartPos.y < 0 || bulletStartPos.y >= levelHeight) {
            return;
        }

        BulletModel bullet = new BulletModel(bulletStartPos.x, bulletStartPos.y, tankDirection, 25);
        gameObjectManager.addBullet(bullet);
    }
}
