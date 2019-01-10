package edu.kit.ifv.mobitopp.dataimport;

import java.util.List;

import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class StructuralData {

  static final int defaultValue = 0;

  private final CsvFile structuralData;
  private int index;

  public StructuralData(CsvFile structuralData) {
    super();
    this.structuralData = structuralData;
    resetIndex();
  }

  public List<String> getAttributes() {
    return structuralData.getAttributes();
  }

  public int currentZone() {
    return Integer.parseInt(getValue("ID"));
  }

  public String getValue(String key) {
    return structuralData.getValue(index, key);
  }

  public boolean hasValue(String key) {
    return structuralData.hasAttribute(key) && !getValue(key).isEmpty();
  }

  public int valueOrDefault(String key) {
    if (hasValue(key)) {
      return parsedValue(key);
    }
    return defaultValue;
  }

  private int parsedValue(String key) {
    return Math.toIntExact(Math.round(Double.parseDouble(getValue(key))));
  }

  public void next() {
    index++;
  }

  public void resetIndex() {
    index = 0;
  }

  public boolean hasNext() {
    return index < structuralData.getLength();
  }

}
