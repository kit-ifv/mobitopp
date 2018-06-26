package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public interface VehicleQueue {

	boolean hasNextUntil(Time time);

	Vehicle next();

	void add(Time nextProcessingTime, Vehicle vehicle);

}