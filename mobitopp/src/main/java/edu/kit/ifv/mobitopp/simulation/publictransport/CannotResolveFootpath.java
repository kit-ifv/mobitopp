package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;


public class CannotResolveFootpath implements SearchFootpath {

	@Override
	public StationFinder createStationFinder(Stations stations) {
		return location -> null;
	}

	@Override
	public NodeResolver nodeResolver() {
		return node -> null;
	}

}
