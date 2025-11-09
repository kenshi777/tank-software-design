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

class MoveTankCommandSampleLevelTest {
    @Mock
    private CollisionDetector collisionDetector;

    private TankModel tankModel;
    private MoveTankCommand moveTankCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtTopBoundaryOfSampleLevel() {
        // Place tank at top boundary of a 10x6 level (y=5)
        tankModel = new TankModel(1, 5);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.UP, collisionDetector, 10, 6);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(5, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testExecuteDoesNotMoveWhenAtBottomBoundaryOfSampleLevel() {
        // Place tank at bottom boundary of a 10x6 level (y=0)
        tankModel = new TankModel(1, 0);
        moveTankCommand = new MoveTankCommand(tankModel, Direction.DOWN, collisionDetector, 10, 6);
        
        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);
        
        moveTankCommand.execute();
        
        // Verify that the tank hasn't moved (boundary check prevented movement)
        assertEquals(1f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(0, tankModel.getDestinationCoordinates().y);
    }
}
