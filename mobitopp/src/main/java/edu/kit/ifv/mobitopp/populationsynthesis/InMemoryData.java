package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class InMemoryData implements DemographyData {

  private final Map<AttributeType, StructuralData> data;

  public InMemoryData() {
    super();
    this.data = new LinkedHashMap<>();
  }

  @Override
  public StructuralData get(AttributeType key) {
    return data.get(key);
  }

  public void store(AttributeType key, StructuralData data) {
    this.data.put(key, data);
  }

  @Override
  public boolean hasData(String zoneId) {
    return data.values().stream().anyMatch(data -> data.hasValue(zoneId, "ID"));
  }
  
  @Override
  public boolean hasAttribute(AttributeType attributeType) {
    return data.containsKey(attributeType);
  }

  @Override
  public List<AttributeType> attributes() {
    return new ArrayList<>(data.keySet());
  }

}
