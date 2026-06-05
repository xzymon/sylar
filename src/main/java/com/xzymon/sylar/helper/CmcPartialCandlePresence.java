package com.xzymon.sylar.helper;

import com.xzymon.sylar.model.CmcPartialCandle;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CmcPartialCandlePresence {
	private CmcPartialCandle ascendingCore;
	private CmcPartialCandle ascendingExtreme;
	private CmcPartialCandle descendingCore;
	private CmcPartialCandle descendingExtreme;

	@Override
	public String toString() {
		return  "CmcPartialCandlePresence{" +
				"ascendingCore=" + ascendingCore != null ? "true" : "false" +
				", ascendingExtreme=" + ascendingExtreme != null ? "true" : "false" +
				", descendingCore=" + descendingCore != null ? "true" : "false" +
				", descendingExtreme=" + descendingExtreme != null ? "true" : "false" +
				'}';
	}
}
