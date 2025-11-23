package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import ru.mipt.bit.platformer.model.TankModel;

public class HealthBarDecorator implements GraphicsComponent {
    private final GraphicsComponent wrappedComponent;
    private final TankModel tank;
    private final Texture barTexture;
    private boolean showHealthBar;

    public HealthBarDecorator(GraphicsComponent component, TankModel tank) {
        this.wrappedComponent = component;
        this.tank = tank;
        this.barTexture = createPixel();
        this.showHealthBar = false;
    }

    @Override
    public TextureRegion getGraphics() {
        return wrappedComponent.getGraphics();
    }

    @Override
    public Rectangle getRectangle() {
        return wrappedComponent.getRectangle();
    }

    @Override
    public void dispose() {
        wrappedComponent.dispose();
        barTexture.dispose();
    }

    public void setShowHealthBar(boolean show) {
        this.showHealthBar = show;
    }

    public boolean isShowHealthBar() {
        return showHealthBar;
    }

    public void renderHealthBar(Batch batch, Rectangle rectangle) {
        if (!showHealthBar) return;

        // Calculate health bar dimensions
        float barWidth = rectangle.width;
        float barHeight = 8f;
        float barX = rectangle.x;
        float barY = rectangle.y + rectangle.height + 4f; // Position above the tank

        // Calculate health percentage
        float healthPercentage = (float) tank.getHealth() / Math.max(1, tank.getMaxHealth());

        // Draw border (black)
        batch.setColor(Color.BLACK);
        batch.draw(barTexture, barX - 1, barY - 1, barWidth + 2, barHeight + 2);

        // Draw background (red)
        batch.setColor(new Color(0.35f, 0f, 0f, 0.8f));
        batch.draw(barTexture, barX, barY, barWidth, barHeight);

        // Draw foreground (green to yellow based on health percentage)
        if (healthPercentage > 0.7f) {
            batch.setColor(Color.GREEN);
        } else if (healthPercentage > 0.3f) {
            batch.setColor(Color.YELLOW);
        } else {
            batch.setColor(Color.RED);
        }
        batch.draw(barTexture, barX, barY, barWidth * healthPercentage, barHeight);

        // Reset color
        batch.setColor(Color.WHITE);
    }

    private Texture createPixel() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
