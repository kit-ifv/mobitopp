package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.function.Consumer;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The class BaseStartedTrip is a base implementation of the StartedTrip interface.
 */
public class BaseStartedTrip implements StartedTrip<BaseStartedTrip> {

	/** The tripData of a StartedTrip describing a planned trip. */
	private final TripData data;
	
	/** An optional vehicleId String */
	private final Optional<String> vehicleId;

	
	/**
	 * Instantiates a new base started trip with the given tripData and an empty optional vehicleId.
	 *
	 * @param data the data
	 */
	public BaseStartedTrip(TripData data) {
		super();
		this.data = data;
		this.vehicleId = Optional.empty();
	}
	
	/**
	 * Instantiates a new base started trip with the given tripData and vehicleId.
	 *
	 * @param data the data
	 * @param vehicleId the vehicle id
	 */
	public BaseStartedTrip(TripData data, String vehicleId) {
		super();
		this.data = data;
		this.vehicleId = Optional.of(vehicleId);
	}


	@Override
	public int getOid() {
		return data.getOid();
	}

	@Override
	public int getLegId() {
		return data.getLegId();
	}

	@Override
	public ZoneAndLocation origin() {
		return data.origin();
	}

	@Override
	public ZoneAndLocation destination() {
		return data.destination();
	}

	@Override
	public Mode mode() {
		return data.mode();
	}

	@Override
	public Time startDate() {
		return data.startDate();
	}

	@Override
	public Time plannedEndDate() {
		return data.calculatePlannedEndDate();
	}

	@Override
	public int plannedDuration() {
		return data.plannedDuration();
	}

	@Override
	public ActivityIfc previousActivity() {
		return data.previousActivity();
	}

	@Override
	public ActivityIfc nextActivity() {
		return data.nextActivity();
	}

	@Override
	public Optional<String> vehicleId() {
		return vehicleId;
	}

	@Override
	public void forEachLeg(Consumer<BaseStartedTrip> consumer) {
		consumer.accept(this);
	}
}
