package com.xzymon.sylar.json;

public class FrameCoords {
	public enum FrameLine {
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}

	Integer top;
	Integer left;
	Integer right;
	Integer bottom;

	public FrameCoords() {
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
