package com.xzymon.sylar.processing;

import com.xzymon.sylar.constants.FontSize8Characters;
import com.xzymon.sylar.constants.FontSize8Characters.FontSize8Character;
import com.xzymon.sylar.helper.Helper;
import com.xzymon.sylar.model.FrameCoords;
import io.nayuki.png.image.BufferedPaletteImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class OptimizedAreaDetector {
	private static final Logger LOGGER = LoggerFactory.getLogger(OptimizedAreaDetector.class);

	private static final int SUBAREA_NOT_PROCESSED = 0;
	private static final int SUBAREA_PROCESSED = 1;

	private int[][] topLeftPointMarkers;
	private int[][] imageArea;

	private final FrameCoords area;
	private final BufferedPaletteImage img;
	private final int paletteColorToSense;
	private final List<FontSize8Character> charsToDetect;
	private final List<FontSize8Character> charsToDetectWhenNotEmpty;
	private final int clearValue;

	public OptimizedAreaDetector(FrameCoords area, BufferedPaletteImage img, int paletteColorToSense, List<FontSize8Character> charsToDetect, int clearValue) {
		this.area = area;
		this.img = img;
		this.paletteColorToSense = paletteColorToSense;
		this.charsToDetect = charsToDetect;
		this.charsToDetectWhenNotEmpty = charsToDetect;
		this.clearValue = clearValue;
		this.imageArea = Helper.extractImageArea(img, area);
	}

	public OptimizedAreaDetector(FrameCoords area, BufferedPaletteImage img, int paletteColorToSense, List<FontSize8Character> charsToDetect, List<FontSize8Character> charsToDetectWhenNotEmpty, int clearValue) {
		this.area = area;
		this.img = img;
		this.paletteColorToSense = paletteColorToSense;
		this.charsToDetect = charsToDetect;
		this.charsToDetectWhenNotEmpty = charsToDetectWhenNotEmpty;
		this.clearValue = clearValue;
		this.imageArea = Helper.extractImageArea(img, area);
	}

	public String detect() {
		StringBuilder resultCollector = new StringBuilder();
		int subarea[][];
		int flattenSubarea[];
		Iterator<FontSize8Character> allowedCharsIt;
		FontSize8Character currentlyAnalyzedChar;
		boolean detected;
		boolean atLeastOneCharDetected = false;
		char detectedChar;
		topLeftPointMarkers = prepareTopLeftPointMarkers();
		for (int y = 0; y < topLeftPointMarkers.length; y++) {
			for (int x = 0; x < topLeftPointMarkers[y].length; x++) {
				if (topLeftPointMarkers[y][x] == SUBAREA_NOT_PROCESSED) {
					// wydobywamy litery o wielkości 11*5 a nie 12*6 -> stąd LINE_WIDTH-2 (a nie -1) i tak samo LINE_HEIGHT
					subarea = Helper.extractSubareaFromArea(imageArea, x, y, x+FontSize8Character.LETTER_WIDTH -1, y+FontSize8Character.LINE_HEIGHT-1);
					Helper.printImageArea(subarea, FontSize8Character.LETTER_WIDTH, FontSize8Character.LINE_HEIGHT, clearValue);
					flattenSubarea = Helper.flattenImageAreaTo1dArray(subarea, FontSize8Character.LETTER_WIDTH, FontSize8Character.LINE_HEIGHT);
					allowedCharsIt = atLeastOneCharDetected ? charsToDetectWhenNotEmpty.iterator() : charsToDetect.iterator();
					detected = false;
					while (!detected && allowedCharsIt.hasNext()) {
						currentlyAnalyzedChar = allowedCharsIt.next();
						detected = match(currentlyAnalyzedChar, flattenSubarea, paletteColorToSense);
						if (detected) {
							if (!atLeastOneCharDetected) {
								atLeastOneCharDetected = true;
							}
							detectedChar = currentlyAnalyzedChar.getCharacter();
							LOGGER.debug("Detected char: {}", detectedChar);
							resultCollector.append(detectedChar);
							//eliminate markers in detected char area
							for (int elY = y; elY < y+FontSize8Character.LINE_HEIGHT && elY < topLeftPointMarkers.length; elY++) {
								for (int elX = x; elX < x+FontSize8Character.LETTER_WIDTH && elX < topLeftPointMarkers[elY].length; elX++) {
									topLeftPointMarkers[elY][elX] = SUBAREA_PROCESSED;
								}
							}
							break;
						}
					}
					if (!detected) {
						topLeftPointMarkers[y][x] = SUBAREA_PROCESSED;
					}
				}
			}
		}
		return resultCollector.toString();
	}

	private boolean match(FontSize8Character charToMatch, int[] flattenSubarea, int paletteColorToSense) {
		int mappedColor;
		for (int i = 0; i < flattenSubarea.length; i++) {
			mappedColor = flattenSubarea[i] == paletteColorToSense ? FontSize8Characters.LETTER_COLOR : FontSize8Characters.BACKGROUND_COLOR;
			if (charToMatch.getArray()[i] != mappedColor) {
				return false;
			}
		}
		return true;
	}

	private int[][] prepareTopLeftPointMarkers() {
		int areaHeight = imageArea.length;
		int areaWidth = imageArea[0].length;
		if (areaHeight >= FontSize8Character.LINE_HEIGHT && areaWidth >= FontSize8Character.LETTER_WIDTH) {
			int markersHeight = areaHeight - FontSize8Character.LINE_HEIGHT + 1;
			int markersWidth = areaWidth - FontSize8Character.LETTER_WIDTH + 1;
			return new int[markersHeight][markersWidth];
		} else {
			throw new RuntimeException("To small area to detect characters");
		}
	}
}
