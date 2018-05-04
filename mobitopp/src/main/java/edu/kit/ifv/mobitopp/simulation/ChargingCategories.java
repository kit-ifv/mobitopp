package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class ChargingCategories {

	public final Category chargingAggregated = chargingAggregated();
	public final Category charging = charging();

	private Category chargingAggregated() {
		List<String> header = new ArrayList<>();
		return new Category("demandsimulationResultChargingAgg", header);
	}

	private Category charging() {
		List<String> header = new ArrayList<>();
		header.add("CH");
		header.add("carType");
		header.add("carSegment");
		header.add("carId");
		header.add("currentMileage");
		header.add("fuelLevel");
		header.add("chargingStartDay");
		header.add("chargingStartTime");
		header.add("standingDurationMinutes");
		header.add("chargingDurationMinutes");
		header.add("roundedOldBatteryLevel");
		header.add("roundedNewBatteryLevel");
		header.add("electricityChargedInKWh");
		header.add("zone");
		header.add("coordinates");
		header.add("chargingPointId");
		header.add("chargingPointType");
		header.add("chargingPowerKW");
		header.add("cumulatedElectricitySuppliedInKWh");
		header.add("stationId");
		return new Category("demandsimulationResultCharging", header);
	}

}
