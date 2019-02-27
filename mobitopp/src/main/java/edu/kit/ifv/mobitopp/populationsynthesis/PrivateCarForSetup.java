package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface PrivateCarForSetup {

  PrivateCar toCar(HouseholdForDemand household);

}
