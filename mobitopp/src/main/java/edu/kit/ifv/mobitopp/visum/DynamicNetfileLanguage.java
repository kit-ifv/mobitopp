package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;

public class DynamicNetfileLanguage implements NetfileLanguage {

  private final Map<StandardAttributes, String> attributes;
  private final Map<Table, String> tables;
  private final Map<Unit, String> units;

  public DynamicNetfileLanguage() {
    super();
    attributes = new HashMap<>();
    tables = new HashMap<>();
    units = new HashMap<>();
  }

  @Override
  public String resolve(StandardAttributes attribute) {
    if (attributes.containsKey(attribute)) {
      return attributes.get(attribute);
    }
    throw new IllegalArgumentException(
        String.format("Missing visum attribute name for mobitopp name: %s", attribute));
  }

  public void add(StandardAttributes mobiTopp, String visum) {
    attributes.put(mobiTopp, visum);
  }

  @Override
  public String resolve(Table table) {
    if (tables.containsKey(table)) {
      return tables.get(table);
    }
    throw new IllegalArgumentException(
        String.format("Missing visum table name for mobitopp name: %s", table));
  }

  public void add(Table mobiTopp, String visum) {
    tables.put(mobiTopp, visum);
  }
  
  @Override
  public String resolve(Unit unit) {
    if (units.containsKey(unit)) {
      return units.get(unit);
    }
    throw new IllegalArgumentException(
        String.format("Missing visum unit for mobitopp name: %s", unit));
  }

  public void add(Unit unit, String visum) {
    units.put(unit, visum);
  }
}