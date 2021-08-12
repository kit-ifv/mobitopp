package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface StartedTrip representing a planned trip.
 */
public interface StartedTrip {
	
	/**
	 * Gets the oid of the trip.
	 *
	 * @return the trip's oid
	 */
	int getOid();

	/**
	 * Gets the leg id of the trip.
	 *
	 * @return the trip's leg id
	 */
	int getLegId();

	/**
	 * Gets the origin of the trip as a ZoneAndLocation.
	 *
	 * @return the zone and location where the trip originates from
	 */
	ZoneAndLocation origin();

	/**
	 * Gets the destination of the trip as a ZoneAndLocation..
	 *
	 * @return the destination zone and location of the trip 
	 */
	ZoneAndLocation destination();

	/**
	 * Gets the mode used during the trip.
	 *
	 * @return the trip's mode
	 */
	Mode mode();

	/**
	 * Gets the start date of the trip.
	 *
	 * @return the time at which the trip starts
	 */
	Time startDate();

	/**
	 * Gets the planned end date of the trip.
	 *
	 * @return the estimated arrival time of the trip
	 */
	Time plannedEndDate();

	/**
	 * Gets the planned duration of the trip.
	 *
	 * @return the trip's planned duration
	 */
	int plannedDuration();

	/**
	 * Gets the trip's previous activity.
	 *
	 * @return the activity preceding the trip
	 */
	ActivityIfc previousActivity();

	/**
	 * Gets the trip's next activity.
	 *
	 * @return the activity succeeding the trip
	 */
	ActivityIfc nextActivity();

	/**
	 * Gets the optional vehicle id(s).
	 *
	 * @return the optional string representation of the vehicle id(s) used during the trip
	 */
	Optional<String> vehicleId();

	/**
	 * Apply the given Consumer to each of the trip's Legs.
	 *
	 * @param consumer the consumer to be applied to each Leg
	 */
	void forEachStartedLeg(Consumer<StartedTrip> consumer);
}
