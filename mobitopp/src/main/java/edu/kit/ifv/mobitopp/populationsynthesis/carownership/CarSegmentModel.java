package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;

public interface CarSegmentModel {

	public Car.Segment determineCarSegment(PersonForSetup person);

}
