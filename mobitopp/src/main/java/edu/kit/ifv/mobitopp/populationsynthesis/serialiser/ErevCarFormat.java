package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;

public class ErevCarFormat implements SerialiserFormat<ExtendedRangeElectricCar> {

	private static final int fullPowerRangeIndex = AbstractElectricCarFormat.lastIndex + 1;

	private final AbstractElectricCarFormat electricCarFormat;

	public ErevCarFormat(AbstractElectricCarFormat electricCarFormat) {
		super();
		this.electricCarFormat = electricCarFormat;
	}

	@Override
	public List<String> header() {
		List<String> header = new ArrayList<>(electricCarFormat.header());
		header.add("fullPowerRange");
		return header;
	}

	@Override
	public List<String> prepare(ExtendedRangeElectricCar car) {
		List<String> attributes = new ArrayList<>(electricCarFormat.prepare(car));
		attributes.add(valueOf(car.fullPowerRange()));
		return attributes;
	}

	@Override
	public Optional<ExtendedRangeElectricCar> parse(List<String> data) {
		int id = electricCarFormat.idOf(data);
		CarPosition positions = electricCarFormat.positionOf(data);
		Segment segment = electricCarFormat.segementOf(data);
		int capacity = electricCarFormat.capacityOf(data);
		float initialMileage = electricCarFormat.initialMileageOf(data);
		float batteryLevel = electricCarFormat.batteryLeveOf(data);
		float fuelLevel = electricCarFormat.fuelLevelOf(data);
		float electricRange = electricCarFormat.electricRangeOf(data);
		float conventionalRange = electricCarFormat.maxRangeOf(data) - electricRange;
		float fullPowerRange = fullPowerRangeOf(data);
		float batteryCapacity = electricCarFormat.batteryCapacityOf(data);
		float minimumChargingLevel = electricCarFormat.minimumChargingLevelOf(data);
		return Optional.of(new ExtendedRangeElectricCar(id, positions, segment, capacity, initialMileage,
				batteryLevel, fuelLevel, electricRange, conventionalRange, fullPowerRange, batteryCapacity,
				minimumChargingLevel));
	}

	private float fullPowerRangeOf(List<String> data) {
		return Float.parseFloat(data.get(fullPowerRangeIndex));
	}

}
