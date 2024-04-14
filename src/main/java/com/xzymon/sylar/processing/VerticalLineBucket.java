package com.xzymon.sylar.processing;

import java.math.BigDecimal;

public class VerticalLineBucket {
	private Integer referencePointOnAxis;
	private BigDecimal min;
	private BigDecimal max;

	public VerticalLineBucket(Integer referencePointOnAxis) {
		this.referencePointOnAxis = referencePointOnAxis;
	}

	public VerticalLineBucket(Integer referencePointOnAxis, BigDecimal min, BigDecimal max) {
		this.referencePointOnAxis = referencePointOnAxis;
		this.min = min;
		this.max = max;
	}

	public Integer getReferencePointOnAxis() {
		return referencePointOnAxis;
	}

	public void setReferencePointOnAxis(Integer referencePointOnAxis) {
		this.referencePointOnAxis = referencePointOnAxis;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}
}
