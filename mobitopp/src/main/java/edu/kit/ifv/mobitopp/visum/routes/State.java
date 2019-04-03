package edu.kit.ifv.mobitopp.visum.routes;

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
      routeReader.addZone(zone);
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
    return "0".equals(row.get("INDEX"));
  }

  public abstract State newRouteState();

  public abstract void parse(Row row, RouteReader routeReader);

}