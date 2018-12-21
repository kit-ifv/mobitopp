package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;

public interface ProbabilityForElectricCarOwnershipModel {

	double calculateProbabilityForElectricCar(final PersonForSetup person, Car.Segment segment);

	CarTypeSelector calculateProbabilities(PersonForSetup person, Car.Segment segment);

}
