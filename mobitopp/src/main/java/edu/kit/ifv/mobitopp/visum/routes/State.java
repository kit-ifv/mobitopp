package edu.kit.ifv.mobitopp.visum.routes;

enum State {
  start {

    @Override
    public void parse(Row row, RouteReader routeReader) {
      routeReader.startNewRoute(row);
    }

    @Override
    public State getCurrentState(Row row) {
      if (isStartOfNewRoute(row)) {
        return start;
      }
      return intermediateZone;
    }
  },
  intermediateZone {

    @Override
    public void parse(Row row, RouteReader routeReader) {
      String zone = row.get("STRECKE\\NACHKNOTEN\\BEZIRKNR");
      routeReader.addZone(zone);
    }

    @Override
    public State getCurrentState(Row row) {
      if (isStartOfNewRoute(row)) {
        return nextRoute;
      }
      return intermediateZone;
    }
  },
  nextRoute {

    @Override
    public State getCurrentState(Row row) {
      return intermediateZone;
    }

    @Override
    public void parse(Row row, RouteReader routeReader) {
      routeReader.addRoute();
      routeReader.startNewRoute(row);
    }

  };

  public abstract State getCurrentState(Row row);

  public abstract void parse(Row row, RouteReader routeReader);

  private static boolean isStartOfNewRoute(Row row) {
    return "0".equals(row.get("INDEX"));
  }
}