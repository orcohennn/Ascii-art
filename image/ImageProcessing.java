package image;

import java.awt.Color;

/**
 * The ImageProcessing class provides utility methods for processing and manipulating images.
 */
public class ImageProcessing {
    /**
     * Represents 2^0, used in power of two calculations.
     */
    private static final int TWO_POW_ZERO = 1;
    /**
     * Represents 2^1, used in power of two calculations.
     */
    private static final int TWO_POW_ONE = 2;
    /**
     * Represents 2^2, used in power of two calculations.
     */
    private static final int TWO_POW_TWO = 4;
    /**
     * Represents 2^3, used in power of two calculations.
     */
    private static final int TWO_POW_THREE = 8;
    /**
     * Represents 2^4, used in power of two calculations.
     */
    private static final int TWO_POW_FOUR = 16;
    /**
     * Represents 2^5, used in power of two calculations.
     */
    private static final int TWO_POW_FIVE = 32;
    /**
     * Weight for blue color in grayscale conversion.
     */
    private static final double BLUE_FACTOR = 0.0722;
    /**
     * Weight for red color in grayscale conversion.
     */
    private static final double RED_FACTOR = 0.2126;
    /**
     * Weight for green color in grayscale conversion.
     */
    private static final double GREEN_FACTOR = 0.7152;
    /**
     * Half factor for certain calculations.
     */
    private static final double HALF_FACTOR = 0.5;
    /**
     * Maximum pixel value in the RGB color space.
     */
    private static final double PIXEL_MAX_VALUE = 255;

    /**
     * Constructor.
     * Private constructor to avoid instantiation of the object.
     */
    private ImageProcessing() {
    }

    /**
     * Wraps the input image to the nearest power of two dimensions if they are not already.
     *
     * @param image The input image to be wrapped.
     * @return The wrapped image with dimensions as the nearest power of two.
     */
    public static Image imageWrapper(Image image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // Check if dimensions are not powers of two
        if (!isPowerOfTwo(imageWidth) || !isPowerOfTwo(imageHeight)) {
            // Calculate the nearest power of two dimensions
            int powTwoImageWidth = closestPowerOfTwo(imageWidth);
            int powTwoImageHeight = closestPowerOfTwo(imageHeight);

            // Create a wrapped image with the new dimensions
            image = createWrapImage(powTwoImageWidth, powTwoImageHeight,
                    powTwoImageWidth - imageWidth,
                    powTwoImageHeight - imageHeight, image);
        }
        return image;
    }

    /**
     * Partitions the input image into smaller sub-images based on the specified resolution.
     *
     * @param image      The input image to be partitioned.
     * @param resolution The resolution of each sub-image.
     * @return An array of sub-images representing the partitioned sections of the input image.
     */
    public static Image[] subImagesPartition(Image image, int resolution) {
        int subImageRow = image.getHeight() / resolution;
        int subImageCol = image.getWidth() / resolution;
        Color[][][] colorPartitionArray =
                new Color[resolution * resolution][subImageRow][subImageCol];

        // Populate the color partition array based on the input image
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                colorPartitionArray[(i / subImageRow) * resolution + (j / subImageCol)]
                        [i % subImageRow][j % subImageCol] = image.getPixel(i, j);
            }
        }

        // Create an array of Image objects from the color partition array
        Image[] imagePartitionArr = new Image[resolution * resolution];
        for (int i = 0; i < imagePartitionArr.length; i++) {
            imagePartitionArr[i] = new Image(colorPartitionArray[i], subImageCol, subImageRow);
        }
        return imagePartitionArr;
    }

    /**
     * Calculates the brightness of a sub-image using a grayscale conversion formula.
     *
     * @param subImage The sub-image for which brightness is to be calculated.
     * @return The brightness value of the sub-image.
     */
    public static double subImageBrightnessFormula(Image subImage) {
        double greyscaleSum = 0;
        // Iterate through each pixel in the sub-image and calculate grayscale value
        for (int i = 0; i < subImage.getHeight(); i++) {
            for (int j = 0; j < subImage.getWidth(); j++) {
                Color curPixel = subImage.getPixel(i, j);
                greyscaleSum += grayConverter(curPixel);
            }
        }
        // Normalize and return the calculated brightness value
        return greyscaleSum / (subImage.getWidth() * subImage.getHeight() * PIXEL_MAX_VALUE);
    }

    /*
     * Converts a given color to grayscale using predefined weights.
     *
     * @param color The color to be converted to grayscale.
     * @return The grayscale value of the input color.
     */
    private static double grayConverter(Color color) {
        return color.getRed() * RED_FACTOR + color.getGreen() * GREEN_FACTOR + color.getBlue() * BLUE_FACTOR;
    }

    /*
     * Creates a wrapped image with specified dimensions and wrap amounts.
     * @param newImageWidth    The width of the new wrapped image.
     * @param newImageHeight   The height of the new wrapped image.
     * @param widthWrapAmount  The amount to wrap on the width dimension.
     * @param heightWrapAmount The amount to wrap on the height dimension.
     * @param image            The original image to be wrapped.
     * @return The wrapped image with the specified dimensions.
     */
    private static Image createWrapImage(int newImageWidth, int newImageHeight,
                                         int widthWrapAmount, int heightWrapAmount, Image image) {
        Color[][] colorArray = new Color[newImageHeight][newImageWidth];
        int imageRightBoundary = newImageWidth - (int) (widthWrapAmount * HALF_FACTOR);
        int imageLeftBoundary = (int) (widthWrapAmount * HALF_FACTOR);
        int imageDownBoundary = newImageHeight - (int) (heightWrapAmount * HALF_FACTOR);
        int imageUpBoundary = (int) (heightWrapAmount * HALF_FACTOR);
        // Populate the color array with wrapped or white pixels based on boundaries
        for (int i = 0; i < newImageHeight; i++) {
            for (int j = 0; j < newImageWidth; j++) {
                if (imageUpBoundary <= i && i < imageDownBoundary
                        && imageLeftBoundary <= j && j < imageRightBoundary) {
                    colorArray[i][j] = image.getPixel(i - imageUpBoundary, j - imageLeftBoundary);
                } else {
                    colorArray[i][j] = Color.WHITE;
                }
            }
        }
        return new Image(colorArray, newImageWidth, newImageHeight);
    }

    /**
     * Finds the closest power of two greater than or equal to the given number.
     *
     * @param num The number for which the closest power of two is to be found.
     * @return The closest power of two greater than or equal to the given number.
     */
    public static int closestPowerOfTwo(int num) {
        num--;
        num |= num >> TWO_POW_ZERO;
        num |= num >> TWO_POW_ONE;
        num |= num >> TWO_POW_TWO;
        num |= num >> TWO_POW_THREE;
        num |= num >> TWO_POW_FOUR;
        num |= num >> TWO_POW_FIVE;
        return num + TWO_POW_ZERO;
    }

    /*
     * Checks if a given number is a power of two.
     *
     * @param num The number to be checked for being a power of two.
     * @return True if the number is a power of two, false otherwise.
     */
    private static boolean isPowerOfTwo(int num) {
        return num != 0 && (num & (num - TWO_POW_ZERO)) == 0;
    }
}