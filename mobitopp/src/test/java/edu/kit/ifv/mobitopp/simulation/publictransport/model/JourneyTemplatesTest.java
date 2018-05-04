package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;

import java.awt.geom.Point2D;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import nl.jqno.equalsverifier.EqualsVerifier;

public class JourneyTemplatesTest {

	@Test
	public void equalsAndHashCode() throws Exception {
		Stop oneStop = someStop();
		Stop anotherStop = anotherStop();
		EqualsVerifier
				.forClass(JourneyTemplates.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, oneStop, anotherStop)
				.usingGetClass()
				.verify();
	}
}
