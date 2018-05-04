package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.car.AbstractElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;

public class AbstractElectricCarFormat {

	private static final int batteryLevelIndex = ConventionalCarFormat.lastIndex + 1;
	private static final int electricRangeIndex = batteryLevelIndex + 1;
	private static final int batteryCapacityIndex = electricRangeIndex + 1;
	private static final int minimumChargingLevelIndex = batteryCapacityIndex + 1;
	public static final int lastIndex = minimumChargingLevelIndex;

	private final ConventionalCarFormat conventionalCarFormat;

	public AbstractElectricCarFormat(ConventionalCarFormat conventionalCarFormat) {
		super();
		this.conventionalCarFormat = conventionalCarFormat;
	}

	public List<String> header() {
		List<String> header = new ArrayList<>(conventionalCarFormat.header());
		header.add("currentBatteryLevel");
		header.add("electricRange");
		header.add("currentBatteryCapacity");
		header.add("minimumChargingLevel");
		return header;
	}

	public List<String> prepare(AbstractElectricCar car) {
		List<String> attributes = new ArrayList<>(conventionalCarFormat.prepare(car));
		attributes.add(valueOf(car.currentBatteryLevel()));
		attributes.add(valueOf(car.electricRange()));
		attributes.add(valueOf(car.currentBatteryCapacity()));
		attributes.add(valueOf(car.minimumChargingLevel()));
		return attributes;
	}

	float batteryLeveOf(List<String> data) {
		return Float.parseFloat(data.get(batteryLevelIndex));
	}

	int electricRangeOf(List<String> data) {
		return Integer.parseInt(data.get(electricRangeIndex));
	}

	float batteryCapacityOf(List<String> data) {
		return Float.parseFloat(data.get(batteryCapacityIndex));
	}

	float minimumChargingLevelOf(List<String> data) {
		return Float.parseFloat(data.get(minimumChargingLevelIndex));
	}

	int idOf(List<String> data) {
		return conventionalCarFormat.idOf(data);
	}

	CarPosition positionOf(List<String> data) {
		return conventionalCarFormat.positionOf(data);
	}

	Segment segementOf(List<String> data) {
		return conventionalCarFormat.segementOf(data);
	}

	int capacityOf(List<String> data) {
		return conventionalCarFormat.capacityOf(data);
	}

	float initialMileageOf(List<String> data) {
		return conventionalCarFormat.initialMileageOf(data);
	}

	int maxRangeOf(List<String> data) {
		return conventionalCarFormat.maxRangeOf(data);
	}

	float fuelLevelOf(List<String> data) {
		return conventionalCarFormat.fuelLevelOf(data);
	}
	
}
