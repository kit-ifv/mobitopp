package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.dataimport.BikeSharingBike;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

public interface BikeSharingCompany {

	String name();

	boolean isBikeAvailableFor(SimulationPerson person);

	Bike bookBikeFor(SimulationPerson person);

	void returnBike(BikeSharingBike bike, ZoneId zone);

}
