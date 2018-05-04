package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;

public class BevCarFormat implements SerialiserFormat<BatteryElectricCar> {

	private final AbstractElectricCarFormat electricCarFormat;

	public BevCarFormat(AbstractElectricCarFormat electricCarFormat) {
		super();
		this.electricCarFormat = electricCarFormat;
	}
	
	@Override
	public List<String> header() {
		return electricCarFormat.header();
	}

	@Override
	public List<String> prepare(BatteryElectricCar car) {
		return electricCarFormat.prepare(car);
	}

	@Override
	public BatteryElectricCar parse(List<String> data) {
		int id = electricCarFormat.idOf(data);
		CarPosition position = electricCarFormat.positionOf(data);
		Segment segment = electricCarFormat.segementOf(data);
		int capacity = electricCarFormat.capacityOf(data);
		float initialMileage = electricCarFormat.initialMileageOf(data);
		float batteryLevel = electricCarFormat.batteryLeveOf(data);
		int electricRange = electricCarFormat.electricRangeOf(data);
		float batteryCapacity = electricCarFormat.batteryCapacityOf(data);
		float minimumChargingLevel = electricCarFormat.minimumChargingLevelOf(data);
		return new BatteryElectricCar(id, position, segment, capacity, initialMileage, batteryLevel,
				electricRange, batteryCapacity, minimumChargingLevel);
	}

}
