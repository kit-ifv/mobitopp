package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiserFormat;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class CarSharingStationFormat implements SerialiserFormat<CarSharingStation> {

	private static final int companyIndex = 0;
	private static final int zoneIndex = 1;
	private static final int idIndex = 2;
	private static final int nameIndex = 3;
	private static final int parkingSpaceIndex = 4;
	private static final int locationIndex = 5;
	private static final int numberOfCarsIndex = 6;

	private final Map<String, StationBasedCarSharingOrganization> organizations;
	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;

	public CarSharingStationFormat(
			List<StationBasedCarSharingOrganization> organizations, ZoneRepository zoneRepository) {
		super();
		this.organizations = organizations
				.stream()
				.collect(toMap(CarSharingOrganization::name, Function.identity()));
		this.zoneRepository = zoneRepository;
		this.locationParser = new LocationParser();
	}

	@Override
	public List<String> header() {
		return asList("company", "zone", "id", "name", "parkingSpace", "location", "numberOfCars");
	}

	@Override
	public List<String> prepare(CarSharingStation station) {
		String company = station.carSharingCompany.name();
		String zoneId = valueOf(station.zone.getOid());
		String id = valueOf(station.id);
		String name = station.name;
		String parkingSpace = station.parkingSpace;
		String location = valueOf(locationParser.serialise(station.location));
		String numberOfCars = valueOf(station.numberOfCars);
		return asList(company, zoneId, id, name, parkingSpace, location, numberOfCars);
	}

	@Override
	public Optional<CarSharingStation> parse(List<String> data) {
		StationBasedCarSharingOrganization company = companyOf(data);
		Zone zone = zoneOf(data);
		Location location = locationOf(data);
		Integer id = idOf(data);
		String name = nameOf(data);
		String parkingSpace = parkingSpaceOf(data);
		Integer numberOfCars = numberOfCarsOf(data);
		CarSharingStation station = new CarSharingStation(company, zone, id, name, parkingSpace,
				location, numberOfCars);
		return Optional.of(station);
	}

	private StationBasedCarSharingOrganization companyOf(List<String> data) {
		String company = data.get(companyIndex);
		return organizations.get(company);
	}

	private Zone zoneOf(List<String> data) {
		int zone = Integer.parseInt(data.get(zoneIndex));
		return zoneRepository.getZoneByOid(zone);
	}

	private Location locationOf(List<String> data) {
		String location = data.get(locationIndex);
		return locationParser.parse(location);
	}

	private Integer idOf(List<String> data) {
		String id = data.get(idIndex);
		return Integer.parseInt(id);
	}

	private String nameOf(List<String> data) {
		return data.get(nameIndex);
	}

	private String parkingSpaceOf(List<String> data) {
		return data.get(parkingSpaceIndex);
	}

	private Integer numberOfCarsOf(List<String> data) {
		String numberOfCars = data.get(numberOfCarsIndex);
		return Integer.parseInt(numberOfCars);
	}

}
