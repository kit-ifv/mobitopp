package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.network.NodeBuilder.node;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.ShortestPathSearch;
import edu.kit.ifv.mobitopp.simulation.publictransport.ShortestPathsToStations;

public class ReachableStationsFinderTest {

	private ShortestPathSearch search;
	private HashMap<Node, Station> stations;
	private Node someNode;
	private Node anotherNode;
	private Station someStation;
	private Station anotherStation;
	private Location someLocation;
	private ShortestPathsToStations result;

	@Before
	public void initialise() throws Exception {
		search = mock(ShortestPathSearch.class);
		result = mock(ShortestPathsToStations.class);
		stations = new HashMap<>();
		someNode = node().withId(1).build();
		anotherNode = node().withId(2).build();
		someStation = station().with(1).build();
		anotherStation = station().with(2).build();
		stations.put(someNode, someStation);
		stations.put(anotherNode, anotherStation);
		someLocation = new Example().location();
	}

	@Test
	public void resolvesSearchResultToPathFromLocationToStations() throws Exception {
		StationPaths delegated = placeholder();
		when(search.search(someLocation, nodes())).thenReturn(result);
		when(result.mapPathsToStations(stations)).thenReturn(delegated);
		ReachableStationsFinder factory = new ReachableStationsFinder(search, stations);

		StationPaths resolvedStations = factory.findReachableStations(someLocation);

		assertThat(resolvedStations, is(sameInstance(delegated)));
		verify(search).search(someLocation, nodes());
	}

	private static StationPaths placeholder() {
		return new StationPaths(emptyList());
	}

	private Collection<Node> nodes() {
		return stations.keySet();
	}

}
