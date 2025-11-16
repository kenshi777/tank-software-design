package ru.mipt.bit.platformer.commands;

import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.CollisionDetector;
import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

/**
 * Concrete command for shooting a bullet from a tank
 */
public class ShootCommand implements Command {
    private final TankModel tank;
    private final List<BulletModel> bullets;
    private final CollisionDetector collisionDetector;
    private final int levelWidth;
    private final int levelHeight;

    public ShootCommand(TankModel tank, List<BulletModel> bullets,
                       CollisionDetector collisionDetector, int levelWidth, int levelHeight) {
        this.tank = tank;
        this.bullets = bullets;
        this.collisionDetector = collisionDetector;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }

    @Override
    public void execute() {
        // Check if tank is already moving - if so, don't shoot
        if (tank.getMovementProgress() != 1f) {
            return;
        }

        // Create a new bullet at the tank's position, moving in the tank's direction
        Direction tankDirection = Direction.fromRotation(tank.getRotation());
        GridPoint2 bulletStartPos = tankDirection.applyTo(tank.getCoordinates());
        
        // Check boundaries for bullet start position
        if (bulletStartPos.x < 0 || bulletStartPos.x >= levelWidth || 
            bulletStartPos.y < 0 || bulletStartPos.y >= levelHeight) {
            return;
        }

        // Check for collisions at the bullet start position (trees, other tanks)
        // We need to temporarily create a bullet to check for collisions
        BulletModel tempBullet = new BulletModel(bulletStartPos.x, bulletStartPos.y, tankDirection, 25);
        if (collisionDetector.isCollision(tempBullet, bulletStartPos)) {
            return;
        }

        // Create the actual bullet
        BulletModel bullet = new BulletModel(bulletStartPos.x, bulletStartPos.y, tankDirection, 25);
        bullets.add(bullet);
    }
}
