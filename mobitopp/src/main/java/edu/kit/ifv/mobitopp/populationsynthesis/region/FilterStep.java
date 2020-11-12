package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public class FilterStep implements PopulationSynthesisStep {

  private final PopulationSynthesisStep other;
  private final Predicate<DemandRegion> predicate;

  public FilterStep(final PopulationSynthesisStep other, final Predicate<DemandRegion> predicate) {
    super();
    this.other = other;
    this.predicate = predicate;
  }

  @Override
  public void process(DemandRegion region) {
    if (predicate.test(region)) {
      other.process(region);
    }
  }

}
