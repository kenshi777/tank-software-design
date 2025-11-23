package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Gdx;
import ru.mipt.bit.platformer.model.Direction;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;

public class InputHandler {
    private final Map<Integer, ButtonHandler> buttonHandlers;

    public InputHandler() {
        this.buttonHandlers = new HashMap<>();
    }

    /**
     * Registers a handler for a specific key
     * @param keyCode the key code to handle
     * @param handler the handler to register
     */
    public void registerHandler(int keyCode, ButtonHandler handler) {
        buttonHandlers.put(keyCode, handler);
    }

    /**
     * Processes all registered button handlers for currently pressed keys
     */
    public void processInput() {
        for (Map.Entry<Integer, ButtonHandler> entry : buttonHandlers.entrySet()) {
            if (Gdx.input.isKeyJustPressed(entry.getKey())) {
                entry.getValue().handle();
            }
        }
    }

    /**
     * Gets direction from arrow keys or WASD
     * @return the direction or null if no direction key is pressed
     */
    public static Direction getDirectionFromInput() {
        if (Gdx.input.isKeyPressed(UP) || Gdx.input.isKeyPressed(W)) {
            return Direction.UP;
        }
        if (Gdx.input.isKeyPressed(LEFT) || Gdx.input.isKeyPressed(A)) {
            return Direction.LEFT;
        }
        if (Gdx.input.isKeyPressed(DOWN) || Gdx.input.isKeyPressed(S)) {
            return Direction.DOWN;
        }
        if (Gdx.input.isKeyPressed(RIGHT) || Gdx.input.isKeyPressed(D)) {
            return Direction.RIGHT;
        }
        return null;
    }
}
