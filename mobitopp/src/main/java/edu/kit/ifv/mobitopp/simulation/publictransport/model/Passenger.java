package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.time.Time;

public interface Passenger {

	int getOid();
	
	void arriveAtStop(EventQueue queue, Time currentDate);

	void vehicleArriving(EventQueue queue, Vehicle vehicle, Time currentDate);

}
