package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.FootJourney.footJourney;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class FootVehicle implements Vehicle {

	@Override
	public void board(Passenger person, Stop exitStop) {
	}

	@Override
	public void getOff(Passenger person) {
	}

	@Override
	public int passengerCount() {
		return 0;
	}

	@Override
	public boolean hasFreePlace() {
		return true;
	}

	@Override
	public int journeyId() {
		return footJourney.id();
	}

	@Override
	public Time firstDeparture() {
		return Time.future;
	}

	@Override
	public void moveToNextStop(Time current) {
	}

	@Override
	public Stop currentStop() {
		throw new RuntimeException("Foot vehicle is not moving");
	}

	@Override
	public Optional<ConnectionId> nextConnection() {
		return Optional.empty();
	}

	@Override
	public Optional<Time> nextDeparture() {
		return Optional.empty();
	}

	@Override
	public Optional<Time> nextArrival() {
		return Optional.empty();
	}

	@Override
	public void notifyPassengers(EventQueue queue, Time currentDate) {
	}

}
