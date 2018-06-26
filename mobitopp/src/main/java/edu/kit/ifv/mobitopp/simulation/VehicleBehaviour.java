package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.time.Time;

public interface VehicleBehaviour extends PublicTransportBehaviour {

	static final VehicleBehaviour noBehaviour = new NoVehicleBehaviour();

	void letVehiclesArriveAt(Time currentDate, EventQueue queue);

	void letVehiclesDepartAt(Time currentDate);

}
