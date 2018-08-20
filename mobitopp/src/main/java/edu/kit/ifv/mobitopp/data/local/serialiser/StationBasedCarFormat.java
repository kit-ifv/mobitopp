package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedCarFormat implements SerialiserFormat<StationBasedCarSharingCar> {

	private static final int ownerIndex = 0;
	private static final int stationIndex = 1;
	private static final int carIndex = 2;

	private final Map<Integer, Car> cars;
	private final Map<String, CarSharingOrganization> owners;
	private final Map<Integer, CarSharingStation> stations;

	public StationBasedCarFormat(
			Collection<StationBasedCarSharingOrganization> owners, Collection<CarSharingStation> stations,
			Collection<Car> cars) {
		super();
		this.cars = cars.stream().collect(toMap(Car::id, Function.identity()));
		this.owners = owners.stream().collect(toMap(CarSharingOrganization::name, Function.identity()));
		this.stations = stations.stream().collect(toMap(s -> s.id, Function.identity()));
	}

	@Override
	public List<String> header() {
		return asList("owner", "stationId", "carId");
	}

	@Override
	public List<String> prepare(StationBasedCarSharingCar car) {
		return asList(car.owner().name(), valueOf(car.station.id), valueOf(car.id()));
	}

	@Override
	public Optional<StationBasedCarSharingCar> parse(List<String> data) {
		Car car = carOf(data);
		CarSharingOrganization owner = ownerOf(data);
		CarSharingStation station = stationOf(data);
		StationBasedCarSharingCar carSharingCar = new StationBasedCarSharingCar(car, owner, station);
		return Optional.of(carSharingCar);
	}

	private Car carOf(List<String> data) {
		int carId = Integer.parseInt(data.get(carIndex));
		return cars.get(carId);
	}

	private CarSharingOrganization ownerOf(List<String> data) {
		String name = data.get(ownerIndex);
		return owners.get(name);
	}

	private CarSharingStation stationOf(List<String> data) {
		int id = Integer.parseInt(data.get(stationIndex));
		return stations.get(id);
	}

}
