package edu.kit.ifv.mobitopp.populationsynthesis.carownership;


import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;


public interface CarOwnershipModel {

	Collection<PrivateCar> createCars(HouseholdForSetup household, int numberOfCars);

}
