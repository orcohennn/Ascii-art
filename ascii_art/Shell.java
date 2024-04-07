package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exception.EmptySetException;
import exception.ResolutionException;
import image.Image;
import image.ImageProcessing;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.*;

/**
 * Shell class,  represents the command-line interface for the ASCII Art Generator program.
 * It allows users to interactively configure and generate ASCII art based on various parameters.
 */
public class Shell {
    /**
     * ASCII character constants
     */
    private static final int FIRST_ASCII_NUMBER = 48;

    /**
     * Minimum ASCII value used in the default character set.
     */
    private static final char MIN_ASCII_VALUE = 32;

    /**
     * Maximum ASCII value used in the default character set.
     */
    private static final char MAX_ASCII_VALUE = 126;

    /**
     * ASCII value representing a space character.
     */
    private static final char SPACE_ASCII_VALUE = ' ';

    /**
     * Separator character used in character ranges.
     */
    private static final char CHARS_SEPARATOR = '-';

    /**
     * Default parameters
     */
    private static final int DEFAULT_RESOLUTION = 128;

    /**
     * Size of the default character set.
     */
    private static final int DEFAULT_CHARSET_SIZE = 10;
    /**
     * Represent dot string.
     */
    private static final String DOT = ".";
    /**
     * Default file path for the image to be loaded.
     */
    private static final String DEFAULT_IMAGE_PATH = "cat.jpeg";

    /**
     * Default font used in HTML output.
     */
    private static final String DEFAULT_HTML_FONT = "Courier New";

    /**
     * Command-related constants
     */
    private static final String EXIT_COMMAND = "exit";

    /**
     * Command to run the ASCII art generation process.
     */
    private static final String RUN_ASCII_COMMAND = "asciiArt";

    /**
     * Command to print the current character set.
     */
    private static final String PRINT_CHARS_COMMAND = "chars";

    /**
     * Command to add characters to the character set.
     */
    private static final String ADD_CHARS_COMMAND = "add";

    /**
     * Command to remove characters from the character set.
     */
    private static final String REMOVE_CHARS_COMMAND = "remove";

    /**
     * Command to adjust the resolution of the ASCII art.
     */
    private static final String RESOLUTION_COMMAND = "res";

    /**
     * Command to add all ASCII characters to the character set.
     */
    private static final String ALL_COMMAND = "all";

    /**
     * Command to add the space character to the character set.
     */
    private static final String SPACE_COMMAND = "space";

    /**
     * Command to increase the resolution of the ASCII art.
     */
    private static final String RESOLUTION_UP_COMMAND = "up";

    /**
     * Command to decrease the resolution of the ASCII art.
     */
    private static final String RESOLUTION_DOWN_COMMAND = "down";

    /**
     * Command to specify the image to be used.
     */
    private static final String IMAGE_COMMAND = "image";

    /**
     * Command to specify the output format.
     */
    private static final String OUTPUT_COMMAND = "output";

    /**
     * Command to specify HTML output format.
     */
    private static final String HTML_OUTPUT_COMMAND = "html";

    /**
     * Command to specify console output format.
     */
    private static final String CONSOLE_OUTPUT_COMMAND = "console";

    /**
     * Command error messages
     */
    private static final String INVALID_COMMAND_ERR_MSG = "Did not execute due to incorrect command.";

    /**
     * Error message displayed when there is a problem loading the default image.
     */
    private static final String LOAD_IMAGE_ERR_MSG = "Did not execute due to problem with image file.";

    /**
     * Error message displayed when there is an issue during initialization.
     */
    private static final String INITIALIZATION_ERR_MSG =
            "Can not start running due to problem with initialization.";

    /**
     * Error message displayed when there is an issue with the output command.
     */
    private static final String OUTPUT_COMMAND_ERR_MSG =
            "Did not change output method due to incorrect format.";

    /**
     * Error message displayed when resolution boundaries are exceeded.
     */
    private static final String RESOLUTION_BOUNDARIES_ERR_COMMAND =
            "Did not change resolution due to exceeding boundaries.";

    /**
     * Error message displayed when there is an incorrect usage of the resolution command.
     */
    private static final String BAD_USE_RESOLUTION_COMMAND_ERR_MSG =
            "Did not change resolution due to incorrect format.";

    /**
     * Error message displayed when there is an issue with the add command.
     */
    private static final String ADD_COMMAND_ERR_MSG = "Did not add due to incorrect format.";

