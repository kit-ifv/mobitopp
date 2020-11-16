package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class Setup {

  private final DemandZone someZone;
  private final DemandZone otherZone;
  private final DemandZone anotherZone;
  private final HouseholdOfPanelData somePanelHousehold;
  private final HouseholdOfPanelData otherPanelHousehold;

  private final WeightedHouseholds weightedHouseholds;
  private final List<Double> scalingFactors;
  private final double[] expectedWeights;
  private final double initialGoodnessOfFit;
  private final double scaledGoodnessOfFit;

}