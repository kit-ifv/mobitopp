package edu.kit.ifv.mobitopp.simulation.car;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtendedRangeElectricCar
	extends AbstractElectricCar
  implements Car
    , Serializable
{

	private static final long serialVersionUID = 1L;
	private static final float conventionalFuelLevel = 1.0f;
	private static final float initialMileage = 0.0f;
	private static final int capacity = 4;

	public static enum Mode { ALLELECTRIC, FULLPOWER, DEGRADED};


	protected final float fullPowerRange;
	protected final float consumptionRateElectricFullPower;
	protected final float consumptionRateFuelFullPower;
	protected final float consumptionRateFuelDegraded;

	protected Mode lastMode = Mode.ALLELECTRIC;


	public ExtendedRangeElectricCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment,
		int capacity,
		float intitialMileage,
		float batteryLevel,
		float fuelLevel,
		float electricRange,
		float conventionalRange,
		float fullPowerRange,
		float batteryCapacity,
		float minimumChargingLevel
	) {

		super(ids, position, carSegment, capacity, intitialMileage, 
						batteryLevel, fuelLevel, 
						electricRange, conventionalRange, batteryCapacity, minimumChargingLevel);

		this.fullPowerRange = fullPowerRange;
		this.consumptionRateElectricFullPower = 1.0f/this.fullPowerRange;
		this.consumptionRateFuelFullPower = 1.0f/this.fullPowerRange;
		this.consumptionRateFuelDegraded = 1.0f/this.fullPowerRange;

	}

	public ExtendedRangeElectricCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment,
		float batteryLevel,
		float electricRange,
		float conventionalRange,
		float fullPowerRange,
		float batteryCapacity,
		float minimumChargingLevel
	) {
		this(ids, position, carSegment, capacity, initialMileage, batteryLevel, conventionalFuelLevel,
				electricRange, conventionalRange, fullPowerRange, batteryCapacity, minimumChargingLevel);
	}

	public ExtendedRangeElectricCar(
			int id, CarPosition position, Segment segment, int capacity, float initialMileage,
			float batteryLevel, float fuelLevel, float electricRange, float conventionalRange,
			float fullPowerRange, float batteryCapacity, float minimumChargingLevel) {
		super(id, position, segment, capacity, initialMileage, batteryLevel, fuelLevel,
				electricRange, conventionalRange, batteryCapacity, minimumChargingLevel);

		this.fullPowerRange = fullPowerRange;
		this.consumptionRateElectricFullPower = 1.0f/this.fullPowerRange;
		this.consumptionRateFuelFullPower = 1.0f/this.fullPowerRange;
		this.consumptionRateFuelDegraded = 1.0f/this.fullPowerRange;
	}

	public String getType() {
		return CarType.erev.getName();
	}

	public float fullPowerRange() {
		return fullPowerRange;
	}


  @Override
	public void driveDistance(float distanceKm) {

		if (distanceKm <= 0.0f) {

			throw warn(new IllegalArgumentException("distance must be positive"), log);
		}
			

		if (distanceKm <= remainingElectricRange() ) {

			driveAllElectric(distanceKm);
			return;
		}

		if (distanceKm <= remainingFullPowerRange() ) {

			driveFullPower(distanceKm);
			return;
		}

		float remainingDistanceKm = distanceKm;

		while (remainingDistanceKm > remainingConventionalRange() ) {

			driveDegraded(remainingConventionalRange());
			remainingDistanceKm -= remainingConventionalRange();
			refuel(1.0f);
		}

		driveDegraded(remainingDistanceKm);

	}


	private void driveAllElectric(final float distanceKm) {

		assert distanceKm <= remainingElectricRange();

		mileage += distanceKm;
		batteryLevel -= consumptionRateElectric*distanceKm;
		lastMode=Mode.ALLELECTRIC;
	}

	private void driveFullPower(final float distanceKm) {

		assert distanceKm <= remainingFullPowerRange() : ("distance=" + distanceKm 
																											+ ",remaining=" + remainingFullPowerRange());

		if (distanceKm >= remainingConventionalRange()) {
			refuel(1.0f);
		}

		mileage += distanceKm;
		batteryLevel -= consumptionRateElectricFullPower*distanceKm;
		fuelLevel -= consumptionRateFuelFullPower*distanceKm;
		lastMode=Mode.FULLPOWER;
	}

	private void driveDegraded(final float distanceKm) {

		assert distanceKm <= remainingConventionalRange();

		fuelLevel -= consumptionRateFuelDegraded*distanceKm;
		mileage += distanceKm;
		lastMode=Mode.DEGRADED;
	}

	public float remainingFullPowerRange() {

		return ((float) Math.floor(batteryLevel*fullPowerRange*1000.0f)) / 1000.0f;
	}

	@Override
	public float remainingRange() {

 	 return remainingFullPowerRange();
	}

	@Override
	public float effectiveRange() {
		return Float.MAX_VALUE;
	}
	
	@Override
	public float maxRange() {
		return maxRange + electricRange;
	}

	public Mode lastMode() {
		return this.lastMode;
	}

	public String electricModeAsChar() {

		switch(lastMode) {
			case ALLELECTRIC: return "B";
			case FULLPOWER: return "F";
			case DEGRADED: return "D";
		}

		throw new AssertionError();
	}
	
	@Override
	public Car copy(int id) {
		return new ExtendedRangeElectricCar(id, position, carSegment, capacity, mileage, batteryLevel,
				fuelLevel, electricRange, remainingConventionalRange(), fullPowerRange, batteryCapacity_kWh,
				minimumChargingLevel());
	}

}
