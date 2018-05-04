package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

class DefaultUsedConnections implements UsedConnections {

	private final Connection[] arrivals;

	DefaultUsedConnections(int numberOfStops) {
		super();
		arrivals = new Connection[numberOfStops];
	}

	@Override
	public void update(Stop stop, Connection connection) {
		arrivals[stop.id()] = connection;
	}

	@Override
	public List<Connection> collectConnections(Stop fromStart, Stop toEnd) throws StopNotReachable {
		Predicate<Stop> isStart = fromStart::equals;
		return collectConnections(toEnd, isStart);
	}

	private List<Connection> collectConnections(Stop toEnd, Predicate<Stop> isStart)
			throws StopNotReachable {
		List<Connection> connections = new ArrayList<>();
		Stop currentStop = toEnd;
		while (isStart.negate().test(currentStop)) {
			Connection connection = connectionArrivingAt(currentStop);
			connections.add(connection);
			currentStop = connection.start();
		}
		Collections.reverse(connections);
		return connections;
	}

	@Override
	public List<Connection> collectConnections(StopPaths starts, Stop toEnd, Time time)
			throws StopNotReachable {
		List<Connection> connections = new ArrayList<>();
		Stop currentStop = toEnd;
		Connection connection = connectionArrivingAt(currentStop);
		while (!starts.isConnectionReachableAt(currentStop, time, connection)) {
			connection = connectionArrivingAt(currentStop);
			connections.add(connection);
			currentStop = connection.start();
		}
		Collections.reverse(connections);
		return connections;
	}

	private Connection connectionArrivingAt(Stop currentStop) throws StopNotReachable {
		if (arrivals[currentStop.id()] == null) {
			throw new StopNotReachable(currentStop);
		}
		return arrivals[currentStop.id()];
	}
}