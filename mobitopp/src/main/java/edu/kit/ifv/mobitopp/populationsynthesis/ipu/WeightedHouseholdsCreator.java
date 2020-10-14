package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;

public class WeightedHouseholdsCreator {

  private final PanelDataRepository panelData;
  private final AttributeType householdFilterType;

  public WeightedHouseholdsCreator(
      final PanelDataRepository panelData, final AttributeType householdFilterType) {
    this.panelData = panelData;
    this.householdFilterType = householdFilterType;
  }

  public WeightedHouseholds createFor(DemandRegion region) {
    List<WeightedHousehold> households = region
        .zones()
        .map(zone -> createHouseholds(zone))
        .flatMap(List::stream)
        .collect(toList());
    return new WeightedHouseholds(households);
  }

  private List<WeightedHousehold> createHouseholds(DemandZone zone) {
    List<Attribute> householdAttributes = householdAttributesOf(zone);
    AreaType areaType = zone.getAreaType();
    RegionalContext regionalContext = zone.getRegionalContext();
    return new TransferHouseholds(panelData, householdAttributes, regionalContext)
        .forAreaType(areaType);
  }

  private List<Attribute> householdAttributesOf(DemandRegion region) {
    return householdFilterType.createAttributes(region.nominalDemography(), region.getRegionalContext()).collect(toList());
//    RangeDistributionIfc distribution = householdDistributionFor(region);
//    return distribution.items().map(householdFilterType::createInstanceName).collect(toList());
  }

  private RangeDistributionIfc householdDistributionFor(DemandRegion forRegion) {
    return forRegion.nominalDemography().getDistribution(householdFilterType);
  }

}
