package ru.mipt.bit.platformer.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mipt.bit.platformer.model.GameObjectModel;
import ru.mipt.bit.platformer.model.GameObjectManager;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;
import com.badlogic.gdx.math.GridPoint2;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCollisionDetectorTest {
    @Mock
    private TreeModel obstacle;
    
    @Mock
    private TankModel stationaryTank;
    
    @Mock
    private TankModel movingTank;
    
    @Mock
    private GameObjectModel movingObject;

    private GameObjectManager gameObjectManager;
    private SimpleCollisionDetector collisionDetector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameObjectManager = new GameObjectManager();
    }

    @Test
    void testIsCollisionWithObstacle() {
        // Setup
        when(obstacle.getCoordinates()).thenReturn(new GridPoint2(5, 5));
        gameObjectManager.addTree((TreeModel) obstacle);
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test collision with obstacle
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(5, 5));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithStationaryTank() {
        // Setup
        TankModel stationary = (TankModel) stationaryTank;
        when(stationary.getCoordinates()).thenReturn(new GridPoint2(3, 3));
        when(stationary.getMovementProgress()).thenReturn(1.0f); // Stationary tank
        gameObjectManager.addTank(stationary);
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test collision with stationary tank's position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(3, 3));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithMovingTankCurrentPosition() {
        // Setup
        TankModel moving = (TankModel) movingTank;
        when(moving.getCoordinates()).thenReturn(new GridPoint2(2, 2));
        when(moving.getDestinationCoordinates()).thenReturn(new GridPoint2(2, 3));
        when(moving.getMovementProgress()).thenReturn(0.5f); // Moving tank
        gameObjectManager.addTank(moving);
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test collision with moving tank's current position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(2, 2));
        assertTrue(result);
    }

    @Test
    void testIsCollisionWithMovingTankDestination() {
        // Setup
        TankModel moving = (TankModel) movingTank;
        when(moving.getCoordinates()).thenReturn(new GridPoint2(2, 2));
        when(moving.getDestinationCoordinates()).thenReturn(new GridPoint2(2, 3));
        when(moving.getMovementProgress()).thenReturn(0.5f); // Moving tank
        gameObjectManager.addTank(moving);
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test collision with moving tank's destination
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(2, 3));
        assertTrue(result);
    }

    @Test
    void testNoCollisionWithOwnPosition() {
        // Setup
        when(movingObject.getCoordinates()).thenReturn(new GridPoint2(1, 1));
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test no collision with own position
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(1, 1));
        assertFalse(result);
    }

    @Test
    void testNoCollisionWithEmptySpace() {
        // Setup
        TankModel stationary = (TankModel) stationaryTank;
        when(obstacle.getCoordinates()).thenReturn(new GridPoint2(5, 5));
        when(stationary.getCoordinates()).thenReturn(new GridPoint2(3, 3));
        when(stationary.getMovementProgress()).thenReturn(1.0f);
        gameObjectManager.addTree((TreeModel) obstacle);
        gameObjectManager.addTank(stationary);
        collisionDetector = new SimpleCollisionDetector(gameObjectManager);
        
        // Test no collision with empty space
        boolean result = collisionDetector.isCollision(movingObject, new GridPoint2(1, 1));
        assertFalse(result);
    }
}
