package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

public interface BikeSharingDataForZone {

	boolean isBikeAvailableFor(SimulationPerson person);

	Bike bookFreeBike(SimulationPerson person);

	boolean isBikeSharingAreaFor(Bike bike);

}
