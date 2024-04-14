package com.xzymon.sylar.processing;

public class ConfirmationGear {
	private final int level;
	private final int[] levelPoints;

	public ConfirmationGear(int level, int rangeMax) {
		this(level, 0, rangeMax);
	}

	public ConfirmationGear(int level, int rangeMin, int rangeMax) {
		this.level = level;
		levelPoints = new int[level];
		int normalizedMax = rangeMax - rangeMin;
		double offsetY = ((double)normalizedMax) / ((double)(level + 1));
		double intermediateResult;
		for (int loop = 0; loop < level; loop++) {
			intermediateResult = ((loop + 1) * offsetY) + rangeMin;
			levelPoints[loop] = (int)Math.round(intermediateResult);
			//System.out.format("[%1$d] = %2$d%n", loop, confirmYArr[loop]);
		}
	}

	public int getLevel() {
		return level;
	}

	public int[] getLevelPoints() {
		return levelPoints;
	}
}
