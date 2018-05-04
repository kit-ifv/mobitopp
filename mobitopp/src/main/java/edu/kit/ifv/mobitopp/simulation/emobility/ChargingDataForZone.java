package edu.kit.ifv.mobitopp.simulation.emobility;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ChargingListener;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface ChargingDataForZone {

	int numberOfChargingPoints();

	int numberOfAvailableChargingPoints();

	double privateChargingProbability();

	boolean isChargingPointAvailable();

	boolean canElectricCarCharge(ActivityType activityType, Household household);

	void startCharging(
			ElectricCar car, Household household, ActivityIfc activity, Time time,
			float kwh);

	float stopCharging(ElectricCar car, Time currentTime);

	void register(ChargingListener chargingListener);

}