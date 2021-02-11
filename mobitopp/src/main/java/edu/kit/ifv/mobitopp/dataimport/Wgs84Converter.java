package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.text.ParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * Converts Coordinates from WGS84 format to the internal cartesian coordinates.
 *
 * valid examples are:
 * 
 * <pre>
 *  123.10075W 49.075N 45.00m
 *  123d06.45000W 49d04.50000N 45.00m
 *  123d06'21.00"W 49d44'32"N 120.0m  (as yet unsupported)
 *  123 06 21.00 W 49 44 32 N 45m     (as yet unsupported)
 * </pre>
 *
 */

@Slf4j
public final class Wgs84Converter {

	public final static String SYSTEM_IDENTIFIER = "WGS84";

	private final static double parseLatitude(String latitude_) throws ParseException {
		verify("latitude_", latitude_);

		final MessageFormat input = new MessageFormat("{0}d{1}");
		final int lastChar = latitude_.length() - 1;

		char direction = latitude_.charAt(lastChar);

		verifyNorthSouth(direction);

		Object[] parts = parseAllExceptTheLastCharacter(latitude_, input, lastChar);

		double degree = Double.NaN;
		double minute = Double.NaN;
		try {
			degree = parseDouble((String) parts[0], Double.NaN);
			minute = parseDouble((String) parts[1], Double.NaN);
		} catch (NumberFormatException nfe) {
			throw warn(
					new ParseException("illegal number format in '" + parts[0] + "' or '" + parts[1] + "'", -1),
					log);
		}

		return (degree + minute / 60.0) * ((direction == 'N') ? 1.0 : -1.0);
	}

	private static void verifyNorthSouth(char direction) throws ParseException {
		if ((direction != 'N') && (direction != 'S')) {
			throw warn(new ParseException("last character must be either 'N' or 'S'", -1), log);
		}
	}

	private static Object[] parseAllExceptTheLastCharacter(String latitude_,
			final MessageFormat input, final int lastChar) throws ParseException {
		return input.parse(latitude_.substring(0, lastChar));
	}

	private static void verify(String parameter, String value) {
		if (null == value) {
			throw warn(new IllegalArgumentException(parameter + "is null"), log);
		}
	}

	private final static double parseLatitudeFromDecimal(String latitudeDecimal_)
			throws ParseException {
		verify("latitudeDecimal_", latitudeDecimal_);

		final int lastChar = latitudeDecimal_.length() - 1;

		char direction = latitudeDecimal_.charAt(lastChar);

		verifyNorthSouth(direction);

		double result = 0.0;
		try {
			result = parseDouble(latitudeDecimal_.substring(0, lastChar), Double.NaN);
		} catch (NumberFormatException nfe) {
			throw warn(new ParseException("illegal number format in '" + latitudeDecimal_ + "'", -1), log);
		}

		if (result == Double.NaN) {
			throw warn(
					new ParseException("illegal number format (NaN) in '" + latitudeDecimal_ + "'", -1),
					log);
		}

		return result;
	}

	private final static double parseLongitude(String longitude_) throws ParseException {
		verify("longitude_", longitude_);

		final MessageFormat input = new MessageFormat("{0}d{1}");
		final int lastChar = longitude_.length() - 1;

		char direction = longitude_.charAt(lastChar);

		verifyWestEast(direction);

		Object[] parts = parseAllExceptTheLastCharacter(longitude_, input, lastChar);

		double degree = Double.NaN;
		double minute = Double.NaN;
		try {
			degree = parseDouble((String) parts[0], Double.NaN);
			minute = parseDouble((String) parts[1], Double.NaN);
		} catch (NumberFormatException nfe) {
			throw warn(
					new ParseException("illegal number format in '" + parts[0] + "' or '" + parts[1] + "'", -1),
					log);
		}

		return (degree + minute / 60.0) * ((direction == 'E') ? 1.0 : -1.0);
	}

	private static void verifyWestEast(char direction) throws ParseException {
		if ((direction != 'W') && (direction != 'E')) {
			throw warn(new ParseException("last character must be either 'W' or 'E'", -1), log);
		}
	}

	private final static double parseLongitudeFromDecimal(String longitudeDecimal_)
			throws ParseException {
		verify("longitudeDecimal_", longitudeDecimal_);

		final int lastChar = longitudeDecimal_.length() - 1;

		char direction = longitudeDecimal_.charAt(lastChar);

		verifyWestEast(direction);

		double result = 0.0;
		try {
			result = parseDouble(longitudeDecimal_.substring(0, lastChar), Double.NaN);
		} catch (NumberFormatException nfe) {
			throw warn(new ParseException("illegal number format in '" + longitudeDecimal_ + "'", -1), log);
		}

		if (result == Double.NaN) {
			throw warn(new ParseException("illegal number format (NaN) in '" + longitudeDecimal_ + "'", -1), log);
		}

		return result;
	}

	private static double parseDouble(String value, double defaultValue) {
		if (null == value) {
			return defaultValue;
		}
		return Double.parseDouble(value);
	}

	private static final Point2D getWorldPoint(double latitude, double longitude)
			throws IllegalArgumentException {
		return new Point2D.Double(longitude, latitude);
	}

	public static Point2D getWorldPoint(String wgs84Coordinate_) throws ParseException {
		verify("wgs84Coordinate_", wgs84Coordinate_);

		final MessageFormat input = createParserFormat();

		Object[] parts = input.parse(wgs84Coordinate_);
		double latitude = parseLatitude(parts);
		double longitude = parseLongitude(parts);
		return getWorldPoint(latitude, longitude);
	}

	/**
	 * Attention Normally latitude is given before longitude for coordinates. In
	 * this case it was requested to be the other way around.
	 * 
	 * @return format to parse coordinates
	 */
	private static MessageFormat createParserFormat() {
		return new MessageFormat("{1} {0} {2}");
	}

	private static double parseLatitude(Object[] parts) throws ParseException {
		double latitude = 0.0;
		try {
			latitude = parseLatitudeFromDecimal((String) parts[0]);
		} catch (ParseException pe) {
			try {
				latitude = parseLatitude((String) parts[0]);
			} catch (ParseException innerPe) {
				throw warn(new ParseException("illegal coordinate component in latitude: " + parts[0], -1), log);
			}
		}
		return latitude;
	}

	private static double parseLongitude(Object[] parts) throws ParseException {
		double longitude = 0.0;
		try {
			longitude = parseLongitudeFromDecimal((String) parts[1]);
		} catch (ParseException pe) {
			try {
				longitude = parseLongitude((String) parts[1]);
			} catch (ParseException innerPe) {
				throw warn(new ParseException("illegal coordinate component in longitude: " + parts[1], -1), log);
			}
		}
		return longitude;
	}

}
