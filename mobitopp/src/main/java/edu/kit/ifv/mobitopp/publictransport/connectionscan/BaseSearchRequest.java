package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

abstract class BaseSearchRequest implements PreparedSearchRequest {

	private static final int firstConnection = 0;
	
	private final ArrivalTimes times;
	private final UsedConnections usedConnections;
	private final UsedJourneys usedJourneys;

	BaseSearchRequest(ArrivalTimes times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super();
		this.times = times;
		this.usedConnections = usedConnections;
		this.usedJourneys = usedJourneys;
	}

	protected void initialise(Stop start, Time departure) {
		for (Stop neighbour : start.neighbours()) {
			start.arrivalAt(neighbour, departure).ifPresent(
					arrival -> updateArrivalByFoot(start, neighbour, departure, arrival));
		}
	}

	protected Time arrivalAt(Stop stop) {
		return times.get(stop);
	}

	@Override
	public Time startTime() {
		return times.startTime();
	}

	@Override
	public void updateArrival(Connection connection) {
		if (isNotReachable(connection)) {
			return;
		}
		Time currentArrival = times.get(connection.end());
		if (currentArrival.isAfter(connection.arrival())) {
			updateArrivalInternal(connection);
		}
	}

	private boolean isNotReachable(Connection connection) {
		if (usedJourneys.used(connection.journey())) {
			return false;
		}
		Time currentArrival = times.getConsideringMinimumChangeTime(connection.start());
		return currentArrival.isAfter(connection.departure());
	}

	private void updateArrivalInternal(Connection connection) {
		updateTimeAndConnection(connection);
		updateArrivalAtNeighbours(connection);
	}

	private void updateTimeAndConnection(Connection connection) {
		Stop end = connection.end();
		Time arrival = connection.arrival();
		times.set(end, arrival);
		usedConnections.update(end, connection);
		usedJourneys.use(connection.journey());
	}

	private void updateArrivalAtNeighbours(Connection connection) {
		Stop end = connection.end();
		Time arrival = connection.arrival();
		for (Stop neighbour : end.neighbours()) {
			end.arrivalAt(neighbour, arrival).ifPresent(arrivalByFoot -> updateArrivalByFoot(end,
					neighbour, connection.arrival(), arrivalByFoot));
		}
	}

	private void updateArrivalByFoot(Stop start, Stop end, Time arrivalAtStart, Time arrivalAtEnd) {
		Time currentArrival = times.get(end);
		if (currentArrival.isAfter(arrivalAtEnd)) {
			updateTransfer(start, end, arrivalAtStart, arrivalAtEnd);
		}
	}

	private void updateTransfer(Stop fromStart, Stop end, Time departure, Time arrival) {
		Connection connection = Connection.byFootFrom(fromStart, end, departure, arrival);
		updateTimeAndConnection(connection);
	}

	protected boolean isAfterArrivalAt(Time departure, Stop end) {
		Time arrival = times.getConsideringMinimumChangeTime(end);
		return arrival.isBefore(departure);
	}

	@Override
	public Optional<PublicTransportRoute> createRoute() {
		try {
			Time time = times.startTime();
			List<Connection> connections = collectConnections(usedConnections, time);
			if (connections.isEmpty()) {
				return empty();
			}
			Stop start = firstStopOf(connections);
			Stop end = lastStopOf(connections);
			return createRoute(start, end, time, connections);
		} catch (StopNotReachable e) {
			return empty();
		}
	}

	protected abstract List<Connection> collectConnections(UsedConnections usedConnections, Time time) throws StopNotReachable;

	private Stop firstStopOf(List<Connection> connections) {
		return connections.get(firstConnection).start();
	}
	
	private Stop lastStopOf(List<Connection> connections) {
		return connections.get(connections.size() - 1).end();
	}

	private Optional<PublicTransportRoute> createRoute(Stop start, Stop end, Time time, List<Connection> connections) {
		Time arrivalTime = times.get(end);
		return of(new ScannedRoute(start, end, time, arrivalTime, connections));
	}

}