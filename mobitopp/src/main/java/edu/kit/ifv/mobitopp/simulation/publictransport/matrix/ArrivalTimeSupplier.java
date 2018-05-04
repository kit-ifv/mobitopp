package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import edu.kit.ifv.mobitopp.time.Time;

public interface ArrivalTimeSupplier {

	Time startingAt(Time departure);

}