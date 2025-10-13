package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.GameObjectModel;

public interface CollisionDetector {
    boolean isCollision(GameObjectModel movingObject, GridPoint2 newDestination);
}
