package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import java.time.Duration;

import edu.kit.ifv.mobitopp.time.Time;

public interface TravelTime {

	Duration inHourAfter(Time startOfSlice);

}