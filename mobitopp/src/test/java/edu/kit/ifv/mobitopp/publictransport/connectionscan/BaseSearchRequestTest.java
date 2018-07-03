package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class BaseSearchRequestTest {

	private ArrivalTimes times;
	private UsedConnections usedConnections;
	private UsedJourneys usedJourneys;
	private BaseSearchRequest request;
	private Stop start;
	private Stop neighbour;
	private RelativeTime walkTime;
	private Time departureAtStart;
	
	@Before
	public void initialise() {
		times = mock(ArrivalTimes.class);
		usedConnections = mock(UsedConnections.class);
		usedJourneys = mock(UsedJourneys.class);
		start = Data.someStop();
		neighbour = Data.anotherStop();
		walkTime = RelativeTime.ofMinutes(1);
		start.addNeighbour(neighbour, walkTime);
		departureAtStart = Data.someTime();
		when(times.get(start)).thenReturn(departureAtStart);
		
		request = newRequest();
	}

	@Test
	public void updatesArrivalAtNeighbours() {
		Time departureAtNeighbour = departureAtStart.plus(walkTime.multiplyBy(2));
		when(times.get(neighbour)).thenReturn(departureAtNeighbour);
		
		request.initialise(start, departureAtStart);
		
		verify(times).get(neighbour);
		verify(times).set(neighbour, departureAtStart.plus(walkTime));
	}
	
	@Test
	public void considersCurrentArrivalBeforeUpdate() {
		Time departureAtNeighbour = departureAtStart;
		when(times.get(neighbour)).thenReturn(departureAtNeighbour);
		
		request.initialise(start, departureAtStart);
		
		verify(times).get(neighbour);
		verifyNoMoreInteractions(times);
	}

	private BaseSearchRequest newRequest() {
		return new BaseSearchRequest(times, usedConnections, usedJourneys) {
			
			@Override
			public boolean departsAfterArrivalAtEnd(Connection connection) {
				throw new RuntimeException("Not necessary for this test");
			}
			
			@Override
			protected List<Connection> collectConnections(UsedConnections usedConnections, Time time)
					throws StopNotReachable {
				throw new RuntimeException("Not necessary for this test");
			}
		};
	}
}
