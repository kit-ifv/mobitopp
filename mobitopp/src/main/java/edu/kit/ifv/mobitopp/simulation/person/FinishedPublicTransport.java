package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class FinishedPublicTransport implements FinishedTrip {

	private final PublicTransportTrip trip;
	private final Time endDate;
	private final Statistic statistic;

	public FinishedPublicTransport(
			PublicTransportTrip trip, Time endDate, Statistic statistic) {
		super();
		this.trip = trip;
		this.endDate = endDate;
		this.statistic = statistic;
	}


	@Override
	public int getOid() {
		return trip.getOid();
	}
	
	@Override
	public int getLegId() {
		return trip.getLegId();
	}

	@Override
	public ZoneAndLocation origin() {
		return trip.origin();
	}

	@Override
	public ZoneAndLocation destination() {
		return trip.destination();
	}

	@Override
	public Mode mode() {
		return trip.mode();
	}

	@Override
	public Time startDate() {
		return trip.startDate();
	}

	@Override
	public Time endDate() {
		return endDate;
	}

	@Override
	public Time plannedEndDate() {
		return trip.calculatePlannedEndDate();
	}

	@Override
	public int plannedDuration() {
		return trip.plannedDuration();
	}

	@Override
	public ActivityIfc previousActivity() {
		return trip.previousActivity();
	}

	@Override
	public ActivityIfc nextActivity() {
		return trip.nextActivity();
	}

	@Override
	public Statistic statistic() {
		return statistic;
	}

  @Override
  public Optional<String> vehicleId() {
    return Optional.empty();
  }
	
  @Override
  public void forEachLeg(Consumer<FinishedTrip> consumer) {
	consumer.accept(this);
  }
}
