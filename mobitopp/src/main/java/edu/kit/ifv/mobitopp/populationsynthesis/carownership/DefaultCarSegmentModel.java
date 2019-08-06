package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;


public class DefaultCarSegmentModel 
	implements CarSegmentModel 
{

	private final Car.Segment defaultSegment;

	public DefaultCarSegmentModel(Car.Segment defaultSegment) {

		this.defaultSegment = defaultSegment;
	}

	public Car.Segment determineCarSegment(PersonBuilder person) {

		return this.defaultSegment;
	}

}
