package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.network.NodeBuilder.node;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPath;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class DijkstraResultTest {

	private static final float oneMinute = 1.0f;
	private Node existingNode;
	private Node missingNode;
	private Station existingStation;
	private Station missingStation;
	private Path oneMinutePath;

	@Before
	public void initialise() throws Exception {
		oneMinutePath = mock(Path.class);
		when(oneMinutePath.travelTime()).thenReturn(oneMinute);
		existingNode = node().withId(1).build();
		missingNode = node().withId(2).build();
		existingStation = station().with(1).build();
		missingStation = station().with(2).build();
	}

	@Test
	public void durationToExistingNode() throws Exception {
		ShortestPathsToStations result = new DijkstraResult(singlePath());

		Optional<RelativeTime> duration = result.durationTo(existingNode);

		assertThat(duration, isPresent());
		assertThat(duration, hasValue(oneMinute()));
	}

	@Test
	public void durationToMissingNode() throws Exception {
		ShortestPathsToStations result = new DijkstraResult(singlePath());

		Optional<RelativeTime> duration = result.durationTo(missingNode);

		assertThat(duration, isEmpty());
	}

	@Test
	public void resolvesExistingStationToExistingNode() throws Exception {
		when(oneMinutePath.isValid()).thenReturn(true);
		ShortestPathsToStations result = new DijkstraResult(singlePath());

		StationPaths resolvedStations = result.mapPathsToStations(singleStationMap());

		StationPaths expectedStations = new StationPaths(singleDistance());
		assertThat(resolvedStations, is(equalTo(expectedStations)));
	}

	@Test
	public void doesNotResolveMissingStation() throws Exception {
		ShortestPathsToStations result = new DijkstraResult(singlePath());

		StationPaths resolvedStations = result.mapPathsToStations(missingStationMap());

		StationPaths expectedStations = new StationPaths(emptyList());
		assertThat(resolvedStations, is(equalTo(expectedStations)));
	}

	private List<StationPath> singleDistance() {
		return asList(new StationPath(existingStation, oneMinute()));
	}

	private Map<Node, Station> singleStationMap() {
		Map<Node, Station> resolved = new HashMap<>();
		resolved.put(existingNode, existingStation);
		return resolved;
	}

	private Map<Node, Station> missingStationMap() {
		Map<Node, Station> resolved = new HashMap<>();
		resolved.put(missingNode, missingStation);
		return resolved;
	}

	private Map<Node, Path> singlePath() {
		Map<Node, Path> result = new HashMap<>();
		result.put(existingNode, oneMinutePath);
		return result;
	}

	private RelativeTime oneMinute() {
		return RelativeTime.of(1, ChronoUnit.MINUTES);
	}
}
