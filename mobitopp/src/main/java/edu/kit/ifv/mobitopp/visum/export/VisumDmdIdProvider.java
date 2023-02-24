package edu.kit.ifv.mobitopp.visum.export;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.BaseHousehold;
import edu.kit.ifv.mobitopp.simulation.BasePerson;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

/**
 * VisumDmdIdProvider defines how the ids of various objects and entities are
 * computed. These ids are used for serializing mobiTopp results in the visum
 * dmd format.
 */
public class VisumDmdIdProvider {

	/**
	 * Computes the visum id of the given person.
	 *
	 * @param person the person
	 * @return the visum person id
	 */
	public static int personId(BasePerson person) {
		return person.getId().getOid(); // +1??
	}

	/**
	 * Computes the visum id of the given household.
	 *
	 * @param household the household
	 * @return the visum household id
	 */
	public static int householdId(BaseHousehold household) {
		return 1 + household.getId().getOid();
	}

	/**
	 * Computes the visum location id of the given opportunity.
	 *
	 * @param opportunity the opportunity
	 * @return the visum location id
	 */
	public static int locationIdOf(Opportunity opportunity) {
		return locationIdOf(opportunity.location());
	}

	/**
	 * Computes the visum location id of the given zone's centroid.
	 *
	 * @param zone the zone
	 * @return the visum location id
	 */
	public static int locationIdOf(Zone zone) {
		return locationIdOf(zone.centroidLocation());
	}

	/**
	 * Computes the visum location id of the given household.
	 *
	 * @param household the household
	 * @return the visum location id
	 */
	public static int locationIdOf(BaseHousehold household) {
		return locationIdOf(household.homeLocation());
	}

	/**
	 * Computes the visum location id of the given location.
	 *
	 * @param location the location
	 * @return the visum location id
	 */
	public static int locationIdOf(Location location) {
		return Math.max(Math.abs(location.hashCode()), 1);
	}
}
