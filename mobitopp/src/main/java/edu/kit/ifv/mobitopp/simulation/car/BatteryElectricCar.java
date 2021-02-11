package edu.kit.ifv.mobitopp.simulation.car;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatteryElectricCar extends AbstractElectricCar implements Car, Serializable {

	private static final long serialVersionUID = 1L;
	private static final float conventionalFuelLevel = 0.0f;
	private static final int conventionalRange = 0;

	public static enum Mode { BATTERY, TOWED};

	protected Mode lastMode = Mode.BATTERY;

	public BatteryElectricCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment,
		int capacity,
		float intitialMileage,
		float batteryLevel,
		int maxRange,
		float batteryCapacity,
		float minimumChargingLevel
	) {
		super(ids, position, carSegment, capacity, intitialMileage, batteryLevel, conventionalFuelLevel, 
					maxRange, conventionalRange, batteryCapacity, minimumChargingLevel);
	}

	public BatteryElectricCar(
		IdSequence ids, 
		CarPosition position,
		Car.Segment carSegment,
		float batteryLevel,
		int maxRange,
		float batteryCapacity, 
		float minimumChargingLevel
	) {
		this(ids, position, carSegment, 4, 0.0f, batteryLevel, maxRange, batteryCapacity, minimumChargingLevel);
	}


	public BatteryElectricCar(
			int id, CarPosition position, Segment segment, int capacity, float initialMileage,
			float batteryLevel, int electricRange, float batteryCapacity, float minimumChargingLevel) {
		super(id, position, segment, capacity, initialMileage, batteryLevel, conventionalFuelLevel, electricRange, 0,
				batteryCapacity, minimumChargingLevel);
	}

	public String getType() {
		return CarType.bev.getName();
	}

	@Override
	public float currentFuelLevel() {
		return 0.0f;
	}

  @Override
	public void driveDistance(float distanceKm) {

		if (distanceKm <= 0.0f) {

			throw warn(new IllegalArgumentException("distance must be positive"), log);
		}

		lastMode = Mode.BATTERY;
			
		if (distanceKm > remainingRange() ) {

			log.warn("WARNING: BatteryElectricCar.drive(): distance > remainingRange !!!");
			log.warn("distance:" + distanceKm);
			log.warn("remaining Range:" +  remainingRange());
			log.warn("effective Range:" +  effectiveRange());
			log.warn("battery Level:" +  currentBatteryLevel());
			log.warn(this.toString());

			lastMode = Mode.TOWED;

			driveTowed(distanceKm);
		} else {
			driveInternal(distanceKm);
		}
	}

	@Override
	protected void driveInternal(float distanceKm) {

		mileage += distanceKm;
		batteryLevel -= consumptionRateElectric*distanceKm;
	}

	protected void driveTowed(float distanceKm) {

		mileage += distanceKm;
		batteryLevel = 0.0f;
	}

	@Override
	public int remainingRange() {

 	 return (int) Math.floor(batteryLevel*electricRange);
	}

	@Override
	public Integer effectiveRange() {
		return remainingRange();
	}

	@Override
	public int maxRange() {

		return electricRange;
	}

	public Mode lastMode() {
		return this.lastMode;
	}

	public String electricModeAsChar() {

		switch(lastMode) {
			case BATTERY: return "B";
			case TOWED: return "X";
		}
		throw new AssertionError();
	}
	
	@Override
	public Car copy(int id) {
		return new BatteryElectricCar(id, position, carSegment, capacity, mileage, batteryLevel,
				electricRange, batteryCapacity_kWh, minimumChargingLevel());
	}

}
