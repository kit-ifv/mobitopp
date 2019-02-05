package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class DemographyContentChecker {

  public void verify(DemographyData data) {
    List<AttributeType> missingTypes = calculateMissingAttributes(data);

    verify(missingTypes);
  }

  private void verify(List<AttributeType> missingTypes) {
    if (!missingTypes.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Attribute %s is missing in demography", missingTypes));
    }
  }

  List<AttributeType> calculateMissingAttributes(DemographyData data) {
    return data.attributes().stream().flatMap(type -> check(data, type)).collect(toList());
  }

  private Stream<AttributeType> check(DemographyData data, AttributeType type) {
    List<String> valueAttributes = data.get(type).getAttributes();
    boolean isAvailable = valueAttributes
        .stream()
        .anyMatch(attribute -> attribute.startsWith(type.attributeName()));
    if (isAvailable) {
      return Stream.empty();
    }
    return Stream.of(type);
  }

}
