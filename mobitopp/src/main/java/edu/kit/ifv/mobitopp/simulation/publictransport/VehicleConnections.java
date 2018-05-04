package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleConnections {

	private final Iterator<Connection> connections;
	private final Time firstDeparture;
	private Optional<Time> nextDeparture;
	private Optional<Time> nextArrival;

	public VehicleConnections(Collection<Connection> connections) {
		super();
		this.connections = connections.iterator();
		assignNextTimes();
		firstDeparture = nextDeparture.get();
	}

	public Optional<Time> nextDeparture() {
		return nextDeparture;
	}

	public void move() {
		if (connections.hasNext()) {
			assignNextTimes();
			return;
		}
		nextDeparture = Optional.empty();
		nextArrival = Optional.empty();
	}

	private void assignNextTimes() {
		Connection next = connections.next();
		nextDeparture = Optional.of(next.departure());
		nextArrival = Optional.of(next.arrival());
	}

	public Time firstDeparture() {
		return firstDeparture;
	}

	public Optional<Time> nextArrival() {
		return nextArrival;
	}

}
