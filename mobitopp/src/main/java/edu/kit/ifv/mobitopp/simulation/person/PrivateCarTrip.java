package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripDecorator;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PrivateCarTrip extends TripDecorator implements TripIfc {

  public PrivateCarTrip(TripIfc trip, SimulationPerson person) {
    super(trip, person);
  }

  @Override
  public void allocateVehicle(ImpedanceIfc impedance, Time currentTime) {
    Objects.requireNonNull(impedance);
    Objects.requireNonNull(currentTime);
    if (person().currentActivity().activityType().isHomeActivity()) {
      useCarOfHousehold(impedance, currentTime);
    } else {
      useParkedCar();
    }
  }

  private void useParkedCar() {
    assert person().hasParkedCar();
    assert !person().isCarDriver();
    person().takeCarFromParking();
  }

  private void useCarOfHousehold(ImpedanceIfc impedance, Time currentTime) {
    int origin = origin().zone().getOid();
    int destination = destination().zone().getOid();
    float distance = impedance.getDistance(origin, destination);
    float distanceKm = distance / 1000.0f;
    PrivateCar car = person().household().takeAvailableCar(person(), distanceKm);
    person().useCar(car, currentTime);
  }

  @Override
  public FinishedTrip finish(Time currentDate, PersonResults results) {
    Person person = person();
    Integer carId = null;
    if (person.isCarDriver()) {
      ActivityIfc activity = trip().nextActivity();
      carId = stopCar(currentDate);
      notifyFinishCarTrip(person, trip(), activity, results);
    }
    returnCar(currentDate);
    FinishedTrip finishedTrip = super.finish(currentDate, results);
    return new FinishedCarTrip(finishedTrip, carId);
  }

  private Integer stopCar(Time currentDate) {
    assert mode() == Mode.CAR : (trip() + "\n" 
                                  + "prev: " + previousActivity() + "\n"
                                  + "next : " + nextActivity()  + "\n --- \n"
                  + person().activitySchedule() + "\n"
                  ) ;
    Location location = nextActivity().location();
    Zone zone = nextActivity().zone();
    Car car = person().whichCar();
    car.stop(currentDate, new CarPosition(zone, location));
    return car.id();
  }

  private void notifyFinishCarTrip(Person person, TripIfc trip, ActivityIfc activity, PersonResults results) {
    ActivityIfc prevActivity = trip.previousActivity();
    assert prevActivity != null;
    results.notifyFinishCarTrip(person, person.whichCar(), trip, activity);
  }

  private void returnCar(Time currentDate) {
    ActivityIfc activity = nextActivity();
    Zone zone = activity.zone();
    Location location = activity.location();
    if (activity.activityType().isHomeActivity()) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Car car = person().parkCar(zone, location, currentDate);
      assert car instanceof PrivateCar;
    }
  }
}
