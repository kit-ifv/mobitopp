package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZoneLocationSelectorTest {

	private static final int someZoneId = 1;
	private static final float xCoordinate = 1.0f;
	private static final float yCoordinate = 2.0f;

	private SimpleRoadNetwork roadNetwork;
	private Location locationOnEdge;
	private Zone zone;
	private SimpleEdge edge;
	private VisumPoint2 point;

	@Before
	public void initialise() {
		roadNetwork = mock(SimpleRoadNetwork.class);
		zone = mock(Zone.class);
		edge = mock(SimpleEdge.class);
		
		point = new VisumPoint2(xCoordinate, yCoordinate);
		int edgeId = 2;
		double roadPosition = 0.0d;
		locationOnEdge = new Location(coordinate(), edgeId, roadPosition);
		
		when(edge.nearestPositionOnEdge(coordinate())).thenReturn(roadPosition);
		when(zone.nearestEdge(coordinate())).thenReturn(edge);
		when(roadNetwork.zones()).thenReturn(singletonMap(someZoneId, zone));
	}

	@Test
	public void selectNearestOnEdge() {
		VisumZone visumZone = visumZone().withId(someZoneId).build();
		ZoneLocationSelector selector = new ZoneLocationSelector(roadNetwork);
		
		Location location = selector.selectLocation(visumZone, point);

		assertThat(location, is(equalTo(locationOnEdge)));

		verify(zone).nearestEdge(coordinate());
		verify(edge).nearestPositionOnEdge(coordinate());
	}

	private Point2D coordinate() {
		return point.asPoint2D();
	}

}
