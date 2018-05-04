package edu.kit.ifv.mobitopp.simulation;

import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationParser {

	static final String precise = "(%s,%s: %d, %s)";
	static final String rounded = "(%.3f,%.3f: %d, %.3f)";
	private static final Pattern format = Pattern.compile("\\((.*),(.*): (.*), (.*)\\)");

	public String serialise(Location location) {
		return serialise(location, precise);
	}

	private String serialise(Location location, String format) {
		return String.format(Locale.ENGLISH, format, location.coordinatesP().getX(),
				location.coordinatesP().getY(), location.roadAccessEdgeId(), location.roadPosition());
	}
	
	public String serialiseRounded(Location location) {
		return serialise(location, rounded);
	}

	public Location parse(String serialise) {
		Matcher matcher = format.matcher(serialise);
		if (matcher.matches()) {
			Point2D coordinate = coordinateOf(matcher);
			int roadAccessEdgeId = roadAccessOf(matcher);
			double roadPosition = roadPositionOf(matcher);
			return new Location(coordinate, roadAccessEdgeId, roadPosition);
		}
		throw new IllegalArgumentException("Could not convert to Location: " + serialise);
	}

	private Point2D coordinateOf(Matcher matcher) {
		double x = Double.parseDouble(matcher.group(1));
		double y = Double.parseDouble(matcher.group(2));
		return new Point2D.Double(x, y);
	}

	private int roadAccessOf(Matcher matcher) {
		return Integer.parseInt(matcher.group(3));
	}

	private double roadPositionOf(Matcher matcher) {
		return Double.parseDouble(matcher.group(4));
	}
}
