package edu.kit.ifv.mobitopp.visum;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.Collections;
import java.io.Serializable;

public class VisumRoadNetwork implements Serializable {

  private static final long serialVersionUID = 1L;

  public final VisumTransportSystems transportSystems;
  public final VisumLinkTypes linkTypes;

  public final Map<Integer, VisumNode> nodes;
  public final VisumLinks links;
  public final Map<Integer, List<VisumTurn>> turns;
  public final Map<Integer, VisumZone> zones;
  public final Map<Integer, VisumSurface> areas;
  public final Map<Integer, VisumTerritory> territories;
  public final Map<Integer, List<VisumConnector>> connectors;

  public final Map<Integer, VisumChargingFacility> chargingFacilities;
  public final Map<Integer, VisumChargingPoint> chargingPoints;
  public final Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations;

  public VisumRoadNetwork(
		VisumTransportSystems transportSystems,
		VisumLinkTypes linkTypes, 
		Map<Integer, VisumNode> nodes,
		Map<Integer, VisumLink> links,
		Map<Integer, List<VisumTurn>> turns,
		Map<Integer, VisumZone> zones,
		Map<Integer, VisumSurface> areas,
		Map<Integer, VisumTerritory> territories,
      Map<Integer, List<VisumConnector>> connectors,
      Map<Integer, VisumChargingFacility> chargingFacilities,
      Map<Integer, VisumChargingPoint> chargingPoints,
		Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations
	) {

    this.transportSystems = transportSystems;
    this.linkTypes = linkTypes;

    this.nodes = Collections.unmodifiableMap(nodes);
    this.links = new VisumLinks(links);
    this.turns = turns;
    this.zones = Collections.unmodifiableMap(zones);
    this.areas = Collections.unmodifiableMap(areas);
    this.territories = Collections.unmodifiableMap(territories);
    this.connectors = Collections.unmodifiableMap(connectors);

    this.chargingFacilities = Collections.unmodifiableMap(chargingFacilities);
    this.chargingPoints = Collections.unmodifiableMap(chargingPoints);
    this.carSharingStations = Collections.unmodifiableMap(carSharingStations);

  }

  public VisumTransportSystem getTransportSystem(String code) {
    return transportSystems.getBy(code);
  }

  public boolean containsTransportSystem(String code) {
    return transportSystems.containsFor(code);
  }

  public VisumLinkType getLinkType(int id) {
    return linkTypes.getById(id);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(areas, carSharingStations, chargingFacilities, chargingPoints, connectors, linkTypes,
            links, nodes, territories, transportSystems, turns, zones);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumRoadNetwork other = (VisumRoadNetwork) obj;
    return Objects.equals(areas, other.areas)
        && Objects.equals(carSharingStations, other.carSharingStations)
        && Objects.equals(chargingFacilities, other.chargingFacilities)
        && Objects.equals(chargingPoints, other.chargingPoints)
        && Objects.equals(connectors, other.connectors)
        && Objects.equals(linkTypes, other.linkTypes) && Objects.equals(links, other.links)
        && Objects.equals(nodes, other.nodes) && Objects.equals(territories, other.territories)
        && Objects.equals(transportSystems, other.transportSystems)
        && Objects.equals(turns, other.turns) && Objects.equals(zones, other.zones);
  }

  @Override
  public String toString() {
    return "VisumRoadNetwork [transportSystems=" + transportSystems + ", linkTypes=" + linkTypes
        + ", nodes=" + nodes + ", links=" + links + ", turns=" + turns + ", zones=" + zones
        + ", areas=" + areas + ", territories=" + territories + ", connectors=" + connectors
        + ", chargingFacilities=" + chargingFacilities + ", chargingPoints=" + chargingPoints
        + ", carSharingStations=" + carSharingStations + "]";
  }

}
