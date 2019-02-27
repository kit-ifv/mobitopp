package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;

public class DynamicNetfileAttributes implements NetfileLanguage {

  private final Map<StandardAttributes, String> attributes;
  private final Map<Table, String> tables;

  public DynamicNetfileAttributes() {
    super();
    attributes = new HashMap<>();
    tables = new HashMap<>();
  }

  @Override
  public String resolve(StandardAttributes attribute) {
    if (attributes.containsKey(attribute)) {
      return attributes.get(attribute);
    }
    throw new IllegalArgumentException(String.format("Missing visum argument name for mobitopp name %s", attribute));
  }

  public void add(StandardAttributes mobiTopp, String visum) {
    attributes.put(mobiTopp, visum);
  }
  
  @Override
  public String resolve(Table table) {
    if (tables.containsKey(table)) {
      return tables.get(table);
    }
    throw new IllegalArgumentException(String.format("Missing visum argument name for mobitopp name %s", table));
  }
  
  public void add(Table mobiTopp, String visum) {
    tables.put(mobiTopp, visum);
  }
}