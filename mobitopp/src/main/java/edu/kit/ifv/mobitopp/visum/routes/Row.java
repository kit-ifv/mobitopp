package edu.kit.ifv.mobitopp.visum.routes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Row {

  private final Map<String, String> values;

  public Row(Map<String, String> values) {
    super();
    this.values = values;
  }

  public String get(String key) {
    return values.get(key);
  }

  public static Row createRow(List<String> values, List<String> attributes)
      throws IllegalArgumentException {
    if (values.size() != attributes.size()) {
      LinkedList<String> extended = extendValues(values, attributes);
      return createRow(extended, attributes);
    }

    return doCreateRow(values, attributes);
  }

  private static LinkedList<String> extendValues(List<String> values, List<String> attributes) {
    System.out
        .println(String
            .format("Fewer values (%s) than attributes (%s). Adding empty values.", values.size(),
                attributes.size()));
    System.out.println("Attributes: " + attributes);
    System.out.println("Values: " + values);
    LinkedList<String> extended = new LinkedList<>(values);
    extended.add("");
    return extended;
  }

  private static Row doCreateRow(List<String> values, List<String> attributes) {
    Map<String, String> row = new HashMap<String, String>();
    for (int i = 0; i < attributes.size(); i++) {
      row.put(attributes.get(i), values.get(i));
    }
    return new Row(row);
  }

}
