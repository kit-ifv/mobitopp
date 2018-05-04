package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionScan implements RouteSearch {

	private final TransitNetwork transitNetwork;

	public ConnectionScan(TransitNetwork transitNetwork) {
		super();
		this.transitNetwork = transitNetwork;
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, Time atTime) {
		if (scanNotNeeded(fromStart, toEnd, atTime)) {
			return Optional.empty();
		}
		PreparedSearchRequest searchRequest = newSweeperData(fromStart, toEnd, atTime);
		return sweepOver(searchRequest);
	}

	private boolean scanNotNeeded(Stop start, Stop end, Time time) {
		return transitNetwork.scanNotNeeded(start, end, time);
	}
	
	private PreparedSearchRequest newSweeperData(Stop fromStart, Stop toEnd, Time atTime) {
		return SingleSearchRequest.from(fromStart, toEnd, atTime, arrivalSize());
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(
			StopPaths fromStarts, StopPaths toEnds, Time atTime) {
		if (scanNotNeeded(fromStarts, toEnds, atTime)) {
			return Optional.empty();
		}
		PreparedSearchRequest searchRequest = newSearchRequest(fromStarts, toEnds, atTime);
		return sweepOver(searchRequest);
	}

	private Optional<PublicTransportRoute> sweepOver(PreparedSearchRequest searchRequest) {
		return transitNetwork.connections().sweep(searchRequest);
	}

	private boolean scanNotNeeded(StopPaths startStops, StopPaths endStops, Time time) {
		return transitNetwork.scanNotNeeded(startStops, endStops, time);
	}

	PreparedSearchRequest newSearchRequest(StopPaths fromStarts, StopPaths toEnds, Time atTime) {
		return MultipleSearchRequest.from(fromStarts, toEnds, atTime, arrivalSize());
	}
	
	private int arrivalSize() {
		return transitNetwork.stops().size();
	}
	
	@Override
	public String toString() {
		return "ConnectionScan [transitNetwork=" + transitNetwork + "]";
	}

}
