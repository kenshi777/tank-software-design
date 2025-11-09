package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class HealthBarDecorator implements GraphicsComponent {
    private final GraphicsComponent wrappedComponent;
    private final int currentHealth;
    private final int maxHealth;
    private boolean showHealthBar;

    public HealthBarDecorator(GraphicsComponent component, int currentHealth, int maxHealth) {
        this.wrappedComponent = component;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
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
        float barHeight = 6f;
        float barX = rectangle.x;
        float barY = rectangle.y + rectangle.height + 2f; // Position above the tank

        // Calculate health percentage
        float healthPercentage = (float) currentHealth / maxHealth;

        // Draw border (black)
        batch.setColor(Color.BLACK);
        batch.draw(getGraphics(), barX - 1, barY - 1, barWidth + 2, barHeight + 2);

        // Draw background (red)
        batch.setColor(Color.RED);
        batch.draw(getGraphics(), barX, barY, barWidth, barHeight);

        // Draw foreground (green to yellow based on health percentage)
        if (healthPercentage > 0.7f) {
            batch.setColor(Color.GREEN);
        } else if (healthPercentage > 0.3f) {
            batch.setColor(Color.YELLOW);
        } else {
            batch.setColor(Color.RED);
        }
        batch.draw(getGraphics(), barX, barY, barWidth * healthPercentage, barHeight);

        // Reset color
        batch.setColor(Color.WHITE);
    }
}
