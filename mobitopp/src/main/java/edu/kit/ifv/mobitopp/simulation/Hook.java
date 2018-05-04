package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.time.Time;

@FunctionalInterface
public interface Hook {

	void process(Time date);
	
}
