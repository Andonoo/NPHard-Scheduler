package se306.io;

import io.CommandLineException;
import io.InputHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static se306.io.InputTestConstants.*;


public class InputTest {

    @Test
    public void testNoInput() {
        try {
            InputHandler inputHandler = new InputHandler(NO_INPUT);
            fail(INVALID_INPUT_FAIL);
        } catch (CommandLineException e) {
            assertEquals(INVALID_INPUT_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testOneInput() {
        try {
            InputHandler inputHandler = new InputHandler(ONE_INPUT);
            fail(INVALID_INPUT_FAIL);
        } catch (CommandLineException e) {
            assertEquals(INVALID_INPUT_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testInvalidDotFile() {
        try {
            InputHandler inputHandler = new InputHandler(INVALID_FILE_INPUT);
            fail(INVALID_FILE_FAIL);
        } catch (CommandLineException e) {
            assertEquals(INVALID_FILE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNonExistentFile() {
        try {
            InputHandler inputHandler = new InputHandler(NON_EXISTENT_FILE_INPUT);
            fail(NON_EXISTENT_FILE_FAIL);
        } catch (CommandLineException e) {
            assertEquals(NON_EXISTENT_FILE_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNonInteger() {
        try {
            InputHandler inputHandler = new InputHandler(NON_INTEGER_INPUT);
            fail(NON_INTEGER_FAIL);
        } catch (CommandLineException e) {
            assertEquals(NON_INTEGER_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testNegativeInteger() {
        try {
            InputHandler inputHandler = new InputHandler(NEGATIVE_INTEGER_INPUT);
            fail(NEGATIVE_INTEGER_FAIL);
        } catch (CommandLineException e) {
            assertEquals(NEGATIVE_INTEGER_MESSAGE, e.getMessage());
        }
    }
}
