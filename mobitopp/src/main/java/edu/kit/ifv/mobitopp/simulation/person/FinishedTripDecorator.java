package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;


public class FinishedTripDecorator implements FinishedTrip {
  
  private final FinishedTrip trip;

  public FinishedTripDecorator(FinishedTrip trip) {
    this.trip = trip;
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
    return trip.endDate();
  }

  @Override
  public Time plannedEndDate() {
    return trip.plannedEndDate();
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
    return trip.statistic();
  }

  @Override
  public Optional<String> vehicleId() {
    return trip.vehicleId();
  }
  
  @Override
  public Stream<FinishedTrip> trips() {
  	return trip.trips();
  }

}
