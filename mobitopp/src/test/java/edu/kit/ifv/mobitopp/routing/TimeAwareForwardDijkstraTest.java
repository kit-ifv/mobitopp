package edu.kit.ifv.mobitopp.routing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.routing.util.SimplePQ;

public class TimeAwareForwardDijkstraTest {

	private ExampleNetwork network;
	private TravelTime resolveTravelTime;

	@Before
	public void initialise() {
		network = ExampleNetwork.createDefault();
		resolveTravelTime = mock(TravelTime.class);
	}

	@Test
	public void shortestPath() {
		TimeAwareForwardDijkstra dijkstra = new TimeAwareForwardDijkstra(new SimplePQ<>());

		float startTime = 0.0f;
		float expectedTravelTime = 2.0f;
		when(resolveTravelTime.travelTime(network.link12, startTime)).thenReturn(expectedTravelTime);

		Path path = dijkstra.shortestPath(network.graph, resolveTravelTime, network.node1,
				network.node2, startTime);
		float travelTime = path.travelTime();

		assertThat(travelTime, is(equalTo(expectedTravelTime)));

		verify(resolveTravelTime).travelTime(network.link12, startTime);
	}
}
