package edu.kit.ifv.mobitopp.simulation;

import java.awt.geom.Point2D;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import nl.jqno.equalsverifier.EqualsVerifier;

public class LocationTest {

	private static final double margin = 0.0000001d;

	@Test
	public void equalsAndHashCode() throws Exception {
		Point2D point = new Point2D.Double(0, 0);
		Point2D other = new Point2D.Double(1, 1);
		EqualsVerifier
				.forClass(Location.class)
				.withPrefabValues(Point2D.class, point, other)
				.withOnlyTheseFields("coordinate")
				.usingGetClass()
				.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifiesTooLowRoadPosition() {
		double tooLowRoadPosition = 0.0d - margin;
		newLocation(tooLowRoadPosition);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifiesTooHighRoadPosition() {
		double tooHighRoadPosition = 1.0d + margin;
		newLocation(tooHighRoadPosition);
	}

	private Location newLocation(double tooLowRoadPosition) {
		Point2D somePoint = Data.coordinate(0, 0);
		int someEdge = 0;
		return new Location(somePoint, someEdge, tooLowRoadPosition);
	}
}
