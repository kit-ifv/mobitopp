package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.time.Time;

public interface Car {

	public static enum Segment { SMALL, MIDSIZE, LARGE};

	int id();

	Segment carSegment();

	float currentMileage();

	/**
	 * Fuel level between 0.0d and 1.0d
	 * 
	 * @return fuel level
	 */
	float currentFuelLevel();
	
	/**
	 * Battery level between 0.0d and 1.0d
	 * 
	 * @return battery level
	 */
	float currentBatteryLevel();

	/**
	 * 
	 * @return remaining range in km
	 */
	int remainingRange();
	
	/**
	 * 
	 * @return maximum range in km
	 */
	int maxRange();
	
	/**
	 * 
	 * @return effective range in km
	 */
	Integer effectiveRange();

	/**
	 * Should throw an {@link IllegalArgumentException} when {@link #remainingRange()} is smaller than
	 * {@code distanceKm}. Currently only a warning is print on standard out.
	 * 
	 * @param distanceKm
	 * @throws IllegalArgumentException
	 */
	void driveDistance(float distanceKm) throws IllegalArgumentException;

	void refuel(float aimedFuelLevel) throws IllegalArgumentException;

	String getType();

	void use(Person person, Time currentTime);
	void release(Person person, Time currentTime);
	Person driver();
	boolean isUsed();

	Time startOfLastUsage();
	Time endOfLastUsage();
	
	void useAsPassenger(Person person);
	void leave(Person person);

	int capacity();
	int remainingCapacity();

	String passengersAsString();


	void start(Time currentTime);
	void stop(Time currentTime, CarPosition position);

	CarPosition position();

	boolean isStarted();
	boolean isStopped();

	public String forLogging(); 
	public String statusForLogging(); 

	public boolean isElectric();
}
