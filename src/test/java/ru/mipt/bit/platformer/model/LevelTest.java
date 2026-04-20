package ru.mipt.bit.platformer.model;

import org.junit.jupiter.api.Test;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LevelTest {
    
    @Test
    void testLoadLevelFromFileCreatesCorrectObjects() {
        // Create a mock batch
        SpriteBatch batch = mock(SpriteBatch.class);
        
        try {
            // Load the sample level
            Level level = new Level("levels/sample_level.txt", batch, true);
            
            // Check level dimensions
            assertEquals(10, level.getLevelWidth());
            assertEquals(6, level.getLevelHeight());
            
            // Check player start position
            GridPoint2 playerPos = level.getPlayerStartPosition();
            assertNotNull(playerPos);
            assertEquals(5, playerPos.x);
            assertEquals(1, playerPos.y); // Row 4 in file becomes y=1 in game coords
            
            // Check tree positions
            List<GridPoint2> treePositions = level.getTreePositions();
            assertNotNull(treePositions);
            assertEquals(17, treePositions.size()); // Count of 'T' characters in sample_level.txt
            
            // Check some specific tree positions
            assertTrue(treePositions.contains(new GridPoint2(3, 5))); // First row, column 3
            assertTrue(treePositions.contains(new GridPoint2(6, 5))); // First row, column 6
            assertTrue(treePositions.contains(new GridPoint2(5, 1))); // Row with player, column 5 (should be player pos, but tree is also there)
            
        } catch (Exception e) {
            fail("Failed to load level: " + e.getMessage());
        }
    }
}
