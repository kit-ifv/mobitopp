package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.HashMap;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.ShortestPathSearch;
import edu.kit.ifv.mobitopp.simulation.publictransport.ShortestPathsToStations;

class ReachableStationsFinder implements StationFinder {

	private final ShortestPathSearch search;
	private final HashMap<Node, Station> stations;

	/**
	 * Reachability of stations has to be configured in the implementation of {@link ShortestPathSearch}.
	 */
	ReachableStationsFinder(ShortestPathSearch search, HashMap<Node, Station> nodeToStation) {
		super();
		this.search = search;
		stations = nodeToStation;
	}

	@Override
	public StationPaths findReachableStations(Location location) {
		ShortestPathsToStations result = search.search(location, stations.keySet());

		return result.mapPathsToStations(stations);
	}

}