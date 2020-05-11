package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public interface Bike {

	void returnBike(Zone zone);

	int getId();

	void use(Person person, Time time);

	void release(Person person, Time time);

}
