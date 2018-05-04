package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionScanAlgorithm implements RouteSearchAlgorithm {

	public ConnectionScanAlgorithm() {
		super();
	}

	/**
	 * SnakeYaml needs a single argument constructor for classes without attributes.
	 * @param dummy  
	 */
	public ConnectionScanAlgorithm(String dummy) {
		super();
	}

	public RouteSearch createRouteScan(
			PublicTransportTimetable publicTransport, Time simulationStart) {
		return publicTransport.createRouteScan();
	}

	@Override
	public Optional<Hook> cleanCacheHook() {
		return Optional.empty();
	}

}
