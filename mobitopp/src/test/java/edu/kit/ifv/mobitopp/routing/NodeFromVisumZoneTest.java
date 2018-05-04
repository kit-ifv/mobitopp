package edu.kit.ifv.mobitopp.routing;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.kit.ifv.mobitopp.visum.VisumZone;

public class NodeFromVisumZoneTest {

	@Test
	public void useExternalInRouteSearch() {
		VisumZone zone = visumZone().withId(1).external().build();
		
		Node node = NodeFromVisumZone.useExternalInRouteSeach(zone);
		
		assertFalse(node.isSink());
	}
	
	@Test
	public void doNotUseInternalInRouteSearch() {
		VisumZone zone = visumZone().withId(1).internal().build();
		
		Node node = NodeFromVisumZone.useExternalInRouteSeach(zone);
		
		assertTrue(node.isSink());
	}
}
