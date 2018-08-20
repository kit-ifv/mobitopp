package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.LocalZoneRepository;
import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.dataimport.AttractivitiesBuilder;
import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvDeserialiser;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvSerialiser;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class ZoneRepositorySerialiser {

	private final File zoneRepositoryFolder;
	private final ChargingDataFactory factory;
	private final File attractivitiesDataFile;

	public ZoneRepositorySerialiser(
			File zoneRepositoryFolder, ChargingDataFactory factory, File attractivitiesDataFile) {
		super();
		this.zoneRepositoryFolder = zoneRepositoryFolder;
		this.factory = factory;
		this.attractivitiesDataFile = attractivitiesDataFile;
	}

	public boolean isAvailable() {
		return DemandDataInput.zones.exists(zoneRepositoryFolder);
	}

	public ZoneRepository load() {
		List<ZoneChargingFacility> chargingFacilities = deserialiseChargingFacilities();
		ZoneRepository zoneRepository = deserialiseZones(chargingFacilities);
		deserialiseCarSharing(zoneRepository);
		return zoneRepository;
	}
	
	private void deserialiseCarSharing(ZoneRepository zoneRepository) {
		Collection<Car> cars = carsharingCars(zoneRepository);
		for (Zone zone : zoneRepository.getZones()) {
			List<StationBasedCarSharingOrganization> stationBasedOrganizations = stationBasedOrganizations();
			List<FreeFloatingCarSharingOrganization> freeFloatingOrganizations = freeFloatingOrganizations();
			Map<String, List<CarSharingStation>> carSharingStations = carSharingStations(
					stationBasedOrganizations, zoneRepository);
			loadStationBasedCars(stationBasedOrganizations, cars);
			Map<String, Boolean> freeFloatingArea = freeFloatingArea();
			Map<String, Integer> freeFloatingCars = freeFloatingCars(zoneRepository, freeFloatingOrganizations, cars);
			Map<String, Float> carsharingCarDensities = freeFloatingDensities();
			CarSharingDataForZone carSharingDataForZone = new CarSharingDataForZone(zone,
					stationBasedOrganizations, carSharingStations, freeFloatingOrganizations,
					freeFloatingArea, freeFloatingCars, carsharingCarDensities);
			zone.setCarSharing(carSharingDataForZone);
		}
	}

	private void loadStationBasedCars(
			Collection<StationBasedCarSharingOrganization> owners, Collection<Car> cars) {
		Collection<CarSharingStation> stations = owners
				.stream()
				.flatMap(StationBasedCarSharingOrganization::stations)
				.collect(toList());
		Map<String, StationBasedCarSharingOrganization> nameToOwner = owners
				.stream()
				.collect(toMap(o -> o.name(), Function.identity()));
		StationBasedCarFormat format = new StationBasedCarFormat(owners, stations, cars);
		try (CsvDeserialiser<StationBasedCarSharingCar> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.stationBasedCars), format)) {
			List<StationBasedCarSharingCar> carSharingCars = deserialiser.deserialise();
			for (StationBasedCarSharingCar car : carSharingCars) {
				nameToOwner
						.get(car.owner().name())
						.ownCar(car, car.station.zone);
			}
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	private Collection<Car> carsharingCars(ZoneRepository zoneRepository) {
		ConventionalCarFormat format = new ConventionalCarFormat(zoneRepository);
		try (CsvDeserialiser<Car> deserialiser = new CsvDeserialiser<>(readerFor(DemandDataInput.carSharingCars), format)) {
			return deserialiser.deserialise();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private Map<String, Float> freeFloatingDensities() {
		return emptyMap();
	}

	private Map<String, Integer> freeFloatingCars(
			ZoneRepository zoneRepository, List<FreeFloatingCarSharingOrganization> owners,
			Collection<Car> cars) {
		Map<String, FreeFloatingCarSharingOrganization> nameToOwner = owners
				.stream()
				.collect(toMap(o -> o.name(), Function.identity()));
		FreeFloatingCarFormat format = new FreeFloatingCarFormat(zoneRepository, owners, cars);
		try (CsvDeserialiser<FreeFloatingCar> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.freeFloatingCars), format)) {
			List<FreeFloatingCar> freeFloating = deserialiser.deserialise();
			for (FreeFloatingCar freeFloatingCar : freeFloating) {
				nameToOwner
						.get(freeFloatingCar.car.owner().name())
						.ownCar(freeFloatingCar.car, freeFloatingCar.startZone);
			}
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
		return emptyMap();
	}

	private Map<String, Boolean> freeFloatingArea() {
		return emptyMap();
	}

	private Map<String, List<CarSharingStation>> carSharingStations(
			List<StationBasedCarSharingOrganization> organizations, ZoneRepository zoneRepository) {
		CarSharingStationFormat format = new CarSharingStationFormat(organizations, zoneRepository);
		try (CsvDeserialiser<CarSharingStation> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.stations), format)) {
			return deserialiser
					.deserialise()
					.stream()
					.collect(groupingBy(this::owner));
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	private String owner(CarSharingStation station) {
		return station.carSharingCompany.name();
	}
	
	private List<StationBasedCarSharingOrganization> stationBasedOrganizations() {
		StationBasedCarSharingOrganizationFormat format = new StationBasedCarSharingOrganizationFormat();
		try (CsvDeserialiser<StationBasedCarSharingOrganization> deserialiser = new CsvDeserialiser<>(readerFor(DemandDataInput.stationBased), format )) {
			return deserialiser.deserialise();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
	
	private List<FreeFloatingCarSharingOrganization> freeFloatingOrganizations() {
		FreeFloatingCarSharingOrganizationFormat format = new FreeFloatingCarSharingOrganizationFormat();
		try (CsvDeserialiser<FreeFloatingCarSharingOrganization> deserialiser = new CsvDeserialiser<>(readerFor(DemandDataInput.stationBased), format )) {
			return deserialiser.deserialise();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private ZoneRepository deserialiseZones(List<ZoneChargingFacility> chargingFacilities) {
		Map<Integer, ChargingDataForZone> charging = chargingFrom(chargingFacilities);
		Map<Integer, Attractivities> attractivities = attractivities();
		DefaultZoneFormat format = new DefaultZoneFormat(charging, attractivities);
		try (
				CsvDeserialiser<Zone> deserialiser = new CsvDeserialiser<>(readerFor(DemandDataInput.zones),
						format)) {
			List<Zone> zones = deserialiser.deserialise();
			return LocalZoneRepository.from(zones);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private Map<Integer, Attractivities> attractivities() {
		HashMap<Integer, Attractivities> mapping = new HashMap<>();
		StructuralData structuralData = structuralDataFrom(attractivitiesDataFile);
		while (structuralData.hasNext()) {
			Attractivities attractivities = new AttractivitiesBuilder(structuralData).attractivities();
			mapping.put(structuralData.currentZone(), attractivities);
			structuralData.next();
		}
		return mapping;
	}

	private static StructuralData structuralDataFrom(File structuralDataFile) {
		return new StructuralData(new CsvFile(structuralDataFile.getAbsolutePath()));
	}

	private Map<Integer, ChargingDataForZone> chargingFrom(
			List<ZoneChargingFacility> chargingFacilities) {
		Map<Integer, List<ChargingFacility>> perZone = chargingFacilities
				.stream()
				.collect(groupingBy(ZoneChargingFacility::id,
						mapping(ZoneChargingFacility::facility, toList())));
		HashMap<Integer, ChargingDataForZone> mapping = new HashMap<>();
		for (Entry<Integer, List<ChargingFacility>> entry : perZone.entrySet()) {
			ChargingDataForZone charginData = factory.create(entry.getValue());
			mapping.put(entry.getKey(), charginData);
		}
		return mapping;
	}

	private List<ZoneChargingFacility> deserialiseChargingFacilities() {
		ChargingFacilityFormat format = new ChargingFacilityFormat();
		try (CsvDeserialiser<ZoneChargingFacility> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.chargingData), format)) {
			return deserialiser.deserialise();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	public void serialise(ZoneRepository repository) {
		zoneRepositoryFolder.mkdirs();
		List<Zone> zones = repository.getZones();
		Consumer<Zone> serialise = zone -> serialise(zone, repository);
		zones.stream().forEach(serialise);
	}

	private void serialise(Zone zone, ZoneRepository zoneRepository) {
		serialiseChargingFacilitiesOf(zone);
		serialiseZones(zoneRepository);
		serialiseCarSharingOf(zone, zoneRepository);
	}

	private void serialiseChargingFacilitiesOf(Zone zone) {
		ChargingFacilityFormat format = new ChargingFacilityFormat();
		try (CsvSerialiser<ZoneChargingFacility> serialiser = new CsvSerialiser<>(
				writerFor(DemandDataInput.chargingData), format)) {
			serialiser.writeHeader();
			zone
					.charging()
					.facilities()
					.map(facility -> new ZoneChargingFacility(zone.getOid(), facility))
					.forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseZones(ZoneRepository zoneRepository) {
		Map<Integer, ChargingDataForZone> zoneToCharging = zoneRepository
				.getZones()
				.stream()
				.collect(toMap(Zone::getOid, Zone::charging));
		DefaultZoneFormat format = new DefaultZoneFormat(zoneToCharging, attractivities());
		try (CsvSerialiser<Zone> serialiser = new CsvSerialiser<>(writerFor(DemandDataInput.zones), format)) {
			serialiser.writeHeader();
			zoneRepository.getZones().forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseCarSharingOf(Zone zone, ZoneRepository zoneRepository) {
		CarSharingDataForZone carSharing = zone.carSharing();
		serialiseStationBasedOrganizations(carSharing);
		serialiseFreeFloatingOrganizations(carSharing);
		serialiseStations(carSharing, zoneRepository);
		serialiseCars(carSharing, zoneRepository);
	}

	private void serialiseStationBasedOrganizations(CarSharingDataForZone carSharing) {
		StationBasedCarSharingOrganizationFormat format = new StationBasedCarSharingOrganizationFormat();
		try (CsvSerialiser<StationBasedCarSharingOrganization> serialiser = new CsvSerialiser<>(
				writerFor(DemandDataInput.stationBased), format)) {
			serialiser.writeHeader();
			carSharing.stationBasedCarSharingCompanies().forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseFreeFloatingOrganizations(CarSharingDataForZone carSharing) {
		FreeFloatingCarSharingOrganizationFormat format = new FreeFloatingCarSharingOrganizationFormat();
		try (CsvSerialiser<FreeFloatingCarSharingOrganization> serialiser = new CsvSerialiser<>(
				writerFor(DemandDataInput.freeFloating), format)) {
			serialiser.writeHeader();
			carSharing.freeFloatingCarSharingCompanies().forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseStations(CarSharingDataForZone carSharing, ZoneRepository zoneRepository) {
		List<StationBasedCarSharingOrganization> companies = carSharing
				.stationBasedCarSharingCompanies();
		CarSharingStationFormat format = new CarSharingStationFormat(companies, zoneRepository);
		try (CsvSerialiser<CarSharingStation> serialiser = new CsvSerialiser<>(writerFor(DemandDataInput.stations),
				format)) {
			serialiser.writeHeader();
			carSharing
					.stationBasedCarSharingCompanies()
					.stream()
					.flatMap(StationBasedCarSharingOrganization::stations)
					.forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseCars(CarSharingDataForZone carSharing, ZoneRepository zoneRepository) {
		serialiseConventionalCars(carSharing, zoneRepository);
		serialiseStationBasedCars(carSharing);
		serialiseFreeFloatingCars(carSharing, zoneRepository);
	}

	private void serialiseConventionalCars(
			CarSharingDataForZone carSharing, ZoneRepository zoneRepository) {
		ConventionalCarFormat format = new ConventionalCarFormat(zoneRepository);
		try (CsvSerialiser<Car> serialiser = new CsvSerialiser<>(writerFor(DemandDataInput.carSharingCars), format)) {
			serialiser.writeHeader();
			Stream
					.concat(stationBasedCars(carSharing), freeFloatingCars(carSharing).map(c -> c.car))
					.forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private void serialiseStationBasedCars(CarSharingDataForZone carSharing) {
		Collection<StationBasedCarSharingOrganization> owners = carSharing
				.stationBasedCarSharingCompanies();
		Collection<CarSharingStation> stations = carSharing
				.stationBasedCarSharingCompanies()
				.stream()
				.flatMap(StationBasedCarSharingOrganization::stations)
				.collect(toList());
		Collection<Car> cars = stationBasedCars(carSharing).collect(toList());
		StationBasedCarFormat format = new StationBasedCarFormat(owners, stations, cars);
		try (CsvSerialiser<StationBasedCarSharingCar> serialiser = new CsvSerialiser<>(
				writerFor(DemandDataInput.stationBasedCars), format)) {
			serialiser.writeHeader();
			stationBasedCars(carSharing).forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private Stream<StationBasedCarSharingCar> stationBasedCars(CarSharingDataForZone carSharing) {
		return carSharing
				.stationBasedCarSharingCompanies()
				.stream()
				.flatMap(StationBasedCarSharingOrganization::ownedCars);
	}

	private void serialiseFreeFloatingCars(CarSharingDataForZone carSharing, ZoneRepository zoneRepository) {
		Collection<FreeFloatingCarSharingOrganization> owners = carSharing
				.freeFloatingCarSharingCompanies();
		Collection<Car> cars = freeFloatingCars(carSharing).map(c -> c.car).collect(toList());
		FreeFloatingCarFormat format = new FreeFloatingCarFormat(zoneRepository, owners, cars);
		try (CsvSerialiser<FreeFloatingCar> serialiser = new CsvSerialiser<>(writerFor(DemandDataInput.freeFloatingCars),
				format)) {
			serialiser.writeHeader();
			freeFloatingCars(carSharing).forEach(serialiser::write);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private Stream<FreeFloatingCar> freeFloatingCars(CarSharingDataForZone carSharing) {
		return carSharing
				.freeFloatingCarSharingCompanies()
				.stream()
				.flatMap(FreeFloatingCarSharingOrganization::availableCars);
	}

	private CSVReader readerFor(DemandDataInput input) throws IOException {
		return input.readerIn(zoneRepositoryFolder);
	}

	private CSVWriter writerFor(DemandDataInput input) throws IOException {
		return input.writerIn(zoneRepositoryFolder);
	}

}
