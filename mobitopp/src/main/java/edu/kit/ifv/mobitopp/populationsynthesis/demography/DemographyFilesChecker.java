package edu.kit.ifv.mobitopp.populationsynthesis.demography;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class DemographyFilesChecker {

  private final ArrayList<AttributeType> requiredAttributes;

  public DemographyFilesChecker(ArrayList<AttributeType> requiredAttributes) {
    super();
    this.requiredAttributes = requiredAttributes;
  }

  public void verify(DemographyData data) {
    Predicate<AttributeType> isAvailable = data.attributes()::contains;
    String missingAttributes = requiredAttributes
        .stream()
        .filter(isAvailable.negate())
        .map(AttributeType::attributeName)
        .collect(joining(", "));
    if (missingAttributes.isEmpty()) {
      return;
    }
    throw new IllegalArgumentException(
        String.format("Missing demography for: %s", missingAttributes));
  }

}
