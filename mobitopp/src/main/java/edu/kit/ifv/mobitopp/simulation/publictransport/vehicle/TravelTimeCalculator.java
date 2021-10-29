package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import edu.kit.ifv.mobitopp.time.Time;

public interface TravelTimeCalculator {

	boolean hasNext();

	TravelTime next(Time currentTime);
}
