package edu.kit.ifv.mobitopp.simulation.publictransport.model;

public class VisumBuilder {

	public static VisumStopPointBuilder visumStopPoint() {
		return new VisumStopPointBuilder();
	}

	public static VisumStopAreaBuilder visumStopArea() {
		return new VisumStopAreaBuilder();
	}

	public static VisumStopBuilder visumStop() {
		return new VisumStopBuilder();
	}

	public static VisumNodeBuilder visumNode() {
		return new VisumNodeBuilder();
	}
	
	public static VisumTurnBuilder visumTurn() {
	  return new VisumTurnBuilder();
	}

	public static VisumLinkTypeBuilder visumLinkType() {
		return new VisumLinkTypeBuilder();
	}
	
	public static VisumLinkBuilder visumLink() {
	  return new VisumLinkBuilder();
	}

	public static VisumOrientedLinkBuilder visumOrientedLink() {
		return new VisumOrientedLinkBuilder();
	}

	public static VisumConnectorBuilder visumConnector() {
		return new VisumConnectorBuilder();
	}

	public static VisumNetworkBuilder visumNetwork() {
		return new VisumNetworkBuilder();
	}
	
	public static VisumSurfaceBuilder visumSurface() {
		return new VisumSurfaceBuilder();
	}
	
	public static VisumFaceBuilder visumFace(int id) {
	  return new VisumFaceBuilder(id);
	}
	
	public static VisumZoneBuilder visumZone() {
		return new VisumZoneBuilder();
	}
	
	public static VisumTerritoryBuilder visumTerritory() {
	  return new VisumTerritoryBuilder();
	}

	public static VisumPtJourneyBuilder visumJourney() {
		return new VisumPtJourneyBuilder();
	}

	public static VisumPtTimeProfileBuilder visumTimeProfile() {
		return new VisumPtTimeProfileBuilder();
	}

	public static VisumPtLineRouteBuilder visumLineRoute() {
		return new VisumPtLineRouteBuilder();
	}

	public static VisumPtLineBuilder visumLine() {
		return new VisumPtLineBuilder();
	}

	public static VisumVehicleCombinationBuilder visumVehicleCombination() {
		return new VisumVehicleCombinationBuilder();
	}

	public static VisumVehicleUnitBuilder visumVehicleUnit() {
		return new VisumVehicleUnitBuilder();
	}

	public static VisumPtVehicleJourneySectionBuilder visumJourneySection() {
		return new VisumPtVehicleJourneySectionBuilder();
	}
	
	public static VisumChargingFacilityBuilder visumChargingFacility() {
		return new VisumChargingFacilityBuilder();
	}
	
	public static VisumChargingPointBuilder visumChargingPoint() {
		return new VisumChargingPointBuilder();
	}
	
	public static VisumCarSharingStationBuilder visumCarSharingStation() {
		return new VisumCarSharingStationBuilder();
	}
}
