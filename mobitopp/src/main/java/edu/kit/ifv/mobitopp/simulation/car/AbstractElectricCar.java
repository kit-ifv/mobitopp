package edu.kit.ifv.mobitopp.simulation.car;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.ElectricCar;
import edu.kit.ifv.mobitopp.time.Time;

abstract public class AbstractElectricCar
	extends ConventionalCar
  implements Car, ElectricCar
    , Serializable
{

	public static final long serialVersionUID = -6040595907154277626L;

	protected final static float EPSILON = 0.000001f;

	protected final float electricRange;
	protected final float consumptionRateElectric;

	protected float batteryLevel = 1.0f;

	protected Time chargingStart;

	protected final float batteryCapacity_kWh;
	private final float minimumChargingLevel;


	public AbstractElectricCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment,
		int capacity,
		float intitialMileage,
		float batteryLevel,
		float fuelLevel,
		float electricRange,
		float conventionalRange,
		float batteryCapacity_kWh,
		float minimumChargingLevel
	) {

		super(ids, position, carSegment, capacity, intitialMileage, fuelLevel, conventionalRange);

		this.electricRange = electricRange;
		this.consumptionRateElectric = 1.0f/this.electricRange;

		this.batteryLevel = batteryLevel;
		this.batteryCapacity_kWh = batteryCapacity_kWh;
		this.minimumChargingLevel = minimumChargingLevel;
		this.chargingStart = null;
	}

	public AbstractElectricCar(
			int id, CarPosition position, Segment segment, int capacity, float initialMileage,
			float batteryLevel, float conventionalFuelLevel, float electricRange, float conventionalRange,
			float batteryCapacity_kWh, float minimumChargingLevel) {
		super(id, position, segment, capacity, initialMileage, conventionalFuelLevel, conventionalRange);
		this.electricRange = electricRange;
		this.consumptionRateElectric = 1.0f/this.electricRange;

		this.batteryLevel = batteryLevel;
		this.batteryCapacity_kWh = batteryCapacity_kWh;
		this.minimumChargingLevel = minimumChargingLevel;
		this.chargingStart = null;		
	}

	abstract public String getType();

	public float electricRange() {
		return electricRange;
	}
	
	public float currentBatteryCapacity() {
		return batteryCapacity_kWh;
	}

	public float minimumChargingLevel() {
		return minimumChargingLevel;
	}

	@Override
	public float currentBatteryLevel() {
		return batteryLevel;
	}

	@Override
  public void start(Time currentTime){

		if (isCharging()) {
			stopCharging(currentTime);
		}

		super.start(currentTime);
	}

	@Override
  public void stop(Time currentTime, CarPosition position){
		super.stop(currentTime, position);

		if (canCharge() && needsElectricity()) {
			startCharging(currentTime);
		}
	}

	private boolean needsElectricity() {
		return currentBatteryLevel() < minimumChargingLevel;
	}

	public boolean isCharging() {
		return chargingStart != null;
	}

	protected boolean canCharge() {
		assert isUsed();
		Person driver = driver();
		Household household = driver.household();
		ActivityType activityType = driver.nextActivity().activityType();
		Zone zone = position().zone;
		return zone.charging().canElectricCarCharge(activityType, household);
	}

  protected void startCharging(Time currentTime){
		assert !isCharging();

		this.chargingStart = currentTime;

		ActivityIfc nextActivity = driver.nextActivity();
		Zone zone = position().zone;

		float electricityNeeded_kWh = electricityNeeded();
		Household household = driver().household();

		zone.charging().startCharging(this, household, nextActivity, currentTime, electricityNeeded_kWh);
	}

	protected float electricityNeeded() {

		return (1.0f-this.batteryLevel) * this.batteryCapacity_kWh;
	}

	protected float chargeElectricity(float electricty_kwH) {

		assert electricty_kwH/batteryCapacity_kWh <= 1.0+EPSILON;
		assert this.batteryLevel + electricty_kwH/batteryCapacity_kWh <= 1.0+EPSILON : 
																							("" + this.batteryLevel + "," + electricty_kwH/batteryCapacity_kWh);

		this.batteryLevel += electricty_kwH/batteryCapacity_kWh;

		assert this.batteryLevel <= 1.0+EPSILON;

		return this.batteryLevel;
	}

	protected void stopCharging(Time currentTime) {
		assert isCharging();
		Zone zone = position().zone;
		float electrictyProvided_kwH = zone.charging().stopCharging(this, currentTime);
		chargeElectricity(electrictyProvided_kwH);
		this.chargingStart = null;
	}

	protected float chargingRatePercentPerMinute(float chargingKW) {
		return (chargingKW/batteryCapacity_kWh)/60;
	}

	public int remainingChargingTime(float chargingKW) {
		
		return (int) Math.ceil( (1.0-batteryLevel) / chargingRatePercentPerMinute(chargingKW));
	}


  @Override
	abstract public void driveDistance(float distanceKm);

	@Override
	abstract public float remainingRange();

	protected float remainingElectricRange() {
		return ((float) Math.floor(batteryLevel*electricRange*1000.0f)) / 1000.0f;
	}

	protected float remainingConventionalRange() {
		return ((float) Math.floor(fuelLevel*maxRange*1000.0f)) / 1000.0f;
	}

	@Override
	abstract public float effectiveRange();
	
	@Override
	abstract public float maxRange();


	abstract public String electricModeAsChar();


	@Override
	public String toString() {

		return "Car(id=" + id + ",type=" + getType() 
									+ ",mileage=" + mileage 
									+ ",battery=" + batteryLevel 
									+ ",range=" + effectiveRange() 
									+ ")";
	}

	@Override
	public String statusForLogging() {
		String cartype 					= getType().substring(0,1).toUpperCase();
		int carid 							= id();
		Car.Segment carSegment 	= carSegment();

		int driveroid						= driver().getOid();
		String passengers 			= "(" + passengersAsString() + ")";

		String electricCarMode 	= electricModeAsChar();

		double currentMileage = Math.floor(10.0f*currentMileage())/10.0f;
		double fuelLevel 			= Math.floor(100.0f*currentFuelLevel())/100.0f;
		double batteryLevel 	= Math.floor(100.0f*currentBatteryLevel())/100.0f;
		float remainingRange 		= remainingRange();

		CsvBuilder builder = new CsvBuilder();
		builder.append(cartype);
		builder.append(carid);
		builder.append(carSegment);
		builder.append(currentMileage);
		builder.append(fuelLevel);
		builder.append(batteryLevel);
		builder.append(electricCarMode);
		builder.append(remainingRange);
		builder.append(driveroid);
		builder.append(passengers);
		return builder.toString();
	}

	public boolean isElectric() {
		return true;
	}


}
