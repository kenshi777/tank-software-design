package ru.mipt.bit.platformer.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.CollisionDetector;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveTankCommandBoundaryTest {
    @Mock
    private CollisionDetector collisionDetector;

    private TankModel tankModel;
    private MoveTankCommand moveTankCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtLeftBoundary() {
        // Place tank at left boundary (x=0)
        tankModel = new TankModel(0, 1);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.LEFT, collisionDetector, 10, 10);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(0, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtRightBoundary() {
        // Place tank at right boundary (x=maxWidth-1)
        tankModel = new TankModel(9, 1);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.RIGHT, collisionDetector, 10, 10);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(9, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtBottomBoundary() {
        // Place tank at bottom boundary (y=0)
        tankModel = new TankModel(1, 0);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.DOWN, collisionDetector, 10, 10);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(0, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtTopBoundary() {
        // Place tank at top boundary (y=maxHeight-1)
        tankModel = new TankModel(1, 9);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.UP, collisionDetector, 10, 10);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(9, tankModel.getDestinationCoordinates().y);
    }
}
