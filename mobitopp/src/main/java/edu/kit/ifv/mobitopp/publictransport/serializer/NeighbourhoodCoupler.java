package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface NeighbourhoodCoupler {

	void addNeighboursshipBetween(Stop start, Stop end);

}
