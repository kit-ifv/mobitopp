package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.BaseData;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultTripFactory implements TripFactory {

  private int tripCount;
  private final ModeToTrip modeToTrip;

  public DefaultTripFactory(ModeToTrip modeToTrip) {
    super();
    tripCount = 0;
    this.modeToTrip = modeToTrip;
  }

  public DefaultTripFactory() {
    this(ModeToTrip.createDefault());
  }

  @Override
  public Trip createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    BaseData tripData = createTripData(impedance, mode, previousActivity, nextActivity);
    return modeToTrip.create(mode, tripData, person);
  }

  @Override
  public BaseData createTripData(
      ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity, ActivityIfc nextActivity) {
    assert mode != null;
    assert previousActivity != null;
    assert nextActivity != null;

    assert previousActivity.isLocationSet();
    assert nextActivity.isLocationSet();

    ZoneId originId = previousActivity.zone().getId();
    ZoneId destinationId = nextActivity.zone().getId();

    Time plannedEnd = previousActivity.calculatePlannedEndDate();

    int duration = (int) impedance.getTravelTime(originId, destinationId, mode, plannedEnd);

    if (duration > java.lang.Short.MAX_VALUE) {
      System.out.println("WARNING: duration > java.lang.Short.MAX_VALUE");
      duration = java.lang.Short.MAX_VALUE;
    }
    duration = Math.max(1, duration);

    assert duration > 0;

    int legId = 0;
		return new BaseData(nextTripId(), legId, mode, previousActivity, nextActivity, (short) duration);
  }

  @Override
  public int nextTripId() {
    return ++tripCount;
  }
}
