package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportResults extends VehicleTripResults, PassengerResults {

	void waitingAt(Stop stop, Time date, int persons);

	void vehicleCrowded(Vehicle vehicle, PublicTransportLeg leg);

}