    /**
     * Error message displayed when there is an issue with the remove command.
     */
    private static final String REMOVE_COMMAND_ERR_MSG = "Did not remove due to incorrect format.";

    /**
     * Error message displayed when the character set is empty.
     */
    private static final String EMPTY_SET_ERR_MSG = "Did not execute. Charset is empty.";

    /**
     * Command output messages
     */
    private static final String RESOLUTIONS_SET_MSG = "Resolution set to ";

    /**
     * Prompt message for user input.
     */
    private static final String USER_WAIT_COMMAND = ">>> ";

    /**
     * Default file path for HTML output.
     */
    private static final String HTML_OUTPUT_PATH = "out.html";

    /**
     * Command related constants
     */
    private static final String SPACE = " ";

    /**
     * Command-related index and length constants
     */
    private static final int MAX_COMMAND_LENGTH = 2;

    /**
     * Maximum length of a command.
     */
    private static final int UPDATES_COMMAND_MAX_LENGTH = 3;

    /**
     * Index of the character separator in a command.
     */
    private static final int CHARS_SEPARATOR_IND = 1;

    /**
     * Length of a character update command.
     */
    private static final int CHAR_UPDATE_COMMAND_LENGTH = 1;

    /**
     * Index of the start range in a command.
     */
    private static final int START_RANGE_IND = 0;

    /**
     * Index of the end range in a command.
     */
    private static final int END_RANGE_IND = 2;

    /**
     * Index of the first command in a command array.
     */
    private static final int FIRST_COMMAND_IND = 0;

    /**
     * Index of the second command in a command array.
     */
    private static final int SECOND_COMMAND_IND = 1;

    /**
     * Resolution adjustment factors
     */
    private static final int RESOLUTION_UP_FACTOR = 2;

    /**
     * Factor for decreasing resolution.
     */
    private static final double RESOLUTION_DOWN_FACTOR = 0.5;


    private final HashSet<Character> charsHashSet;
    private int wrappedImageHeight;
    private int wrappedImageWidth;
    private int resolution;
    private final SubImgCharMatcher matcher;
    private Image image;
    private AsciiOutput output;
    private boolean parametersChanged;
    private AsciiArtAlgorithm asciiArtAlgorithm;

    /**
     * Constructor.
     * Initializes the default parameters including resolution, character set, output format, matcher,
     * and ASCII art algorithm. Also loads the default image.
     *
     * @throws IOException If an error occurs during initialization, such as loading the default image.
     */
    public Shell() throws IOException {
        this.resolution = DEFAULT_RESOLUTION;
        this.charsHashSet = new HashSet<>();
        char[] defaultCharSet = initDefaultCharset();
        this.output = new ConsoleAsciiOutput();
        this.matcher = new SubImgCharMatcher(defaultCharSet);
        this.image = new Image(DEFAULT_IMAGE_PATH);
        this.wrappedImageHeight = ImageProcessing.closestPowerOfTwo(this.image.getHeight());
        this.wrappedImageWidth = ImageProcessing.closestPowerOfTwo(this.image.getWidth());
        this.parametersChanged = false;
        this.asciiArtAlgorithm = new AsciiArtAlgorithm(this.image, this.resolution, this.matcher);
    }


