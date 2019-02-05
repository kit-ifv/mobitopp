package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class ContinuousDistributionBuilder {

  private final StructuralData structuralData;
  private final AttributeType attributeType;

  public ContinuousDistributionBuilder(StructuralData structuralData, AttributeType attributeType) {
    super();
    this.structuralData = structuralData;
    this.attributeType = attributeType;
  }

  public <T extends ContinuousDistributionIfc> T buildFor(
      String zoneId, Supplier<T> continuousDistributionFactory) {
    T distribution = continuousDistributionFactory.get();
    List<String> values = new ArrayList<>();
    for (String attribute : structuralData.getAttributes()) {
      if (attribute.startsWith(attributeType.attributeName())) {
        values.add(attribute);
      }
    }
    for (int index = 0; index < values.size(); index++) {
      String value = values.get(index);
      ContinuousDistributionItem valueItem = distributionItemFrom(zoneId, value);
      distribution.addItem(valueItem);
    }
    verify(distribution);
    return distribution;
  }

  private void verify(ContinuousDistributionIfc distribution) {
    int lastUpper = 0;
    for (ContinuousDistributionItem item : distribution.getItems()) {
      if (lastUpper + 1 < item.lowerBound()) {
        throw new IllegalArgumentException(String
            .format("Distribution for type %s is not continuous. Missing item between %s and %s",
                attributeType.attributeName(), lastUpper, item.lowerBound()));
      }
      lastUpper = item.upperBound();
    }
  }

  private ContinuousDistributionItem distributionItemFrom(String zoneId, String columnName) {
    String tmp = columnName.replaceFirst(attributeType.prefix(), "");
    String[] parts = tmp.split("-");
    int lowerBound = Integer.parseInt(parts[0]);
    int upperBound = (parts.length == 2) ? Integer.parseInt(parts[1]) : Integer.MAX_VALUE;
    int number = structuralData.valueOrDefault(zoneId, columnName);
    return new ContinuousDistributionItem(lowerBound, upperBound, number);
  }

}
