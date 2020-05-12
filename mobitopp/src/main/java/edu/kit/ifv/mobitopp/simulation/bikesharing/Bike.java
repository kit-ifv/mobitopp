package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public interface Bike {

	void returnBike(ZoneId zone);

	String getId();

	void use(Person person, Time time);

	void release(Person person, Time time);

	BikeSharingCompany owner();

}
