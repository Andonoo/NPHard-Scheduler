package io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputTest {

    public static final String[] NO_INPUT = {};
    public static final String[] ONE_INPUT = {"./src/test/resources/T2.dot"};
    public static final String[] INVALID_FILE_INPUT = {"./src/test/resources/T2", "2"};
    public static final String[] NON_EXISTENT_FILE_INPUT = {"./src/test/resources/T15.dot", "2"};
    public static final String[] NON_INTEGER_INPUT = {"./src/test/resources/T2.dot", "a"};
    public static final String[] NEGATIVE_INTEGER_INPUT = {"./src/test/resources/T2.dot", "-2"};

    public static final String INVALID_INPUT_MESSAGE = "Please enter a valid input file and number of processors";
    public static final String INVALID_FILE_MESSAGE = "Please enter a valid input file (.dot file)";
    public static final String NON_EXISTENT_FILE_MESSAGE = "Please enter a valid input file that exists";
    public static final String NON_INTEGER_MESSAGE = "Please enter an integer for the number of processors to be used";
    public static final String NEGATIVE_INTEGER_MESSAGE = "Please enter a valid number for the number of processors (at least 1)";

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
