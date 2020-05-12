package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

public interface BikeSharingDataForZone {

	boolean isBikeSharingAreaFor(Bike bike);

	boolean isBikeAvailableFor(SimulationPerson person);

	/**
	 * Book a {@link Bike} for the given {@link SimulationPerson}. The {@link Bike} can only be
	 * booked, if one is available. The availability of a bike must be checked in advance via
	 * {@link #isBikeAvailableFor(SimulationPerson)}
	 * 
	 * @param person to book a bike for
	 * @return booked {@link Bike}
	 * @throws IllegalStateException if {@link Bike} should be booked but is not available
	 */
	Bike bookBike(SimulationPerson person);

}
