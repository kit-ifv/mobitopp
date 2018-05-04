package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class CsvFormat {

	private static final int yIndex = 1;
	private static final int xIndex = 0;
	protected static final String separator = ";";
	protected static final String coordinateSeparator = ",";

	public CsvFormat() {
		super();
	}

	protected long asSeconds(RelativeTime changeTime) {
		return changeTime.toDuration().getSeconds();
	}

	protected String serialized(Point2D point) {
		return point.getX() + coordinateSeparator + point.getY();
	}

	protected Point2D pointOf(String serialized) {
		String[] coordinates = serialized.split(coordinateSeparator);
		double x = Double.parseDouble(coordinates[xIndex]);
		double y = Double.parseDouble(coordinates[yIndex]);
		return new Point2D.Double(x, y);
	}

}