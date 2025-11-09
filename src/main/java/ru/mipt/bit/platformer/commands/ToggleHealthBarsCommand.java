package ru.mipt.bit.platformer.commands;

import ru.mipt.bit.platformer.graphics.HealthBarDecorator;

import java.util.List;

public class ToggleHealthBarsCommand implements Command {
    private final List<HealthBarDecorator> healthBarDecorators;

    public ToggleHealthBarsCommand(List<HealthBarDecorator> healthBarDecorators) {
        this.healthBarDecorators = healthBarDecorators;
    }

    @Override
    public void execute() {
        // Toggle the visibility of all health bars
        for (HealthBarDecorator decorator : healthBarDecorators) {
            decorator.setShowHealthBar(!decorator.isShowHealthBar());
        }
    }
}
