package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyChecker {

  private final List<Integer> zoneIds;
  private final Consumer<String> consumer;

  public DemographyChecker(List<Integer> zoneIds, Consumer<String> consumer) {
    super();
    this.zoneIds = zoneIds;
    this.consumer = consumer;
  }

  public void check(DemographyData data) {
    checkDemography(data);
    checkZones(data);
  }

  private void checkZones(DemographyData data) {
    zoneIds.stream().map(String::valueOf).filter(missing(data)).sorted().forEach(this::log);
  }

  private Predicate<String> missing(DemographyData data) {
    return id -> !data.hasData(id);
  }

  private void checkDemography(DemographyData data) {
    ArrayList<AttributeType> requiredData = new ArrayList<>();
    requiredData.add(StandardAttribute.householdSize);
    requiredData.add(StandardAttribute.femaleAge);
    requiredData.add(StandardAttribute.maleAge);
    Predicate<AttributeType> isAvailable = data.attributes()::contains;
    String missingAttributes = requiredData.stream().filter(isAvailable.negate()).map(AttributeType::attributeName).collect(joining(", "));
    if (missingAttributes.isEmpty()) {
      return;
    }
    throw new IllegalArgumentException(String.format("Missing demography for: %s", missingAttributes));
  }

  private void log(String zoneId) {
    consumer.accept(zoneId);
  }

}
