/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Objects;


/**
 * A last-modification time (tIME) chunk. This gives the time
 * of the last image modification. Instances are immutable.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11tIME
 */
public record Time(
		int year,
		int month,
		int day,
		int hour,
		int minute,
		int second)
	implements SmallDataChunk {
	
	
	static final String TYPE = "tIME";
	
	
	/*---- Constructors and factory ----*/
	
	public Time {
		if (!(0 <= year   && year   <= Short.MAX_VALUE)) throw new IllegalArgumentException("Year out of range"  );
		if (!(1 <= month  && month  <= 12             )) throw new IllegalArgumentException("Month out of range" );
		int maxDay = YearMonth.of(year, month).lengthOfMonth();
		if (!(1 <= day    && day    <= maxDay         )) throw new IllegalArgumentException("Day out of range"   );
		if (!(0 <= hour   && hour   <= 23             )) throw new IllegalArgumentException("Hour out of range"  );
		if (!(0 <= minute && minute <= 59             )) throw new IllegalArgumentException("Minute out of range");
		if (!(0 <= second && second <= 60             )) throw new IllegalArgumentException("Second out of range");
	}
	
	
	/**
	 * Constructs a {@code Time} object containing the date and time in UTC representing the specified instant.
	 * @param inst the instant to represent
	 */
	public Time(Instant inst) {
		this(OffsetDateTime.ofInstant(inst, ZoneOffset.UTC));
	}
	
	
	private Time(OffsetDateTime dt) {  // Must be in UTC
		this(dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
			dt.getHour(), dt.getMinute(), dt.getSecond());
	}
	
	
	/**
	 * Returns a {@code Time} object containing the current date and time in UTC.
	 * @return the current date and time (not {@code null})
	 */
	public static Time now() {
		return new Time(Instant.now());
	}
	
	
	static Time read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int year   = in.readUint16();
		int month  = in.readUint8();
		int day    = in.readUint8();
		int hour   = in.readUint8();
		int minute = in.readUint8();
		int second = in.readUint8();
		return new Time(year, month, day, hour, minute, second);
	}
	
	
	/*---- Methods ----*/
	
	/**
	 * Returns an {@code Instant} object representing this date and time in UTC.
	 * Leap seconds (60) are changed (lossy) to 59 to fit the capabilities of {@code OffsetDateTime}.
	 * @return this date and time as an {@code Instant} (not {@code null})
	 */
	public Instant toInstant() {
		return OffsetDateTime.of(year, month, day, hour, minute, Math.min(second, 59), 0, ZoneOffset.UTC).toInstant();
	}
	
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeUint16(year  );
		out.writeUint8 (month );
		out.writeUint8 (day   );
		out.writeUint8 (hour  );
		out.writeUint8 (minute);
		out.writeUint8 (second);
	}
	
}
