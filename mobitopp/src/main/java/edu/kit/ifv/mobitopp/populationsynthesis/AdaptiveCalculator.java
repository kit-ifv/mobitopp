package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemographicSelector;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class AdaptiveCalculator implements DemandDataForDemandRegionCalculator {

  private final DemandDataForDemandRegionCalculator other;
  private final Function<DemandRegion, Stream<DemandRegion>> demographicSelector;

  public AdaptiveCalculator(
      DemandDataForDemandRegionCalculator other,
      Function<DemandRegion, Stream<DemandRegion>> demographicSelector) {
    super();
    this.other = other;
    this.demographicSelector = demographicSelector;
  }
  
  public AdaptiveCalculator(DemandDataForDemandRegionCalculator other) {
    this(other, new DemographicSelector()::select);
  }

  @Override
  public void calculateDemandData(DemandRegion region, ImpedanceIfc impedance) {
    selectBasedOnDemography(region).forEach(part -> other.calculateDemandData(part, impedance));
  }

  private Stream<DemandRegion> selectBasedOnDemography(DemandRegion region) {
    return demographicSelector.apply(region);
  }

}
