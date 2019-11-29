package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PrivateCarTrip extends CarBasedTrip implements Trip {

  public PrivateCarTrip(TripData trip, SimulationPerson person) {
    super(trip, person, PrivateCar.class, StandardMode.CAR);
  }

  @Override
  protected boolean hasPreviouslyUsedCar() {
    return !person().currentActivity().activityType().isHomeActivity();
  }
  
  @Override
  protected Car allocateCar(ImpedanceIfc impedance, Time currentTime) {
    ZoneId originId = origin().zone().getId();
    ZoneId destinationId = destination().zone().getId();
    float distance = impedance.getDistance(originId, destinationId);
    float distanceKm = distance / 1000.0f;
    return person().household().takeAvailableCar(person(), distanceKm);
  }
  
  @Override
  protected void notifyBeforeReturn(PersonListener listener, FinishedTrip finishedTrip) {
    ActivityIfc prevActivity = trip().previousActivity();
    assert prevActivity != null;
    listener.notifyFinishCarTrip(person(), person().whichCar(), finishedTrip, nextActivity());
  }

  @Override
  protected boolean canReturnCar(ActivityIfc activity) {
    return activity.activityType().isHomeActivity();
  }
}
