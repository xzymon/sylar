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
 * An image data (IDAT) chunk. This contains pixel data that is filtered and compressed.
 * Instances should be treated as immutable, but arrays are not copied defensively.
 * The interpretation of this chunk depends extensively on the IHDR chunk.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11IDAT
 */
public record Idat(byte[] data) implements BytesDataChunk {
	
	static final String TYPE = "IDAT";
	
	
	/*---- Constructor and factory ----*/
	
	public Idat {
		Objects.requireNonNull(data);
	}
	
	
	static Idat read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		return new Idat(in.readRemainingBytes());
	}
	
	
	/*---- Method ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
}
