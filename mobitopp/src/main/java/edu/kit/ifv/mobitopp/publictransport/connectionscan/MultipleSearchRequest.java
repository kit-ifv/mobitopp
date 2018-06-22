package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.Time;

class MultipleSearchRequest extends BaseSearchRequest {

	private final StopPaths fromStarts;
	private final StopPaths toEnds;
	
	private MultipleSearchRequest(
			StopPaths starts, StopPaths toEnds, ArrivalTimes times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.fromStarts = starts;
		this.toEnds = toEnds;
	}

	static MultipleSearchRequest from(StopPaths fromStarts, StopPaths toEnds, Time atTime, int numberOfStops) {
		ArrivalTimes times = MultipleStarts.create(fromStarts, atTime, numberOfStops);
		UsedConnections usedConnections = new DefaultUsedConnections(numberOfStops);
		UsedJourneys usedJourneys = new DefaultUsedJourneys();
		return from(fromStarts, toEnds, times, usedConnections, usedJourneys);
	}

	static MultipleSearchRequest from(
			StopPaths fromStarts, StopPaths toEnds, ArrivalTimes times, UsedConnections usedConnections,
			UsedJourneys usedJourneys) {
		MultipleSearchRequest searchRequest = new MultipleSearchRequest(fromStarts, toEnds, times, usedConnections,
				usedJourneys);
		times.initialise(searchRequest::initialise);
		return searchRequest;
	}

	@Override
	public Optional<PublicTransportRoute> createRoute() {
		return super.createRoute().map(this::addFootpaths);
	}
	
	private PublicTransportRoute addFootpaths(PublicTransportRoute route) {
		StopPath pathToStart = fromStarts.pathTo(route.start());
		StopPath pathFromEnd = toEnds.pathTo(route.end());
		return new RouteIncludingFootpaths(route, pathToStart, pathFromEnd);
	}

	@Override
	protected List<Connection> collectConnections(UsedConnections usedConnections, Time time)
			throws StopNotReachable {
		Optional<Stop> toEnd = stopWithEarliestArrival();
		if (toEnd.isPresent()) {
			return usedConnections.collectConnections(fromStarts, toEnd.get(), time);
		}
		return emptyList();
	}

	private Optional<Stop> stopWithEarliestArrival() {
		Stop stop = null;
		Time currentArrival = null;
		for (StopPath path : toEnds.stopPaths()) {
			Stop current = path.stop();
			Time currentTime = arrivalAt(current);
			Time includingFootpath = path.arrivalTimeStartingAt(currentTime);
			if (null == currentArrival || includingFootpath.isBefore(currentArrival)) {
				stop = current;
				currentArrival = includingFootpath;
			}
		}
		return ofNullable(stop);
	}

	@Override
	public boolean departsAfterArrivalAtEnd(Connection connection) {
		return isAfterArrivalAtEnd(connection.departure());
	}
	
	private boolean isAfterArrivalAtEnd(Time departure) {
		for (StopPath stopPath : toEnds.stopPaths()) {
			Stop stop = stopPath.stop();
			Time arrivalAtStop = arrivalAt(stop);
			Time arrival = stopPath.arrivalTimeStartingAt(arrivalAtStop);
			if (arrival.isBefore(departure)) {
				return true;
			}
		}
		return false;
	}

}
