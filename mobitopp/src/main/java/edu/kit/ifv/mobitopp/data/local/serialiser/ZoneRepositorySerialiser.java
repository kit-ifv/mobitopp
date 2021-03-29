package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
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
import java.util.function.Function;

import au.com.bytecode.opencsv.CSVReader;
import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.MaasDataForZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.LocalZoneRepository;
import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.dataimport.AttractivitiesData;
import edu.kit.ifv.mobitopp.dataimport.BikeSharingPropertiesData;
import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvDeserialiser;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZoneRepositorySerialiser {

	private final File zoneRepositoryFolder;
	private final ChargingDataFactory factory;
	private final File attractivitiesDataFile;
	private final File bikeSharingDataFile;
	private final AreaTypeRepository areaTypeRepository;
	private BikeSharingPropertiesData bikeSharingProperties;

	public ZoneRepositorySerialiser(final File zoneRepositoryFolder,
		final ChargingDataFactory factory, final File attractivitiesDataFile,
		final File bikeSharingDataFile, final AreaTypeRepository areaTypeRepository) {
		super();
		this.zoneRepositoryFolder = zoneRepositoryFolder;
		this.factory = factory;
		this.attractivitiesDataFile = attractivitiesDataFile;
		this.bikeSharingDataFile = bikeSharingDataFile;
		this.areaTypeRepository = areaTypeRepository;
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
		List<StationBasedCarSharingOrganization> stationBasedOrganizations = stationBasedOrganizations();
		List<FreeFloatingCarSharingOrganization> freeFloatingOrganizations = freeFloatingOrganizations();
		Map<String, List<CarSharingStation>> carSharingStations = carSharingStations(
			stationBasedOrganizations, zoneRepository);
		loadStationBasedCars(stationBasedOrganizations, cars);
		loadFreeFloatingCars(zoneRepository, freeFloatingOrganizations, cars);
		Map<String, Float> carsharingCarDensities = freeFloatingDensities();
		for (Zone zone : zoneRepository.getZones()) {
			Map<String, Boolean> freeFloatingArea = freeFloatingArea(freeFloatingOrganizations,
				zone);
			Map<String, Integer> freeFloatingCars = freeFloatingCars(freeFloatingOrganizations,
				zone);
			CarSharingDataForZone carSharingDataForZone = new CarSharingDataForZone(zone,
				stationBasedOrganizations, carSharingStations, freeFloatingOrganizations,
				freeFloatingArea, freeFloatingCars, carsharingCarDensities);
			zone.setBikeSharing(createBikeSharing(zone.getId(), zoneRepository.idMapper()));
			zone.setCarSharing(carSharingDataForZone);
			zone.setMaas(MaasDataForZone.everywhereAvailable());
		}
	}

	private BikeSharingDataForZone createBikeSharing(ZoneId zoneId, IdToOidMapper idMapper) {
		if (null == bikeSharingProperties) {
			loadBikeSharingData(idMapper);
		}
		return bikeSharingProperties.getData(zoneId);
	}

	private void loadBikeSharingData(IdToOidMapper idMapper) {
		StructuralData properties = new StructuralData(CsvFile.createFrom(bikeSharingDataFile));
		bikeSharingProperties = new BikeSharingPropertiesData(properties, idMapper);
	}

	private void loadStationBasedCars(Collection<StationBasedCarSharingOrganization> owners,
		Collection<Car> cars) {
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
				nameToOwner.get(car.owner().name()).ownCar(car, car.station.zone);
			}
		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private Collection<Car> carsharingCars(ZoneRepository zoneRepository) {
		ConventionalCarFormat format = new ConventionalCarFormat(zoneRepository);
		try (CsvDeserialiser<Car> deserialiser = new CsvDeserialiser<>(
			readerFor(DemandDataInput.carSharingCars), format)) {
			return deserialiser.deserialise();

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private Map<String, Float> freeFloatingDensities() {
		return emptyMap();
	}

	private void loadFreeFloatingCars(ZoneRepository zoneRepository,
		List<FreeFloatingCarSharingOrganization> owners, Collection<Car> cars) {
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
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private Map<String, Boolean> freeFloatingArea(List<FreeFloatingCarSharingOrganization> owners,
		Zone zone) {
		return owners
			.stream()
			.collect(StreamUtils
				.toLinkedMap(FreeFloatingCarSharingOrganization::name,
					owner -> owner.isCarAvailable(zone)));
	}

	private Map<String, Integer> freeFloatingCars(List<FreeFloatingCarSharingOrganization> owners,
		Zone zone) {
		return owners
			.stream()
			.collect(StreamUtils
				.toLinkedMap(FreeFloatingCarSharingOrganization::name,
					owner -> owner.availableCars(zone)));
	}

	private Map<String, List<CarSharingStation>> carSharingStations(
		List<StationBasedCarSharingOrganization> organizations, ZoneRepository zoneRepository) {
		CarSharingStationFormat format = new CarSharingStationFormat(organizations, zoneRepository);
		try (CsvDeserialiser<CarSharingStation> deserialiser = new CsvDeserialiser<>(
			readerFor(DemandDataInput.stations), format)) {
			return deserialiser.deserialise().stream().collect(groupingBy(this::owner));

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private String owner(CarSharingStation station) {
		return station.carSharingCompany.name();
	}

	private List<StationBasedCarSharingOrganization> stationBasedOrganizations() {
		StationBasedCarSharingOrganizationFormat format = new StationBasedCarSharingOrganizationFormat();
		try (
			CsvDeserialiser<StationBasedCarSharingOrganization> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.stationBased), format)) {
			return deserialiser.deserialise();

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private List<FreeFloatingCarSharingOrganization> freeFloatingOrganizations() {
		FreeFloatingCarSharingOrganizationFormat format = new FreeFloatingCarSharingOrganizationFormat();
		try (
			CsvDeserialiser<FreeFloatingCarSharingOrganization> deserialiser = new CsvDeserialiser<>(
				readerFor(DemandDataInput.freeFloating), format)) {
			return deserialiser.deserialise();

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private ZoneRepository deserialiseZones(List<ZoneChargingFacility> chargingFacilities) {
		ChargingDataResolver charging = chargingFrom(chargingFacilities);
		Map<Integer, Attractivities> attractivities = attractivities();
		DefaultZoneFormat format = new DefaultZoneFormat(charging, attractivities,
			areaTypeRepository);
		try (CsvDeserialiser<Zone> deserialiser = new CsvDeserialiser<>(
			readerFor(DemandDataInput.zones), format)) {
			List<Zone> zones = deserialiser.deserialise();
			return LocalZoneRepository.from(zones);

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private Map<Integer, Attractivities> attractivities() {
		return attractivityDataFrom(attractivitiesDataFile).build();
	}

	private static AttractivitiesData attractivityDataFrom(File structuralDataFile) {
		StructuralData data = new StructuralData(
			CsvFile.createFrom(structuralDataFile.getAbsolutePath()));
		return new AttractivitiesData(data);
	}

	private ChargingDataResolver chargingFrom(List<ZoneChargingFacility> chargingFacilities) {
		Map<Integer, List<ChargingFacility>> perZone = chargingFacilities
			.stream()
			.collect(groupingBy(ZoneChargingFacility::zoneId,
				mapping(ZoneChargingFacility::facility, toList())));
		HashMap<Integer, ChargingDataForZone> mapping = new HashMap<>();
		for (Entry<Integer, List<ChargingFacility>> entry : perZone.entrySet()) {
			ChargingDataForZone charginData = factory.create(entry.getValue());
			mapping.put(entry.getKey(), charginData);
		}
		return new CreateMissingChargingData(mapping, factory);
	}

	private List<ZoneChargingFacility> deserialiseChargingFacilities() {
		ChargingFacilityFormat format = new ChargingFacilityFormat();
		try (CsvDeserialiser<ZoneChargingFacility> deserialiser = new CsvDeserialiser<>(
			readerFor(DemandDataInput.chargingData), format)) {
			return deserialiser.deserialise();
		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	public void serialise(ZoneRepository repository) throws IOException {
		try (ZoneSerialiser serialiser = ZoneSerialiser
			.in(zoneRepositoryFolder, factory, repository, areaTypeRepository)) {
			zoneRepositoryFolder.mkdirs();
			serialiser.serialise();
		}
	}

	private CSVReader readerFor(DemandDataInput input) throws IOException {
		return input.readerIn(zoneRepositoryFolder);
	}

}
