package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class MultipleSearchRequestTest {

	private ArrivalTimes times;
	private UsedConnections usedConnections;
	private UsedJourneys usedJourneys;
	
	@Before
	public void initialise() {
		times = mock(ArrivalTimes.class);
		usedConnections = mock(UsedConnections.class);
		usedJourneys = mock(UsedJourneys.class);
	}

	@Test
	public void isAfterArrivalAtOneOfSeveralEndStops() throws Exception {
		StopPaths starts = mock(StopPaths.class);
		StopPaths ends = mock(StopPaths.class);
		when(ends.stops()).thenReturn(asList(nearStop(), farStop()));
		PreparedSearchRequest searchRequest = dataFromPaths(starts, ends);
		Time beforeArrival = someTime();
		Time atArrival = Data.oneMinuteLater();
		Time afterArrival = Data.twoMinutesLater();
		Connection beforeArrivalConnection = departing(beforeArrival);
		Connection arrivalConnection = departing(atArrival);
		Connection afterArrivalConnection = departing(afterArrival);

		arrivingAt(nearStop(), atArrival);
		arrivingAt(farStop(), afterArrival);

		assertFalse(searchRequest.departsAfterArrivalAtEnd(beforeArrivalConnection));
		assertFalse(searchRequest.departsAfterArrivalAtEnd(arrivalConnection));
		assertTrue(searchRequest.departsAfterArrivalAtEnd(afterArrivalConnection));
	}

	private void arrivingAt(Stop stop, Time atArrival) {
		when(times.getConsideringMinimumChangeTime(stop)).thenReturn(atArrival);
	}

	private Connection departing(Time beforeArrival) {
		return connection().departsAt(beforeArrival).build();
	}

	private Stop nearStop() {
		return Data.someStop();
	}

	private Stop farStop() {
		return Data.anotherStop();
	}
	
	private PreparedSearchRequest dataFromPaths(StopPaths starts, StopPaths ends) {
		return MultipleSearchRequest.from(starts, ends, times, usedConnections, usedJourneys);
	}
}
