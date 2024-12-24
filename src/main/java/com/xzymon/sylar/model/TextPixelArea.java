package com.xzymon.sylar.model;

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

	public int getXLength() {
		return xLength;
	}

	public int getYLength() {
		return yLength;
	}

	public String getExtractedText() {
		return extractedText;
	}

	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}
}
