package edu.kit.ifv.mobitopp.dataimport;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class CarSharingBuilder extends BaseCarSharingBuilder {

	private final VisumNetwork visumNetwork;

	public CarSharingBuilder(VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, IdSequence carSharingCarIds) {
		super(roadNetwork, carSharingCarIds);
		this.visumNetwork = visumNetwork;
	}

	public CarSharingDataForZone carsharingIn(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		Map<String, List<CarSharingStation>> carSharingStations = new TreeMap<>();
		carSharingStations.put("Stadtmobil",
				carSharingStationsWithinZonePolygon(visumZone.id, polygon.polygon(), "Stadtmobil", zone));
		carSharingStations.put("Flinkster",
				carSharingStationsWithinZonePolygon(visumZone.id, polygon.polygon(), "Flinkster", zone));
		List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies = new ArrayList<>();
		for (Entry<String, List<CarSharingStation>> entry : carSharingStations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				stationBasedCarSharingCompanies.add(carSharingCompany(entry.getKey()));
			}
		}
		Map<String, Boolean> freeFloatingArea = new LinkedHashMap<>();
		Map<String, Integer> freeFloatingCars = new LinkedHashMap<>();
		if (1 == visumZone.freeFloatingCarSharingArea) {
			String companyName = visumZone.freeFloatingCarSharingCompany;
			createFreeFloatingCarSharingOrganizationFor(companyName);
			freeFloatingArea.put(companyName, true);
			freeFloatingCars.put(companyName, visumZone.freeFloatingCarSharingCars);
		}
		List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies = new ArrayList<>(
				getFreeFloatingOrganizations().values());
		Map<String, Float> carsharingCarDensities = new LinkedHashMap<>(
				visumZone.carsharingcarDensities);
		CarSharingDataForZone carSharingData = new CarSharingDataForZone(zone,
				stationBasedCarSharingCompanies, carSharingStations, freeFloatingCarSharingCompanies,
				freeFloatingArea, freeFloatingCars, carsharingCarDensities);
		return carSharingData;
	}

	private List<CarSharingStation> carSharingStationsWithinZonePolygon(
			int zoneId, VisumSurface polygon, String company, Zone zone) {
		List<CarSharingStation> result = new ArrayList<>();
		for (VisumCarSharingStation station : visumNetwork.carSharingStations.get(company).values()) {
			Point2D coordinates = station.coord.asPoint2D();
			if (polygon.isPointInside(coordinates)) {
				StationBasedCarSharingOrganization organisation = carSharingCompany(company);
				int id = station.id;
				String name = station.name;
				String parkingSpace = station.parkingSpace;
				int numberOfCars = station.numberOfCars;
				Location location = makeLocation(zoneId, coordinates);
				CarSharingStation realStation = new CarSharingStation(organisation, zone, id, name,
						parkingSpace, location, numberOfCars);
				result.add(realStation);
			}
		}
		return result;
	}

}
