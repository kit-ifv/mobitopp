package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.populationsynthesis.demography.DemographyFilesChecker;
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
    checkDemographyFiles(data);
    checkDemographyContent(data);
    checkZones(data);
  }

  private void checkDemographyFiles(DemographyData data) {
    ArrayList<AttributeType> requiredAttributes = requiredAttributes();
    new DemographyFilesChecker(requiredAttributes).verify(data);
  }

  private ArrayList<AttributeType> requiredAttributes() {
    ArrayList<AttributeType> requiredAttributes = new ArrayList<>();
    requiredAttributes.add(StandardAttribute.householdSize);
    requiredAttributes.add(StandardAttribute.femaleAge);
    requiredAttributes.add(StandardAttribute.maleAge);
    return requiredAttributes;
  }

  private void checkDemographyContent(DemographyData data) {
    new DemographyContentChecker().verify(data);
  }

  private void checkZones(DemographyData data) {
    new DemographyZoneIdChecker(zoneIds, consumer).verify(data);
  }
}
