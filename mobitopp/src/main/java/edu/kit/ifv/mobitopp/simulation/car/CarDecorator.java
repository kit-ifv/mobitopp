package edu.kit.ifv.mobitopp.simulation.car;


import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;



public class CarDecorator implements Car, Serializable {

	private static final long serialVersionUID = 1L;

	protected final Car car;

	public CarDecorator(Car car) {
		assert car != null;
		this.car = car;
	}


	public int id() { return car.id(); }

	public Segment carSegment() { return car.carSegment(); }

	public float currentMileage() { return car.currentMileage(); }

	public float currentFuelLevel() { return car.currentFuelLevel(); }
	public float currentBatteryLevel() { return car.currentBatteryLevel(); }

	public int remainingRange() { return car.remainingRange(); }
	public int maxRange() { return car.maxRange(); }
	public Integer effectiveRange() { return car.effectiveRange(); }

	public void driveDistance(float distanceKm) throws IllegalArgumentException { car.driveDistance(distanceKm); }

	public void refuel(float aimedFuelLevel) throws IllegalArgumentException { car.refuel(aimedFuelLevel); }

	public String getType() { return car.getType(); }



	public void use(Person person, Time currentTime) { car.use(person, currentTime); }
	public void release(Person person, Time currentTime) { car.release(person, currentTime); }
	public Person driver() { return car.driver(); }

	public boolean isUsed() { 
		assert car != null;
		return car.isUsed(); 
	}
	
  public void returnCar(Zone zone) {
    car.returnCar(zone);
  }

	public Time startOfLastUsage() { return car.startOfLastUsage(); }
	public Time endOfLastUsage() { return car.endOfLastUsage(); }
	
	public void useAsPassenger(Person person) { car.useAsPassenger(person); }
	public void leave(Person person) { car.leave(person); }


	public int capacity() { return car.capacity(); }
	public int remainingCapacity() { return car.remainingCapacity(); }

	public String passengersAsString() { return car.passengersAsString(); }


	public void start(Time currentTime) { car.start(currentTime); }
	public void stop(Time currentTime, CarPosition position) { car.stop(currentTime, position); }

	public boolean isStarted() { return car.isStarted(); }
	public boolean isStopped() { return car.isStopped(); }

	public CarPosition position() { return car.position(); }

	public String forLogging() { return car.forLogging(); }
	public String statusForLogging() { return car.statusForLogging(); }

	public boolean isElectric() { return car.isElectric(); }

  @Override
  public boolean hasCapacity() {
    return car.hasCapacity();
  }

  @Override
  public boolean hasDriver() {
    return car.hasDriver();
  }

  @Override
  public boolean canCarryPassengers() {
    return car.canCarryPassengers();
  }
  
  @Override
  public Car copy(int id) {
  	return car.copy(id);
  }

}
