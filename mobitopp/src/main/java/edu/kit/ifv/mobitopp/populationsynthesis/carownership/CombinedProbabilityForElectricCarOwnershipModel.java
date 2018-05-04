package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;


public class CombinedProbabilityForElectricCarOwnershipModel
	implements ProbabilityForElectricCarOwnershipModel
{

	private final ProbabilityForElectricCarOwnershipModel model1;
	private final ProbabilityForElectricCarOwnershipModel model2;
	private final BevProbabilities bevProbabilities;

	public CombinedProbabilityForElectricCarOwnershipModel (
		 ProbabilityForElectricCarOwnershipModel model1,
		 ProbabilityForElectricCarOwnershipModel model2,
		 BevProbabilities bevProbabilities
	) {
		this.model1 = model1;
		this.model2 = model2;
		this.bevProbabilities = bevProbabilities;
	}

	@Override
	public double calculateProbabilityForElectricCar(final Person person, Car.Segment segment){

		double p1 = model1.calculateProbabilityForElectricCar(person, segment);
		double p2 = model2.calculateProbabilityForElectricCar(person, segment);

		return p1*p2;
	}

	@Override
	public CarTypeSelector calculateProbabilities(Person person, Segment segment) {
		double probability = calculateProbabilityForElectricCar(person, segment);
		return new SegmentProbabilities(segment, probability , bevProbabilities);
	}

}

