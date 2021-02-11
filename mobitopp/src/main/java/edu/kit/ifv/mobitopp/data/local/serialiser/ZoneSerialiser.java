package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.ZoneChargingFacility;
import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.CsvSerialiser;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZoneSerialiser implements Closeable {

	private final ZoneRepository repository;
	private final CsvSerialiser<ZoneChargingFacility> chargingData;
	private final CsvSerialiser<Zone> zones;
	private final CsvSerialiser<StationBasedCarSharingOrganization> stationCompany;
	private final CsvSerialiser<FreeFloatingCarSharingOrganization> freeFloatingCompany;
	private final CsvSerialiser<CarSharingStation> stations;
	private final CsvSerialiser<Car> cars;
	private final CsvSerialiser<StationBasedCarSharingCar> stationCars;
	private final CsvSerialiser<FreeFloatingCar> freeFloatingCars;

	public ZoneSerialiser(
			ZoneRepository repository, CsvSerialiser<ZoneChargingFacility> chargingData,
			CsvSerialiser<Zone> zone, CsvSerialiser<StationBasedCarSharingOrganization> stationCompany,
			CsvSerialiser<FreeFloatingCarSharingOrganization> freeFloatingCompany,
			CsvSerialiser<CarSharingStation> stations, CsvSerialiser<Car> cars,
			CsvSerialiser<StationBasedCarSharingCar> stationCars,
			CsvSerialiser<FreeFloatingCar> freeFloatingCars) {
		super();
		this.repository = repository;
		this.chargingData = chargingData;
		this.zones = zone;
		this.stationCompany = stationCompany;
		this.freeFloatingCompany = freeFloatingCompany;
		this.stations = stations;
		this.cars = cars;
		this.stationCars = stationCars;
		this.freeFloatingCars = freeFloatingCars;
	}

	public static ZoneSerialiser in(
			File zoneRepositoryFolder, ChargingDataFactory factory, ZoneRepository repository,
			AreaTypeRepository areaTypeRepository) {
		try {
			return new SerialiserBuilder(zoneRepositoryFolder, repository, factory, areaTypeRepository)
					.build();
			
		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	public void serialise() {
		List<Zone> zones = repository.getZones();
		writeHeaders();
    serialiseStationBasedOrganizations(zones);
    serialiseFreeFloatingOrganizations(zones);
		zones.stream().forEach(this::serialise);
	}

	private void writeHeaders() {
		chargingData.writeHeader();
		zones.writeHeader();
		stationCompany.writeHeader();
		freeFloatingCompany.writeHeader();
		stations.writeHeader();
		cars.writeHeader();
		stationCars.writeHeader();
		freeFloatingCars.writeHeader();
	}

	private void serialise(Zone zone) {
		serialiseChargingFacilitiesOf(zone);
		serialiseZone(zone);
		serialiseCarSharingOf(zone);
	}

	private void serialiseChargingFacilitiesOf(Zone zone) {
		zone
				.charging()
				.facilities()
				.map(facility -> new ZoneChargingFacility(zone.getId().getMatrixColumn(), facility))
				.forEach(chargingData::write);
	}

	private void serialiseZone(Zone zone) {
		zones.write(zone);
	}

	private void serialiseCarSharingOf(Zone zone) {
		CarSharingDataForZone carSharing = zone.carSharing();
		serialiseStations(carSharing);
		serialiseCars(carSharing);
	}

	private void serialiseStationBasedOrganizations(List<Zone> zones) {
	  new StationBasedOrganizationSerialiser(stationCompany::write).serialise(zones);
	}

	private void serialiseFreeFloatingOrganizations(List<Zone> zones) {
	  new FreeFloatingOrganizationSerialiser(freeFloatingCompany::write).serialise(zones);
	}

	private void serialiseStations(CarSharingDataForZone carSharing) {
		carSharing
				.stationBasedCarSharingCompanies()
				.stream()
				.flatMap(StationBasedCarSharingOrganization::stations)
				.forEach(stations::write);
	}

	private void serialiseCars(CarSharingDataForZone carSharing) {
		serialiseConventionalCars(carSharing);
		serialiseStationBasedCars(carSharing);
		serialiseFreeFloatingCars(carSharing);
	}

	private void serialiseConventionalCars(CarSharingDataForZone carSharing) {
		Stream
				.concat(stationBasedCars(carSharing), freeFloatingCars(carSharing).map(c -> c.car))
				.forEach(cars::write);
	}

	private void serialiseStationBasedCars(CarSharingDataForZone carSharing) {
		stationBasedCars(carSharing).forEach(stationCars::write);
	}

	private Stream<StationBasedCarSharingCar> stationBasedCars(CarSharingDataForZone carSharing) {
		return carSharing
				.stationBasedCarSharingCompanies()
				.stream()
				.flatMap(StationBasedCarSharingOrganization::ownedCars);
	}

	private void serialiseFreeFloatingCars(CarSharingDataForZone carSharing) {
		freeFloatingCars(carSharing).forEach(freeFloatingCars::write);
	}

	private Stream<FreeFloatingCar> freeFloatingCars(CarSharingDataForZone carSharing) {
		return carSharing
				.freeFloatingCarSharingCompanies()
				.stream()
				.flatMap(FreeFloatingCarSharingOrganization::availableCars);
	}

	@Override
	public void close() throws IOException {
		chargingData.close();
		zones.close();
		stationCompany.close();
		freeFloatingCompany.close();
		stations.close();
		cars.close();
		stationCars.close();
		freeFloatingCars.close();
	}
}
