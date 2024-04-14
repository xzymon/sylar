package com.xzymon.sylar.json;

public class RawValueInBuckets {
	private Integer referencePointOnAxis;
	private Integer min;
	private Integer max;

	public RawValueInBuckets(Integer referencePointOnAxis) {
		this.referencePointOnAxis = referencePointOnAxis;
	}

	public RawValueInBuckets(Integer referencePointOnAxis, Integer min, Integer max) {
		this.referencePointOnAxis = referencePointOnAxis;
		this.min = min;
		this.max = max;
	}

	public Integer getHorizontal() {
		return referencePointOnAxis;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}
