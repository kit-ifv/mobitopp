package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class InMemoryData implements DemographyData {

  private final Map<RegionalLevel, Map<AttributeType, StructuralData>> data;

  public InMemoryData() {
    super();
    this.data = new LinkedHashMap<>();
  }

  @Override
	public StructuralData get(RegionalLevel level, AttributeType key) {
		if (!hasDataFor(level)) {
			throw new IllegalArgumentException(
					"No demography data is available for regional level: " + level);
		}
		return data.get(level).get(key);
	}

	public void store(RegionalLevel level, AttributeType key, StructuralData data) {
  	if (!hasDataFor(level)) {
  		this.data.put(level, new LinkedHashMap<>());
  	}
    this.data.get(level).put(key, data);
  }

	private boolean hasDataFor(RegionalLevel level) {
		return this.data.containsKey(level);
	}

	@Override
	public boolean hasData(RegionalLevel level, String zoneId) {
		return hasDataFor(level)
				&& data.get(level).values().stream().anyMatch(data -> data.hasValue(zoneId, "zoneId"));
	}
  
	@Override
	public boolean hasAttribute(RegionalLevel level, AttributeType attributeType) {
		return hasDataFor(level) && data.get(level).containsKey(attributeType);
	}

  @Override
  public List<AttributeType> attributes(RegionalLevel level) {
  	if (hasDataFor(level)) {
  		return new ArrayList<>(data.get(level).keySet());
  	}
  	return emptyList();
  }
  
  @Override
  public Map<RegionalLevel, List<AttributeType>> allAttributes() {
		return data
				.entrySet()
				.stream()
				.collect(toMap(Entry::getKey, entry -> new LinkedList<>(entry.getValue().keySet())));
  }

}
