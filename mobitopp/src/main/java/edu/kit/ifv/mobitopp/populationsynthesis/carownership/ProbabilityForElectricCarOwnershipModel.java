package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface ProbabilityForElectricCarOwnershipModel {

	public double calculateProbabilityForElectricCar(final Person person, Car.Segment segment);

	public CarTypeSelector calculateProbabilities(Person person, Car.Segment segment);

}
