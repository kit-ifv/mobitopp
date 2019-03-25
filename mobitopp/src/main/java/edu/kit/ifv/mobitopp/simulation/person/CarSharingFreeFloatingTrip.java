package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingFreeFloatingTrip extends BaseTrip implements Trip {

  public CarSharingFreeFloatingTrip(TripData trip, SimulationPerson person) {
    super(trip, person);
  }

  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
    Zone zone = person().currentActivity().zone();
    if (person().isCarDriver()) {
      usePreviouslyBookedCar(zone);
    } else {
      bookCar(currentTime, zone);
    }
  }

  private void usePreviouslyBookedCar(Zone zone) {
    Car car = person().whichCar();
    assert car != null;
    assert car instanceof CarSharingCar;
    assert !zone.carSharing().isFreeFloatingZone((CarSharingCar) car);
  }

  private void bookCar(Time currentTime, Zone zone) {
    assert zone.carSharing().isFreeFloatingCarSharingCarAvailable(person()) : (person());

    Car car = zone.carSharing().bookFreeFloatingCar(person());
    person().useCar(car, currentTime);
  }
  
  @Override
  public FinishedTrip finish(Time currentDate, PersonListener listener) {
    FinishedTrip finishedTrip = super.finish(currentDate, listener);
    assert mode() == Mode.CARSHARING_FREE;
    assert person().isCarDriver();
    Zone zone = nextActivity().zone();
    if (zone.carSharing().isFreeFloatingZone((CarSharingCar) person().whichCar())) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    }
    return finishedTrip;
  }
}
