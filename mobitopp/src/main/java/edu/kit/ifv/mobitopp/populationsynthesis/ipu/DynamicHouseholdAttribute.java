package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DynamicHouseholdAttribute implements Attribute {

  private final String prefix;
  private final int lowerBound;
  private final int upperBound;
  private final Function<HouseholdOfPanelData, Integer> householdValue;
  private final Function<Demography, RangeDistributionIfc> selectDistribution;

  public DynamicHouseholdAttribute(String prefix,
      int lowerBound, int upperBound, Function<HouseholdOfPanelData, Integer> householdValue,
      Function<Demography, RangeDistributionIfc> selectDistribution) {
    super();
    this.prefix = prefix;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.householdValue = householdValue;
    this.selectDistribution = selectDistribution;
  }

  @Override
  public Constraint createConstraint(Demography demography) {
    RangeDistributionItem item = selectDistribution.apply(demography).getItem(lowerBound);
    int amount = item.amount();
    return new HouseholdConstraint(amount, name());
  }

  @Override
  public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
    return lowerBound <= householdValue.apply(household)
        && upperBound >= householdValue.apply(household) ? 1 : 0;
  }

  @Override
  public String name() {
    return prefix + lowerBound + "-" + upperBound;
  }
}
