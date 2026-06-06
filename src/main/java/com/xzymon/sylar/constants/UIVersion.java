package com.xzymon.sylar.constants;

public enum UIVersion {
	V01("01"),
	V02("02"),
	V03("03"),
	V04("04"),
	V05("05");

	public final String versionNumber;

	private UIVersion(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	@Override
	public String toString() {
		return versionNumber;
	}

	public static UIVersion fromString(String version) {
		for (UIVersion v : UIVersion.values()) {
			if (v.versionNumber.equals(version)) {
				return v;
			}
		}
		throw new IllegalArgumentException("Invalid UI version: " + version);
	}
}
