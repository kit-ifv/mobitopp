package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DynamicHouseholdAttribute implements Attribute {

  private final AttributeType attributeType;
  private final int lowerBound;
  private final int upperBound;
  private final Function<HouseholdOfPanelData, Integer> householdValue;

  public DynamicHouseholdAttribute(
      AttributeType attributeType, int lowerBound, int upperBound,
      Function<HouseholdOfPanelData, Integer> householdValue) {
    super();
    this.attributeType = attributeType;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.householdValue = householdValue;
  }

  @Override
  public Constraint createConstraint(Demography demography) {
    int requestedWeight = demography.getDistribution(attributeType).amount(lowerBound);
    return new HouseholdConstraint(requestedWeight, name());
  }

  @Override
  public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
    return lowerBound <= householdValue.apply(household)
        && upperBound >= householdValue.apply(household) ? 1 : 0;
  }

  @Override
  public String name() {
    return attributeType.createInstanceName(lowerBound, upperBound);
  }
}
