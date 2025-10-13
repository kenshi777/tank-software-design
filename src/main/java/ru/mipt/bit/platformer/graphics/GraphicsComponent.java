package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public interface GraphicsComponent {
    TextureRegion getGraphics();
    Rectangle getRectangle();
    void dispose();
}
