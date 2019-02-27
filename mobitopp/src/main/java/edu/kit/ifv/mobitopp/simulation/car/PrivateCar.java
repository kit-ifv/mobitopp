package edu.kit.ifv.mobitopp.simulation.car;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;

public interface PrivateCar extends Car {

	Car car();

	Household owner();

	boolean isPersonal();

	PersonId personalUser();

	PersonId mainUser();

}
