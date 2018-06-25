package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleConnections {

	private static class VehicleDriving {

		private final RelativeTime travelTime;
		private final ConnectionId id;

		public VehicleDriving(ConnectionId id, RelativeTime travelTime) {
			super();
			this.travelTime = travelTime;
			this.id = id;
		}

	}

	private static class VehicleWaiting {

		private final RelativeTime waitTime;

		public VehicleWaiting(RelativeTime waitTime) {
			super();
			this.waitTime = waitTime;
		}

	}

	private final Iterator<VehicleWaiting> waiting;
	private final Iterator<VehicleDriving> driving;
	private final Time firstDeparture;
	private Optional<Time> nextDeparture;
	private Optional<Time> nextArrival;
	private Optional<ConnectionId> nextConnection;

	public VehicleConnections(Collection<Connection> connections) {
		super();
		verify(connections);
		waiting = waiting(connections);
		driving = driving(connections);
		assignStartOf(connections);
		firstDeparture = nextDeparture().get();
	}

	private void verify(Collection<Connection> connections) {
		if (connections.isEmpty()) {
			throw new IllegalArgumentException("Cannot create a route without connections.");
		}
	}

	private void assignStartOf(Collection<Connection> connections) {
		Connection firstConnection = connections.iterator().next();
		Time departure = firstConnection.departure();
		nextDeparture = Optional.of(departure);
		assignArrival();
	}

	private Iterator<VehicleWaiting> waiting(Collection<Connection> connections) {
		List<VehicleWaiting> waiting = new ArrayList<>();
		Iterator<Connection> iterator = connections.iterator();
		Connection previous = iterator.next();
		while (iterator.hasNext()) {
			Connection current = iterator.next();
			waiting.add(waitingBetween(previous, current));
			previous = current;
		}
		return waiting.iterator();
	}

	private Iterator<VehicleDriving> driving(Collection<Connection> connections) {
		return connections.stream().map(VehicleConnections::drivingBetween).iterator();
	}

	private static VehicleDriving drivingBetween(Connection current) {
		return new VehicleDriving(current.id(), current.duration());
	}

	private static VehicleWaiting waitingBetween(Connection previous, Connection current) {
		RelativeTime waitTime = current.departure().differenceTo(previous.arrival());
		return new VehicleWaiting(waitTime);
	}

	public Optional<Time> nextDeparture() {
		return nextDeparture;
	}

	public Optional<Time> nextArrival() {
		return nextArrival;
	}

	public Optional<ConnectionId> nextConnection() {
		return nextConnection;
	}

	public void move(Time current) {
		if (driving.hasNext() && waiting.hasNext()) {
			assignNext(current);
			return;
		}
		assignEnd();
	}

	private void assignNext(Time current) {
		assignDeparture(current);
		assignArrival();
	}

	private void assignDeparture(Time current) {
		Time nextDeparture = current.plus(waiting.next().waitTime);
		this.nextDeparture = Optional.of(nextDeparture);
	}

	private void assignArrival() {
		VehicleDriving nextVehicleLink = driving.next();
		this.nextArrival = nextDeparture.map(time -> time.plus(nextVehicleLink.travelTime));
		this.nextConnection = Optional.of(nextVehicleLink.id);
	}

	private void assignEnd() {
		nextDeparture = Optional.empty();
		nextArrival = Optional.empty();
		nextConnection = Optional.empty();
	}

	public Time firstDeparture() {
		return firstDeparture;
	}

}
