/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.image;


/**
 * A paletted (indexed-color) image that can be read. Implementations can be mutable or immutable.
 * Implementations can explicitly store pixel values in memory or implicitly compute them when requested.
 */
public interface PaletteImage {
	
	/**
	 * Returns the width of this image, a positive number.
	 * @return the width of this image
	 */
	public int getWidth();
	
	
	/**
	 * Returns the height of this image, a positive number.
	 * @return the height of this image
	 */
	public int getHeight();
	
	
	/**
	 * Returns the bit depths of the channels of this image, a length-4 array:
	 * <ul>
	 *   <li>Index 0: The bit depth of the red channel, in the range [1, 8]</li>
	 *   <li>Index 1: The bit depth of the green channel, in the range [1, 8]</li>
	 *   <li>Index 2: The bit depth of the blue channel, in the range [1, 8]</li>
	 *   <li>Index 3: The bit depth of the alpha channel, either 0 or 8, and where 0 means all pixels are opaque</li>
	 * </ul>
	 * @return the bit depths of the channels of this image (not {@code null})
	 */
	public int[] getBitDepths();
	
	
	/**
	 * Returns the palette of this image, with length between 1 and 256 (inclusive).
	 * Each entry equals {@code (red << 48 | green << 32 | blue << 16 | alpha << 0)},
	 * where {@code red} is in the range [0, 2<sup>bitDepths[0]</sup>), etc.
	 * Duplicate values in the palette are allowed and preserved.
	 * @return the palette of this image (not {@code null})
	 */
	public long[] getPalette();
	
	
	/**
	 * Returns the palette index of the pixel at the specified coordinates. The top left corner
	 * has the coordinates (0, 0), positive {@code x} is right, and positive {@code y} is down.
	 * The returned value is in the range [0, <code>getPalette().length</code>).
	 * @param x the <var>x</var> coordinate of the pixel to get, in the range [0, {@code getWidth()})
	 * @param y the <var>y</var> coordinate of the pixel to get, in the range [0, {@code getHeight()})
	 * @return the palette index of the pixel
	 * @throws IndexOutOfBoundsException if the (<var>x</var>, <var>y</var>) coordinates are out of bounds
	 */
	public int getPixel(int x, int y);
	
}
