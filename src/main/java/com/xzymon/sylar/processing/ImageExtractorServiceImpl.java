package com.xzymon.sylar.processing;

import com.xzymon.sylar.helper.Helper;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.RawDataContainer;
import com.xzymon.sylar.model.TextPixelArea;
import io.nayuki.png.PngImage;
import io.nayuki.png.image.BufferedPaletteImage;
import io.nayuki.png.ImageDecoder;

import java.io.File;
import java.io.IOException;

public class ImageExtractorServiceImpl {

	//private final String dataImageFilePath = "/home/coder/Images/solana-test.png";
	//private final String dataImageFilePath = "/home/coder/Downloads/stooq/SOL.V_y.png";
	private final String dataImageFilePath = "/home/coder/Downloads/stooq/YALL_US.png";
	//private final String dataImageFilePath = "/home/coder/Downloads/stooq/INJ.V/ln_20240320.png";
	private final RawDataContainer rawDataContainer = new RawDataContainer();

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

		//rawDataContainer.extractData(buffPalImg);
	}

	private void extractSymbolFromPixelArea(int areaXStart, int areaYStart, BufferedPaletteImage buffPalImg) {
		int areaXEnd = areaXStart + 4; // + 6
		int areaYEnd = areaYStart + 10;

		int areaXLength = areaXEnd - areaXStart + 1;
		int areaYLength = areaYEnd - areaYStart + 1;

		int[][] imageArea = Helper.extractImageArea(buffPalImg, areaXStart, areaYStart, areaXEnd, areaYEnd);
		Helper.printImageArea(imageArea, areaXLength, areaYLength, Helper.CLEAR_VALUE);
		Helper.flattenImageAreaTo1dArray(imageArea, areaXLength, areaYLength);
	}

	public static TextPixelArea exposeFromPixelArea(FrameCoords areaCoords, BufferedPaletteImage buffPalImg, int paletteColorToSense, int clearValue) {
		FrameCoords imageFC = new FrameCoords(0, buffPalImg.getWidth()-1, buffPalImg.getHeight()-1, 0);
		FrameCoords normalizedCoords = Helper.normalize(areaCoords);
		FrameCoords intersectionCoords = Helper.intersection(imageFC, normalizedCoords);

		int[][] imageArea = Helper.extractImageArea(buffPalImg, intersectionCoords);
		int areaXLength = intersectionCoords.getRight() - intersectionCoords.getLeft() +1;
		int areaYLength = intersectionCoords.getBottom() - intersectionCoords.getTop() +1;
		Helper.printImageArea(imageArea, areaXLength, areaYLength, clearValue);
		return new TextPixelArea(imageArea, areaXLength, areaYLength);
	}
}