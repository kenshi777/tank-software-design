package ru.mipt.bit.platformer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TankModelTest {
    private TankModel tankModel;

    @BeforeEach
    void setUp() {
        tankModel = new TankModel(1, 1);
    }

    @Test
    void testInitialPosition() {
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(0f, tankModel.getRotation());
    }

    @Test
    void testMoveUp() {
        tankModel.move(Direction.UP);
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(2, tankModel.getDestinationCoordinates().y);
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(90f, tankModel.getRotation());
    }

    @Test
    void testMoveDown() {
        tankModel.move(Direction.DOWN);
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(0, tankModel.getDestinationCoordinates().y);
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(-90f, tankModel.getRotation());
    }

    @Test
    void testMoveLeft() {
        tankModel.move(Direction.LEFT);
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(0, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(-180f, tankModel.getRotation());
    }

    @Test
    void testMoveRight() {
        tankModel.move(Direction.RIGHT);
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(2, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(0f, tankModel.getRotation());
    }

    @Test
    void testCannotMoveWhileMoving() {
        tankModel.move(Direction.UP);
        tankModel.setMovementProgress(0.5f);
        
        tankModel.move(Direction.DOWN);
        
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(2, tankModel.getDestinationCoordinates().y);
        assertEquals(0.5f, tankModel.getMovementProgress());
        assertEquals(90f, tankModel.getRotation());
    }

    @Test
    void testUpdatePositionWhenNotMoving() {
        tankModel.updatePosition();
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(1, tankModel.getCoordinates().y);
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testUpdatePositionWhenFinishedMoving() {
        tankModel.move(Direction.UP);
        tankModel.setMovementProgress(1f);
        tankModel.updatePosition();
        
        assertEquals(1, tankModel.getCoordinates().x);
        assertEquals(2, tankModel.getCoordinates().y);
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(2, tankModel.getDestinationCoordinates().y);
        assertEquals(1f, tankModel.getMovementProgress());
    }

    @Test
    void testSetMovementProgress() {
        tankModel.setMovementProgress(0.5f);
        assertEquals(0.5f, tankModel.getMovementProgress());
        
        tankModel.setMovementProgress(0.8f);
        assertEquals(0.8f, tankModel.getMovementProgress());
    }
}
