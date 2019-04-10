package edu.kit.ifv.mobitopp.visum.routes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class RouteBuilder {

  private final ZoneIdTime origin;
  private final ZoneIdTime destination;
  private final LinkedList<ZoneIdTime> zones;
  private RelativeTime travelTime;

  public RouteBuilder(ZoneIdTime origin, ZoneIdTime destination) {
    super();
    this.origin = origin;
    this.destination = destination;
    zones = new LinkedList<>();
    travelTime = RelativeTime.ZERO;
  }

  public void addZone(ZoneIdTime zone) {
    if (lastElementMatchesZoneOf(zone)) {
      travelTime = travelTime.plus(zone.time());
      return;
    }
    ZoneIdTime combinedTime = zone.addTime(travelTime);
    zones.add(combinedTime);
    travelTime = RelativeTime.ZERO;
  }

  private boolean lastElementMatchesZoneOf(ZoneIdTime zoneTime) {
    return !zones.isEmpty() && zones.getLast().zone().equals(zoneTime.zone());
  }

  public OdPair buildOdPair() {
    return new OdPair(origin.zone(), destination.zone());
  }

  public ZoneRoute buildZoneRoute() {
    removeOrigin();
    removeDestination();
    return new ZoneRoute(zones());
  }

  private void removeDestination() {
    if (lastElementMatchesZoneOf(destination)) {
      zones.removeLast();
    }
  }

  private void removeOrigin() {
    if (firstElementMatchesZoneOf(origin)) {
      zones.removeFirst();
    }
  }

  private boolean firstElementMatchesZoneOf(ZoneIdTime origin2) {
    return !zones.isEmpty() && zones.getFirst().zone().equals(origin2.zone());
  }

  private List<ZoneIdTime> zones() {
    return Collections.unmodifiableList(zones);
  }

}
