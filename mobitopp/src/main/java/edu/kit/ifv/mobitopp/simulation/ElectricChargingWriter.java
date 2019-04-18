package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.emobility.ElectricCar;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class ElectricChargingWriter implements ChargingListener {

	private static final int minutesPerWeek = 7 * 24 * 60;
	private static final int TOTAL_HOURS = 7*24+12;
	private static final int TOTAL_MINUTES = TOTAL_HOURS*60;
	
	private final double energyConsumed[];
	private final ResultWriter results;
	private final ChargingCategories categories; 
	
	public ElectricChargingWriter(ResultWriter results) {
		super();
		this.energyConsumed = new double[TOTAL_MINUTES];
		this.results = results;
		categories = new ChargingCategories();
	}
	
	public void clear() {
		for(int i=0; i< TOTAL_MINUTES; i++) {
			energyConsumed[i] = 0.0;
		}
	}
	
	public void print() {
		String message = "Energy consumption per minute:";
		results().write(categories.chargingAggregated, message);

		Double hourly[] = new Double[TOTAL_HOURS];

		for(int i=0; i< TOTAL_HOURS; i++) {
			hourly[i]=0.0;
		}

		for(int i=0; i< TOTAL_MINUTES; i++) {
			Double energy = energyConsumed[i];


			int hour = i / 60;
			int day = hour / 24;
			int hour_day = hour % 24;

			message = "" + i + ";" + hour + ";" + day + ";" + hour_day + ";" + energy; 
			results().write(categories.chargingAggregated, message);

			hourly[i/60] += energy;
		}

		message = "Energy consumption per hour:";
		results().write(categories.chargingAggregated, message);

		for(int i=0; i< TOTAL_HOURS; i++) {
			Double energy = hourly[i];

			int hour = i;
			int day = hour / 24;
			int hour_day = hour % 24;

			message = "" + i + ";" + hour + ";" + day + ";" + hour_day + ";" + energy; 
			results().write(categories.chargingAggregated, message);
		}
	}

	private ResultWriter results() {
		return results;
	}
  
  @Override
	public void stopCharging(
			ElectricCar car,
			Time chargingStart,
			int standingDurationMinutes,
			int chargingDurationMinutes,
			float oldBatteryLevel,
			float newBatteryLevel,
			float electricityCharged_kWh,
			int chargingPointId,
			String chargingPointType,
			float cumulatedElectricitySupplied_kWh,
			float chargingPowerKW,
			int stationId
	) {

		assert oldBatteryLevel < 1.0f;

		int carid 						= car.id();
		String cartype = car.getType().substring(0,1).toUpperCase();
		Car.Segment carSegment  = car.carSegment();

		double currentMileage = Math.floor(10.0f*car.currentMileage())/10.0f;
		double fuelLevel = Math.floor(100.0f*car.currentFuelLevel())/100.0f;

		double roundedOldBatteryLevel = Math.floor(1000.0f*oldBatteryLevel)/1000.0f;
		double roundedNewBatteryLevel = Math.floor(1000.0f*newBatteryLevel)/1000.0f;

		String chargingStartDay = results().dateFormat().asDay(chargingStart);
		String chargingStartTime = results().dateFormat().asTime(chargingStart);

		String zone = car.position().zone.getId().getExternalId();
		String coordinates = car.position().coordinates();

		CsvBuilder message = new CsvBuilder(); 
		message.append("CH");
		message.append(cartype);
		message.append(carSegment);
		message.append(carid);
		message.append(currentMileage);
		message.append(fuelLevel);
		message.append(chargingStartDay);
		message.append(chargingStartTime);
		message.append(standingDurationMinutes);
		message.append(chargingDurationMinutes);
		message.append(roundedOldBatteryLevel);
		message.append(roundedNewBatteryLevel);
		message.append(electricityCharged_kWh);
		message.append(zone);
		message.append(coordinates);
		message.append(chargingPointId);
		message.append(chargingPointType);
		message.append(chargingPowerKW);
		message.append(cumulatedElectricitySupplied_kWh);
		message.append(stationId);

		results().write(categories.charging, message.toString());

		accumulateEnergyConsumed(chargingStart, chargingDurationMinutes, electricityCharged_kWh);
	}


	private void accumulateEnergyConsumed(
				Time start, 
				int chargingDurationMinutes, 
				double energyConsumptionKWH
	) {

		int chargingStart = minutesSinceStartOfWeek(start);

		double consumptionPerMinute = energyConsumptionKWH/chargingDurationMinutes;

		for(int i=chargingStart; i<chargingStart+chargingDurationMinutes; i++) {

			if (i < TOTAL_MINUTES) {
				energyConsumed[i] += consumptionPerMinute;
			}
		}

	}

	private int minutesSinceStartOfWeek(Time date) {
		
		DayOfWeek day = date.weekDay();

		int minutesFromElapsedDays = daysElapsed(day) * 24 * 60;

		long minutesToday = date.differenceTo(date.startOfDay()).toMinutes();

		long minutes = minutesFromElapsedDays + minutesToday;

		assert minutes < minutesPerWeek;

		return Math.toIntExact(minutes);
	}

	 private int daysElapsed(DayOfWeek day) {
		return day.getTypeAsInt();
	}


}
