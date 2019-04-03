package edu.kit.ifv.mobitopp.simulation.car;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Person;

public class AutonomousCar extends BaseCar {

  private static final long serialVersionUID = 1L;

  public AutonomousCar(
      int id, CarPosition position, Segment segment, int capacity, float initialMileage,
      float fuelLevel, int maxRange) {
    super(id, position, segment, capacity, initialMileage, fuelLevel, maxRange);
  }

  @Override
  public Person driver() {
    throw new IllegalStateException("Should not call driver for non driver car. Call hasDriver before.");
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

}
