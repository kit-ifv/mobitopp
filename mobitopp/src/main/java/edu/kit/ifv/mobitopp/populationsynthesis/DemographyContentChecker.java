package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemographyContentChecker {

  public void verify(DemographyData data) {
    List<AttributeType> missingTypes = calculateMissingAttributes(data);

    verify(missingTypes);
  }

  private void verify(List<AttributeType> missingTypes) {
    if (!missingTypes.isEmpty()) {
      throw warn(new IllegalArgumentException(
          String.format("Attribute %s is missing in demography", missingTypes)), log);
    }
  }

	List<AttributeType> calculateMissingAttributes(DemographyData data) {
		return Stream.of(RegionalLevel.values()).flatMap(level -> data.attributes(level)
				.stream()
				.flatMap(type -> check(data, level, type)))
				.collect(toList());
	}

  private Stream<AttributeType> check(DemographyData data, RegionalLevel level, AttributeType type) {
    List<String> valueAttributes = data.get(level, type).getAttributes();
    boolean isAvailable = valueAttributes
        .stream()
        .anyMatch(attribute -> attribute.startsWith(type.attributeName()));
    if (isAvailable) {
      return Stream.empty();
    }
    return Stream.of(type);
  }

}
