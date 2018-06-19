package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehicle implements Vehicle {

	private final Journey journey;
	private final VehicleLocation location;
	private final VehicleConnections connections;
	private final PassengerCompartment passengers;

	SimulatedVehicle(
			Journey journey, VehicleLocation location, VehicleConnections connections,
			PassengerCompartment passengersPerStop) {
		super();
		this.journey = journey;
		this.location = location;
		this.connections = connections;
		passengers = passengersPerStop;
	}

	public static Vehicle from(Journey journey) {
		VehicleFactory factory = new VehicleFactory();
		return factory.createFrom(journey);
	}

	@Override
	public int journeyId() {
		return journey.id();
	}

	@Override
	public void board(Passenger person, Stop exitStop) {
		passengers.board(person, exitStop);
	}

	@Override
	public void getOff(Passenger person) {
		passengers.getOff(person, currentStop());
	}

	@Override
	public int passengerCount() {
		return passengers.count();
	}

	@Override
	public boolean hasFreePlace() {
		return passengers.hasFreePlace();
	}

	@Override
	public void moveToNextStop() {
		location.move();
		connections.move();
	}

	@Override
	public Stop currentStop() {
		return location.current();
	}

	@Override
	public Optional<Connection> nextConnection() {
		return connections.nextConnection();
	}
	
	@Override
	public Optional<Time> nextDeparture() {
		return connections.nextDeparture();
	}

	@Override
	public Optional<Time> nextArrival() {
		return connections.nextArrival();
	}

	@Override
	public Time firstDeparture() {
		return connections.firstDeparture();
	}

	@Override
	public void notifyPassengers(EventQueue queue, Time currentDate) {
		Consumer<Passenger> notify = passenger -> passenger.arriveAtStop(queue, currentDate);
		passengers.forEachAt(currentStop(), notify);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimulatedVehicle other = (SimulatedVehicle) obj;
		if (journey == null) {
			if (other.journey != null)
				return false;
		} else if (!journey.equals(other.journey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimulatedVehicle [journey=" + journey + "]";
	}

}
