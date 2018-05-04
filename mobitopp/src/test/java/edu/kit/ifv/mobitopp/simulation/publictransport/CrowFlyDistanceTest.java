package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.network.NodeBuilder.node;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPath;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class CrowFlyDistanceTest {

	private static final Point2D.Double farAway = new Point2D.Double(2, 2);
	private static final Point2D.Double notThatFarAway = new Point2D.Double(1, 1);
	private static final int onlyNearest = 1;
	private static final int twoNearest = 2;
	private static final int all = Integer.MAX_VALUE;
	private Node someNode;
	private Station someStation;
	private Node anotherNode;
	private Station anotherStation;
	private Node otherNode;
	private Station otherStation;
	private Location someLocation;

	@Before
	public void initialise() throws Exception {
		someLocation = new Example().location();
		someNode = node().withId(1).at(someLocation.coordinate).build();
		someStation = station().at(someNode).build();
		anotherNode = node().withId(2).at(farAway).build();
		anotherStation = station().at(anotherNode).build();
		otherNode = node().withId(3).at(notThatFarAway).build();
		otherStation = station().at(otherNode).build();
	}

	@Test
	public void durationIsAlwaysZero() throws Exception {
		ShortestPathsToStations result = new CrowFlyDistance(someLocation, all);

		Optional<RelativeTime> duration = result.durationTo(someNode);

		assertThat(duration, hasValue(RelativeTime.ZERO));
	}

	@Test
	public void findSingleStationWhenOnlyOneIsAvailable() throws Exception {
		ShortestPathsToStations result = new CrowFlyDistance(someLocation, all);

		StationPaths resolved = result.mapPathsToStations(singleStation());

		assertThat(resolved, is(equalTo(singleExpectedDistance())));
	}

	@Test
	public void findNearestStationWhenSeveralAreAvailable() throws Exception {
		ShortestPathsToStations result = new CrowFlyDistance(someLocation, onlyNearest);

		StationPaths resolved = result.mapPathsToStations(severalStations());

		assertThat(resolved, is(equalTo(nearestDistance())));
	}

	@Test
	public void findSeveralNearestStationsWhenSeveralAreAvailable() throws Exception {
		ShortestPathsToStations result = new CrowFlyDistance(someLocation, twoNearest);

		StationPaths resolved = result.mapPathsToStations(severalStations());

		assertThat(resolved, is(equalTo(twoNearestDistances())));
	}

	private Map<Node, Station> singleStation() {
		HashMap<Node, Station> singleStation = new HashMap<>();
		singleStation.put(someNode, someStation);
		return singleStation;
	}

	private Map<Node, Station> severalStations() {
		HashMap<Node, Station> singleStation = new HashMap<>();
		singleStation.put(someNode, someStation);
		singleStation.put(otherNode, otherStation);
		singleStation.put(anotherNode, anotherStation);
		return singleStation;
	}

	private StationPaths singleExpectedDistance() {
		StationPath singleDistance = pathTo(someStation);
		return new StationPaths(asList(singleDistance));
	}

	private StationPaths nearestDistance() {
		StationPath singleDistance = pathTo(someStation);
		return new StationPaths(asList(singleDistance));
	}

	private StationPaths twoNearestDistances() {
		StationPath nearestDistance = pathTo(someStation);
		StationPath secondNearestDistance = pathTo(otherStation);
		return new StationPaths(asList(nearestDistance, secondNearestDistance));
	}

	private StationPath pathTo(Station station) {
		StationPath nearestDistance = new StationPath(station, RelativeTime.ZERO);
		return nearestDistance;
	}
}
