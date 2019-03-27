package edu.kit.ifv.mobitopp.visum.routes;

import java.util.Collections;
import java.util.LinkedList;

public class RouteBuilder {

  private final String origin;
  private final String destination;
  private final LinkedList<String> zones;

  public RouteBuilder(String origin, String destination) {
    super();
    this.origin = origin;
    this.destination = destination;
    zones = new LinkedList<>();
  }

  public void addZone(String zone) {
    if (isAlreadyPresent(zone)) {
      return;
    }
    zones.add(zone);
  }

  private boolean isAlreadyPresent(String zone) {
    return !zones.isEmpty() && zones.getLast().equals(zone);
  }

  public OdPair buildOdPair() {
    return new OdPair(origin, destination);
  }

  public ZoneRoute buildZoneRoute() {
    zones.remove(origin);
    zones.remove(destination);
    return new ZoneRoute(Collections.unmodifiableList(zones));
  }

}
