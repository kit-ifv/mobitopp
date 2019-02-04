package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public class InMemoryData implements DemographyData {

  private final Map<String, StructuralData> data;

  public InMemoryData() {
    super();
    this.data = new HashMap<>();
  }

  @Override
  public StructuralData get(String key) {
    return data.get(key);
  }

  public void store(String key, StructuralData data) {
    this.data.put(key, data);
  }

}
