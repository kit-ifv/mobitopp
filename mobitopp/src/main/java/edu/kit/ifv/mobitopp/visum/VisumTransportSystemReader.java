package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;

public class VisumTransportSystemReader {

  private final VisumTable table;
  private final NetfileLanguage language;
  private final Map<String, VisumTransportSystem> readSystems;

  public VisumTransportSystemReader(VisumTable table, NetfileLanguage language) {
    super();
    this.table = table;
    this.language = language;
    readSystems = new HashMap<>();
  }

  public VisumTransportSystems readTransportSystems() {
    for (int row = 0; row < table.numberOfRows(); row++) {
      createSystemIn(row);
    }
    return new VisumTransportSystems(readSystems);
  }

  private void createSystemIn(int row) {
    verifySystemIn(row);
    readSystems.put(codeIn(row), systemIn(row));
  }

  private void verifySystemIn(int row) {
    if (readSystems.containsKey(codeIn(row))) {
      throw new IllegalArgumentException(
          "VisumTransportSystem: transport system with code=" + codeIn(row) + " already exists");
    }
  }

  private VisumTransportSystem systemIn(int row) {
    return new VisumTransportSystem(codeIn(row), nameIn(row), typeIn(row));
  }

  private String codeIn(int row) {
    return table.getValue(row, language.resolve(StandardAttributes.code));
  }

  private String nameIn(int row) {
    return table.getValue(row, language.resolve(StandardAttributes.name));
  }

  private String typeIn(int i) {
    return table.getValue(i, language.resolve(StandardAttributes.type));
  }

}
