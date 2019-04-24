package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class RangeDistributionBuilder {

  private final StructuralData structuralData;
  private final AttributeType attributeType;
  private RangeItemParser itemParser;

  public RangeDistributionBuilder(StructuralData structuralData, AttributeType attributeType) {
    super();
    this.structuralData = structuralData;
    this.attributeType = attributeType;
    itemParser = new RangeItemParser(attributeType.prefix());
  }

  public <T extends RangeDistributionIfc> T buildFor(
      String zoneId, Supplier<T> distributionFactory) {
    T distribution = distributionFactory.get();
    for (String value : types()) {
      RangeDistributionItem valueItem = distributionItemFrom(zoneId, value);
      distribution.addItem(valueItem);
    }
    verify(distribution);
    return distribution;
  }

  private List<String> types() {
    List<String> values = new ArrayList<>();
    for (String attribute : structuralData.getAttributes()) {
      if (attribute.startsWith(attributeType.attributeName())) {
        values.add(attribute);
      }
    }
    return values;
  }

  private void verify(RangeDistributionIfc distribution) {
    if (distribution.isEmpty()) {
      return;
    }
    int lastUpper = getStartOfDistribution(distribution);
    for (RangeDistributionItem item : distribution.getItems()) {
      if (lastUpper + 1 < item.lowerBound()) {
        throw new IllegalArgumentException(String
            .format("Distribution for type %s is not continuous. Missing item between %s and %s",
                attributeType.attributeName(), lastUpper, item.lowerBound()));
      }
      lastUpper = item.upperBound();
    }
  }

  private int getStartOfDistribution(RangeDistributionIfc distribution) {
    return distribution.getItems().first().upperBound() - 1;
  }

  private RangeDistributionItem distributionItemFrom(String zoneId, String columnName) {
    int number = structuralData.valueOrDefault(zoneId, columnName);
    return itemParser.parse(number, columnName);
  }

}
