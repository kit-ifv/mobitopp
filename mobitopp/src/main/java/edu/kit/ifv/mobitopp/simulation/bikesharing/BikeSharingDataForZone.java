package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.simulation.Person;

public interface BikeSharingDataForZone {

	/**
	 * Returns whether bike sharing in general is available. Bike sharing is available as soon as one company provides a service area.
	 * 
	 * @return if bike sharing in general is available in this zone
	 */
	boolean isBikeSharingArea();
	
	/**
	 * Returns whether this zone is a service area of the bike. If it is a service area, the bike can be returned in this zone.
	 * 
	 * @param bike used {@link Bike}
	 * @return if the owner of the {@link Bike} offers bike sharing in this zone
	 */
	boolean isBikeSharingAreaFor(Bike bike);

	boolean isBikeAvailableFor(Person person);

	/**
	 * Book a {@link Bike} for the given {@link Person}. The {@link Bike} can only be
	 * booked, if one is available. The availability of a bike must be checked in advance via
	 * {@link #isBikeAvailableFor(Person)}
	 * 
	 * @param person to book a bike for
	 * @return booked {@link Bike}
	 * @throws IllegalStateException if {@link Bike} should be booked but is not available
	 */
	Bike bookBike(Person person);

}
