package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPath;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.RelativeTime;

class DijkstraResult implements ShortestPathsToStations {

	private static final int minutesOfHour = 60;
	private static final int minuteToHour = 60;
	private static final int minuteToSecond = 60;

	private final Map<Node, Path> paths;

	DijkstraResult(Map<Node, Path> paths) {
		super();
		this.paths = paths;
	}

	@Override
	public Optional<RelativeTime> durationTo(Node node) {
		if (paths.containsKey(node)) {
			Path path = paths.get(node);
			return Optional.of(durationOf(path));
		}
		return Optional.empty();
	}

	@Override
	public StationPaths mapPathsToStations(Map<Node, Station> stations) {
		List<StationPath> resolved = new ArrayList<>();
		for (Entry<Node, Path> entry : paths.entrySet()) {
			Node node = entry.getKey();
			if (stations.containsKey(node)) {
				Station station = stations.get(node);
				Path path = entry.getValue();
				if (isValid(path)) {
					RelativeTime duration = durationOf(path);
					resolved.add(new StationPath(station, duration));
				}
			}
		}
		return new StationPaths(resolved);
	}

	private static RelativeTime durationOf(Path path) {
		float hours = hoursOf(path);
		float minutes = minutesOf(path);
		float seconds = secondsOf(minutes);
    return RelativeTime
        .ofHours((int) hours)
        .plusMinutes((int) minutes)
        .plusSeconds((int) seconds);
	}

	private static float hoursOf(Path path) {
		return path.travelTime() / minuteToHour;
	}

	private static float minutesOf(Path path) {
		return path.travelTime() % minutesOfHour;
	}

	private static float secondsOf(float minutes) {
		return minutes * minuteToSecond % minuteToSecond;
	}

	private boolean isValid(Path path) {
		return null != path && path.isValid();
	}

}
