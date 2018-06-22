package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class MultipleSearchRequestTest {

	private ArrivalTimes times;
	private UsedConnections usedConnections;
	private UsedJourneys usedJourneys;
	private StopPaths starts;
	private StopPaths ends;
	private MultipleSearchRequest searchRequest;

	@Before
	public void initialise() {
		times = mock(ArrivalTimes.class);
		usedConnections = mock(UsedConnections.class);
		usedJourneys = mock(UsedJourneys.class);
		starts = mock(StopPaths.class);
		ends = mock(StopPaths.class);
		when(ends.stopPaths()).thenReturn(asList(nearStopPath(), farStopPath()));
		searchRequest = dataFromPaths(starts, ends);
	}

	@Test
	public void isAfterArrivalAtOneOfSeveralEndStops() throws Exception {
		Time beforeArrival = someTime();
		Time atArrival = Data.oneMinuteLater();
		Time afterArrivalAtStop = Data.twoMinutesLater();
		Time afterArrivalAtDestination = atArrival.plus(nearStopPath().duration());
		Connection beforeArrivalConnection = departing(beforeArrival);
		Connection arrivalConnection = departing(atArrival);
		Connection afterArrivalAtStopConnection = departing(afterArrivalAtStop);
		Connection afterArrivalAtDestinationConnection = departing(afterArrivalAtDestination);

		arrivingAt(nearStop(), atArrival);
		arrivingAt(farStop(), afterArrivalAtStop);

		assertFalse(searchRequest.departsAfterArrivalAtEnd(beforeArrivalConnection));
		assertFalse(searchRequest.departsAfterArrivalAtEnd(arrivalConnection));
		assertFalse(searchRequest.departsAfterArrivalAtEnd(afterArrivalAtStopConnection));
		assertTrue(searchRequest.departsAfterArrivalAtEnd(afterArrivalAtDestinationConnection));
	}
	
	@Test
	public void considersFootpathToDestinationWhenCollectingConnections() throws StopNotReachable {
		Time startOfSearch = someTime();
		Time atArrival = Data.oneMinuteLater();
		Time afterArrivalAtStop = Data.twoMinutesLater();
		
		arrivingAt(nearStop(), atArrival);
		arrivingAt(farStop(), afterArrivalAtStop);
		
		searchRequest.collectConnections(usedConnections, startOfSearch);
		
		verify(usedConnections).collectConnections(starts, farStop(), startOfSearch);
	}

	private void arrivingAt(Stop stop, Time atArrival) {
		when(times.get(stop)).thenReturn(atArrival);
	}

	private Connection departing(Time departure) {
		return connection().departsAt(departure).build();
	}

	private Stop nearStop() {
		return Data.someStop();
	}

	private StopPath nearStopPath() {
		return new StopPath(nearStop(), RelativeTime.ofMinutes(10));
	}

	private Stop farStop() {
		return Data.anotherStop();
	}

	private StopPath farStopPath() {
		return new StopPath(farStop(), RelativeTime.ofMinutes(1));
	}

	private MultipleSearchRequest dataFromPaths(StopPaths starts, StopPaths ends) {
		return MultipleSearchRequest.from(starts, ends, times, usedConnections, usedJourneys);
	}
}
