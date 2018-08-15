package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;

public class ConventionalCarFormat implements SerialiserFormat<ConventionalCar> {

	private static final int idIndex = 0;
	private static final int zoneIndex = 1;
	private static final int locationIndex = 2;
	private static final int segmentIndex = 3;
	private static final int capacityIndex = 4;
	private static final int mileageIndex = 5;
	private static final int fuelLevelIndex = 6;
	private static final int maxRangeIndex = 7;
	public static final int lastIndex = maxRangeIndex;
	
	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;

	public ConventionalCarFormat(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		locationParser = new LocationParser();
	}
	
	@Override
	public List<String> header() {
		return asList("carId", "position", "segment", "capacity", "currentMileage", "currentFuelLevel",
				"maxRange");
	}

	@Override
	public List<String> prepare(ConventionalCar car) {
		String location = locationParser.serialise(car.position().location());
		return asList(
				valueOf(car.id()),
				valueOf(car.position().zone.getOid()),
				location,
				valueOf(car.carSegment()),
				valueOf(car.capacity()),
				valueOf(car.currentMileage()),
				valueOf(car.currentFuelLevel()),
				valueOf(car.maxRange())
		);
	}

	@Override
	public Optional<ConventionalCar> parse(List<String> data) {
		int id = idOf(data);
		CarPosition position = positionOf(data);
		Segment segment = segementOf(data);
		int capacity = capacityOf(data);
		float initialMileage = initialMileageOf(data);
		float fuelLevel = fuelLevelOf(data);
		int maxRange = maxRangeOf(data);
		return Optional
				.of(new ConventionalCar(id, position, segment, capacity, initialMileage, fuelLevel,
						maxRange));
	}

	int idOf(List<String> data) {
		return Integer.parseInt(data.get(idIndex));
	}

	CarPosition positionOf(List<String> data) {
		int zoneOid = Integer.parseInt(data.get(zoneIndex));
		Zone zone = zoneRepository.getZoneByOid(zoneOid);
		Location location = locationParser.parse(data.get(locationIndex));
		return new CarPosition(zone, location );
	}

	Segment segementOf(List<String> data) {
		return Segment.valueOf(data.get(segmentIndex));
	}

	int capacityOf(List<String> data) {
		return Integer.parseInt(data.get(capacityIndex));
	}

	float initialMileageOf(List<String> data) {
		return Float.parseFloat(data.get(mileageIndex));
	}

	float fuelLevelOf(List<String> data) {
		return Float.parseFloat(data.get(fuelLevelIndex));
	}

	int maxRangeOf(List<String> data) {
		return Integer.parseInt(data.get(maxRangeIndex));
	}
}
