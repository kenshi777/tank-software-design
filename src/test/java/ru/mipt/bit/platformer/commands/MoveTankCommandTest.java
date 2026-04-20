package ru.mipt.bit.platformer.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.CollisionDetector;
import com.badlogic.gdx.math.GridPoint2;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveTankCommandTest {
    @Mock
    private CollisionDetector collisionDetector;

    private TankModel tankModel;
    private MoveTankCommand moveTankCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tankModel = new TankModel(1, 1);
    }

    @Test
    void testExecuteMovesTankInValidDirection() {
        moveTankCommand = new MoveTankCommand(tankModel, Direction.UP, collisionDetector, 10, 10);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank has started moving
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(2, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenAlreadyMoving() {
        moveTankCommand = new MoveTankCommand(tankModel, Direction.UP, collisionDetector, 10, 10);
        
        // Set tank to be in motion
        tankModel.setMovementProgress(0.5f);
        
        moveTankCommand.execute();
        
        // Verify that the tank's state hasn't changed
        assertEquals(0.5f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenOutOfBounds() {
        moveTankCommand = new MoveTankCommand(tankModel, Direction.DOWN, collisionDetector, 10, 10);
        
        // Move tank to edge of level
        tankModel = new TankModel(1, 0);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.DOWN, collisionDetector, 10, 10);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(0, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenCollisionDetected() {
        moveTankCommand = new MoveTankCommand(tankModel, Direction.UP, collisionDetector, 10, 10);
        
        // Mock collision detector to return true (collision detected)
        when(collisionDetector.isCollision(any(), any())).thenReturn(true);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }
}
