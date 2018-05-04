package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface CarSegmentModel {

	public Car.Segment determineCarSegment(Person person);

}
