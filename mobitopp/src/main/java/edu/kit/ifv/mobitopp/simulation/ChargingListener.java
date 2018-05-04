package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.emobility.ElectricCar;
import edu.kit.ifv.mobitopp.time.Time;

public interface ChargingListener {

	void stopCharging(
			ElectricCar car, Time chargingStart, int standingDurationMinutes,
			int chargingDurationMinutes, float oldBatteryLevel, float newBatteryLevel,
			float electricityCharged_kWh, int chargingPointId, String chargingPointType,
			float cumulatedElectricitySupplied_kWh, float chargingPowerKW, int stationId);

}