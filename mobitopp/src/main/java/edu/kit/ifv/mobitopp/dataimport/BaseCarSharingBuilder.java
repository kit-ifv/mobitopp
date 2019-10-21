package edu.kit.ifv.mobitopp.dataimport;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class BaseCarSharingBuilder {

	private final Map<String, StationBasedCarSharingOrganization> carSharingCompanies;
	private final Map<String, FreeFloatingCarSharingOrganization> freeFloatingOrganizations;
	private final SimpleRoadNetwork roadNetwork;
	private final IdSequence carSharingCarIds;

	public BaseCarSharingBuilder(SimpleRoadNetwork roadNetwork, IdSequence carSharingCarIds) {
		super();
		this.roadNetwork = roadNetwork;
		this.carSharingCarIds = carSharingCarIds;
		carSharingCompanies = new LinkedHashMap<>();
		freeFloatingOrganizations = new LinkedHashMap<>();
	}

	protected StationBasedCarSharingOrganization carSharingCompany(String company) {
		if (!carSharingCompanies.containsKey(company)) {
			carSharingCompanies.put(company, new StationBasedCarSharingOrganization(company));
		}
		return carSharingCompanies.get(company);
	}

	protected FreeFloatingCarSharingOrganization createFreeFloatingCarSharingOrganizationFor(
			String companyName) {
		if (!freeFloatingOrganizations.containsKey(companyName)) {
			FreeFloatingCarSharingOrganization company = new FreeFloatingCarSharingOrganization(
					companyName);
			freeFloatingOrganizations.put(companyName, company);
		}
		return freeFloatingOrganizations.get(companyName);
	}

	protected Map<String, FreeFloatingCarSharingOrganization> getFreeFloatingOrganizations() {
		return freeFloatingOrganizations;
	}

	protected Location makeLocation(int zoneId, Point2D coord) {
		SimpleEdge road = roadNetwork.zones().get(zoneId).nearestEdge(coord);
		double pos = road.nearestPositionOnEdge(coord);
		return new Location(coord, road.id(), pos);
	}

	protected int nextId() {
		return carSharingCarIds.nextId();
	}

	protected IdSequence getCarSharingCarIds() {
		return carSharingCarIds;
	}
}