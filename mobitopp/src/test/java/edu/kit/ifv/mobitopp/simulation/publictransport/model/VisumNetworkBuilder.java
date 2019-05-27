package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumTurn;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumNetworkBuilder {

	private final Map<String, VisumTransportSystem> transportSystems;
	private final Map<Integer, VisumLinkType> linkTypes;
	private final Map<Integer, VisumNode> nodes;
	private final Map<Integer, VisumLink> links;
	private final Map<Integer, List<VisumTurn>> turns;
	private final Map<Integer, VisumZone> zones;
	private final Map<Integer, List<VisumConnector>> connectors;
	private final Map<Integer, VisumVehicleCombination> vehicleCombinations;
	private final Map<Integer, VisumPtStop> ptStops;
	private final Map<Integer, VisumPtStopArea> ptStopAreas;
	private final Map<Integer, VisumPtStopPoint> ptStopPoints;
	private final Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes;
	private final Map<String, VisumPtLine> ptLines;
	private final Map<String, VisumPtLineRoute> ptLineRoutes;
	private final Map<String, VisumPtTimeProfile> ptTimeProfiles;
	private final List<VisumPtVehicleJourney> ptVehicleJourneys;
	private final Map<Integer, VisumSurface> areas;
	private final Map<Integer, VisumChargingFacility> chargingFacilities;
	private final Map<Integer, VisumChargingPoint> chargingPoints;
	private final Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations;
	private final Map<Integer, VisumTerritory> territories;

	public VisumNetworkBuilder() {
		super();
		transportSystems = new HashMap<>();
		linkTypes = new HashMap<>();
		nodes = new HashMap<>();
		links = new HashMap<>();
		turns = new HashMap<>();
		zones = new HashMap<>();
		connectors = new HashMap<>();
		vehicleCombinations = new HashMap<>();
		ptStops = Collections.emptyMap();
		ptStopAreas = Collections.emptyMap();
		ptStopPoints = Collections.emptyMap();
		walkTimes = Collections.emptyMap();
		ptLines = Collections.emptyMap();
		ptLineRoutes = Collections.emptyMap();
		ptTimeProfiles = Collections.emptyMap();
		ptVehicleJourneys = Collections.emptyList();
		areas = new HashMap<>();
		chargingFacilities = new HashMap<>();
		chargingPoints = new HashMap<>();
		carSharingStations = new HashMap<>();
		territories = Collections.emptyMap();
	}

	public VisumNetwork build() {
		VisumTransportSystems systems = new VisumTransportSystems(transportSystems);
		VisumLinkTypes linkTypes = new VisumLinkTypes(this.linkTypes);
		return new VisumNetwork(systems, linkTypes, nodes, links, turns, zones, connectors, vehicleCombinations,
				ptStops, ptStopAreas, ptStopPoints, walkTimes, ptLines, ptLineRoutes, ptTimeProfiles,
				ptVehicleJourneys, areas, chargingFacilities, chargingPoints, carSharingStations,
				territories);
	}
	
	public VisumNetworkBuilder with(VisumTransportSystem system) {
		transportSystems.put(system.code, system);
		return this;
	}
	
	public VisumNetworkBuilder withDefaultCarSystem() {
		with(new VisumTransportSystem("P", "P", "P"));
		return this;
	}

	public VisumNetworkBuilder with(VisumLink link) {
		links.put(link.id, link);
		return this;
	}

	public VisumNetworkBuilder with(VisumNode node) {
		nodes.put(node.id(), node);
		return this;
	}
	
  public VisumNetworkBuilder add(VisumTurn turn) {
    if (!turns.containsKey(turn.node.id())) {
      turns.put(turn.node.id(), new LinkedList<>());
    }
    turns.get(turn.node.id()).add(turn);
    return this;
  }

	public VisumNetworkBuilder with(VisumSurface area) {
		areas.put(area.id, area);
		return this;
	}

	public VisumNetworkBuilder with(VisumZone zone) {
		zones.put(zone.id, zone);
		return this;
	}

	public VisumNetworkBuilder with(VisumLinkType linkType) {
	  linkTypes.put(linkType.id, linkType);
	  return this;
	}

	public VisumNetworkBuilder addConnector(VisumZone zone, VisumConnector connector) {
		if (!connectors.containsKey(zone.id)) {
			connectors.put(zone.id, new ArrayList<>());
		}
		this.connectors.get(zone.id).add(connector);
		return this;
	}

	public VisumNetworkBuilder add(VisumChargingFacility chargingFacility) {
		chargingFacilities.put(chargingFacility.id, chargingFacility);
		return this;
	}

	public VisumNetworkBuilder add(VisumChargingPoint chargingPoint) {
		chargingPoints.put(chargingPoint.id, chargingPoint);
		return this;
	}

	public VisumNetworkBuilder addCarSharing(
			String company, Map<Integer, VisumCarSharingStation> stations) {
		carSharingStations.put(company, stations);
		return this;
	}

  public VisumNetworkBuilder with(VisumVehicleCombination vehicleCombination) {
    vehicleCombinations.put(vehicleCombination.id, vehicleCombination);
    return this;
  }


}
