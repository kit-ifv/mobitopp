package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.board;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.getOff;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.wait;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VehicleEvent.arrival;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VehicleEvent.departure;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.PublicTransportResults;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class TripfileLogger implements PublicTransportLogger {

	private final PublicTransportResults results;

	public TripfileLogger(PublicTransportResults results) {
		super();
		this.results = results;
	}

	@Override
	public void arrive(Time time, Vehicle vehicle) {
		Stop currentStop = vehicle.currentStop();
		results.writeVehicleTrip(arrival, vehicle, currentStop, time);
	}

	@Override
	public void depart(Time time, Vehicle vehicle) {
		Stop currentStop = vehicle.currentStop();
		results.writeVehicleTrip(departure, vehicle, currentStop, time);
	}

	@Override
	public void board(SimulationPerson person, Time time, PublicTransportLeg part) {
		Stop stop = part.start();
		results.writePassenger(board, person, time, stop, part);
	}

	@Override
	public void getOff(SimulationPerson person, Time time, PublicTransportLeg part) {
		Stop stop = part.end();
		results.writePassenger(getOff, person, time, stop, part);
		results.writeRouteLeg(person, part);
	}

	@Override
	public void wait(SimulationPerson person, Time time, PublicTransportLeg part) {
		Stop stop = part.start();
		results.writePassenger(wait, person, time, stop, part);
	}

	@Override
	public void waitingAt(Stop stop, Time date, int persons) {
		results.writeStop(stop, date, persons);
	}

	@Override
	public void vehicleFull(
			SimulationPerson person, Time time, PublicTransportTrip trip) {
		results.writeVehicleFull(person, time, trip);
	}

	@Override
	public void vehicleCrowded(Vehicle vehicle, PublicTransportLeg leg) {
		results.writeVehicleCrowded(vehicle, leg);
	}
}
