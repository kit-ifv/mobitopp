package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.simulation.Location;

public interface ShortestPathSearch {

	ShortestPathsToStations search(Node start, Collection<Node> targets);

	ShortestPathsToStations search(Location location, Collection<Node> targets);

}
