package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public interface DemographyData {

  boolean hasData(RegionalLevel level, String zoneId);

  StructuralData get(RegionalLevel level, AttributeType key);

  boolean hasAttribute(RegionalLevel level, AttributeType attributeType);

  List<AttributeType> attributes(RegionalLevel level);

	Map<RegionalLevel, List<AttributeType>> allAttributes();

}