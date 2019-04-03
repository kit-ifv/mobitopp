package edu.kit.ifv.mobitopp.simulation.car;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class ConventionalCar extends BaseCar implements Car, Serializable {

	private static final long serialVersionUID = 1L;

	protected Person driver;

	public ConventionalCar(
			int id, CarPosition position, Segment segment, int capacity, float initialMileage,
			float fuelLevel, int maxRange) {
		super(id, position, segment, capacity, initialMileage, fuelLevel, maxRange);
	}

	public ConventionalCar(
			IdSequence ids, CarPosition position, Car.Segment carSegment, int capacity,
			float initialMileage, float fuelLevel, int maxRange) {
		this(ids.nextId(), position, carSegment, capacity, initialMileage, fuelLevel, maxRange);
	}

	public ConventionalCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment
	) {
		this(ids, position, carSegment, 4, 0.0f,1.0f,1000);
	}

	public boolean isUsed() {
		return hasDriver();
	}

  @Override
  public boolean hasDriver() {
    return this.driver != null;
  }

	public void use(Person person, Time time) {
		assert !isUsed();
		this.driver = person;
		super.use(person, time);
	}

	public Person driver() {
		assert isUsed();

		return this.driver;
	}

	public void release(Person person, Time time) {
		assert driver() == person;
		this.driver = null;
		super.release(person, time);
	}

  public void returnCar(Zone zone) {
    
  }

	@Override
	public boolean canCarryPassengers() {
	  return hasDriver() && hasCapacity();
	}

  @Override
  public boolean hasCapacity() {
    return remainingCapacity() > 0;
  }

	public int remainingCapacity() {
		return this.capacity - (isUsed() ? 1 : 0) - passengers.size();
	}

}
