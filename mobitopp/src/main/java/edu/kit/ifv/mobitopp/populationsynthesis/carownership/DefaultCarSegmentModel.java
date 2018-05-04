package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;


public class DefaultCarSegmentModel 
	implements CarSegmentModel 
{

	private final Car.Segment defaultSegment;

	public DefaultCarSegmentModel(Car.Segment defaultSegment) {

		this.defaultSegment = defaultSegment;
	}

	public Car.Segment determineCarSegment(Person person) {

		return this.defaultSegment;
	}

}
