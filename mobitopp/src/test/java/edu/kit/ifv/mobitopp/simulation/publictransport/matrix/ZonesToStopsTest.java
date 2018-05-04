package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class ZonesToStopsTest {

	private static final int someId = 10;
	private static final int anotherId = 11;

	private Stop someStop;
	private Stop anotherStop;
	private Zone someZone;
	private Zone anotherZone;
	private ZonePolygon somePolygon;
	private ZonePolygon anotherPolygon;

	@Before
	public void initialise() throws Exception {
		someStop = Data.someStop();
		anotherStop = Data.anotherStop();
		someZone = mock(Zone.class);
		anotherZone = mock(Zone.class);
		somePolygon = mock(ZonePolygon.class);
		anotherPolygon = mock(ZonePolygon.class);
		when(someZone.getZonePolygon()).thenReturn(somePolygon);
		when(anotherZone.getZonePolygon()).thenReturn(anotherPolygon);
		when(someZone.getOid()).thenReturn(someId);
		when(anotherZone.getOid()).thenReturn(anotherId);
	}

	@Test
	public void freshInstance() {
		ZonesToStops zonesToStops = new ZonesToStops(someZone);

		assertTrue(zonesToStops.stopFor(someZone).neighbours().isEmpty());
	}

	@Test
	public void assignsSingleStopToSingleZone() throws Exception {
		when(somePolygon.contains(any())).thenReturn(true);
		ZonesToStops zonesToStops = new ZonesToStops(someZone);

		zonesToStops.assign(someStop);

		assertThat(zonesToStops.stopFor(someId).neighbours(), contains(someStop));
		assertThat(zonesToStops.stopFor(someZone).neighbours(), contains(someStop));
		verify(somePolygon).contains(any());
	}

	@Test
	public void doesNotAssignStopToZoneWhenStopIsOutside() throws Exception {
		when(somePolygon.contains(any())).thenReturn(false);
		ZonesToStops zonesToStops = new ZonesToStops(someZone);

		zonesToStops.assign(someStop);

		assertThat(zonesToStops.stopFor(someId).neighbours(), not(contains(someStop)));
		assertThat(zonesToStops.stopFor(someZone).neighbours(), not(contains(someStop)));
		verify(somePolygon).contains(any());
	}

	@Test
	public void assignStopsToDifferentZones() throws Exception {
		when(somePolygon.contains(someStop.coordinate())).thenReturn(true);
		when(anotherPolygon.contains(anotherStop.coordinate())).thenReturn(true);
		when(somePolygon.contains(anotherStop.coordinate())).thenReturn(false);
		when(anotherPolygon.contains(someStop.coordinate())).thenReturn(false);
		ZonesToStops zonesToStops = new ZonesToStops(someZone, anotherZone);

		zonesToStops.assign(someStop);
		zonesToStops.assign(anotherStop);

		assertThat(zonesToStops.stopFor(someId).neighbours(), contains(someStop));
		assertThat(zonesToStops.stopFor(anotherId).neighbours(), contains(anotherStop));
		assertThat(zonesToStops.stopFor(someZone).neighbours(), contains(someStop));
		assertThat(zonesToStops.stopFor(anotherZone).neighbours(), contains(anotherStop));
		verify(somePolygon, times(2)).contains(any());
		verify(anotherPolygon, times(2)).contains(any());
	}

	@Test
	public void assignSeveralStopsToSingleZone() throws Exception {
		when(somePolygon.contains(any())).thenReturn(true);
		ZonesToStops zonesToStops = new ZonesToStops(someZone);

		zonesToStops.assign(someStop);
		zonesToStops.assign(anotherStop);

		assertThat(zonesToStops.stopFor(someId).neighbours(), containsInAnyOrder(someStop, anotherStop));
		assertThat(zonesToStops.stopFor(someZone).neighbours(), containsInAnyOrder(someStop, anotherStop));
		verify(somePolygon, times(2)).contains(any());
	}
}
