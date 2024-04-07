package image_char_matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The SubImgCharMatcher class is responsible for matching characters with their corresponding image
 * brightness values.
 */
public class SubImgCharMatcher {
    /**
     * Represents the size of the character image (e.g., 16x16 pixels).
     */
    private static final double CHAR_IMAGE_SIZE = 16;

    /**
     * Index for the character brightness in the ArrayList.
     */
    private static final int CHAR_BRIGHTNESS_IND = 0;

    /**
     * Index for the normalized brightness in the ArrayList.
     */
    private static final int NORMALIZED_BRIGHTNESS_IND = 1;

    /**
     * Size of the ArrayList containing brightness values.
     */
    private static final int BRIGHTNESS_ARR_SIZE = 2;

    /**
     * Minimum pixel value in the RGB color space.
     */
    private static final double PIXEL_MIN_VALUE = 0;

    /**
     * Maximum pixel value in the RGB color space.
     */
    private static final double PIXEL_MAX_VALUE = 255;

    private final char[] charset;
    // Create hashmap that every element is- char:[charBrightness, normalizedBrightness]
    private final Map<Character, ArrayList<Double>> charBrightnessMap;
    private final Map<Character, Double> allBrightnessMap;
    private double minBrightness;
    private double maxBrightness;

    /**
     * Constructs a SubImgCharMatcher object with the given character set.
     *
     * @param charset The character set used for matching.
     */
    public SubImgCharMatcher(char[] charset) {
        this.charset = charset;
        this.charBrightnessMap = new HashMap<>();
        this.allBrightnessMap = new HashMap<>();
        initialCharBrightnessMap();
        this.maxBrightness = getMaxBrightness();
        this.minBrightness = getMinBrightness();
        updateNormalizedCharBrightness();
    }

    /**
     * Gets the character associated with the closest image brightness value.
     *
     * @param brightness The image brightness value to match.
     * @return The character associated with the closest brightness value.
     */
    public char getCharByImageBrightness(double brightness) {
        double closestBrightness = PIXEL_MAX_VALUE;
        char closestChar = 0;
        for (Character c : this.charBrightnessMap.keySet()) {
            double curBrightness = this.charBrightnessMap.get(c).get(NORMALIZED_BRIGHTNESS_IND);
            if (Math.abs(curBrightness - brightness) < closestBrightness) {
                closestBrightness = Math.abs(curBrightness - brightness);
                closestChar = c;
            }
        }
        return closestChar;
    }

    /**
     * Adds a character to the character set and updates the brightness values.
     *
     * @param c The character to be added.
     */
    public void addChar(char c) {
        if (!this.charBrightnessMap.containsKey(c)) {
            initializeMapEntry(c, true);
            double charBrightness = this.charBrightnessMap.get(c).get(CHAR_BRIGHTNESS_IND);
            if (charBrightness > this.maxBrightness) {
                this.maxBrightness = charBrightness;
                updateNormalizedCharBrightness();
            }
            if (charBrightness < this.minBrightness) {
                this.minBrightness = charBrightness;
                updateNormalizedCharBrightness();
            }
        }
    }

    /**
     * Removes a character from the character set and updates the brightness values.
     *
     * @param c The character to be removed.
     */
    public void removeChar(char c) {
        if (!this.charBrightnessMap.containsKey(c)) {
            return;
        }
        double charBrightness = this.charBrightnessMap.get(c).get(CHAR_BRIGHTNESS_IND);
        this.charBrightnessMap.remove(c);
        if (charBrightness == this.maxBrightness) {
            this.maxBrightness = getMaxBrightness();
            updateNormalizedCharBrightness();
        }
        if (charBrightness == this.minBrightness) {
            this.minBrightness = getMinBrightness();
            updateNormalizedCharBrightness();
        }
    }

    /*
     * Updates the normalized brightness values of the characters in the character set.
     */
    private void updateNormalizedCharBrightness() {
        for (Character character : this.charBrightnessMap.keySet()) {
            ArrayList<Double> brightnessValues = this.charBrightnessMap.get(character);
            double charBrightness = brightnessValues.get(CHAR_BRIGHTNESS_IND);
            brightnessValues.set(NORMALIZED_BRIGHTNESS_IND, normalizedBrightnessCalculator(charBrightness));
        }
    }

    /*
     * Initializes the brightness values for a character in the character set.
     *
     * @param c             The character to be initialized.
     * @param isInitialized Flag indicating if the character is being initialized.
     */
    private void initializeMapEntry(char c, boolean isInitialized) {
        // Avoiding calculate the char brightness if its already calculated.
        double charBrightness = this.allBrightnessMap.containsKey(c) ?
                this.allBrightnessMap.get(c) : charBrightnessCalculator(c);
        double normalizedCharBrightness = PIXEL_MIN_VALUE;
        if (isInitialized) { // If its initialization the normalization is unnecessary
            normalizedCharBrightness = normalizedBrightnessCalculator(charBrightness);
        }
        ArrayList<Double> brightnessValues = new ArrayList<>(BRIGHTNESS_ARR_SIZE);
        brightnessValues.add(charBrightness);
        brightnessValues.add(normalizedCharBrightness);
        this.charBrightnessMap.put(c, brightnessValues);
        this.allBrightnessMap.put(c, charBrightness);
    }

    /*
     * Calculates the brightness of a character in the character set.
     *
     * @param c The character for which brightness is to be calculated.
     * @return The brightness value of the character.
     */
    private double charBrightnessCalculator(char c) {
        boolean[][] charImage = CharConverter.convertToBoolArray(c);
        double whiteCounter = 0;
        for (boolean[] row : charImage) {
            for (boolean pixelValue : row) {
                if (pixelValue) {
                    whiteCounter++;
                }
            }
        }
        return whiteCounter / (CHAR_IMAGE_SIZE * CHAR_IMAGE_SIZE);
    }

    /*
     * Gets the maximum brightness value among the characters in the character set.
     *
     * @return The maximum brightness value.
     */
    private double getMaxBrightness() {
        double maxBrightness = PIXEL_MIN_VALUE;
        for (ArrayList<Double> list : this.charBrightnessMap.values()) {
            double curBrightness = list.get(CHAR_BRIGHTNESS_IND);
            maxBrightness = Math.max(maxBrightness, curBrightness);
        }
        return maxBrightness;
    }

    /*
     * Gets the minimum brightness value among the characters in the character set.
     *
     * @return The minimum brightness value.
     */
    private double getMinBrightness() {
        double minBrightness = PIXEL_MAX_VALUE;
        for (ArrayList<Double> list : this.charBrightnessMap.values()) {
            double curBrightness = list.get(CHAR_BRIGHTNESS_IND);
            minBrightness = Math.min(minBrightness, curBrightness);
        }
        return minBrightness;
    }

    /*
     * Initializes the brightness values for characters in the character set.
     */
    private void initialCharBrightnessMap() {
        for (char c : this.charset) {
            if (!this.charBrightnessMap.containsKey(c)) {
                initializeMapEntry(c, false);
            }
        }
    }

    /*
     * Calculates the normalized brightness value for a given brightness value.
     *
     * @param brightness The brightness value to be normalized.
     * @return The normalized brightness value.
     */
    private double normalizedBrightnessCalculator(double brightness) {
        return (brightness - this.minBrightness) / (this.maxBrightness - this.minBrightness);
    }
}
