package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class TransitNetworkTest {

	private Time searchTime;
	private Stop start;
	private Stop end;
	private Stop unreachableStop;
	private TransitNetwork transitNetwork;
	private Time tooLate;
	private StopPaths starts;
	private StopPaths ends;
	
	@Before
	public void initialise() {
		searchTime = someTime();
		tooLate = oneMinuteLater();
		start = someStop();
		end = anotherStop();
		unreachableStop = otherStop();
		List<Stop> stops = asList(start, end);
		transitNetwork = transitNetwork(stops);
		starts = mock(StopPaths.class);
		ends = mock(StopPaths.class);
		when(starts.stops()).thenReturn(asList(start));
		when(ends.stops()).thenReturn(asList(end));
	}

	private TransitNetwork transitNetwork(List<Stop> stops) {
		Connections connections = new Connections();
		connections.add(usableConnection(start, end));
		return TransitNetwork.createOf(stops, connections);
	}

	private Connection usableConnection(Stop start, Stop end) {
		return connection().startsAt(start).endsAt(end).departsAt(searchTime).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnWrongStopIds() {
		Stop first = stop().withId(0).build();
		Stop tooHighId = stop().withId(2).build();
		Collection<Stop> stops = asList(first, tooHighId);

		TransitNetwork.createOf(stops, noConnections());
	}

	private Connections noConnections() {
		return new Connections();
	}

	@Test
	public void missingEndStop() {
		Stop unreachableEnd = otherStop();

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(start, unreachableEnd, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void correctSearchRequest() {
		boolean scanNotNeeded = transitNetwork.scanNotNeeded(start, end, searchTime);

		assertFalse(scanNotNeeded);
	}

	@Test
	public void missingStartStop() {
		Stop anotherStart = otherStop();

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(anotherStart, end, searchTime);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void whenTimeIsAfterLatestDeparture() {
		boolean scanNotNeeded = transitNetwork.scanNotNeeded(start, end, tooLate);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void correctSearchRequestBetweenSeveralStops() {
		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, searchTime);

		assertFalse(scanNotNeeded);
	}

	@Test
	public void tooEarlySearchRequestBetweenSeveralStops() {
		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, tooLate);

		assertTrue(scanNotNeeded);
	}

	@Test
	public void missingStartStopsOnSearchRequestBetweenSeveralStops() {
		startsAt(unreachableStop);

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, searchTime);

		assertTrue(scanNotNeeded);
	}

	private void startsAt(Stop stop) {
		List<Stop> startStops = asList(stop);
		when(starts.stops()).thenReturn(startStops);
	}

	@Test
	public void missingEndStopsOnSearchRequestBetweenSeveralStops() throws Exception {
		endsAt(unreachableStop);

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, searchTime);

		assertTrue(scanNotNeeded);
	}

	private void endsAt(Stop stop) {
		List<Stop> endStops = asList(stop);
		when(this.ends.stops()).thenReturn(endStops);
	}

	@Test
	public void noEndStopsInSearchRequest() throws Exception {
		endsNowhere();

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, searchTime);

		assertTrue(scanNotNeeded);
	}

	private void endsNowhere() {
		List<Stop> endStops = emptyList();
		when(ends.stops()).thenReturn(endStops);
	}

	@Test
	public void noStartStopsInSearchRequest() throws Exception {
		startsNowhere();

		boolean scanNotNeeded = transitNetwork.scanNotNeeded(starts, ends, searchTime);

		assertTrue(scanNotNeeded);
	}

	private void startsNowhere() {
		List<Stop> startStops = emptyList();
		when(starts.stops()).thenReturn(startStops);
	}

}
