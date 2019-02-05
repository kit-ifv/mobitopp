package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public interface DemographyData {

  boolean hasData(String zoneId);

  StructuralData get(AttributeType key);

  boolean hasAttribute(AttributeType attributeType);

  List<AttributeType> attributes();

}