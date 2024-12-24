package com.xzymon.sylar.model;

import java.math.BigDecimal;

public class VerticalLineBucket {
	private Integer referencePointOnXAxis;
	private BigDecimal min;
	private BigDecimal max;

	public VerticalLineBucket(Integer referencePointOnXAxis) {
		this.referencePointOnXAxis = referencePointOnXAxis;
	}

	public VerticalLineBucket(Integer referencePointOnXAxis, BigDecimal min, BigDecimal max) {
		this.referencePointOnXAxis = referencePointOnXAxis;
		this.min = min;
		this.max = max;
	}

	public Integer getReferencePointOnXAxis() {
		return referencePointOnXAxis;
	}

	public void setReferencePointOnXAxis(Integer referencePointOnAxis) {
		this.referencePointOnXAxis = referencePointOnAxis;
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
