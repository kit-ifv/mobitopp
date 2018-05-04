package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPath;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class CrowFlyDistance implements ShortestPathsToStations {

	private final Location location;
	private final int targetsToResolve;

	CrowFlyDistance(Location location, int targetsToResolve) {
		super();
		this.location = location;
		this.targetsToResolve = targetsToResolve;
	}

	@Override
	public Optional<RelativeTime> durationTo(Node node) {
		return Optional.of(RelativeTime.ZERO);
	}

	@Override
	public StationPaths mapPathsToStations(Map<Node, Station> stations) {
		return nearestByLinearDistance(stations.values());
	}

	private StationPaths nearestByLinearDistance(Collection<Station> values) {
		List<StationPath> distances = values
				.stream()
				.sorted(comparing(this::distance))
				.map(station -> new StationPath(station, RelativeTime.ZERO))
				.limit(targetsToResolve)
				.collect(toList());
		return new StationPaths(distances);
	}
	
	private double distance(Station station) {
		Distance distance = new Distance(location);
		station.forEachNode(distance::ifNearer);
		return distance.shortest();
	}

}
