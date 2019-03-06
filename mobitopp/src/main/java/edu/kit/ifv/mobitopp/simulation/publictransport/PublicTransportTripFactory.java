package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.simulation.BaseData;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportTripFactory implements TripFactory {

  private final TripFactory base;
  private final PublicTransportBehaviour publicTransportBehaviour;

  public PublicTransportTripFactory(
      TripFactory base, PublicTransportBehaviour publicTransportBehaviour) {
    super();
    this.base = base;
    this.publicTransportBehaviour = publicTransportBehaviour;
  }

  @Override
  public Trip createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    if (Mode.PUBLICTRANSPORT.equals(mode)) {
      return doCreateTrip(person, impedance, mode, previousActivity, nextActivity);
    }
    return base.createTrip(person, impedance, mode, previousActivity, nextActivity);
  }

  private Trip doCreateTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    int sourceZoneOid = previousActivity.zone().getOid();
    int targetZoneOid = nextActivity.zone().getOid();

    Time plannedEnd = previousActivity.calculatePlannedEndDate();

    int matrixDuration = (int) impedance
        .getTravelTime(sourceZoneOid, targetZoneOid, mode, plannedEnd);
    Optional<PublicTransportRoute> route = Optional.empty();
    route = findRoute(impedance, mode, previousActivity, nextActivity, plannedEnd);

    int duration = route
        .map(PublicTransportRoute::duration)
        .map(RelativeTime::toMinutes)
        .map(Long::intValue)
        .orElse(matrixDuration);
    duration = Math.max(1, duration);
    TripData trip = new BaseData(nextTripId(), mode, previousActivity, nextActivity,
        (short) duration);
    return PublicTransportTrip.of(trip, person, publicTransportBehaviour, route);
  }

  protected Optional<PublicTransportRoute> findRoute(
      ImpedanceIfc impedance, Mode modeType, ActivityIfc previousActivity, ActivityIfc nextActivity,
      Time currentTime) {
    return impedance
        .getPublicTransportRoute(previousActivity.location(), nextActivity.location(), modeType,
            currentTime);
  }

  @Override
  public int nextTripId() {
    return base.nextTripId();
  }

  @Override
  public TripData createTripData(ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity, ActivityIfc nextActivity) {
    return base.createTripData(impedance, mode, previousActivity, nextActivity);
  }

}
