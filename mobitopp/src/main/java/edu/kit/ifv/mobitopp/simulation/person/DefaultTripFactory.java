package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.BaseData;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultTripFactory implements TripFactory {

  private int tripCount;

  public DefaultTripFactory() {
    super();
    tripCount = 0;
  }

  @Override
  public TripIfc createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    BaseData tripData = createTripData(impedance, mode, previousActivity, nextActivity);

    if (Mode.CAR.equals(mode)) {
      return new PrivateCarTrip(tripData, person);
    }

    if (Mode.CARSHARING_STATION.equals(mode)) {
      return new CarSharingStationTrip(tripData, person);
    }

    if (Mode.CARSHARING_FREE.equals(mode)) {
      return new CarSharingFreeFloatingTrip(tripData, person);
    }

    if (Mode.PASSENGER.equals(mode)) {
      return new PassengerTrip(tripData, person);
    }

    return new NoActionTrip(tripData, person);
  }

  private BaseData createTripData(
      ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity, ActivityIfc nextActivity) {
    assert mode != null;
    assert previousActivity != null;
    assert nextActivity != null;

    assert previousActivity.isLocationSet();
    assert nextActivity.isLocationSet();

    int sourceZoneOid = previousActivity.zone().getOid();
    int targetZoneOid = nextActivity.zone().getOid();

    Time plannedEnd = previousActivity.calculatePlannedEndDate();

    int duration = (int) impedance.getTravelTime(sourceZoneOid, targetZoneOid, mode, plannedEnd);

    if (duration > java.lang.Short.MAX_VALUE) {
      System.out.println("WARNING: duration > java.lang.Short.MAX_VALUE");
      duration = java.lang.Short.MAX_VALUE;
    }
    duration = Math.max(1, duration);

    assert duration > 0;

    return new BaseData(nextTripId(), mode, previousActivity, nextActivity, (short) duration);
  }

  @Override
  public int nextTripId() {
    return ++tripCount;
  }
}
