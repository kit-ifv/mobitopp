package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.ToString;

@ToString
public class BeamedTrip implements FinishedTrip {

	private final TripData data;
	private final Time endDate;
	private final Statistic statistic;

	public BeamedTrip(TripData data, Time endDate) {
		super();
		this.data = data;
		this.endDate = endDate;
		statistic = new Statistic();
		RelativeTime plannedDuration = RelativeTime.ofMinutes(plannedDuration());
		statistic.add(Element.realDuration, plannedDuration);
		statistic.add(Element.plannedDuration, plannedDuration);
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
	public Time endDate() {
		return endDate;
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
	public Statistic statistic() {
		return statistic;
	}

  @Override
  public Optional<String> vehicleId() {
    return Optional.empty();
  }
  
  @Override
  public void forEachFinishedLeg(Consumer<FinishedTrip> consumer) {
	consumer.accept(this);
  }

@Override
public void forEachLeg(Consumer<StartedTrip> consumer) {
	consumer.accept(this);
}

}
