package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandCreator {

  private final HouseholdBuilder householdBuilder;
  private final PanelDataRepository panelData;
  private final WeightedHouseholdSelector householdSelector;

  public DemandCreator(
      HouseholdBuilder householdBuilder, PanelDataRepository panelDataRepository,
      WeightedHouseholdSelector householdSelector) {
    super();
    this.householdBuilder = householdBuilder;
    this.panelData = panelDataRepository;
    this.householdSelector = householdSelector;
  }

  public List<HouseholdForSetup> demandFor(
      List<WeightedHousehold> households, RangeDistributionIfc distribution) {
    return distribution
        .items()
        .flatMap(item -> filter(households, item))
        .map(this::create)
        .collect(toList());
  }

  private Stream<WeightedHousehold> filter(
      List<WeightedHousehold> households, RangeDistributionItem item) {
    List<WeightedHousehold> householdsByType = households
        .stream()
        .filter(household -> filterType(household, item))
        .collect(toList());
    return householdSelector.selectFrom(householdsByType, item.amount()).stream();
  }

  private boolean filterType(WeightedHousehold household, RangeDistributionItem item) {
    String instanceName = StandardAttribute.householdSize.createInstanceName(item);
    return household.attribute(instanceName) > 0;
  }

  private HouseholdForSetup create(WeightedHousehold household) {
    HouseholdOfPanelData panelHousehold = panelData.getHousehold(household.id());
    return householdBuilder.householdFor(panelHousehold);
  }

}
