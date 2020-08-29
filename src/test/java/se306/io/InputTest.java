package se306.io;

import io.CommandLineException;
import io.InputHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se306.io.InputTestConstants.*;


public class InputTest {

    @Test
    public void testNoInput() {
        try {
            InputHandler inputParser = new InputHandler(NO_INPUT);
        } catch (CommandLineException e) {
            assertEquals(INVALID_INPUT_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testOneInput() {
        try {
            InputHandler inputParser = new InputHandler(ONE_INPUT);
        } catch (CommandLineException e) {
            assertEquals(INVALID_INPUT_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testInvalidDotFile() {
        try {
            InputHandler inputParser = new InputHandler(INVALID_FILE_INPUT);
        } catch (CommandLineException e) {
            assertEquals(INVALID_FILE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNonExistentFile() {
        try {
            InputHandler inputParser = new InputHandler(NON_EXISTENT_FILE_INPUT);
        } catch (CommandLineException e) {
            assertEquals(NON_EXISTENT_FILE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNonInteger() {
        try {
            InputHandler inputParser = new InputHandler(NON_INTEGER_INPUT);
        } catch (CommandLineException e) {
            assertEquals(NON_INTEGER_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNegativeInteger() {
        try {
            InputHandler inputParser = new InputHandler(NEGATIVE_INTEGER_INPUT);
        } catch (CommandLineException e) {
            assertEquals(NEGATIVE_INTEGER_MESSAGE, e.getMessage());
        }
    }
}
