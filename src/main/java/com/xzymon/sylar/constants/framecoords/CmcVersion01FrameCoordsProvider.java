package com.xzymon.sylar.constants.framecoords;

import com.xzymon.sylar.model.FrameCoords;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides frame coordinates for CMC from version 01 to 03.
 */
public class CmcVersion01FrameCoordsProvider {
	public static final FrameCoords STANDARD_FULL_IMAGE = new FrameCoords(0, 2732, 2048, 0);

	public static final FrameCoords SNAPSHOT_DATETIME = new FrameCoords(9, 300, 37, 33);
	public static final FrameCoords NOT_CHART_MARKER = new FrameCoords(77, 90, 120, 35);
	public static final FrameCoords VALOR_NAME = new FrameCoords(165, 182, 201, 30);
	public static final FrameCoords NARROW_TOOLBOX_MARKER = new FrameCoords(1910, 119, 1940, 89);
	public static final FrameCoords WIDE_TOOLBOX_MARKER = new FrameCoords(1910, 163, 1940, 133);

	public static final FrameCoords INTERVAL_LINE_1 = new FrameCoords(1941, 101, 1963, 66);
	public static final FrameCoords INTERVAL_LINE_2 = new FrameCoords(1974, 113, 1991, 66);
	public static final FrameCoords INTERVAL_OPTIONS = new FrameCoords(1906, 81, 1926, 22);

	public static final FrameCoords DEFAULT_NARROW_TOOLBOX_VALUES_FRAME = new FrameCoords(414, 1738, 1889, 10);
	public static final FrameCoords DEFAULT_WIDE_TOOLBOX_VALUES_FRAME = new FrameCoords(414, 2618, 1889, 10);

	public static final FrameCoords DEFAULT_NARROW_VALUES_FRAME = new FrameCoords(414, 1738, 1989, 10);
	public static final FrameCoords DEFAULT_WIDE_VALUES_FRAME = new FrameCoords(414, 2618, 1989, 10);

	public static final Map<String, FrameCoords> FRAME_COORDS_MAP = new HashMap<>();

	static {
		FRAME_COORDS_MAP.put(FrameCoordsNames.STANDARD_FULL_IMAGE, STANDARD_FULL_IMAGE);
		FRAME_COORDS_MAP.put(FrameCoordsNames.SNAPSHOT_DATETIME, SNAPSHOT_DATETIME);
		FRAME_COORDS_MAP.put(FrameCoordsNames.NOT_CHART_MARKER, NOT_CHART_MARKER);
		FRAME_COORDS_MAP.put(FrameCoordsNames.VALOR_NAME, VALOR_NAME);
		FRAME_COORDS_MAP.put(FrameCoordsNames.NARROW_TOOLBOX_MARKER, NARROW_TOOLBOX_MARKER);
		FRAME_COORDS_MAP.put(FrameCoordsNames.WIDE_TOOLBOX_MARKER, WIDE_TOOLBOX_MARKER);
		FRAME_COORDS_MAP.put(FrameCoordsNames.INTERVAL_LINE_1, INTERVAL_LINE_1);
		FRAME_COORDS_MAP.put(FrameCoordsNames.INTERVAL_LINE_2, INTERVAL_LINE_2);
		FRAME_COORDS_MAP.put(FrameCoordsNames.INTERVAL_OPTIONS, INTERVAL_OPTIONS);
		FRAME_COORDS_MAP.put(FrameCoordsNames.DEFAULT_NARROW_TOOLBOX_VALUES_FRAME, DEFAULT_NARROW_TOOLBOX_VALUES_FRAME);
		FRAME_COORDS_MAP.put(FrameCoordsNames.DEFAULT_WIDE_TOOLBOX_VALUES_FRAME, DEFAULT_WIDE_TOOLBOX_VALUES_FRAME);
		FRAME_COORDS_MAP.put(FrameCoordsNames.DEFAULT_NARROW_VALUES_FRAME, DEFAULT_NARROW_VALUES_FRAME);
		FRAME_COORDS_MAP.put(FrameCoordsNames.DEFAULT_WIDE_VALUES_FRAME, DEFAULT_WIDE_VALUES_FRAME);
	}

	public static FrameCoords getFrameCoords(String name) {
		return FRAME_COORDS_MAP.get(name);
	}

	public static boolean containsFrameCoords(String name) {
		return FRAME_COORDS_MAP.containsKey(name);
	}

	public static FrameCoords getFrameCoordsOrDefault(String name, FrameCoords defaultValue) {
		return FRAME_COORDS_MAP.getOrDefault(name, defaultValue);
	}

	public static Map<String, FrameCoords> getFrameCoordsMap() {
		return Collections.unmodifiableMap(FRAME_COORDS_MAP);
	}
}
