package io.nayuki.png;

public class MathHelper {

	/**
	 * Code of Math.ceilDiv(int dividend, int divisor)
	 */
	public static int ceilDiv(int x, int y) {
		final int q = x / y;
		// if the signs are the same and modulo not zero, round up
		if ((x ^ y) >= 0 && (q * y != x)) {
			return q + 1;
		}
		return q;
	}

	public static long ceilDiv(long x, int y) {
		return ceilDiv(x, (long)y);
	}

	public static long ceilDiv(long x, long y) {
		final long q = x / y;
		// if the signs are the same and modulo not zero, round up
		if ((x ^ y) >= 0 && (q * y != x)) {
			return q + 1;
		}
		return q;
	}
}
