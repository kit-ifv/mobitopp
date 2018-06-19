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

	private final PassengerCompartment passengers;
	private final VehicleRoute vehicleRoute;

	SimulatedVehicle(VehicleRoute vehicleRoute, PassengerCompartment passengersPerStop) {
		super();
		passengers = passengersPerStop;
		this.vehicleRoute = vehicleRoute;
	}

	public static Vehicle from(Journey journey) {
		VehicleFactory factory = new VehicleFactory();
		return factory.createFrom(journey);
	}

	@Override
	public int journeyId() {
		return vehicleRoute.journeyId();
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
		vehicleRoute.moveToNextStop();
	}

	@Override
	public Stop currentStop() {
		return vehicleRoute.currentStop();
	}

	@Override
	public Optional<Connection> nextConnection() {
		return vehicleRoute.nextConnection();
	}
	
	@Override
	public Optional<Time> nextDeparture() {
		return vehicleRoute.nextDeparture();
	}

	@Override
	public Optional<Time> nextArrival() {
		return vehicleRoute.nextArrival();
	}

	@Override
	public Time firstDeparture() {
		return vehicleRoute.firstDeparture();
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
		result = prime * result + ((vehicleRoute == null) ? 0 : vehicleRoute.hashCode());
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
		if (vehicleRoute == null) {
			if (other.vehicleRoute != null)
				return false;
		} else if (!vehicleRoute.equals(other.vehicleRoute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [journeyId()=" + journeyId() + "]";
	}

}
