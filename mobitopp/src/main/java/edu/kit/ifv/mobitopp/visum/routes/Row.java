package edu.kit.ifv.mobitopp.visum.routes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Row {

  private final Map<String, String> values;

  public Row(Map<String, String> values) {
    super();
    this.values = values;
  }
  
  public boolean containsAttribute(String key) {
    return values.containsKey(key);
  }

  public String get(String key) {
    if (containsAttribute(key)) {
      return values.get(key);
    }
    throw new IllegalArgumentException(String
        .format("No attribute for key %s available. Available attributes are: %s", key,
            values.keySet()));
  }
  
  public int valueAsInteger(String key) {
    return Integer.parseInt(get(key));
  }
  
  public float valueAsFloat(String key) {
    return Float.parseFloat(get(key));
  }
  
  public double valueAsDouble(String key) {
    return Double.parseDouble(get(key));
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

  @Override
  public int hashCode() {
    return Objects.hash(values);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Row other = (Row) obj;
    return Objects.equals(values, other.values);
  }

  @Override
  public String toString() {
    return "Row [values=" + values + "]";
  }



}
