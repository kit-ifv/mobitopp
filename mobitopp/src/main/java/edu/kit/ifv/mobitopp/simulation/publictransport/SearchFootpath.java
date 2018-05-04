package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;

public interface SearchFootpath {

	StationFinder createStationFinder(Stations stations);

	NodeResolver nodeResolver();

}