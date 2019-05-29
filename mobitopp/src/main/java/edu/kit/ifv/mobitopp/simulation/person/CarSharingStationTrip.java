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

public class CarSharingStationTrip extends BaseTrip implements Trip {

  public CarSharingStationTrip(TripData trip, SimulationPerson person) {
    super(trip, person);
  }
  
  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
    Objects.requireNonNull(impedance, "impedance");
    Objects.requireNonNull(currentTime, "currentTime");
    if (person().currentActivity().activityType().isHomeActivity()) {
      allocateCar(currentTime);
    } else {
      useParkedCar();
    }
  }

  private void useParkedCar() {
    assert person().hasParkedCar() : String.format("Person %s has no parked car.", person().getId());
    assert !person().isCarDriver() : String.format("Person %s is already driving.", person().getId());
    person().takeCarFromParking();
  }

  private void allocateCar(Time currentTime) {
    Zone zone = person().currentActivity().zone();
    assert zone.carSharing().isStationBasedCarSharingCarAvailable(person()) : "Carsharing is not available.";
    Car car = zone.carSharing().bookStationBasedCar(person());
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
    Car car = stopCar(currentDate);
    returnCar(currentDate);
    return new FinishedCarTrip(finishedTrip, car.id());
  }

  private Car stopCar(Time currentDate) {
    assert mode() == Mode.CARSHARING_STATION : (trip() + "\n" 
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

  public void returnCar(Time currentDate) {
    Zone zone = nextActivity().zone();
    if (nextActivity().activityType().isHomeActivity()) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Location location = nextActivity().location();
      Car car = person().parkCar(zone, location, currentDate);
      assert car instanceof CarSharingCar;
    }
  }

}
