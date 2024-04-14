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
 * A frame control (fcTL) chunk. This specifies the size and position of a frame
 * update rectangle, delay, and miscellaneous parameters. Instances are immutable.
 * @see https://wiki.mozilla.org/APNG_Specification#.60fcTL.60:_The_Frame_Control_Chunk
 */
public record Fctl(
		int sequence,
		int width,
		int height,
		int xOffset,
		int yOffset,
		int delayNumerator,
		int delayDenominator,
		DisposeOperation disposeOp,
		BlendOperation blendOp)
	implements SmallDataChunk {
	
	
	static final String TYPE = "fcTL";
	
	
	/*---- Constructor and factory ----*/
	
	public Fctl {
		if (sequence < 0)
			throw new IllegalArgumentException("Invalid sequence number");
		if (width <= 0)
			throw new IllegalArgumentException("Invalid width");
		if (height <= 0)
			throw new IllegalArgumentException("Invalid height");
		if (xOffset < 0)
			throw new IllegalArgumentException("Invalid x offset");
		if (yOffset < 0)
			throw new IllegalArgumentException("Invalid y offset");
		if (!(0x0000 <= delayNumerator && delayNumerator <= 0xFFFF))
			throw new IllegalArgumentException("Invalid delay numerator");
		if (!(0x0000 <= delayDenominator && delayDenominator <= 0xFFFF))
			throw new IllegalArgumentException("Invalid delay denominator");
		Objects.requireNonNull(disposeOp);
		Objects.requireNonNull(blendOp);
	}
	
	
	static Fctl read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int sequence = in.readInt32();
		int width    = in.readInt32();
		int height   = in.readInt32();
		int xOffset  = in.readInt32();
		int yOffset  = in.readInt32();
		int delayNumerator   = in.readUint16();
		int delayDenominator = in.readUint16();
		DisposeOperation disposeOp = in.readEnum(DisposeOperation.values());
		BlendOperation blendOp     = in.readEnum(BlendOperation  .values());
		return new Fctl(sequence, width, height, xOffset, yOffset,
			delayNumerator, delayDenominator, disposeOp, blendOp);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeInt32(sequence);
		out.writeInt32(width   );
		out.writeInt32(height  );
		out.writeInt32(xOffset );
		out.writeInt32(yOffset );
		out.writeUint16(delayNumerator  );
		out.writeUint16(delayDenominator);
		out.writeUint8(disposeOp);
		out.writeUint8(blendOp  );
	}
	
	
	
	/*---- Enumerations ----*/
	
	public enum DisposeOperation {
		NONE,
		BACKGROUND,
		PREVIOUS,
	}
	
	
	public enum BlendOperation {
		SOURCE,
		OVER,
	}
	
}
