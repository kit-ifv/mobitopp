package edu.kit.ifv.mobitopp.simulation.events;

import edu.kit.ifv.mobitopp.time.Time;

public interface EventQueue {

	void add(DemandSimulationEventIfc event);

	boolean hasEventsUntil(Time date);

	DemandSimulationEventIfc nextEvent();

	int size();

}