package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import nl.jqno.equalsverifier.EqualsVerifier;

public class HouseholdAttributesTest {

	@Test
	public void equalsAndHashCode() {
		Point2D someLocation = new Point2D.Double(0, 0);
		Point2D anotherLocation = new Point2D.Double(1, 1);
		Zone someZone = mock(Zone.class);
		Zone anotherZone = mock(Zone.class);
		EqualsVerifier
				.forClass(HouseholdAttributes.class)
				.withPrefabValues(Point2D.class, someLocation, anotherLocation)
				.withPrefabValues(Zone.class, someZone, anotherZone)
				.usingGetClass()
				.verify();
	}
}
