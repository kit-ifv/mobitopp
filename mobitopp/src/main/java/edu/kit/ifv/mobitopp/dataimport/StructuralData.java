package edu.kit.ifv.mobitopp.dataimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class StructuralData {

  private static final int defaultIndex = 0;
  static final int defaultValue = 0;

  private final CsvFile structuralData;
  private final Map<String, Integer> zoneToIndex;
  private int index;

  public StructuralData(CsvFile structuralData) {
    super();
    this.structuralData = structuralData;
    zoneToIndex = buildUpZoneIdMapping(structuralData);
    resetIndex();
  }

  private Map<String, Integer> buildUpZoneIdMapping(CsvFile structuralData) {
    HashMap<String, Integer> idMapping = new HashMap<>();
    for (int index = 0; index < structuralData.getLength(); index++) {
      String zoneId = structuralData.getValue(index, "ID");
      idMapping.put(zoneId, index);
    }
    return idMapping;
  }

  public List<String> getAttributes() {
    return structuralData.getAttributes();
  }

  public String getValue(String zone, String attribute) {
    return structuralData.getValue(indexOf(zone), attribute);
  }

  private int indexOf(String zoneId) {
    if (zoneToIndex.containsKey(zoneId)) {
      return zoneToIndex.getOrDefault(zoneId, defaultIndex);
    }
    throw new IllegalArgumentException("Can not find values for zone with id: " + zoneId);
  }

  public boolean hasValue(String zoneId, String key) {
    return structuralData.hasAttribute(key) && !getValue(zoneId, key).isEmpty();
  }

  public int valueOrDefault(String zoneId, String key) {
    if (hasValue(zoneId, key)) {
      return parsedValue(zoneId, key);
    }
    return defaultValue;
  }

  private int parsedValue(String zoneId, String key) {
    return Math.toIntExact(Math.round(Double.parseDouble(getValue(zoneId, key))));
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

  public int currentZone() {
    return Integer.parseInt(structuralData.getValue(index, "ID"));
  }

}
