package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.time.Time;

public interface Vehicles {

	Vehicle vehicleServing(Journey someJourney);

	boolean hasNextUntil(Time someDate);

	Vehicle next();

	void add(Vehicle vehicle, Time nextProcessingTime);

}
