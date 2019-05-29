package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
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
    Objects.requireNonNull(impedance, "impedance");
    Objects.requireNonNull(currentTime, "currentTime");
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
    if (person().isCarDriver()) {
      return doFinish(currentDate, listener);
    }
    throw new IllegalStateException(String.format("Cannot finish car sharing trip without car: %s" , trip()));
  }

  public FinishedTrip doFinish(Time currentDate, PersonListener listener) {
    FinishedTrip finishedTrip = super.finish(currentDate, listener);
    assert mode() == Mode.CARSHARING_FREE;
    assert person().isCarDriver();
    Zone zone = nextActivity().zone();
    Car car = stopCar(currentDate);
    returnCar(currentDate, zone);
    return new FinishedCarTrip(finishedTrip, car.id());
  }

  public void returnCar(Time currentDate, Zone zone) {
    if (zone.carSharing().isFreeFloatingZone((CarSharingCar) person().whichCar())) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Location location = nextActivity().location();
      Car car = person().parkCar(zone, location, currentDate);
      assert car instanceof CarSharingCar;
    }
  }

  private Car stopCar(Time currentDate) {
    assert mode() == Mode.CARSHARING_FREE : (trip() + "\n" 
        + "prev: " + previousActivity() + "\n"
        + "next : " + nextActivity()  + "\n --- \n"
        + person().activitySchedule() + "\n");
    Zone zone = nextActivity().zone();
    Location location = nextActivity().location();
    CarPosition position = new CarPosition(zone, location);
    Car car = person().whichCar();
    car.stop(currentDate, position);
    return car;
  }
}
