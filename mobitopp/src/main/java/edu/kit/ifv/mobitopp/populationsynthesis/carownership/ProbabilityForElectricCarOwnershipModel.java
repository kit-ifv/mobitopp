package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;

public interface ProbabilityForElectricCarOwnershipModel {

	double calculateProbabilityForElectricCar(final PersonBuilder person, Car.Segment segment);

	CarTypeSelector calculateProbabilities(PersonBuilder person, Car.Segment segment);

}
