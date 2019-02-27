package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;

public interface CarOwnershipModel {

  Collection<PrivateCarForSetup> createCars(HouseholdForSetup household, int numberOfCars);

}
