package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.dataimport.BikeSharingBike;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface BikeSharingCompany {

	String name();

	boolean isBikeAvailableFor(Person person, ZoneId zoneId);

	Bike bookBikeFor(Person person, ZoneId zoneId);

	void returnBike(BikeSharingBike bike, ZoneId zone);

}
