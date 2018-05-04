package edu.kit.ifv.mobitopp.visum;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VisumNetwork extends VisumRoadNetwork {

	private static final long serialVersionUID = 1L;

	public final Map<Integer, VisumVehicleCombination> vehicleCombinations;
	public final Map<Integer, VisumPtStop> ptStops;
	public final Map<Integer, VisumPtStopArea> ptStopAreas;
	public final Map<Integer, VisumPtStopPoint> ptStopPoints;
	public final Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes;
	public final Map<String, VisumPtLine> ptLines;
	public final Map<String, VisumPtLineRoute> ptLineRoutes;
	public final Map<String,VisumPtTimeProfile> ptTimeProfiles;
	public final List<VisumPtVehicleJourney> ptVehicleJourneys;

	public VisumNetwork(
		VisumTransportSystems transportSystems,
		VisumLinkTypes linkTypes,
		Map<Integer, VisumNode> nodes,
		Map<Integer, VisumLink> links,
		Map<Integer, List<VisumTurn>> turns,
		Map<Integer, VisumZone> zones,
		Map<Integer, List<VisumConnector>> connectors,
		Map<Integer, VisumVehicleCombination> vehicleCombinations,
		Map<Integer, VisumPtStop> ptStops,
		Map<Integer, VisumPtStopArea> ptStopAreas,
		Map<Integer, VisumPtStopPoint> ptStopPoints,
		Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes,
		Map<String, VisumPtLine> ptLines,
		Map<String, VisumPtLineRoute> ptLineRoutes,
		Map<String, VisumPtTimeProfile> ptTimeProfiles,
		List<VisumPtVehicleJourney> ptVehicleJourneys,
		Map<Integer, VisumSurface> areas,
		Map<Integer, VisumChargingFacility> chargingFacilities,
		Map<Integer, VisumChargingPoint> chargingPoints,
		Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations,
		Map<Integer,VisumTerritory> territories
	) {

		super(transportSystems, linkTypes, nodes, links, turns, zones, areas, territories, connectors,
				chargingFacilities, chargingPoints, carSharingStations);
		this.vehicleCombinations = Collections.unmodifiableMap(vehicleCombinations);

		this.ptStops = Collections.unmodifiableMap(ptStops);
		this.ptStopAreas = Collections.unmodifiableMap(ptStopAreas);
		this.ptStopPoints = Collections.unmodifiableMap(ptStopPoints);
		this.walkTimes = Collections.unmodifiableMap(walkTimes);
		this.ptLines = Collections.unmodifiableMap(ptLines);
		this.ptLineRoutes = Collections.unmodifiableMap(ptLineRoutes);
		this.ptTimeProfiles = Collections.unmodifiableMap(ptTimeProfiles);
		this.ptVehicleJourneys = Collections.unmodifiableList(ptVehicleJourneys);
	}

}
