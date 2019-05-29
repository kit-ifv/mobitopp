package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingFreeFloatingTrip extends CarBasedTrip implements Trip {

  public CarSharingFreeFloatingTrip(TripData trip, SimulationPerson person) {
    super(trip, person, CarSharingCar.class, Mode.CARSHARING_FREE);
  }

  @Override
  public boolean hasParkedCar() {
    return person().hasParkedCar();
  }

  @Override
  protected Car allocateCar(ImpedanceIfc impedance, Time currentTime) {
    Zone zone = person().currentActivity().zone();
    
    assert zone.carSharing().isFreeFloatingCarSharingCarAvailable(person()) : (person());

    return zone.carSharing().bookFreeFloatingCar(person());
  }
  
  @Override
  protected boolean canReturnCar(ActivityIfc nextActivity) {
    return nextActivity.zone().carSharing().isFreeFloatingZone((CarSharingCar) person().whichCar());
  }

  @Override
  protected void notifyBeforeReturn(PersonListener listener, FinishedTrip finishedTrip) {
  }
}