    /**
     * This function runs the interactive command-line interface for configuring and generating ASCII art.
     * Reads user commands from the console until the exit command is entered.
     * Handles user input errors and exceptions by printing error messages.
     */
    public void run() {
        String userCommand = "";
        while (!userCommand.equals(EXIT_COMMAND)) {
            System.out.print(USER_WAIT_COMMAND);
            userCommand = KeyboardInput.readLine();
            try {
                commandHandler(userCommand);
            } catch (IOException | EmptySetException | ResolutionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /*
     * Initializes the default character set.
     * Adds characters from the first ASCII number up to the specified size to the character set.
     *
     * @return The initialized default character set as an array of characters.
     */
    private char[] initDefaultCharset() {
        char[] charset = new char[DEFAULT_CHARSET_SIZE];
        for (int i = 0; i < DEFAULT_CHARSET_SIZE; i++) {
            char charToAdd = (char) (i + FIRST_ASCII_NUMBER);
            this.charsHashSet.add(charToAdd);
            charset[i] = charToAdd;
        }
        return charset;
    }

    /*
     * Handles user commands for configuring and running the ASCII art generation process.
     *
     * @param userCommand The user command to be processed.
     * @throws IOException       If an I/O error occurs during command processing.
     * @throws EmptySetException If the character set is empty when required.
     * @throws ResolutionException If an error occurs during resolution adjustment.
     */
    private void commandHandler(String userCommand) throws IOException, EmptySetException,
            ResolutionException {
        String[] splitArr = userCommand.split(SPACE);
        if (splitArr.length > MAX_COMMAND_LENGTH) {
            throw new IOException(INVALID_COMMAND_ERR_MSG);
        }
        String firstCommand = splitArr[FIRST_COMMAND_IND];
        if (userCommand.equals(EXIT_COMMAND)) {
            return;
        }
        if (userCommand.equals(PRINT_CHARS_COMMAND)) {
            printChars();
        } else if (splitArr.length == MAX_COMMAND_LENGTH) {
            if (firstCommand.equals(ADD_CHARS_COMMAND)) {
                addRemoveCommandHandler(splitArr[SECOND_COMMAND_IND], true);
            } else if (firstCommand.equals(REMOVE_CHARS_COMMAND)) {
                addRemoveCommandHandler(splitArr[SECOND_COMMAND_IND], false);
            } else if (firstCommand.equals(RESOLUTION_COMMAND)) {
                resolutionCommandHandler(splitArr[SECOND_COMMAND_IND]);
            } else if (firstCommand.equals(IMAGE_COMMAND)) {
                imageCommandHandler(splitArr[SECOND_COMMAND_IND]);
            } else if (firstCommand.equals(OUTPUT_COMMAND)) {
                outputCommandHandler(splitArr[SECOND_COMMAND_IND]);
            } else {
                throw new IOException(INVALID_COMMAND_ERR_MSG);
            }
        } else if (firstCommand.equals(RUN_ASCII_COMMAND)) {
            runAsciiAlgorithm();
        } else {
            throw new IOException(INVALID_COMMAND_ERR_MSG);
        }
    }

    /*
     * Runs the ASCII art algorithm to generate ASCII art based on the configured parameters.
     * Throws an EmptySetException if the character set is empty.
     *
     * @throws EmptySetException If the character set is empty.
     */
    private void runAsciiAlgorithm() throws EmptySetException {
        if (this.charsHashSet.isEmpty()) {
            throw new EmptySetException(EMPTY_SET_ERR_MSG);
        }
        if (this.parametersChanged) {
            this.asciiArtAlgorithm = new AsciiArtAlgorithm(this.image, this.resolution, this.matcher);
        }
        if (this.output instanceof ConsoleAsciiOutput) {
            System.out.print(USER_WAIT_COMMAND);
        }
        this.output.out(this.asciiArtAlgorithm.run());
    }

    /*
     * Handles setting the output type for the generated ASCII art.
     * Throws an IOException if the specified output type is invalid.
     *
     * @param outputType The type of output to be used (e.g., HTML or console).
     * @throws IOException If the specified output type is invalid.
     */
    private void outputCommandHandler(String outputType) throws IOException {
        if (outputType.equals(HTML_OUTPUT_COMMAND)) {
            this.output = new HtmlAsciiOutput(HTML_OUTPUT_PATH, DEFAULT_HTML_FONT);
        } else if (outputType.equals(CONSOLE_OUTPUT_COMMAND)) {
            this.output = new ConsoleAsciiOutput();
        } else {
            throw new IOException(OUTPUT_COMMAND_ERR_MSG);
        }
    }

    /*
     * Handles loading a new image from the specified file path.
     * Throws an IOException if an error occurs during image loading.
     *
     * @param imagePath The file path of the image to load.
     * @throws IOException If an error occurs during image loading.
     */
    private void imageCommandHandler(String imagePath) throws IOException {
        try {
            this.image = new Image(imagePath);
        } catch (IOException e) {
            throw new IOException(LOAD_IMAGE_ERR_MSG);
        }
        this.parametersChanged = true;
        this.wrappedImageHeight = ImageProcessing.closestPowerOfTwo(this.image.getHeight());
        this.wrappedImageWidth = ImageProcessing.closestPowerOfTwo(this.image.getWidth());
    }

    /*
     * Handles resolution adjustment commands.
     * Throws a ResolutionException if the resolution boundaries are violated or if the command is invalid.
     *
     * @param command The resolution adjustment command.
     * @throws ResolutionException If the resolution boundaries are violated or if the command is invalid.
     */
    private void resolutionCommandHandler(String command) throws ResolutionException {
        int minCharsInRow = Math.max(1, this.wrappedImageWidth / this.wrappedImageHeight);
        int maxCharsInRow = this.wrappedImageWidth;
        if (command.equals(RESOLUTION_UP_COMMAND)) {
            if (this.resolution * RESOLUTION_UP_FACTOR > maxCharsInRow) {
                throw new ResolutionException(RESOLUTION_BOUNDARIES_ERR_COMMAND);
            } else {
                this.resolution *= RESOLUTION_UP_FACTOR;
                this.parametersChanged = true;
                System.out.println(RESOLUTIONS_SET_MSG + this.resolution + DOT);
            }
        } else if (command.equals(RESOLUTION_DOWN_COMMAND)) {
            if (this.resolution * RESOLUTION_DOWN_FACTOR < minCharsInRow) {
                throw new ResolutionException(RESOLUTION_BOUNDARIES_ERR_COMMAND);
            } else {
                this.resolution *= RESOLUTION_DOWN_FACTOR;
                this.parametersChanged = true;
                System.out.println(RESOLUTIONS_SET_MSG + this.resolution + DOT);
            }
        } else {
            throw new ResolutionException(BAD_USE_RESOLUTION_COMMAND_ERR_MSG);
        }
    }

    /*
     * Handles adding or removing characters based on the provided command.
     * Throws an IOException if the command is invalid or if an error occurs during processing.
     *
     * @param command The command specifying characters to add/remove.
     * @param toAdd   Indicates whether to add (true) or remove (false) characters.
     * @throws IOException If the command is invalid or an error occurs during processing.
     */
    private void addRemoveCommandHandler(String command, boolean toAdd) throws IOException {
        if (command.equals(ALL_COMMAND)) {
            addRemoveCharsByRange(MIN_ASCII_VALUE, MAX_ASCII_VALUE, toAdd);
        } else if (command.equals(SPACE_COMMAND)) {
            addRemoveCharsByRange(SPACE_ASCII_VALUE, SPACE_ASCII_VALUE, toAdd);
        } else if (command.length() ==
                UPDATES_COMMAND_MAX_LENGTH && command.charAt(CHARS_SEPARATOR_IND) == CHARS_SEPARATOR) {
            addRemoveCharsByRange(command.charAt(START_RANGE_IND), command.charAt(END_RANGE_IND), toAdd);
        } else if (command.length() == CHAR_UPDATE_COMMAND_LENGTH) {
            addRemoveCharsByRange(command.charAt(START_RANGE_IND), command.charAt(START_RANGE_IND), toAdd);
        } else {
            if (toAdd) {
                throw new IOException(ADD_COMMAND_ERR_MSG);
            } else {
                throw new IOException(REMOVE_COMMAND_ERR_MSG);
            }
        }
    }

    /*
     * Adds or removes characters within a specified range to/from the charsHashSet and matcher.
     *
     * @param firstChar  The first character of the range.
     * @param secondChar The second character of the range.
     * @param toAdd      Indicates whether to add (true) or remove (false) characters.
     */
    private void addRemoveCharsByRange(char firstChar, char secondChar, boolean toAdd) {
        int startingChar = Math.min(firstChar, secondChar);
        int endingChar = Math.max(firstChar, secondChar);
        for (int i = startingChar; i <= endingChar; i++) {
            if (toAdd) {
                this.charsHashSet.add((char) i);
                this.matcher.addChar((char) i);
            } else {
                this.charsHashSet.remove((char) i);
                this.matcher.removeChar((char) i);
            }
        }
    }

    /*
     * Prints the characters stored in the charsHashSet in sorted order.
     * Each character is separated by a space, followed by a newline.
     */
    private void printChars() {
        ArrayList<Character> charsArray = new ArrayList<>(this.charsHashSet);
        Collections.sort(charsArray);
        for (char c : charsArray) {
            System.out.print(c + SPACE);
        }
        System.out.println();
    }

    /**
     * Initializes a Shell object and runs it.
     * Handles IOException by printing an error message.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        Shell shell;
        try {
            shell = new Shell();
        } catch (IOException e) {
            System.out.println(INITIALIZATION_ERR_MSG);
            return;
        }
        shell.run();
    }
}
