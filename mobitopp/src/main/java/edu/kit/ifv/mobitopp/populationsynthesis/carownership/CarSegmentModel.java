package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;

public interface CarSegmentModel {

	public Car.Segment determineCarSegment(PersonBuilder person);

}
