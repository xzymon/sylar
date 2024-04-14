package com.xzymon.sylar.processing;

public class TextPixelArea {
	private int[][] pixelArea;
	private int xLength;
	private int yLength;
	private String extractedText;

	public TextPixelArea(int[][] pixelArea, int xLength, int yLength) {
		this.pixelArea = pixelArea;
		this.xLength = xLength;
		this.yLength = yLength;
	}

	public int[][] getPixelArea() {
		return pixelArea;
	}

	public int getxLength() {
		return xLength;
	}

	public int getyLength() {
		return yLength;
	}

	public String getExtractedText() {
		return extractedText;
	}
}
