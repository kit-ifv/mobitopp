package edu.kit.ifv.mobitopp.simulation.car;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;

public interface PrivateCar extends Car {

	Car car();

	HouseholdId owner();

	boolean isPersonal();

	PersonId personalUser();

	PersonId mainUser();

}
