package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportBehaviour {

	boolean hasVehicleDeparted(PublicTransportLeg leg);

	boolean isVehicleAvailable(PublicTransportLeg leg);

	boolean hasPlaceInVehicle(PublicTransportLeg leg);
	
	void board(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	void getOff(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	TripIfc searchNewTrip(SimulationPerson person, Time someDate, PublicTransportTrip trip);

	void wait(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip);

	void enterWaitingArea(SimulationPerson person, Stop stop);

	void leaveWaitingArea(SimulationPerson person, Stop stop);

}
