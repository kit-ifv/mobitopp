package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public class ProcessOnLowerRegionLevelStep implements PopulationSynthesisStep {

  private final PopulationSynthesisStep other;
  private final RegionalLevel expectedLevel;

  public ProcessOnLowerRegionLevelStep(
      final PopulationSynthesisStep other, final RegionalLevel expectedLevel) {
    this.other = other;
    this.expectedLevel = expectedLevel;
  }

  @Override
  public void process(final DemandRegion region) {
    if (expectedLevel.equals(region.regionalLevel())) {
      other.process(region);
      return;
    }
    processPartsOf(region);
  }

  private void processPartsOf(final DemandRegion region) {
    List<DemandRegion> parts = region.parts();
    for (DemandRegion part : parts) {
      process(part);
    }
  }

}
