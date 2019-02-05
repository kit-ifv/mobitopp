package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DemographyZoneIdChecker {

  private final List<Integer> zoneIds;
  private final Consumer<String> consumer;

  public DemographyZoneIdChecker(List<Integer> zoneIds, Consumer<String> consumer) {
    super();
    this.zoneIds = zoneIds;
    this.consumer = consumer;
  }

  public void verify(DemographyData data) {
    zoneIds.stream().map(String::valueOf).filter(missing(data)).sorted().forEach(this::log);
  }

  private Predicate<String> missing(DemographyData data) {
    return id -> !data.hasData(id);
  }

  private void log(String zoneId) {
    consumer.accept(zoneId);
  }
}
