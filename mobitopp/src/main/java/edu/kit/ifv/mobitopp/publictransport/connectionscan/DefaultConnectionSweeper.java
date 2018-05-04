package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.time.Time;

class DefaultConnectionSweeper implements ConnectionSweeper {

	private static final int defaultDistance = 100;
	private static final Function<Time, Boolean> alwaysTooLate = (time) -> true;
	private final List<Connection> connections;
	private final Function<Time, Integer> lookup;
	private final Function<Time, Boolean> isTooLate;
	private final int intervalToCheckArrivalAtEnd;

	private DefaultConnectionSweeper(
			List<Connection> connections, Function<Time, Integer> lookup,
			Function<Time, Boolean> isTooLate, int intervalToCheckArrivalAtEnd) {
		super();
		this.connections = connections;
		this.lookup = lookup;
		this.isTooLate = isTooLate;
		this.intervalToCheckArrivalAtEnd = intervalToCheckArrivalAtEnd;
	}

	static DefaultConnectionSweeper from(Connections connections) {
		return from(connections, defaultDistance);
	}

	static DefaultConnectionSweeper from(Connections connections, int intervalToCheckArrivalAtEnd) {
		Stream<Connection> stream = connections.asCollection().stream();
		List<Connection> sorted = stream.sorted(new ConnectionComparator()).collect(toList());
		List<Connection> fixedConnections = Collections.unmodifiableList(sorted);
		Function<Time, Integer> lookup = initialiseLookup(fixedConnections);
		Function<Time, Boolean> isTooLate = createIsTooLate(fixedConnections);
		return new DefaultConnectionSweeper(fixedConnections, lookup, isTooLate, intervalToCheckArrivalAtEnd);
	}

	private static Function<Time, Integer> initialiseLookup(List<Connection> connections) {
		TreeMap<Time, Integer> lookup = new TreeMap<>();
		for (int index = 0; index < connections.size(); index++) {
			Time departure = connections.get(index).departure();
			lookup.putIfAbsent(departure, index);
		}
		return (time) -> indexIn(lookup, time, connections.size());
	}

	private static Integer indexIn(TreeMap<Time, Integer> lookup, Time time, int noEntryFound) {
		Entry<Time, Integer> possibleEntry = lookup.ceilingEntry(time);
		if (possibleEntry == null) {
			return noEntryFound;
		}
		return possibleEntry.getValue();
	}

	private static Function<Time, Boolean> createIsTooLate(List<Connection> connections) {
		if (connections.isEmpty()) {
			return alwaysTooLate;
		}
		Connection latestConnection = connections.get(connections.size() - 1);
		return latestConnection::departsBefore;
	}

	public List<Connection> asList() {
		return Collections.unmodifiableList(connections);
	}

	@Override
	public boolean areDepartedBefore(Time time) {
		return isTooLate.apply(time);
	}

	@Override
	public Optional<PublicTransportRoute> sweep(PreparedSearchRequest searchRequest) {
		scanConnections(searchRequest);
		return searchRequest.createRoute();
	}

	private int startIndexFor(PreparedSearchRequest searchRequest) {
		Time atTime = searchRequest.startTime();
		return lookup.apply(atTime);
	}

	private void scanConnections(PreparedSearchRequest searchRequest) {
		int startIndex = startIndexFor(searchRequest);
		for (int index = startIndex; index < connections.size(); index++) {
			Connection connection = connections.get(index);
			if (sweepCanBeCancled(searchRequest, index, connection)) {
				break;
			}
			searchRequest.updateArrival(connection);
		}
	}

	private boolean sweepCanBeCancled(PreparedSearchRequest searchRequest, int index, Connection connection) {
		return checkOnlyAtInterval(index) && searchRequest.departsAfterArrivalAtEnd(connection);
	}

	/**
	 * Reduces overhead when stopping connection sweep.
	 * 
	 * @param index
	 * @return
	 */
	private boolean checkOnlyAtInterval(int index) {
		return 0 == index % intervalToCheckArrivalAtEnd;
	}

	@Override
	public String toString() {
		return "PreparedConnections [connections=" + connections
				+ ", distanceBetweenConnectionsToCheck=" + intervalToCheckArrivalAtEnd + "]";
	}

}
