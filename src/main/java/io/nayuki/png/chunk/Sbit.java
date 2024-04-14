/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.IOException;
import java.util.Objects;


/**
 * A significant bits (sBIT) chunk. This defines the original number of significant bits per
 * channel. Instances should be treated as immutable, but arrays are not copied defensively.
 * The interpretation of this chunk depends on the color type in the IHDR chunk.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11sBIT
 */
public record Sbit(byte[] data) implements BytesDataChunk {
	
	static final String TYPE = "sBIT";
	
	
	/*---- Constructor and factory ----*/
	
	public Sbit {
		Objects.requireNonNull(data);
		if (!(1 <= data.length && data.length <= 4))
			throw new IllegalArgumentException("Array length out of range");
		for (int bits : data) {
			if (!(1 <= bits && bits <= 16))
				throw new IllegalArgumentException("Number of significant bits out of range");
		}
	}
	
	
	static Sbit read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		return new Sbit(in.readRemainingBytes());
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
}
