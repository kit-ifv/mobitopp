package edu.kit.ifv.mobitopp.populationsynthesis.carownership;


import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

import java.util.Collection;


public interface CarOwnershipModel {

	Collection<PrivateCar> createCars(Household household, int numberOfCars);

}
