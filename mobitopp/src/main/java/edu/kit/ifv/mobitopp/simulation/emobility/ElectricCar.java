package edu.kit.ifv.mobitopp.simulation.emobility;

import edu.kit.ifv.mobitopp.simulation.Car;

public interface ElectricCar extends Car {

	public boolean isCharging();

	/**
	 * @return battery level from 0.0d to 1.0d
	 */
	public float currentBatteryLevel();

}
