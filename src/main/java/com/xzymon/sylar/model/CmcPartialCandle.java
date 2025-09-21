package com.xzymon.sylar.model;

import com.xzymon.sylar.helper.ValuesAreaColors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CmcPartialCandle {
	private Integer referencePointOnAxis;
	private Integer min;
	private Integer max;
	private ValuesAreaColors color;
}
