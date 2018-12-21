package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;


public class DefaultCarSegmentModel 
	implements CarSegmentModel 
{

	private final Car.Segment defaultSegment;

	public DefaultCarSegmentModel(Car.Segment defaultSegment) {

		this.defaultSegment = defaultSegment;
	}

	public Car.Segment determineCarSegment(PersonForSetup person) {

		return this.defaultSegment;
	}

}
