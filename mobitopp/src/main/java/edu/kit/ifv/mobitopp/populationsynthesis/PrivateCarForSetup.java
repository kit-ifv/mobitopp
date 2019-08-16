package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface PrivateCarForSetup {

  PrivateCar toCar(HouseholdForDemand household);
  
  boolean isPersonal();

	PersonId getPersonalUser();

	PersonId getMainUser();

	HouseholdId getOwner();

	Car getCar();

}
