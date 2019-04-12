package edu.kit.ifv.mobitopp.visum.routes;

import edu.kit.ifv.mobitopp.time.RelativeTime;

enum State {
  start {

    @Override
    public void parse(Row row, RouteReader routeReader) {
      routeReader.startNewRoute(row);
    }

    @Override
    public State newRouteState() {
      return start;
    }

  },
  intermediateZone {

    @Override
    public void parse(Row row, RouteReader routeReader) {
      String zone = row.get("STRECKE\\NACHKNOTEN\\BEZIRKNR");
      RelativeTime time = parse(row.get("STRECKE\\T0-IVSYS(P)"));
      int from = row.valueAsInteger("VONKNOTNR");
      int to = row.valueAsInteger("NACHKNOTNR");
      NodeNode fromTo = new NodeNode(from, to);
      RelativeTime connectorTime = routeReader.getConnectorTime(fromTo);
      RelativeTime completeTime = time.plus(connectorTime);
      ZoneIdTime zoneTime = new ZoneIdTime(zone, completeTime);
      routeReader.addZone(zoneTime);
    }

    private RelativeTime parse(String time) {
      return VisumUtils.parseTime(time);
    }

    @Override
    public State newRouteState() {
      return nextRoute;
    }

  },
  nextRoute {

    @Override
    public State newRouteState() {
      return nextRoute;
    }

    @Override
    public void parse(Row row, RouteReader routeReader) {
      routeReader.addRoute();
      routeReader.startNewRoute(row);
    }

  };

  public State getCurrentState(Row row) {
    if (isStartOfNewRoute(row)) {
      return newRouteState();
    }
    return intermediateZone;
  }

  private boolean isStartOfNewRoute(Row row) {
    return !row.get("QBEZNR").isEmpty() && !row.get("ZBEZNR").isEmpty();
  }

  public abstract State newRouteState();

  public abstract void parse(Row row, RouteReader routeReader);

}