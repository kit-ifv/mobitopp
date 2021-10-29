package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import java.util.Iterator;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleTimes {

	private final Iterator<WaitTime> waiting;
	private final TravelTimeCalculator driving;
	private final Time firstDeparture;
	private Optional<Time> nextDeparture;
	private Optional<Time> nextArrival;
	private Optional<ConnectionId> nextConnection;

	public VehicleTimes(
			Time firstDeparture, TravelTimeCalculator driving, Iterator<WaitTime> waiting) {
		super();
		this.waiting = waiting;
		this.driving = driving;
		this.firstDeparture = firstDeparture;
		initialise();
	}

	private void initialise() {
		nextDeparture = Optional.of(firstDeparture);
		assignArrival(firstDeparture);
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
		assignArrival(current);
	}

	private void assignDeparture(Time current) {
		Time nextDeparture = waiting.next().nextDeparture(current);
		this.nextDeparture = Optional.of(nextDeparture);
	}

	private void assignArrival(Time currentTime) {
		TravelTime nextVehicleLink = driving.next(currentTime);
		this.nextArrival = nextVehicleLink.nextArrival(nextDeparture);
		this.nextConnection = Optional.of(nextVehicleLink.nextConnection());
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
