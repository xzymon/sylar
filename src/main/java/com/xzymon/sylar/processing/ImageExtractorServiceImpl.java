package com.xzymon.sylar.processing;

import com.xzymon.sylar.json.ExtractedData;
import io.nayuki.png.PngImage;
import io.nayuki.png.image.BufferedPaletteImage;
import io.nayuki.png.ImageDecoder;

import java.io.File;
import java.io.IOException;

public class ImageExtractorServiceImpl {
	private static final int CLEAR_VALUE = 0;

	//private final String dataImageFilePath = "/home/coder/Images/solana-test.png";
	//private final String dataImageFilePath = "/home/coder/Downloads/stooq/SOL.V_y.png";
	private final String dataImageFilePath = "/home/coder/Downloads/stooq/YALL_US.png";
	//private final String dataImageFilePath = "/home/coder/Downloads/stooq/INJ.V/ln_20240320.png";
	private final ExtractedData extractedData = new ExtractedData();

	public void start() throws IOException {
		PngImage png = PngImage.read(new File(dataImageFilePath));
		BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
		int height = buffPalImg.getHeight();
		int width = buffPalImg.getWidth();
		System.out.println("bitdepths: " + buffPalImg.getBitDepths());
		int paletteLen = buffPalImg.getPalette().length;
		int[] pRed = new int[paletteLen];
		int[] pGreen = new int[paletteLen];
		int[] pBlue = new int[paletteLen];
		for (int pi = 0; pi < buffPalImg.getPalette().length; pi++) {
			long col = buffPalImg.getPalette()[pi];
			pRed[pi] = (int)(col >> 48) % 256;
			pGreen[pi] = (int)(col >> 32) % 256;
			pBlue[pi] = (int)(col >> 16) % 256;
			System.out.format("%1$d = %5$d, (red:%2$d, green:%3$d, blue:%4$d)\n", pi, pRed[pi], pGreen[pi], pBlue[pi], col);
		}

		//0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  16,  17,
		//8, 14, 20, 26, 32, 38, 44, 50, 56, 62, 68, 74, 80, 86, 92, 98, 104, 110,
		int areaXStart = 38; // + 6
		int areaYStart = 2;

		extractSymbolFromPixelArea(areaXStart, areaYStart, buffPalImg);

		extractedData.extractData(buffPalImg);
	}

	private void extractSymbolFromPixelArea(int areaXStart, int areaYStart, BufferedPaletteImage buffPalImg) {
		int areaXEnd = areaXStart + 4; // + 6
		int areaYEnd = areaYStart + 10;

		int areaXLength = areaXEnd - areaXStart + 1;
		int areaYLength = areaYEnd - areaYStart + 1;

		int[][] imageArea = extractImageArea(buffPalImg, areaXStart, areaYStart, areaXEnd, areaYEnd);
		printImageArea(imageArea, areaXLength, areaYLength);
		flattenImageAreaTo1dArray(imageArea, areaXLength, areaYLength);
	}

	private int[][] extractImageArea(BufferedPaletteImage img, int x0, int y0, int x1, int y1) {
		// normalizacja obszaru
		int xStart = x0 < x1 ? x0 : x1;
		int xEnd = x0 < x1 ? x1 : x0;
		int yStart = y0 < y1 ? y0 : y1;
		int yEnd = y0 < y1 ? y1 : y0;

		int[][] result = new int[yEnd - yStart + 1][xEnd - xStart + 1];
		for (int y = yStart; y <= yEnd; y++) {
			for (int x = xStart; x <= xEnd; x++) {
				result[y-yStart][x-xStart] = img.getPixel(x, y);
			}
		}
		return result;
	}

	private int[] flattenImageAreaTo1dArray(int[][] imageArea, int xLength, int yLength) {
		int[] result = new int[xLength * yLength];
		StringBuilder sb = new StringBuilder();
		StringBuilder sbPrettier = new StringBuilder();
		for (int y = 0; y < yLength; y++) {
			for (int x = 0; x < xLength; x++) {
				int pixelPaletteValue = mapIntToIntForArray(imageArea[y][x], CLEAR_VALUE);
				sb.append(pixelPaletteValue);
				sbPrettier.append(pixelPaletteValue).append(',');
				result[y*xLength+x] = pixelPaletteValue;
			}
		}
		System.out.println(sb);
		sbPrettier.deleteCharAt((sb.length()*2)-1);
		System.out.format("int[] letter = {%1$s};", sbPrettier);
		return result;
	}

	private void printImageArea(int[][] imageArea, int xLength, int yLength) {
		StringBuilder sb;
		for (int y = 0; y < yLength; y++) {
			sb = new StringBuilder();
			for (int x = 0; x < xLength; x++) {
				sb.append(mapIntToCharForVisualization(imageArea[y][x], CLEAR_VALUE));
			}
			System.out.println(sb);
		}
		System.out.println();
	}

	private char mapIntToCharForVisualization(int value, int clearValue) {
		if (value == clearValue) {
			return ' ';
		} else {
			return '#';
		}
	}

	private int mapIntToIntForArray(int value, int clearValue) {
		if (value == clearValue) {
			return 0;
		} else {
			return value;
		}
	}
}