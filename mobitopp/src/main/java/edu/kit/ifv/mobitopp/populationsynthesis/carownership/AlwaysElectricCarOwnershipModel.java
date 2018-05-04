package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;

public class AlwaysElectricCarOwnershipModel implements ProbabilityForElectricCarOwnershipModel {

	private static final double always = 1.0;
	private final BevProbabilities probabilities;

	public AlwaysElectricCarOwnershipModel(BevProbabilities probabilities) {
		super();
		this.probabilities = probabilities;
	}

	@Override
	public double calculateProbabilityForElectricCar(final Person person, Car.Segment segment) {
		return always;
	}

	@Override
	public CarTypeSelector calculateProbabilities(Person person, Segment segment) {
		return new SegmentProbabilities(segment, always, probabilities);
	}

}
