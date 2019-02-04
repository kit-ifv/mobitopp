package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;

public class AttractivitiesData {

  private static final String newClassificationKey = "zoneclassification";
  private static final String oldClassificationKey = "Outlyingarea";

  private final StructuralData data;
  private final AreaTypeRepository areaTypeRepository;

  public AttractivitiesData(StructuralData data, AreaTypeRepository areaTypeRepository) {
    this.data = data;
    this.areaTypeRepository = areaTypeRepository;
  }

  public void next() {
    data.next();
  }

  public void resetIndex() {
    data.resetIndex();
  }

  public boolean hasNext() {
    return data.hasNext();
  }

  public int currentZone() {
    return data.currentZone();
  }

  public boolean hasValue(String zoneId, String key) {
    return data.hasValue(zoneId, key);
  }

  public int valueOrDefault(String zoneId, String key) {
    return data.valueOrDefault(zoneId, key);
  }
  
  private String getValue(String zoneId, String key) {
    return data.getValue(zoneId, key);
  }

  public ZoneClassificationType currentClassification(String zoneId) {
    String classification = classificationValue(zoneId);
    if (Integer.valueOf(classification) > 0) {
      return ZoneClassificationType.outlyingArea;
    }
    return ZoneClassificationType.areaOfInvestigation;
  }

  private String classificationValue(String zoneId) {
    return hasValue(zoneId, newClassificationKey) ? getValue(zoneId, newClassificationKey)
        : getValue(zoneId, oldClassificationKey);
  }

  public AreaType currentZoneAreaType(String zoneId) {
    String areaType = getValue(zoneId, "AreaType");
    try {
      int code = Integer.parseInt(areaType);
      return areaTypeRepository.getTypeForCode(code);
    } catch (NumberFormatException cause) {
      return areaType.isEmpty() ? areaTypeRepository.getDefault()
          : areaTypeRepository.getTypeForName(areaType);
    }
  }

}
