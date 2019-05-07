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
    createRow(asList("Z1", "Z2", wegindex, "1", "2", "0", "", "", "3m 6s", "", "", ""));
    createRow(asList("", "", wegindex, "1", "2", "1", "Z1", viaZone, "", "1m 2s", connectorTime, connectorTime));
    createRow(asList("", "", wegindex, "1", "2", "2", viaZone, viaZone, "", "1m 2s", connectorTime, connectorTime));
    createRow(asList("", "", wegindex, "1", "2", "3", viaZone, "Z2", "", "1m 2s", connectorTime, connectorTime));
  }

  void addSomeRoute() {
    String wegindex = "1";
    String viaZone = "Z3";
    addSomeRoute(wegindex, viaZone);
  }

  void addOtherRoute() {
    createRow(asList("Z4", "Z7", "1", "1", "2", "0", "", "", "3m 6s", "", "", ""));
    createRow(asList("", "", "1", "1", "2", "1", "Z4", "Z5", "", "1m 2s", "0s", "0s"));
    createRow(asList("", "", "1", "1", "2", "2", "Z5", "Z6", "", "1m 2s", "0s", "0s"));
    createRow(asList("", "", "1", "1", "2", "3", "Z6", "Z7", "", "1m 2s", "0s", "0s"));
  }

  void addRouteWithoutIntermediate() {
    createRow(asList("Z2", "Z4", "1", "1", "2", "0", "", "", "1m 2s", "", "0s", "0s"));
  }
  
  void addSomeRouteWithConnector() {
    addSomeRoute("1", "Z3", connectorTime);
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
    return asList("QBEZNR", "ZBEZNR", "WEGIND", "VONKNOTNR", "NACHKNOTNR", "INDEX",
        "STRECKE\\VONKNOTEN\\BEZIRKNR", "STRECKE\\NACHKNOTEN\\BEZIRKNR", "IV-WEG\\T0",
        "STRECKE\\T0-IVSYS(P)", "ABBIEGER\\T0-IVSYS(P)", "ABBIEGER\\TAKT-IVSYS(P)");
  }

  OdPair someOdPair() {
    return new OdPair("Z1", "Z2");
  }

  ZoneRoute someRoute() {
    ZoneIdTime atDestination = new ZoneIdTime("Z2", defaultTravelTime.multiplyBy(2));
    return new ZoneRoute(newZoneTime("Z3"), atDestination);
  }

  private ZoneIdTime newZoneTime(String zone) {
    return new ZoneIdTime(zone, defaultTravelTime);
  }

  OdPair otherOdPair() {
    return new OdPair("Z4", "Z7");
  }

  ZoneRoute otherRoute() {
    return new ZoneRoute(newZoneTime("Z5"), newZoneTime("Z6"), newZoneTime("Z7"));
  }

  OdPair missingIntermediateOdPair() {
    return new OdPair("Z2", "Z4");
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
    ZoneIdTime zoneTime = new ZoneIdTime("Z3", defaultTravelTime.plus(connectorTime()));
    RelativeTime arrivalAtDestination = zoneTime.time().plus(defaultTravelTime).plus(connectorTime());
    ZoneIdTime atDestintion = new ZoneIdTime("Z2", arrivalAtDestination);
    return new ZoneRoute(zoneTime, atDestintion);
  }

}
