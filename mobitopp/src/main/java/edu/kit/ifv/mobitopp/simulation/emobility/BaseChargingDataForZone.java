package edu.kit.ifv.mobitopp.simulation.emobility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ChargingListener;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class BaseChargingDataForZone implements ChargingDataForZone, Serializable {
	
	static final int privateId = -2;
	static final int semiPublicId = -1;
	private static final long serialVersionUID = 1L;
	private static final int longestDifference = Integer.MAX_VALUE;
	private static final double always = 1.0;

	private final List<ChargingFacility> chargingFacilities;
	private final Map<ElectricCar, ChargingFacility> usedChargingPoints;
	private final double privateChargingProbabilty;
	private final DefaultPower defaultPower;
	private transient ChargingListener chargingListener;

	public BaseChargingDataForZone(List<ChargingFacility> facilities, DefaultPower defaultPower) {
		this(facilities, always, defaultPower);
	}
	
	public BaseChargingDataForZone(
			List<ChargingFacility> chargingFacilities, double privateChargingProbabilty, DefaultPower defaultPower) {
		super();
		this.chargingFacilities = chargingFacilities;
		this.privateChargingProbabilty = privateChargingProbabilty;
		this.defaultPower = defaultPower;
		usedChargingPoints = new HashMap<ElectricCar, ChargingFacility>();
	}

	@Override
	public int numberOfAvailableChargingPoints() {
		return numberOfAvailableChargingPoints(chargingFacilities);
	}

	@Override
	public boolean isChargingPointAvailable() {
		return numberOfAvailableChargingPoints() > 0;
	}

	public abstract int numberOfAvailableChargingPoints(List<ChargingFacility> chargingFacilities);

	protected static boolean canChargeAtHome(ActivityType activityType, Household household) {
		return activityType.isHomeActivity() && household.canChargePrivately();
	}

	@Override
	public double privateChargingProbability() {
		return privateChargingProbabilty;
	}

	@Override
	public int numberOfChargingPoints() {
		if (this.chargingFacilities == null) {
			return 0;
		}
		return this.chargingFacilities.size();
	}

	@Override
	public boolean canElectricCarCharge(ActivityType activityType, Household household) {
		return canChargeAtHome(activityType, household) || isChargingPointAvailable();
	}

	private static boolean usesPublicChargingFacility(
			ActivityType activityType, Household household) {
		return !canChargeAtHome(activityType, household);
	}

	private boolean usesSemipublicChargingFacility(ActivityType activityType) {
		return activityType.isWorkActivity() || activityType.isEducationActivity();
	}

	@Override
	public void startCharging(
			ElectricCar withCar, Household household, ActivityIfc activity, Time currentTime,
			float requestedElectricity_kWh) {
		assert canElectricCarCharge(activity.activityType(), household);
		ChargingFacility chargingFacility = chargingFacility(household, activity, currentTime,
				requestedElectricity_kWh);
		use(chargingFacility, withCar);
		chargingFacility.startCharging(currentTime, requestedElectricity_kWh);
	}

	private void use(ChargingFacility chargingFacility, ElectricCar car) {
		usedChargingPoints.put(car, chargingFacility);
	}

	private ChargingFacility chargingFacility(
			Household household, ActivityIfc activity, Time time,
			float requestedElectricity) {
		ActivityType activityType = activity.activityType();
		if (usesPublicChargingFacility(activityType, household)) {
			return freeChargingPoint(time, activity, requestedElectricity);
		} else if (usesSemipublicChargingFacility(activityType)) {
			return semiPublicChargingFacility(activity);
		}
		return privateChargingFacility(activity);
	}

	private ChargingFacility freeChargingPoint(
			Time currentTime, ActivityIfc activity, float requestedElectricity) {
		Time endDate = activity.calculatePlannedEndDate();
		long bestMatchingTime = longestDifference;
		ChargingFacility bestMatching = null;
		for (ChargingFacility facility : chargingFacilities) {
			if (facility.isFree()) {
				assert !usedChargingPoints.containsValue(facility);
				Time timeTillFull = facility.timeTillFull(requestedElectricity, currentTime);
				long differenceInSeconds = Math.abs(timeTillFull.differenceTo(endDate).seconds());
				if (bestMatchingTime > differenceInSeconds) {
					bestMatching = facility;
					bestMatchingTime = differenceInSeconds;
				}
			}
		}
		ChargingFacility facility = bestMatching != null ? bestMatching
				: freshChargingPoint(activity.location(), defaultPower);
		return registerListenerTo(facility);
	}

	private ChargingFacility registerListenerTo(ChargingFacility facility) {
		facility.register(chargingListener);
		return facility;
	}

	protected abstract ChargingFacility freshChargingPoint(Location location, DefaultPower defaultPower);

	ChargingFacility semiPublicChargingFacility(ActivityIfc activity) {
		Location location = activity.zone().getZonePolygon().centroidLocation();
		ChargingFacility facility = new ChargingFacility(semiPublicId, location, ChargingFacility.Type.SEMIPUBLIC,
				defaultPower.semipublicFacility());
		return registerListenerTo(facility);
	}

	ChargingFacility privateChargingFacility(ActivityIfc activity) {
		Location location = activity.zone().getZonePolygon().centroidLocation();
		ChargingFacility facility = new ChargingFacility(privateId, location, ChargingFacility.Type.PRIVATE,
				defaultPower.privateFacility());
		return registerListenerTo(facility);
	}
	
	@Override
	public float stopCharging(ElectricCar car, Time currentTime) {
		assert this.usedChargingPoints.containsKey(car);
		ChargingFacility chargingPoint = usedChargingPoints.remove(car);
		return chargingPoint.stopCharging(car, currentTime);
	}
	
	@Override
	public void register(ChargingListener chargingListener) {
		this.chargingListener = chargingListener;
		for (ChargingFacility chargingFacility : chargingFacilities) {
			chargingFacility.register(chargingListener);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chargingFacilities == null) ? 0 : chargingFacilities.hashCode());
		result = prime * result + ((defaultPower == null) ? 0 : defaultPower.hashCode());
		long temp;
		temp = Double.doubleToLongBits(privateChargingProbabilty);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseChargingDataForZone other = (BaseChargingDataForZone) obj;
		if (chargingFacilities == null) {
			if (other.chargingFacilities != null)
				return false;
		} else if (!chargingFacilities.equals(other.chargingFacilities))
			return false;
		if (defaultPower == null) {
			if (other.defaultPower != null)
				return false;
		} else if (!defaultPower.equals(other.defaultPower))
			return false;
		if (Double.doubleToLongBits(privateChargingProbabilty) != Double
				.doubleToLongBits(other.privateChargingProbabilty))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [chargingFacilities=" + chargingFacilities
				+ ", privateChargingProbabilty=" + privateChargingProbabilty + ", defaultPower="
				+ defaultPower + "]";
	}

}