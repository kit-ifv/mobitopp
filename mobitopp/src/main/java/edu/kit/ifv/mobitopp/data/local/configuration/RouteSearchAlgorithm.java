package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.time.Time;

public interface RouteSearchAlgorithm {

	RouteSearch createRouteScan(
			PublicTransportTimetable publicTransport, Time simulationStart);

	Optional<Hook> cleanCacheHook();

}
