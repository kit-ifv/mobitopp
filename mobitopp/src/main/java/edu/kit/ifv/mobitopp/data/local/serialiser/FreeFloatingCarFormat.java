package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.DefaultCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;

public class FreeFloatingCarFormat implements SerialiserFormat<FreeFloatingCar> {

	private static final int zoneIndex = 0;
	private static final int ownerIndex = 1;
	private static final int carIndex = 2;
	
	private final ZoneRepository zoneRepository;
	private final Map<String, CarSharingOrganization> owners;
	private final Map<Integer, Car> cars;
	
	public FreeFloatingCarFormat(
			ZoneRepository zoneRepository, Collection<FreeFloatingCarSharingOrganization> owners,
			Collection<Car> cars) {
		super();
		this.zoneRepository = zoneRepository;
		this.owners = owners.stream().collect(toMap(CarSharingOrganization::name, Function.identity()));
		this.cars = cars.stream().collect(toMap(Car::id, Function.identity()));
	}

	@Override
	public List<String> header() {
		return asList("zoneOid", "owner", "carId");
	}

	@Override
	public List<String> prepare(FreeFloatingCar car) {
		return asList(valueOf(car.startZone.getOid()), 
				car.car.owner().name(), 
				valueOf(car.car.id()));
	}

	@Override
	public Optional<FreeFloatingCar> parse(List<String> data) {
		Zone zone = zoneOf(data);
		Car car = carOf(data);
		CarSharingOrganization owner = ownerOf(data);
		CarSharingCar carsharingCar = new DefaultCarSharingCar(car, owner);
		return Optional.of(new FreeFloatingCar(zone, carsharingCar));
	}

	private Zone zoneOf(List<String> data) {
		int oid = Integer.parseInt(data.get(zoneIndex));
		return zoneRepository.getZoneByOid(oid);
	}

	private Car carOf(List<String> data) {
		int id = Integer.parseInt(data.get(carIndex));
		return cars.get(id);
	}

	private CarSharingOrganization ownerOf(List<String> data) {
		String name = data.get(ownerIndex);
		return owners.get(name);
	}

}
