package com.xzymon.sylar.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CmcContinuumContainer {
    private Map<String, CmcRawDataContainer> continuumMap;

    public CmcContinuumContainer() {
        this.continuumMap = new HashMap<>();
    }
}
