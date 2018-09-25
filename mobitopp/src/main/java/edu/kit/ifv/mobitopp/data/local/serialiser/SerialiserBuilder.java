package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvSerialiser;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class SerialiserBuilder {

	private final File zoneRepositoryFolder;
	private final ZoneRepository repository;
	private final ChargingDataFactory factory;
	private final AreaTypeRepository areaTypeRepository;

	public SerialiserBuilder(
			File zoneRepositoryFolder, ZoneRepository repository, ChargingDataFactory factory,
			AreaTypeRepository areaTypeRepository) {
		this.zoneRepositoryFolder = zoneRepositoryFolder;
		this.repository = repository;
		this.factory = factory;
		this.areaTypeRepository = areaTypeRepository;
	}

	private CSVWriter writerFor(DemandDataInput input) throws IOException {
		return input.writerIn(zoneRepositoryFolder);
	}

	public ZoneSerialiser build() throws IOException {
		CsvSerialiser<ZoneChargingFacility> chargingData = chargingFormat();
		CsvSerialiser<Zone> zone = zones();
		CsvSerialiser<StationBasedCarSharingOrganization> stationCompany = stationBasedOrganizations();
		CsvSerialiser<FreeFloatingCarSharingOrganization> freeFloatingCompany = freeFloatingOrganizations();
		CsvSerialiser<CarSharingStation> stations = stations();
		CsvSerialiser<Car> cars = cars();
		CsvSerialiser<StationBasedCarSharingCar> stationCars = stationCars();
		CsvSerialiser<FreeFloatingCar> freeFloatingCars = freeFloatingCars();
		return new ZoneSerialiser(repository, chargingData, zone, stationCompany,
				freeFloatingCompany, stations, cars, stationCars, freeFloatingCars);
	}

	private CsvSerialiser<FreeFloatingCar> freeFloatingCars()
			throws IOException {
		FreeFloatingCarFormat freeFloatingCarFormat = new FreeFloatingCarFormat(repository, emptyList(),
				emptyList());
		CsvSerialiser<FreeFloatingCar> freeFloatingCars = new CsvSerialiser<>(
				writerFor(DemandDataInput.freeFloatingCars), freeFloatingCarFormat);
		return freeFloatingCars;
	}

	private CsvSerialiser<StationBasedCarSharingCar> stationCars() throws IOException {
		StationBasedCarFormat format = new StationBasedCarFormat(emptyList(), emptyList(), emptyList());
		CsvSerialiser<StationBasedCarSharingCar> stationCars = new CsvSerialiser<>(
				writerFor(DemandDataInput.stationBasedCars), format);
		return stationCars;
	}

	private CsvSerialiser<Car> cars() throws IOException {
		ConventionalCarFormat carFormat = new ConventionalCarFormat(repository);
		CsvSerialiser<Car> cars = new CsvSerialiser<>(writerFor(DemandDataInput.carSharingCars),
				carFormat);
		return cars;
	}

	private CsvSerialiser<CarSharingStation> stations() throws IOException {
		CarSharingStationFormat stationFormat = new CarSharingStationFormat(emptyList(), repository);
		CsvSerialiser<CarSharingStation> stations = new CsvSerialiser<>(
				writerFor(DemandDataInput.stations), stationFormat);
		return stations;
	}

	private CsvSerialiser<FreeFloatingCarSharingOrganization> freeFloatingOrganizations()
			throws IOException {
		FreeFloatingCarSharingOrganizationFormat freeFloatingFormat = new FreeFloatingCarSharingOrganizationFormat();
		CsvSerialiser<FreeFloatingCarSharingOrganization> freeFloatingCompany = new CsvSerialiser<>(
				writerFor(DemandDataInput.freeFloating), freeFloatingFormat);
		return freeFloatingCompany;
	}

	private CsvSerialiser<StationBasedCarSharingOrganization> stationBasedOrganizations()
			throws IOException {
		StationBasedCarSharingOrganizationFormat stationCompanyFormat = new StationBasedCarSharingOrganizationFormat();
		CsvSerialiser<StationBasedCarSharingOrganization> stationCompany = new CsvSerialiser<>(
				writerFor(DemandDataInput.stationBased), stationCompanyFormat);
		return stationCompany;
	}

	private CsvSerialiser<Zone> zones() throws IOException {
		ChargingDataResolver zoneToCharging = chargingResolverFrom(repository);
		DefaultZoneFormat zoneFormat = new DefaultZoneFormat(zoneToCharging, attractivities(),
				areaTypeRepository);
		return new CsvSerialiser<>(writerFor(DemandDataInput.zones), zoneFormat);
	}

	private ChargingDataResolver chargingResolverFrom(ZoneRepository zoneRepository) {
		Map<Integer, ChargingDataForZone> mapping = zoneRepository
				.getZones()
				.stream()
				.collect(toMap(Zone::getOid, Zone::charging));
		return new CreateMissingChargingData(mapping, factory);
	}

	private Map<Integer, Attractivities> attractivities() {
		return emptyMap();
	}

	private CsvSerialiser<ZoneChargingFacility> chargingFormat() throws IOException {
		ChargingFacilityFormat chargingFormat = new ChargingFacilityFormat();
		return new CsvSerialiser<>(writerFor(DemandDataInput.chargingData), chargingFormat);
	}

}
