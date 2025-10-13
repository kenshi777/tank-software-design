package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface GameObjectModel {
    GridPoint2 getCoordinates();
    GridPoint2 getDestinationCoordinates();
    float getMovementProgress();
    void setMovementProgress(float movementProgress);
    void updatePosition();
    void move(Direction direction);
}
