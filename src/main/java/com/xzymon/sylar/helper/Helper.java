package com.xzymon.sylar.helper;

import com.xzymon.sylar.constants.MonthPlMapping;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.TextPixelArea;
import io.nayuki.png.image.BufferedPaletteImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Helper {
	private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);

	public static final int CLEAR_VALUE = 0;

	public static FrameCoords intersection(FrameCoords source, FrameCoords target) {
		FrameCoords result = new FrameCoords(source);
		result.setLeft(Math.max(source.getLeft(), target.getLeft()));
		result.setTop(Math.max(source.getTop(), target.getTop()));
		result.setRight(Math.min(source.getRight(), target.getRight()));
		result.setBottom(Math.min(source.getBottom(), target.getBottom()));
		return result;
	}

	public static FrameCoords normalize(FrameCoords source) {
		FrameCoords result = new FrameCoords(source);
		result.setBottom(Math.max(result.getTop(), result.getBottom()));
		result.setRight(Math.max(result.getLeft(), result.getRight()));
		return result;
	}

	public static int[][] extractImageArea(BufferedPaletteImage img, FrameCoords areaCoords) {
		return extractImageArea(img, areaCoords.getLeft(), areaCoords.getTop(), areaCoords.getRight(), areaCoords.getBottom());
	}

	public static int[][] extractImageArea(BufferedPaletteImage img, int x0, int y0, int x1, int y1) {
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

	public static int[][] extractSubareaFromArea(int area[][], int x0, int y0, int x1, int y1) {
		// normalizacja obszaru
		int xStart = x0 < x1 ? x0 : x1;
		int xEnd = x0 < x1 ? x1 : x0;
		int yStart = y0 < y1 ? y0 : y1;
		int yEnd = y0 < y1 ? y1 : y0;

		int[][] result = new int[yEnd - yStart + 1][xEnd - xStart + 1];
		for (int y = yStart; y <= yEnd; y++) {
			for (int x = xStart; x <= xEnd; x++) {
				result[y-yStart][x-xStart] = area[y][x];
			}
		}
		return result;
	}

	public static int[] flattenImageAreaTo1dArray(int[][] imageArea, int xLength, int yLength) {
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
		LOGGER.debug(sb.toString());
		sbPrettier.deleteCharAt((sb.length()*2)-1);
		LOGGER.debug(String.format("int[] letter = {%1$s};", sbPrettier));
		return result;
	}

	public static int mapIntToIntForArray(int value, int clearValue) {
		if (value == clearValue) {
			return 0;
		} else {
			return value;
		}
	}

	public static void printImageArea(int[][] imageArea, int xLength, int yLength, int clearValue) {
		StringBuilder sb;
		for (int y = 0; y < yLength && y < imageArea.length; y++) {
			sb = new StringBuilder();
			for (int x = 0; x < xLength && x < imageArea[y].length; x++) {
				sb.append(mapIntToCharForVisualization(imageArea[y][x], clearValue));
			}
			//System.out.println(sb);
			LOGGER.debug(sb.toString());
		}
	}

	public static char mapIntToCharForVisualization(int value, int clearValue) {
		if (value == clearValue) {
			return ' ';
		} else {
			return '#';
		}
	}

	public static Integer getGaugeReferencePointByText(String text, Map<Integer, TextPixelArea> gaugesMap) {
		for (Map.Entry<Integer, TextPixelArea> entry : gaugesMap.entrySet()) {
			if (entry.getValue().getExtractedText().equals(text)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getDateYYYYDashMMDashDD(String dateString) {
		//expected format: 30 Wrz 2024 23:59 CEST
		String trimmed = dateString.trim();
		String[] dateParts = trimmed.split(" ");
		if (dateParts.length != 5) {
			LOGGER.error(String.format("Unexpected date format: %1$s", dateString));
			throw new RuntimeException("Unexpected date format: " + dateString);
		}
		String dd = dateParts[0];
		String mmm = dateParts[1];
		String yyyy = dateParts[2];
		String result = yyyy + "-" + MonthPlMapping.MMM_TO_MM.get(mmm) + "-" + dd;
		return result;
	}

	public static String getDateYYYYMMDD(String dateString) {
		//expected format: 30 Wrz 2024 23:59 CEST
		String trimmed = dateString.trim();
		String[] dateParts = trimmed.split(" ");
		if (dateParts.length != 5) {
			LOGGER.error(String.format("Unexpected date format: %1$s", dateString));
			throw new RuntimeException("Unexpected date format: " + dateString);
		}
		String dd = dateParts[0];
		String mmm = dateParts[1];
		String yyyy = dateParts[2];
		String result = yyyy + MonthPlMapping.MMM_TO_MM.get(mmm) + dd;
		return result;
	}
}
