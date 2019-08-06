package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;

public class AlwaysElectricCarOwnershipModel implements ProbabilityForElectricCarOwnershipModel {

	private static final double always = 1.0;
	private final BevProbabilities probabilities;

	public AlwaysElectricCarOwnershipModel(BevProbabilities probabilities) {
		super();
		this.probabilities = probabilities;
	}

	@Override
	public double calculateProbabilityForElectricCar(final PersonBuilder person, Car.Segment segment) {
		return always;
	}

	@Override
	public CarTypeSelector calculateProbabilities(PersonBuilder person, Segment segment) {
		return new SegmentProbabilities(segment, always, probabilities);
	}

}
