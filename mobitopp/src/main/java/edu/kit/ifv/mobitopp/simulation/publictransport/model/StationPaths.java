package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.DefaultStopPaths;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.StopPaths;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.Time;

public class StationPaths {

	private final List<StationPath> distances;

	public StationPaths(List<StationPath> distances) {
		super();
		this.distances = distances;
	}

	public Optional<PublicTransportRoute> findRoute(StationPaths toEnd, Time atTime, RouteSearch routeSearch) {
		if (!distances.isEmpty() && !toEnd.distances.isEmpty()) {
			return routeSearch.findRoute(pathsToStops(), toEnd.pathsToStops(), atTime);
		}
		return Optional.empty();
	}

	private StopPaths pathsToStops() {
		List<StopPath> paths = distances
				.stream()
				.flatMap(station -> station.pathsToStops().stream())
				.collect(toList());
		return DefaultStopPaths.from(paths);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((distances == null) ? 0 : distances.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StationPaths other = (StationPaths) obj;
		if (distances == null) {
			if (other.distances != null) {
				return false;
			}
		} else if (!distances.equals(other.distances)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ResolvedStations [stations=" + distances + "]";
	}

}
