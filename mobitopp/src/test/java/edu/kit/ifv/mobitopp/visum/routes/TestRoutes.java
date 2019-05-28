package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.VisumTable;

public class TestRoutes {

  private static final RelativeTime defaultTravelTime = RelativeTime.ofMinutes(1).plusSeconds(2);
  private static final String connectorTime = "1m";
  private final VisumTable table;
  private final List<Row> rows;

  public TestRoutes() {
    super();
    table = new VisumTable("tableName", attributes());
    rows = new LinkedList<>();
  }

  void addSomeRoute(String wegindex, String viaZone) {
    String connectorTime = "0s";
    addSomeRoute(wegindex, viaZone, connectorTime);
  }

  private void addSomeRoute(String wegindex, String viaZone, String connectorTime) {
    createRow(asList("1", "2", wegindex, "1", "2", "0", "", "", "3m 6s", "", "", ""));
    createRow(asList("", "", wegindex, "1", "2", "1", "1", viaZone, "", "1m 2s", connectorTime, connectorTime));
    createRow(asList("", "", wegindex, "1", "2", "2", viaZone, viaZone, "", "1m 2s", connectorTime, connectorTime));
    createRow(asList("", "", wegindex, "1", "2", "3", viaZone, "2", "", "1m 2s", connectorTime, connectorTime));
  }

  void addSomeRoute() {
    String wegindex = "1";
    String viaZone = "3";
    addSomeRoute(wegindex, viaZone);
  }

  void addOtherRoute() {
    createRow(asList("4", "7", "1", "1", "2", "0", "", "", "3m 6s", "", "", ""));
    createRow(asList("", "", "1", "1", "2", "1", "4", "5", "", "1m 2s", "0s", "0s"));
    createRow(asList("", "", "1", "1", "2", "2", "5", "6", "", "1m 2s", "0s", "0s"));
    createRow(asList("", "", "1", "1", "2", "3", "6", "7", "", "1m 2s", "0s", "0s"));
  }

  void addRouteWithoutIntermediate() {
    createRow(asList("2", "4", "1", "1", "2", "0", "", "", "1m 2s", "", "0s", "0s"));
  }
  
  void addSomeRouteWithConnector() {
    addSomeRoute("1", "3", connectorTime);
  }

  private void createRow(List<String> values) {
    Map<String, String> row = new HashMap<String, String>();
    for (int i = 0; i < attributes().size(); i++) {
      row.put(attributes().get(i), values.get(i));
    }
    rows.add(new Row(row));
  }

  public VisumTable createTable() {
    return table;
  }

  public Stream<Row> createRows() {
    return rows.stream();
  }

  private List<String> attributes() {
    return asList("ORIGZONENO", "DESTZONENO", "PATHINDEX", "VONKNOTNR", "NACHKNOTNR", "INDEX",
        "LINK\\FROMNODE\\BEZIRKNR", "LINK\\TONODE\\BEZIRKNR", "PRTPATH\\T0",
        "LINK\\T0_PRTSYS(SOV)", "TURN\\T0_PRTSYS(SOV)", "TURN\\TCUR_PRTSYS(SOV)");
  }  

  OdPair someOdPair() {
    return new OdPair("1", "2");
  }

  ZoneRoute someRoute() {
    ZoneIdTime atDestination = new ZoneIdTime("2", defaultTravelTime.multiplyBy(2));
    return new ZoneRoute(newZoneTime("3"), atDestination);
  }

  private ZoneIdTime newZoneTime(String zone) {
    return new ZoneIdTime(zone, defaultTravelTime);
  }

  OdPair otherOdPair() {
    return new OdPair("4", "7");
  }

  ZoneRoute otherRoute() {
    return new ZoneRoute(newZoneTime("5"), newZoneTime("6"), newZoneTime("7"));
  }

  OdPair missingIntermediateOdPair() {
    return new OdPair("2", "4");
  }

  ZoneRoute missingIntermediateRoute() {
    return new ZoneRoute();
  }

  public NodeNode connectorNodeToNode() {
    return new NodeNode(1, 2);
  }

  public RelativeTime connectorTime() {
    return VisumUtils.parseTime(connectorTime);
  }

  public ZoneRoute someRouteWithConnector() {
    ZoneIdTime zoneTime = new ZoneIdTime("3", defaultTravelTime.plus(connectorTime()));
    RelativeTime arrivalAtDestination = zoneTime.time().plus(defaultTravelTime).plus(connectorTime());
    ZoneIdTime atDestintion = new ZoneIdTime("2", arrivalAtDestination);
    return new ZoneRoute(zoneTime, atDestintion);
  }

}
