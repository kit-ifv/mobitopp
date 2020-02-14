package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;
import static java.util.stream.Collectors.groupingBy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.DefaultCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;

public class FileBasedCarSharingBuilder extends BaseCarSharingBuilder {

	private final CsvFile properties;
	private final CsvFile stationData;
	private final CsvFile freeFloatingData;

	public FileBasedCarSharingBuilder(
			SimpleRoadNetwork roadNetwork, IdSequence carSharingCarIds, CsvFile properties,
			CsvFile stationData, CsvFile freeFloatinData) {
		super(roadNetwork, carSharingCarIds);
		this.properties = properties;
		this.stationData = stationData;
		this.freeFloatingData = freeFloatinData;
	}

	public CarSharingDataForZone carsharingIn(Zone zone) {
		CarSharingDataForZone carSharingData = createCarSharingData(zone);
		createStationBasedCarSharingCarsFor(zone, carSharingData);
		createFreeFloatingBasedCarSharingCarsFor(zone, carSharingData);
		return carSharingData;
	}

	protected CarSharingDataForZone createCarSharingData(Zone zone) {
		List<CarSharingStation> all = carSharingStationsFor(zone);
		Map<String, List<CarSharingStation>> carSharingStations = all.stream()
				.collect(groupingBy(s -> s.carSharingCompany.name()));
		List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies = new ArrayList<>();
		for (Entry<String, List<CarSharingStation>> entry : carSharingStations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				stationBasedCarSharingCompanies.add(carSharingCompany(entry.getKey()));
			}
		}
		Map<String, Integer> freeFloatingCars = readFreeFloatingCars(zone);
		Map<String, Boolean> freeFloatingArea = readFreeFloatinArea(freeFloatingCars);
		freeFloatingCars.keySet().forEach(this::createFreeFloatingCarSharingOrganizationFor);
		List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies =  new ArrayList<>(
				getFreeFloatingOrganizations().values());
		Map<String, Float> carsharingCarDensities = readDensities(zone);
		CarSharingDataForZone carSharingData = new CarSharingDataForZone(zone,
			stationBasedCarSharingCompanies, carSharingStations, freeFloatingCarSharingCompanies,
			freeFloatingArea, freeFloatingCars, carsharingCarDensities);
		return carSharingData;
	}

	private Map<String, Float> readDensities(Zone zone) {
		return properties
				.stream()
				.filter(row -> zone.getId().getExternalId().equals(row.get("zone_id")))
				.collect(toLinkedMap(row -> row.get("system"), row -> (float) row.valueAsInteger("density")));
	}

	private Map<String, Integer> readFreeFloatingCars(Zone zone) {
		return freeFloatingData
				.stream()
				.filter(row -> zone.getId().getExternalId().equals(row.get("zone_id")))
				.collect(toLinkedMap(row -> row.get("system"), row -> row.valueAsInteger("num_vehicles")));
	}
	
	private Map<String, Boolean> readFreeFloatinArea(Map<String, Integer> freeFloatingCars) {
		return freeFloatingCars.keySet().stream().collect(toLinkedMap(Function.identity(), s -> true));
	}

	private List<CarSharingStation> carSharingStationsFor(Zone zone) {
		String zoneId = zone.getId().getExternalId();
		List<CarSharingStation> result = new ArrayList<>();
		
		for (int index = 0; index < stationData.getLength(); index++) {
			if (stationData.getValue(index, "zone_ID").equals(zoneId)) {
				Point2D coordinates = new VisumPoint2(
						stationData.getFloat(index, "x_coordinate"),
						stationData.getFloat(index, "y_coordinate")).asPoint2D();
				String company = stationData.getValue(index, "system");
				StationBasedCarSharingOrganization organisation = carSharingCompany(company);
				int id = stationData.getInteger(index, "ID");
				String name = stationData.getValue(index, "name");
				String parkingSpace = "";// dataFile.getValue(index, "parking_space");
				int numberOfCars = stationData.getInteger(index, "num_vehicles");
				Location location = makeLocation(Integer.valueOf(zoneId), coordinates);
				CarSharingStation realStation = new CarSharingStation(organisation, zone, id, name,
						parkingSpace, location, numberOfCars);
				result.add(realStation);
			}
		}
		return result;
	}
	
	private void createStationBasedCarSharingCarsFor(Zone zone, CarSharingDataForZone carSharingData) {
		
		carSharingData.clearCars();
		List<StationBasedCarSharingCar> stationBasedCars = new ArrayList<StationBasedCarSharingCar>();
		
		for (CarSharingOrganization company :	carSharingData.stationBasedCarSharingCompanies()) {
			for (CarSharingStation station : carSharingData.carSharingStations(company.name(), zone)) {
				assert station != null;
				assert station.numberOfCars != null;
				for (int i=0; i<station.numberOfCars; i++) {
					CarPosition position =	new CarPosition(zone, station.location);
					StationBasedCarSharingCar car = new StationBasedCarSharingCar(
							new ConventionalCar(getCarSharingCarIds(), position, Car.Segment.MIDSIZE), company,
							station);	
					stationBasedCars.add(car);
				}
			}
		}
		for (StationBasedCarSharingCar car : stationBasedCars) {
			StationBasedCarSharingOrganization company = car.station.carSharingCompany;
			company.ownCar(car, car.station.zone);
		}
		
	}

	private void createFreeFloatingBasedCarSharingCarsFor(
			Zone zone, CarSharingDataForZone carSharing) {

		for (FreeFloatingCarSharingOrganization company : carSharing
				.freeFloatingCarSharingCompanies()) {

			List<CarSharingCar> freeFloatingCars = createFreeFloatingCars(company, zone, carSharing);
			company.ownCars(freeFloatingCars, zone);
		}
	}

	private List<CarSharingCar> createFreeFloatingCars(
			FreeFloatingCarSharingOrganization company, Zone zone, CarSharingDataForZone carSharing) {
		List<CarSharingCar> freeFloatingCars = new ArrayList<>();
		int numCarsFreeFloating = carSharing.numberOfFreeFloatingCars(company.name());

		for (int i = 0; i < numCarsFreeFloating; i++) {
			Location location = useZoneCenterLocation(zone.getId());
			CarPosition position = new CarPosition(zone, location);
			CarSharingCar car = new DefaultCarSharingCar(
					new ConventionalCar(getCarSharingCarIds(), position, Car.Segment.MIDSIZE), company);
			freeFloatingCars.add(car);
		}
		return freeFloatingCars;
	}
}
