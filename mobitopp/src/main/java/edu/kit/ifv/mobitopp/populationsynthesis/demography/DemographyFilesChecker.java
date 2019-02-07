package edu.kit.ifv.mobitopp.populationsynthesis.demography;

import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyFilesChecker {

  public DemographyFilesChecker() {
    super();
  }

  public void verify(DemographyData data) {
    String missingAttributes = calculateMissingAttributes(data);
    if (missingAttributes.isEmpty()) {
      return;
    }
    throw new IllegalArgumentException(
        String.format("Missing demography for: %s", missingAttributes));
  }

  String calculateMissingAttributes(DemographyData data) {
    boolean containsSize = data.attributes().contains(StandardAttribute.householdSize);
    boolean containsType = data.attributes().contains(StandardAttribute.householdType);
    return containsSize || containsType ? "" : "Missing household size or type";
  }

}
