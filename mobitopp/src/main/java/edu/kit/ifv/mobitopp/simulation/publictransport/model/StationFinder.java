package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.Location;

public interface StationFinder {

	StationPaths findReachableStations(Location location);

}
