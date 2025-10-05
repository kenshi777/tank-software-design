package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void testDirectionValues() {
        assertEquals(0, Direction.UP.getDx());
        assertEquals(1, Direction.UP.getDy());
        assertEquals(90f, Direction.UP.getRotation());

        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(-1, Direction.DOWN.getDy());
        assertEquals(-90f, Direction.DOWN.getRotation());

        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
        assertEquals(-180f, Direction.LEFT.getRotation());

        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
        assertEquals(0f, Direction.RIGHT.getRotation());
    }

    @Test
    void testApplyTo() {
        GridPoint2 point = new GridPoint2(2, 3);
        
        GridPoint2 upResult = Direction.UP.applyTo(point);
        assertEquals(2, upResult.x);
        assertEquals(4, upResult.y);

        GridPoint2 downResult = Direction.DOWN.applyTo(point);
        assertEquals(2, downResult.x);
        assertEquals(2, downResult.y);

        GridPoint2 leftResult = Direction.LEFT.applyTo(point);
        assertEquals(1, leftResult.x);
        assertEquals(3, leftResult.y);

        GridPoint2 rightResult = Direction.RIGHT.applyTo(point);
        assertEquals(3, rightResult.x);
        assertEquals(3, rightResult.y);
    }
}
