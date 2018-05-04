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
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class CarSharingBuilder {

	private final VisumNetwork visumNetwork;
	private final SimpleRoadNetwork roadNetwork;
	private final Map<String, StationBasedCarSharingOrganization> carSharingCompanies;
	private final Map<String, FreeFloatingCarSharingOrganization> freeFloatingOrganizations;

	public CarSharingBuilder(VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork) {
		super();
		this.visumNetwork = visumNetwork;
		this.roadNetwork = roadNetwork;
		carSharingCompanies = new LinkedHashMap<>();
		freeFloatingOrganizations = new LinkedHashMap<>();
	}

	public CarSharingDataForZone carsharingIn(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		Map<String, List<CarSharingStation>> carSharingStations = new TreeMap<>();
		carSharingStations.put("Stadtmobil",
				carSharingStationsWithinZonePolygon(visumZone.id, polygon.polygon(), "Stadtmobil", zone));
		carSharingStations.put("Flinkster",
				carSharingStationsWithinZonePolygon(visumZone.id, polygon.polygon(), "Flinkster", zone));
		List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanaies = new ArrayList<>();
		for (Entry<String, List<CarSharingStation>> entry : carSharingStations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				stationBasedCarSharingCompanaies.add(carSharingCompany(entry.getKey()));
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
				freeFloatingOrganizations.values());
		Map<String, Float> carsharingCarDensities = new LinkedHashMap<>(
				visumZone.carsharingcarDensities);
		CarSharingDataForZone carSharingData = new CarSharingDataForZone(zone,
				stationBasedCarSharingCompanaies, carSharingStations, freeFloatingCarSharingCompanies,
				freeFloatingArea, freeFloatingCars, carsharingCarDensities);
		return carSharingData;
	}

	private FreeFloatingCarSharingOrganization createFreeFloatingCarSharingOrganizationFor(
			String companyName) {
		if (!freeFloatingOrganizations.containsKey(companyName)) {
			FreeFloatingCarSharingOrganization company = new FreeFloatingCarSharingOrganization(
					companyName);
			freeFloatingOrganizations.put(companyName, company);
		}
		return freeFloatingOrganizations.get(companyName);
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

	Location makeLocation(int zoneId, Point2D coord) {
		SimpleEdge road = roadNetwork.zones().get(zoneId).nearestEdge(coord);
		double pos = road.nearestPositionOnEdge(coord);
		return new Location(coord, road.id(), pos);
	}

	private StationBasedCarSharingOrganization carSharingCompany(String company) {
		if (!carSharingCompanies.containsKey(company)) {
			carSharingCompanies.put(company, new StationBasedCarSharingOrganization(company));
		}
		return carSharingCompanies.get(company);
	}

}
