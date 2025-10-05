package ru.mipt.bit.platformer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreeModelTest {
    private TreeModel treeModel;

    @BeforeEach
    void setUp() {
        treeModel = new TreeModel(2, 3);
    }

    @Test
    void testInitialPosition() {
        assertEquals(2, treeModel.getCoordinates().x);
        assertEquals(3, treeModel.getCoordinates().y);
    }

    @Test
    void testDifferentPositions() {
        TreeModel tree1 = new TreeModel(0, 0);
        assertEquals(0, tree1.getCoordinates().x);
        assertEquals(0, tree1.getCoordinates().y);

        TreeModel tree2 = new TreeModel(5, 7);
        assertEquals(5, tree2.getCoordinates().x);
        assertEquals(7, tree2.getCoordinates().y);
    }
}
