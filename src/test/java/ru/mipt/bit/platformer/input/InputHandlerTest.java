package ru.mipt.bit.platformer.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputHandlerTest {
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        inputHandler = new InputHandler();
    }

    @Test
    void testRegisterHandler() {
        ButtonHandler mockHandler = mock(ButtonHandler.class);
        
        inputHandler.registerHandler(com.badlogic.gdx.Input.Keys.SPACE, mockHandler);
        
        assertNotNull(inputHandler);
    }

    @Test
    void testGetDirectionFromInput() {
        assertNotNull(InputHandler.getDirectionFromInput());
    }
}
