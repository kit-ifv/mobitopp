package edu.kit.ifv.mobitopp.dataimport;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Value;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;

public class ZonePropertiesData {

  private static final String relief = "relief";
	private static final String isDestination = "isDestination";
	private static final String newClassificationKey = "zoneClassification";
  private static final String oldClassificationKey = "Outlyingarea";

  private final StructuralData data;
  private final AreaTypeRepository areaTypeRepository;

  public ZonePropertiesData(StructuralData data, AreaTypeRepository areaTypeRepository) {
    this.data = data;
    this.areaTypeRepository = areaTypeRepository;
  }

  public StructuralData data() {
  	return this.data;
  }
  
  public ZoneClassificationType currentClassification(String zoneId) {
		String classification = classificationValue(zoneId);
		if (0 == Integer.valueOf(classification)) {
			return ZoneClassificationType.studyArea;
		}
		if (1 == Integer.valueOf(classification)) {
			return ZoneClassificationType.extendedStudyArea;
		}
		return ZoneClassificationType.outlyingArea;
	}


  private String classificationValue(String zoneId) {
    return data.hasValue(zoneId, newClassificationKey) ? data.getValue(zoneId, newClassificationKey)
        : data.getValue(zoneId, oldClassificationKey);
  }

  public RegionType currentRegionType(String zoneId) {
    return new DefaultRegionType(data.valueOrDefault(zoneId, "RegionType"));
  }

  public AreaType currentZoneAreaType(String zoneId) {
    String areaType = data.getValue(zoneId, "AreaType");
    try {
      int code = Integer.parseInt(areaType);
      return areaTypeRepository.getTypeForCode(code);
    } catch (NumberFormatException cause) {
      return areaType.isEmpty() ? areaTypeRepository.getDefault()
          : areaTypeRepository.getTypeForName(areaType);
    }
  }

	public boolean isDestination(String zoneId) {
		return data.hasValue(zoneId, isDestination)
				&& "1".equals(data.getValue(zoneId, isDestination));
	}

	public double relief(String zoneId) {
		return data.valueOrDefaultAsDouble(zoneId, relief);
	}

  public Map<String, Value> getValues(String zoneId) {
    return data.getValues(zoneId);
  }

}
