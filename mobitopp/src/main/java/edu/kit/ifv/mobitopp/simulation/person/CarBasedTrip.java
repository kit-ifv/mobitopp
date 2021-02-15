package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

/**
 * This class should be used as a base class for all tours where the car is reserved for the driver
 * during the whole tour. It handles parking and releasing behaviour.
 * 
 * @author lars.briem@kit.edu
 */
@Slf4j
public abstract class CarBasedTrip extends BaseTrip {

  private final Class<? extends Car> carType;
  private final Mode mode;

  public CarBasedTrip(
      TripData data, SimulationPerson person, Class<? extends Car> carType, Mode mode) {
    super(data, person);
    this.carType = carType;
    this.mode = mode;
  }

  /**
   * Allocates a car to be used during this trip. The car can either be the car of the last trip or
   * a new one.
   */
  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
    Objects.requireNonNull(impedance, "impedance");
    Objects.requireNonNull(currentTime, "currentTime");
    if (hasPreviouslyUsedCar()) {
      useParkedCar();
    } else {
      useAllocatedCar(impedance, currentTime);
    }
  }

  /**
   * Returns whether the person already has a car from a previous trip or not. 
   * 
   * @return true if the person should use the parked car
   */
  protected abstract boolean hasPreviouslyUsedCar();

  private void useParkedCar() {
    assert person().hasParkedCar() : String
        .format("Person %s has no parked car.", person().getId());
    assert !person().isCarDriver() : String
        .format("Person %s is already driving.", person().getId());
    assertCarTypeOf(person().whichCar());
    person().takeCarFromParking();
  }
  
  private void assertCarTypeOf(Car car) {
    assert carType.isInstance(car) : String
    .format("Car should be of type %s but is %s.", carType, car.getClass());
  }
  
  private void assertMode() {
    assert mode().legMode() == mode : (trip() + "\n" + "prev: " + previousActivity() + "\n" + "next : "
        + nextActivity() + "\n --- \n" + person().activitySchedule() + "\n");
  }

  private void useAllocatedCar(ImpedanceIfc impedance, Time currentTime) {
    Car car = allocateCar(impedance, currentTime);
    person().useCar(car, currentTime);
  }

  /**
   * This method allocates a car for the person.
   * 
   * @param impedance access to all impedance types
   * @param currentTime current simulation time
   * @return car to be used on this trip
   */
  protected abstract Car allocateCar(ImpedanceIfc impedance, Time currentTime);

  /**
   * @see Trip
   */
  @Override
  public FinishedTrip finish(Time currentDate, PersonListener listener) {
    if (person().isCarDriver()) {
      return doFinish(currentDate, listener);
    }
    throw warn(new IllegalStateException(
        String.format("Cannot finish %s trip without car: %s", carType.getName(), trip())), log);
  }

  private FinishedTrip doFinish(Time currentDate, PersonListener listener) {
    FinishedTrip finishedTrip = super.finish(currentDate, listener);
    assertMode();
    assert person().isCarDriver();
    int carId = stopCar(currentDate);
    FinishedCarTrip finishedCarTrip = new FinishedCarTrip(finishedTrip, carId);
    notifyBeforeReturn(listener, finishedCarTrip);
    returnCar(currentDate);
    return finishedCarTrip;
  }

  private int stopCar(Time currentDate) {
    assertMode();
    Zone zone = getCarReturnZone();
    Location location = nextActivity().location();
    CarPosition position = new CarPosition(zone, location);
    Car car = person().whichCar();
    car.stop(currentDate, position);
    return car.id();
  }

  /**
   * This method will be called after the car usage has been stop but before the car is returned or
   * parked.
   * 
   * @param listener listener to be notified about the end of a car trip
   * @param finishedTrip finished trip
   */
  protected abstract void notifyBeforeReturn(PersonListener listener, FinishedTrip finishedTrip);

  private void returnCar(Time currentDate) {
    Zone zone = getCarReturnZone();
    if (canReturnCar(nextActivity())) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Location location = nextActivity().location();
      Car car = person().parkCar(zone, location, currentDate);
      assertCarTypeOf(car);
    }
  }

	protected abstract Zone getCarReturnZone();

  protected abstract boolean canReturnCar(ActivityIfc nextActivity);
}