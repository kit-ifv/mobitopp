package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;

public class LocationParserTest {

	private Point2D coordinate;
	private int roadAccessEdgeId;
	private double roadPosition;
	private Location location;
	private LocationParser parser;

	@Before
	public void initialise() {
		coordinate = Data.coordinate(1.0f, 1.0f);
		roadAccessEdgeId = 2;
		roadPosition = 1.0d;
		location = new Location(coordinate, roadAccessEdgeId, roadPosition);
		parser = new LocationParser();
	}

	@Test
	public void serialiseRounded() {
		String serialised = parser.serialiseRounded(location);

		String expected = expect(LocationParser.rounded);
		assertThat(serialised, is(equalTo(expected)));
	}

	private String expect(String serialiseRoundedformat) {
		return String.format(Locale.ENGLISH, serialiseRoundedformat, coordinate.getX(),
				coordinate.getY(), roadAccessEdgeId, roadPosition);
	}

	@Test
	public void serialise() {
		String serialised = parser.serialise(location);

		String expected = expect(LocationParser.precise);
		assertThat(serialised, is(equalTo(expected)));
	}

	@Test
	public void parse() {
		Location parsed = parser.parse(parser.serialise(location));

		assertThat(parsed, is(equalTo(location)));
	}
	
	@Test
	public void serialiseWholeDouble() {
		double preciseValue = 0.7274998254d;
		Point2D coordinate = new Point2D.Double(preciseValue, preciseValue);
		double position = preciseValue;
		
		String serialised = parser.serialise(new Location(coordinate, roadAccessEdgeId, position));
		
		String expected = "(" + coordinate.getX() + "," + coordinate.getY() + ": " + roadAccessEdgeId + ", "
				+ position + ")";
		assertEquals(expected, serialised);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsParsing() {
		parser.parse("incorrect format");
	}
}
