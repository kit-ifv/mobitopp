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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import au.com.bytecode.opencsv.CSVReader;
import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.MaasDataForZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.LocalZoneRepository;
import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.dataimport.AttractivitiesBuilder;
import edu.kit.ifv.mobitopp.dataimport.AttractivitiesData;
import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvDeserialiser;
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
	private final AreaTypeRepository areaTypeRepository;

	public ZoneRepositorySerialiser(
			File zoneRepositoryFolder, ChargingDataFactory factory, File attractivitiesDataFile,
			AreaTypeRepository areaTypeRepository) {
		super();
		this.zoneRepositoryFolder = zoneRepositoryFolder;
		this.factory = factory;
		this.attractivitiesDataFile = attractivitiesDataFile;
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
			zone.setMaas(MaasDataForZone.everywhereAvailable());
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
		ChargingDataResolver charging = chargingFrom(chargingFacilities);
		Map<Integer, Attractivities> attractivities = attractivities();
		DefaultZoneFormat format = new DefaultZoneFormat(charging, attractivities, areaTypeRepository);
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
		Map<Integer, Attractivities> mapping = new LinkedHashMap<>();
		AttractivitiesData structuralData = attractivityDataFrom(attractivitiesDataFile, areaTypeRepository);
		while (structuralData.hasNext()) {
		  int currentZone = structuralData.currentZone();
		  String zoneId = String.valueOf(currentZone);
			Attractivities attractivities = new AttractivitiesBuilder(structuralData).attractivities(zoneId);
      mapping.put(currentZone, attractivities);
			structuralData.next();
		}
		return mapping;
	}

	private static AttractivitiesData attractivityDataFrom(File structuralDataFile, AreaTypeRepository areaTypeRepository) {
    StructuralData data = new StructuralData(CsvFile.createFrom(structuralDataFile.getAbsoluteFile()));
    return new AttractivitiesData(data, areaTypeRepository);
	}

	private ChargingDataResolver chargingFrom(
			List<ZoneChargingFacility> chargingFacilities) {
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
			throw new UncheckedIOException(cause);
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
