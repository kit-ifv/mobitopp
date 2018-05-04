package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.time.Time;

interface VehicleTripLogger {

	void arrive(Time time, Vehicle vehicle);

	void depart(Time time, Vehicle vehicle);

}
