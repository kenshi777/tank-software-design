package ru.mipt.bit.platformer.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mipt.bit.platformer.model.GameObjectModel;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCollisionDetectorTest {
    @Mock
    private GameObjectModel obstacle;
    
    @Mock
    private GameObjectModel stationaryTank;
    
    @Mock
    private GameObjectModel movingTank;
    
    @Mock
    private GameObjectModel movingObject;

    private SimpleCollisionDetector collisionDetector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsCollisionWithObstacle() {
        // Setup
        when(obstacle.getCoordinates()).thenReturn(new GridPoint2(5, 5));
        collisionDetector = new SimpleCollisionDetector(Arrays.asList(obstacle), Collections.emptyList());
        
        // Test collision with obstacle
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(5, 5));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithStationaryTank() {
        // Setup
        when(stationaryTank.getCoordinates()).thenReturn(new GridPoint2(3, 3));
        when(stationaryTank.getMovementProgress()).thenReturn(1.0f); // Stationary tank
        collisionDetector = new SimpleCollisionDetector(Collections.emptyList(), Arrays.asList(stationaryTank, movingObject));
        
        // Test collision with stationary tank's position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(3, 3));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithMovingTankCurrentPosition() {
        // Setup
        when(movingTank.getCoordinates()).thenReturn(new GridPoint2(2, 2));
        when(movingTank.getDestinationCoordinates()).thenReturn(new GridPoint2(2, 3));
        when(movingTank.getMovementProgress()).thenReturn(0.5f); // Moving tank
        collisionDetector = new SimpleCollisionDetector(Collections.emptyList(), Arrays.asList(movingTank, movingObject));
        
        // Test collision with moving tank's current position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(2, 2));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithMovingTankDestination() {
        // Setup
        when(movingTank.getCoordinates()).thenReturn(new GridPoint2(2, 2));
        when(movingTank.getDestinationCoordinates()).thenReturn(new GridPoint2(2, 3));
        when(movingTank.getMovementProgress()).thenReturn(0.5f); // Moving tank
        collisionDetector = new SimpleCollisionDetector(Collections.emptyList(), Arrays.asList(movingTank, movingObject));
        
        // Test collision with moving tank's destination
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(2, 3));
        assertTrue(result);
    }

    @Test
    void testNoCollisionWithOwnPosition() {
        // Setup
        when(movingObject.getCoordinates()).thenReturn(new GridPoint2(1, 1));
        collisionDetector = new SimpleCollisionDetector(Collections.emptyList(), Arrays.asList(movingObject));
        
        // Test no collision with own position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(1, 1));
        assertFalse(result);
    }

    @Test
    void testNoCollisionWithEmptySpace() {
        // Setup
        when(obstacle.getCoordinates()).thenReturn(new GridPoint2(5, 5));
        when(stationaryTank.getCoordinates()).thenReturn(new GridPoint2(3, 3));
        when(stationaryTank.getMovementProgress()).thenReturn(1.0f);
        collisionDetector = new SimpleCollisionDetector(
            Arrays.asList(obstacle), 
            Arrays.asList(stationaryTank, movingObject)
        );
        
        // Test no collision with empty space
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(1, 1));
        assertFalse(result);
    }
}
