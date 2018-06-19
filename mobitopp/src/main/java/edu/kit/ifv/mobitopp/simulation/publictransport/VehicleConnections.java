package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleConnections {

	private final Iterator<Connection> connections;
	private final Time firstDeparture;
	private Optional<Connection> next;

	public VehicleConnections(Collection<Connection> connections) {
		super();
		this.connections = connections.iterator();
		assignNext();
		firstDeparture = nextDeparture().get();
	}

	public Optional<Time> nextDeparture() {
		return next.map(Connection::departure);
	}

	public Optional<Time> nextArrival() {
		return next.map(Connection::arrival);
	}

	public Optional<ConnectionId> nextConnection() {
		return next.map(Connection::id);
	}

	public void move() {
		if (connections.hasNext()) {
			assignNext();
			return;
		}
		next = assignEnd();
	}

	private void assignNext() {
		next = Optional.of(connections.next());
	}

	private Optional<Connection> assignEnd() {
		return Optional.empty();
	}

	public Time firstDeparture() {
		return firstDeparture;
	}

}
