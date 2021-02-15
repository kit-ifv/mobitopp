package edu.kit.ifv.mobitopp.simulation.car;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutonomousCar extends BaseCar {

  private static final long serialVersionUID = 1L;

  public AutonomousCar(
      int id, CarPosition position, Segment segment, int capacity, float initialMileage,
      float fuelLevel, int maxRange) {
    super(id, position, segment, capacity, initialMileage, fuelLevel, maxRange);
  }

  @Override
  public Person driver() {
    throw warn(new IllegalStateException("Should not call driver for non driver car. Call hasDriver before."), log);
  }

  @Override
  public boolean isUsed() {
    return false;
  }

  @Override
  public void returnCar(Zone zone) {
  }

  @Override
  public boolean canCarryPassengers() {
    return hasCapacity();
  }

  @Override
  public int remainingCapacity() {
    return this.capacity - passengers.size();
  }

  @Override
  public boolean hasCapacity() {
    return remainingCapacity() > 0;
  }

  @Override
  public boolean hasDriver() {
    return false;
  }
  
  @Override
  public Car copy(int id) {
  	return new AutonomousCar(id, position, carSegment, capacity, mileage, fuelLevel, maxRange);
  }

}
