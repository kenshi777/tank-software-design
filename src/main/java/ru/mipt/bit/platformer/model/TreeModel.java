package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TreeModel {
    private GridPoint2 coordinates;

    public TreeModel(int x, int y) {
        this.coordinates = new GridPoint2(x, y);
    }

    // Getters
    public GridPoint2 getCoordinates() {
        return coordinates;
    }
}
