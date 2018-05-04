package edu.kit.ifv.mobitopp.simulation.car;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface PrivateCar extends Car {

	Car car();

	public Household owner();

	boolean isPersonal();

	Person personalUser();

	Person mainUser();

}
