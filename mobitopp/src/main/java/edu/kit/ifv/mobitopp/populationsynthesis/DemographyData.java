package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public interface DemographyData {

  boolean hasData(String zoneId);

  StructuralData get(String key);

}