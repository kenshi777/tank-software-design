package ru.mipt.bit.platformer.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.CollisionDetector;

import java.lang.reflect.Field;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AITankControllerTest {
    @Mock
    private CollisionDetector collisionDetector;

    private AITankController aiTankController;
    private TankModel tankModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiTankController = new AITankController(collisionDetector, 10, 10);
        tankModel = new TankModel(1, 1);
    }

    @Test
    void testMoveAITankMovesTankWhenNotMoving() {
        // Mock the random to always return 0 (Direction.UP)
        try {
            Field randomField = AITankController.class.getDeclaredField("random");
            randomField.setAccessible(true);
            Random mockRandom = mock(Random.class);
            when(mockRandom.nextInt(anyInt())).thenReturn(0);
            randomField.set(aiTankController, mockRandom);
        } catch (Exception e) {
            fail("Failed to mock random field: " + e.getMessage());
        }

        // Mock collision detector to return false (no collision)
        when(collisionDetector.isCollision(any(), any())).thenReturn(false);

        aiTankController.moveAITank(tankModel);

        // Verify that the tank has started moving
        assertEquals(0f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(2, tankModel.getDestinationCoordinates().y);
    }

    @Test
    void testMoveAITankDoesNotMoveWhenAlreadyMoving() {
        // Set tank to be in motion
        tankModel.setMovementProgress(0.5f);

        aiTankController.moveAITank(tankModel);

        // Verify that the tank's state hasn't changed
        assertEquals(0.5f, tankModel.getMovementProgress());
        assertEquals(1, tankModel.getDestinationCoordinates().x);
        assertEquals(1, tankModel.getDestinationCoordinates().y);
    }
}
