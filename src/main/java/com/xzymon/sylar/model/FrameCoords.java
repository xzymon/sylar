package com.xzymon.sylar.model;

public class FrameCoords {
	public enum FrameLine {
		TOP,
		RIGHT,
		BOTTOM,
		LEFT
	}

	Integer top;
	Integer right;
	Integer bottom;
	Integer left;

	public FrameCoords() {
	}

	public FrameCoords(FrameCoords toCopy) {
		this.top = toCopy.top;
		this.right = toCopy.right;
		this.bottom = toCopy.bottom;
		this.left = toCopy.left;
	}

	public FrameCoords(Integer top, Integer right, Integer bottom, Integer left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	public Integer getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public Integer getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public Integer getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	@Override
	public String toString() {
		return "FrameCoords{" +
				       "top=" + top +
				       ", left=" + left +
				       ", right=" + right +
				       ", bottom=" + bottom +
				       '}';
	}
}
