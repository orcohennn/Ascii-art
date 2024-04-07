package ascii_art;

import image.Image;
import image.ImageProcessing;
import image_char_matching.SubImgCharMatcher;

/**
 * The AsciiArtAlgorithm class is responsible for converting an input image into ASCII art.
 */
public class AsciiArtAlgorithm {
    private final Image image;
    private final int resolution;
    private final SubImgCharMatcher matcher;

    /**
     * Constructs an AsciiArtAlgorithm object with the specified image, resolution, and character matcher.
     *
     * @param image    The input image to be converted to ASCII art.
     * @param resolution The resolution of the ASCII art (number of characters per row/column).
     * @param matcher  The character matcher used to map image brightness to ASCII characters.
     */
    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher matcher) {
        this.image = image;
        this.resolution = resolution;
        this.matcher = matcher;
    }

    /**
     * Runs the ASCII art algorithm on the provided image.
     * Returns the resulting ASCII art as a 2D character array.
     * @return The ASCII art representation of the input image.
     */
    public char[][] run() {
        // Wrap the image and partition it into smaller sub-images
        Image wrappedImage = ImageProcessing.imageWrapper(this.image);
        Image[] imagePartition = ImageProcessing.subImagesPartition(wrappedImage, this.resolution);

        // Create ASCII art from the partitioned images
        return asciiImageCreator(imagePartition);
    }

    /*
     * Creates ASCII art from an array of sub-images, each corresponding to a portion of the input image.
     *
     * @param imagePartition The array of sub-images partitioned from the input image.
     * @return The resulting ASCII art represented as a 2D character array.
     */
    private char[][] asciiImageCreator(Image[] imagePartition) {
        char[][] asciiImage = new char[this.resolution][this.resolution];

        // Iterate through each sub-image and calculate the corresponding ASCII character
        for (int i = 0; i < this.resolution; i++) {
            for (int j = 0; j < this.resolution; j++) {
                asciiImage[i][j] = subImageBrightnessCalculator(imagePartition[i * this.resolution + j]);
            }
        }
        return asciiImage;
    }

    /*
     * Calculates the brightness of a sub-image and maps it to an ASCII character using the provided
     * character matcher.
     *
     * @param subImage The sub-image for which brightness is to be calculated.
     * @return The ASCII character corresponding to the brightness of the sub-image.
     */
    private char subImageBrightnessCalculator(Image subImage) {
        // Calculate the brightness of the sub-image and map it to an ASCII character
        double subImageBrightness = ImageProcessing.subImageBrightnessFormula(subImage);
        return this.matcher.getCharByImageBrightness(subImageBrightness);
    }
}
