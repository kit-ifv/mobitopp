package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.VisumTable;
import edu.kit.ifv.mobitopp.visum.routes.OdPair;
import edu.kit.ifv.mobitopp.visum.routes.ZoneRoute;

public class TestRoutes {

  private final VisumTable table;
  private final List<Row> rows;

  public TestRoutes() {
    super();
    table = new VisumTable("tableName", attributes());
    rows = new LinkedList<>();
  }

  void addSomeRoute(String wegindex, String viaZone) {
    createRow(asList("Z1", "Z2", wegindex, "0", "", ""));
    createRow(asList("Z1", "Z2", wegindex, "1", "Z1", viaZone));
    createRow(asList("Z1", "Z2", wegindex, "2", viaZone, viaZone));
    createRow(asList("Z1", "Z2", wegindex, "3", viaZone, "Z2"));
  }

  void addSomeRoute() {
    String wegindex = "1";
    String viaZone = "Z3";
    addSomeRoute(wegindex, viaZone);
  }

  void addOtherRoute() {
    createRow(asList("Z4", "Z7", "1", "0", "", ""));
    createRow(asList("Z4", "Z7", "1", "1", "Z4", "Z5"));
    createRow(asList("Z4", "Z7", "1", "2", "Z5", "Z6"));
    createRow(asList("Z4", "Z7", "1", "3", "Z6", "Z7"));
  }
  
  void addRouteWithoutIntermediate() {
    createRow(asList("Z2", "Z4", "1", "0", "", ""));
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
    return asList("QBEZNR", "ZBEZNR", "WEGIND", "INDEX", "STRECKE\\VONKNOTEN\\BEZIRKNR",
        "STRECKE\\NACHKNOTEN\\BEZIRKNR");
  }

  OdPair someOdPair() {
    return new OdPair("Z1", "Z2");
  }

  ZoneRoute someRoute() {
    return new ZoneRoute("Z3");
  }

  OdPair otherOdPair() {
    return new OdPair("Z4", "Z7");
  }

  ZoneRoute otherRoute() {
    return new ZoneRoute("Z5", "Z6");
  }

  OdPair missingIntermediateOdPair() {
    return new OdPair("Z2", "Z4");
  }

  ZoneRoute missingIntermediateRoute() {
    return new ZoneRoute();
  }

}
