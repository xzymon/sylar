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
 * A primary chromaticities and white point (cHRM) chunk. This specifies the
 * 1931 CIE <var>x</var> and <var>y</var> coordinates of the RGB primaries
 * used in the image and the reference white point. Instances are immutable.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11cHRM
 */
public record Chrm(
		int whitePointX, int whitePointY,
		int redX       , int redY       ,
		int greenX     , int greenY     ,
		int blueX      , int blueY      )
	implements SmallDataChunk {
	
	
	static final String TYPE = "cHRM";
	
	
	/*---- Constructors and factory ----*/
	
	public Chrm {
		if (whitePointX < 0 || whitePointY < 0 ||
		    redX        < 0 || redY        < 0 ||
		    greenX      < 0 || greenY      < 0 ||
		    blueX       < 0 || blueY       < 0)
			throw new IllegalArgumentException("Invalid int32 value");
	}
	
	
	public Chrm(
			double whitePointX, double whitePointY,
			double redX       , double redY       ,
			double greenX     , double greenY     ,
			double blueX      , double blueY      ) {
		this(
			convert(whitePointX), convert(whitePointY),
			convert(redX       ), convert(redY       ),
			convert(greenX     ), convert(greenY     ),
			convert(blueX      ), convert(blueY      ));
	}
	
	
	private static int convert(double val) {
		val *= 100_000;
		if (!(Double.isFinite(val) && -1.0 < val && val <= Integer.MAX_VALUE + 1.0))
			throw new IllegalArgumentException("Coordinate value out of range");
		long result = Math.round(val);
		if (!(0 <= result && result <= Integer.MAX_VALUE))
			throw new IllegalArgumentException("Coordinate value out of range");
		return Math.toIntExact(result);
	}
	
	
	static Chrm read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int whitePointX = in.readInt32();
		int whitePointY = in.readInt32();
		int redX        = in.readInt32();
		int redY        = in.readInt32();
		int greenX      = in.readInt32();
		int greenY      = in.readInt32();
		int blueX       = in.readInt32();
		int blueY       = in.readInt32();
		return new Chrm(
			whitePointX, whitePointY,
			redX       , redY       ,
			greenX     , greenY     ,
			blueX      , blueY      );
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeInt32(whitePointX);
		out.writeInt32(whitePointY);
		out.writeInt32(redX       );
		out.writeInt32(redY       );
		out.writeInt32(greenX     );
		out.writeInt32(greenY     );
		out.writeInt32(blueX      );
		out.writeInt32(blueY      );
	}
	
}
