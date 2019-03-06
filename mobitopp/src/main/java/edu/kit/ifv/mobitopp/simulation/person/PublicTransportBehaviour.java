package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportBehaviour {

	boolean hasVehicleDeparted(PublicTransportLeg leg);

	boolean isVehicleAvailable(PublicTransportLeg leg);

	boolean hasPlaceInVehicle(PublicTransportLeg leg);
	
	void board(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	void getOff(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	Trip searchNewTrip(SimulationPerson person, Time someDate, PublicTransportTrip trip);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part, Trip trip);

	void enterWaitingArea(SimulationPerson person, Stop stop);

	void leaveWaitingArea(SimulationPerson person, Stop stop);

}
