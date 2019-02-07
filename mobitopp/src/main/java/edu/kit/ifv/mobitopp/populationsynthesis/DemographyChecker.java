package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.populationsynthesis.demography.DemographyFilesChecker;

public class DemographyChecker {

  private final List<Integer> zoneIds;
  private final Consumer<String> consumer;

  public DemographyChecker(List<Integer> zoneIds, Consumer<String> consumer) {
    super();
    this.zoneIds = zoneIds;
    this.consumer = consumer;
  }

  public void check(DemographyData data) {
    checkDemographyFiles(data);
    checkDemographyContent(data);
    checkZones(data);
  }

  private void checkDemographyFiles(DemographyData data) {
    new DemographyFilesChecker().verify(data);
  }

  private void checkDemographyContent(DemographyData data) {
    new DemographyContentChecker().verify(data);
  }

  private void checkZones(DemographyData data) {
    new DemographyZoneIdChecker(zoneIds, consumer).verify(data);
  }
}
