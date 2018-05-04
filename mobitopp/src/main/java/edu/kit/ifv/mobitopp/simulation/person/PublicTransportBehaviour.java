package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportBehaviour {

	boolean isVehicleAvailable(PublicTransportLeg leg);

	boolean hasPlaceInVehicle(PublicTransportLeg leg);
	
	void board(SimulationPerson person, Time time, PublicTransportLeg part);

	void getOff(SimulationPerson person, Time time, PublicTransportLeg part);

	TripIfc searchNewTrip(SimulationPerson person, Time someDate, PublicTransportTrip trip);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part);

	void enterWaitingArea(SimulationPerson person, Stop stop);

	void leaveWaitingArea(SimulationPerson person, Stop stop);

}
