package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Value;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StructuralData {

	@Deprecated
  private static final String zoneIdColumn = "zoneId";
  private static final String regionIdColumn = "regionId";
	private static final int defaultIndex = 0;
  static final int defaultValue = 0;

  private final CsvFile structuralData;
  private final Map<String, Integer> idToRow;
  private int row;

  public StructuralData(CsvFile structuralData) {
    super();
    this.structuralData = structuralData;
    idToRow = buildUpIdMapping(structuralData);
    resetRow();
  }

  private Map<String, Integer> buildUpIdMapping(CsvFile structuralData) {
    HashMap<String, Integer> idMapping = new HashMap<>();
    for (int row = 0; row < structuralData.getLength(); row++) {
      String id = structuralData.getValue(row, idColumn());
      idMapping.put(id, row);
    }
    return idMapping;
  }

	private String idColumn() {
		return structuralData.hasAttribute(regionIdColumn) ? regionIdColumn : zoneIdColumn;
	}

	public boolean hasRegion(String id) {
		return hasValue(id, idColumn());
	}

  public List<String> getAttributes() {
    return structuralData.getAttributes();
  }

  public String getValue(String id, String attribute) {
    return structuralData.getValue(rowOf(id), attribute);
  }

  private int rowOf(String id) {
    if (hasRow(id)) {
      return idToRow.getOrDefault(id, StreamUtils.warn(id, "row", defaultIndex, log));
    }
    throw warn(new IllegalArgumentException("Can not find values for region with id: " + id), log);
  }

  public boolean hasValue(String id, String key) {
    return hasAttribute(key) && hasRow(id) && isNotEmpty(id, key);
  }

  private boolean hasAttribute(String key) {
    return structuralData.hasAttribute(key);
  }

  private boolean hasRow(String id) {
    return idToRow.containsKey(id);
  }

  private boolean isNotEmpty(String id, String key) {
    return !getValue(id, key).isEmpty();
  }

  public int valueOrDefault(String id, String key) {
    if (hasValue(id, key)) {
      return parsedValue(id, key);
    }
    return defaultValue;
  }

  public double valueOrDefaultAsDouble(String id, String key) {
    if (hasValue(id, key)) {
      return parsedValueAsDouble(id, key);
    }
    return defaultValue;
  }
  
  public float valueOrDefaultAsFloat(String id, String key) {
  	if (hasValue(id, key)) {
  		return parsedValueAsFloat(id, key);
  	}
  	return defaultValue;
  }
  
  private int parsedValue(String id, String key) {
    return Math.toIntExact(Math.round(Double.parseDouble(getValue(id, key))));
  }
  
  private double parsedValueAsDouble(String id, String key) {
    return Double.parseDouble(getValue(id, key));
  } 
  
  private float parsedValueAsFloat(String id, String key) {
  	return Float.parseFloat(getValue(id, key));
  } 

  public void next() {
    row++;
  }

  public void resetRow() {
    row = 0;
  }

  public boolean hasNext() {
    return row < structuralData.getLength();
  }

  public int currentRegion() {
    return Integer.parseInt(structuralData.getValue(row, idColumn()));
  }

  public Map<String, Value> getValues(String zoneId) {
    List<String> attributes = structuralData.getAttributes();
    Function<String, Value> toValue = attribute -> toValue(zoneId, attribute);
    return attributes.stream().collect(StreamUtils.toLinkedMap(Function.identity(), toValue ));
  }
  
  private Value toValue(String zoneId, String attribute) {
    return new Value(getValue(zoneId, attribute));
  }

}
