package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public interface ShortestPathsToStations {

	Optional<RelativeTime> durationTo(Node node);

	StationPaths mapPathsToStations(Map<Node, Station> stations);

}
