package edu.kit.ifv.mobitopp.visum.routes;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class RouteBuilder {

  private final ZoneIdTime origin;
  private final ZoneIdTime destination;
  private final LinkedList<ZoneIdTime> zones;
  private Duration travelTime;

  public RouteBuilder(ZoneIdTime origin, ZoneIdTime destination) {
    super();
    this.origin = origin;
    this.destination = destination;
    zones = new LinkedList<>();
    travelTime = Duration.ZERO;
  }

  public void addZone(String zone, Duration time) {
    if (lastElementMatchesZoneOf(zone)) {
      travelTime = travelTime.plus(time);
      return;
    }
    doAddZone(zone, time);
  }

  public void doAddZone(String zone, Duration time) {
    Duration completeTime = travelTime.plus(time);
    ZoneIdTime combinedTime = new ZoneIdTime(zone, RelativeTime.of(completeTime));
    zones.add(combinedTime);
    travelTime = Duration.ZERO;
  }

  private boolean lastElementMatchesZoneOf(String zone) {
    return !zones.isEmpty() && zones.getLast().zone().equals(zone);
  }

  public OdPair buildOdPair() {
    return new OdPair(origin.zone(), destination.zone());
  }

  public ZoneRoute buildZoneRoute() {
    removeOrigin();
    return new ZoneRoute(zones());
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
