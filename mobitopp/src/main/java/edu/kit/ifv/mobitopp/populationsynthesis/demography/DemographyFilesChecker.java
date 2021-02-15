package edu.kit.ifv.mobitopp.populationsynthesis.demography;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemographyFilesChecker {

  public DemographyFilesChecker() {
    super();
  }

  public void verify(DemographyData data) {
    String missingAttributes = calculateMissingAttributes(data);
    if (missingAttributes.isEmpty()) {
      return;
    }
    throw warn(new IllegalArgumentException(
        String.format("Missing demography for: %s", missingAttributes)), log);
  }

  String calculateMissingAttributes(DemographyData data) {
    boolean containsSize = dataContains(data, StandardAttribute.householdSize);
    boolean containsDomCode = dataContains(data, StandardAttribute.domCode);
    return containsSize || containsDomCode ? "" : "Missing household size or dom code";
  }

	private boolean dataContains(DemographyData data, StandardAttribute attribute) {
		return Stream
				.of(RegionalLevel.values())
				.map(data::attributes)
				.anyMatch(attributes -> attributes.contains(attribute));
	}

}
